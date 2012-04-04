package com.maishoku.android.activities;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.R;
import com.maishoku.android.Result;
import com.maishoku.android.models.Cart;
import com.maishoku.android.models.Item;
import com.maishoku.android.models.Option;
import com.maishoku.android.models.OptionSet;
import com.maishoku.android.models.Position;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ItemActivity extends RedTitleBarActivity {

	protected static final String TAG = ItemActivity.class.getSimpleName();
	
	private ProgressDialog progressDialog;
	private final AtomicBoolean itemLoaded = new AtomicBoolean(false);
	private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter optionSetAdapter;
	private ArrayList<OptionSet> optionSets = null;
	private int selectedOptionSetIndex;
	private Position position = null;
	private Item item = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.item);
		final TextView quantityTextView = (TextView) findViewById(R.id.itemQuantityTextView);
		final SeekBar seekBar = (SeekBar) findViewById(R.id.itemSeekBar);
		final Button addToCartButton = (Button) findViewById(R.id.itemAddToCartButton);
		final Button addToppingsButton = (Button) findViewById(R.id.itemAddToppingsButton);
		final ListView optionsListView = (ListView) findViewById(R.id.itemListView);
		item = API.item;
		setCustomTitle(item.getName());
		API.addCartButton(this);
		addToCartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				position.setQuantity(seekBar.getProgress());
				Cart.addPosition(position);
				position = new Position(item, 0);
				list.clear();
				for (OptionSet optionSet: optionSets) {
					Option option = optionSet.getOptions()[0];
					position.getOptions().add(option);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("title", optionSet.toString());
					map.put("subtitle", option.toString());
					list.add(map);
				}
				optionSetAdapter.notifyDataSetChanged();
				reloadCurrentlyInCartTextView();
			}
		});
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				addToCartButton.setEnabled(progress > 0);
				quantityTextView.setText(String.format("%s: %d", getResources().getString(R.string.quantity), progress));
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		seekBar.setProgress(1);
		addToppingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				API.toppingsItem = item;
				API.toppingsPosition = position;
				startActivity(new Intent(ItemActivity.this, ToppingsActivity.class));
			}
		});
		optionSetAdapter = new SimpleAdapter(this, list, R.layout.subtitle_list_item, new String[] { "title", "subtitle" }, new int[] { R.id.listItemTitle, R.id.listItemSubtitle });
		optionsListView.setAdapter(optionSetAdapter);
		optionsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedOptionSetIndex = position;
				openContextMenu(optionsListView);
			}
		});
		registerForContextMenu(optionsListView);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		menu.clear();
		OptionSet optionSet = optionSets.get(selectedOptionSetIndex);
		menu.setHeaderTitle(optionSet.getName());
		Option[] options = optionSet.getOptions();
		for (int i = 0, n = options.length; i < n; i++) {
			menu.add(Menu.NONE, i, i, options[i].toString());
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		OptionSet optionSet = optionSets.get(selectedOptionSetIndex);
		Option[] options = optionSet.getOptions();
		Option option = options[item.getItemId()];
		List<Option> positionOptions = position.getOptions();
		outer:
		for (Option o1: positionOptions) {
			for (Option o2: options) {
				if (o1 == o2) {
					positionOptions.remove(o1);
					break outer;
				}
			}
		}
		HashMap<String, String> map = list.get(selectedOptionSetIndex);
		map.put("subtitle", option.toString());
		optionSetAdapter.notifyDataSetChanged();
		positionOptions.add(option);
		return true;
	}
	
	private void reloadCurrentlyInCartTextView() {
		String text = getResources().getString(R.string.quantity_in_cart);
		TextView currentlyInCartTextView = (TextView) findViewById(R.id.itemCurrentlyInCartTextView);
		currentlyInCartTextView.setText(text + Cart.size());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		reloadCurrentlyInCartTextView();
		if (!itemLoaded.get()) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle(R.string.loading);
			progressDialog.show();
			new LoadItemTask().execute();
		}
	}
	
	private class LoadItemTask extends AsyncTask<Void, Void, Result<Item>> {
		
		protected final String TAG = LoadItemTask.class.getSimpleName();
		
		private final AtomicReference<String> message;
		
		public LoadItemTask() {
			this.message = new AtomicReference<String>();
		}
		
		@Override
		protected Result<Item> doInBackground(Void... params) {
			try {
				final URI url = API.getURL(String.format("/items/%d", item.getId()));
				HttpEntity<Void> httpEntity = API.getHttpEntity(ItemActivity.this);
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
				ResponseEntity<Item> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Item.class);
				return new Result<Item>(responseEntity.getStatusCode() == HttpStatus.OK, message.get(), responseEntity.getBody());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				return new Result<Item>(false, message.get(), null);
			}
		}
		
		@Override
		protected void onPostExecute(Result<Item> result) {
			progressDialog.dismiss();
			if (result.success) {
				Log.i(TAG, "Successfully loaded item");
				item = result.resource;
				position = new Position(item, 0);
				new LoadDefaultImageTask(item.getDefault_image_url()).execute();
				setTitle(item.getCategory().getName());
				TextView textView = (TextView) findViewById(R.id.itemTextView);
				textView.setText(String.format("%s\n%d\n%s", item.getName(), item.getPrice(), item.getDescription()));
				List<Option> positionOptions = position.getOptions();
				optionSets = new ArrayList<OptionSet>();
				for (OptionSet optionSet: item.getOption_sets()) {
					Option[] options = optionSet.getOptions();
					if (options.length > 0) {
						optionSets.add(optionSet);
						Option option = options[0];
						positionOptions.add(option);
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("title", optionSet.toString());
						map.put("subtitle", option.toString());
						list.add(map);
					}
				}
				boolean toppingsPresent = item.getToppings().length > 0;
				Button button = (Button) findViewById(R.id.itemAddToppingsButton);
				if (toppingsPresent) {
					button.setEnabled(true);
				} else {
					button.setText(R.string.no_toppings);
				}
				button = (Button) findViewById(R.id.itemAddToCartButton);
				button.setEnabled(true);
				itemLoaded.set(true);
			} else {
				Log.e(TAG, "Failed to load item");
			}
		}
	
	}
	
	private class LoadDefaultImageTask extends AsyncTask<Void, Void, Drawable> {
		
		private final String defaultImageURL;
		
		public LoadDefaultImageTask(String defaultImageURL) {
			this.defaultImageURL = defaultImageURL;
		}
		
		@Override
		protected Drawable doInBackground(Void... params) {
			try {
				return Drawable.createFromStream(API.getImage(ItemActivity.this, defaultImageURL), "src");
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Drawable result) {
			if (result != null) {
				ImageView imageView = (ImageView) findViewById(R.id.itemImageView);
				imageView.setImageDrawable(result);
			}
		}
	
	}

}
