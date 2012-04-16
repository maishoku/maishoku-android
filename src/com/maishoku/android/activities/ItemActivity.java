package com.maishoku.android.activities;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.maishoku.android.ScalingUtilities;
import com.maishoku.android.ScalingUtilities.ScalingLogic;
import com.maishoku.android.models.Cart;
import com.maishoku.android.models.Item;
import com.maishoku.android.models.Option;
import com.maishoku.android.models.OptionSet;
import com.maishoku.android.models.Position;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ItemActivity extends RedTitleBarActivity {

	protected static final String TAG = ItemActivity.class.getSimpleName();
	
	private ProgressDialog progressDialog;
	private final AtomicBoolean itemLoaded = new AtomicBoolean(false);
	private ArrayList<HashMap<String, CharSequence>> list = new ArrayList<HashMap<String, CharSequence>>();
	private ArrayList<OptionSet> optionSets = null;
	private Drawable white;
	private HashSet<AsyncTask<?, ?, ?>> tasks = new HashSet<AsyncTask<?, ?, ?>>();
	private Item item = null;
	private Position position = null;
	private SimpleAdapter optionSetAdapter;
	private int selectedOptionSetIndex;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.item);
		final TextView quantityTextView = (TextView) findViewById(R.id.itemQuantityTextView);
		final SeekBar seekBar = (SeekBar) findViewById(R.id.itemSeekBar);
		final Button addToCartButton = (Button) findViewById(R.id.itemAddToCartButton);
		final Button addToppingsButton = (Button) findViewById(R.id.itemAddToppingsButton);
		final ListView optionsListView = (ListView) findViewById(R.id.itemListView);
		white = getResources().getDrawable(R.drawable.white120x120);
		item = API.item;
		setCustomTitle(Html.fromHtml(item.getCategory().getName()));
		API.addCartButton(this);
		addToCartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (position == null) {
					return;
				}
				position.setQuantity(seekBar.getProgress());
				Cart.addPosition(position);
				position = new Position(item, 0);
				list.clear();
				for (OptionSet optionSet: optionSets) {
					Option option = optionSet.getOptions()[0];
					position.getOptions().add(option);
					HashMap<String, CharSequence> map = new HashMap<String, CharSequence>();
					map.put("title", Html.fromHtml(optionSet.toString()));
					map.put("subtitle", Html.fromHtml(option.toString()));
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
		HashMap<String, CharSequence> map = list.get(selectedOptionSetIndex);
		map.put("subtitle", Html.fromHtml(option.toString()));
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
			LoadItemTask task = new LoadItemTask();
			tasks.add(task);
			task.execute();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		for (AsyncTask<?, ?, ?> task: tasks) {
			task.cancel(true);
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
			if (isCancelled()) {
				return;
			}
			progressDialog.dismiss();
			if (result.success) {
				Log.i(TAG, "Successfully loaded item");
				item = result.resource;
				position = new Position(item, 0);
				LinearLayout linearLayout = (LinearLayout) findViewById(R.id.itemLinearLayout);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
				Display display = getWindowManager().getDefaultDisplay();
				int width = (display.getWidth() - layoutParams.leftMargin - layoutParams.rightMargin) / 3;
				LoadDefaultImageTask task = new LoadDefaultImageTask(item.getDefault_image_url(), width, layoutParams.height);
				tasks.add(task);
				task.execute();
				setTitle(Html.fromHtml(item.getCategory().getName()));
				TextView textView = (TextView) findViewById(R.id.itemTextView);
				CharSequence description = item.getDescription() == null ? "" : item.getDescription();
				textView.setText(Html.fromHtml(String.format("%s<br>¥%d<br>%s", item.getName(), item.getPrice(), description)));
				List<Option> positionOptions = position.getOptions();
				optionSets = new ArrayList<OptionSet>();
				for (OptionSet optionSet: item.getOption_sets()) {
					Option[] options = optionSet.getOptions();
					if (options.length > 0) {
						optionSets.add(optionSet);
						Option option = options[0];
						positionOptions.add(option);
						HashMap<String, CharSequence> map = new HashMap<String, CharSequence>();
						map.put("title", Html.fromHtml(optionSet.toString()));
						map.put("subtitle", Html.fromHtml(option.toString()));
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
	
	private class LoadDefaultImageTask extends AsyncTask<Void, Void, Bitmap> {
		
		private final String defaultImageURL;
		private final int width;
		private final int height;
		
		public LoadDefaultImageTask(String defaultImageURL, int width, int height) {
			this.defaultImageURL = defaultImageURL;
			this.width = width;
			this.height = height;
		}
		
		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				File file = API.getImageFile(ItemActivity.this, defaultImageURL);
				Bitmap unscaledBitmap = ScalingUtilities.decodeFile(file, width, height, ScalingLogic.FIT);
				if (unscaledBitmap == null) {
					return null;
				} else {
					Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, width, height, ScalingLogic.FIT);
					unscaledBitmap.recycle();
					return scaledBitmap;
				}
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if (isCancelled()) {
				return;
			}
			ImageView imageView = (ImageView) findViewById(R.id.itemImageView);
			if (result == null) {
				imageView.setImageDrawable(white);
			} else {
				imageView.setImageBitmap(result);
			}
		}
	
	}

}
