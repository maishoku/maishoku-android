package com.maishoku.android.activities;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.maishoku.android.API;
import com.maishoku.android.IO;
import com.maishoku.android.RedTitleBarListActivity;
import com.maishoku.android.R;
import com.maishoku.android.Result;
import com.maishoku.android.models.Category;
import com.maishoku.android.models.Item;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ItemListActivity extends RedTitleBarListActivity {

	protected static final String TAG = ItemListActivity.class.getSimpleName();
	
	private static final int HEIGHT = 40;
	private static final int WIDTH = 60;
	private static final int ADJUSTED_HEIGHT = (int) (HEIGHT * 1.5);
	private static final int ADJUSTED_WIDTH = (int) (WIDTH * 1.5);
	private final AtomicBoolean itemsLoaded = new AtomicBoolean(false);
	private final HashMap<Integer, TextView> textViewsByPosition = new HashMap<Integer, TextView>();
	private ArrayAdapter<Item> adapter;
	private Drawable blank;
	private ProgressDialog progressDialog;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomTitle(API.restaurant.getName());
		API.addCartButton(this);
		blank = getResources().getDrawable(R.drawable.blank60x40);
		blank.setBounds(0, 0, WIDTH, HEIGHT);
		adapter = new ArrayAdapter<Item>(this, R.layout.list_item) {
			@Override
			public boolean isEnabled(int position) {
				return getItem(position).isAvailable();
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				boolean enabled = isEnabled(position);
				if (enabled) {
					view.setBackgroundColor(Color.WHITE);
				} else {
					view.setBackgroundColor(Color.GRAY);
				}
				if (view instanceof TextView) {
					TextView textView = (TextView) view;
					LinkedList<Integer> positions = new LinkedList<Integer>();
					for (HashMap.Entry<Integer, TextView> entry: textViewsByPosition.entrySet()) {
						if (entry.getValue() == textView && entry.getKey() != position) {
							positions.add(entry.getKey());
						}
					}
					for (Integer i: positions) {
						textViewsByPosition.put(i, null);
					}
					if (enabled) {
						textViewsByPosition.put(position, textView);
						Item item = getItem(position);
						textView.setLines(2);
						textView.setText(Html.fromHtml(String.format("%s<br><small>¥%d</small>", item.getName(), item.getPrice())));
						textView.setCompoundDrawablePadding(textView.getPaddingLeft());
						textView.setCompoundDrawablesWithIntrinsicBounds(blank, null, null, null);
						new LoadThumbnailImageTask(item.getThumbnail_image_url(), position).execute();
					} else {
						textView.setLines(1);
						textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
					}
				}
				return view;
			}
		};
		ListView listView = getListView();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				API.item = adapter.getItem(position);
				startActivity(new Intent(ItemListActivity.this, ItemActivity.class));
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (!itemsLoaded.get()) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle(R.string.loading);
			progressDialog.show();
			new LoadCategoriesTask().execute();
		}
	}
	
	private class LoadThumbnailImageTask extends AsyncTask<Void, Void, Drawable> {
	
		private final String thumbnailImageURL;
		private final int position;
		
		public LoadThumbnailImageTask(String thumbnailImageURL, int position) {
			this.thumbnailImageURL = thumbnailImageURL;
			this.position = position;
		}
		
		@Override
		protected Drawable doInBackground(Void... params) {
			try {
				return Drawable.createFromStream(API.getImage(ItemListActivity.this, thumbnailImageURL), "src");
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Drawable result) {
			TextView textView = textViewsByPosition.get(position);
			if (result != null && textView != null) {
				result.setBounds(0, 0, ADJUSTED_WIDTH, ADJUSTED_HEIGHT);
				textView.setCompoundDrawables(result, null, null, null);
			}
		}
	
	}
	
	private class LoadCategoriesTask extends AsyncTask<Void, Void, Result<Category[]>> {
		
		protected final String TAG = LoadCategoriesTask.class.getSimpleName();
		
		private final AtomicReference<String> message;
		
		public LoadCategoriesTask() {
			this.message = new AtomicReference<String>();
		}
		
		@Override
		protected Result<Category[]> doInBackground(Void... params) {
			try {
				final URI url = API.getURL(String.format("/restaurants/%d/categories", API.restaurant.getId()));
				HttpEntity<Void> httpEntity = API.getHttpEntity(ItemListActivity.this);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.setErrorHandler(new ResponseErrorHandler() {
					@Override
					public boolean hasError(ClientHttpResponse response) throws IOException {
						return response.getStatusCode() != HttpStatus.OK;
					}
					@Override
					public void handleError(ClientHttpResponse response) throws IOException {
						try {
							message.set(IO.readString(response.getBody()));
						} catch (IllegalStateException e) {
							// Content has been consumed
						}
					}
				});
				ResponseEntity<Category[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Category[].class);
				return new Result<Category[]>(responseEntity.getStatusCode() == HttpStatus.OK, message.get(), responseEntity.getBody());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				return new Result<Category[]>(false, message.get(), null);
			}
		}
		
		@Override
		protected void onPostExecute(Result<Category[]> result) {
			progressDialog.dismiss();
			if (result.success) {
				Log.i(TAG, "Successfully loaded categories");
				for (Category category: result.resource) {
					Item i = new Item();
					i.setName_english(category.getName_english());
					i.setName_japanese(category.getName_japanese());
					i.setAvailable(false);
					adapter.add(i);
					for (Item item: category.getItems()) {
						item.setCategory(category);
						adapter.add(item);
					}
				}
				itemsLoaded.set(true);
			} else {
				Log.e(TAG, "Failed to load categories");
			}
		}
	
	}

}
