package com.maishoku.android.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.maishoku.android.API;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.R;
import com.maishoku.android.models.Restaurant;
import com.maishoku.android.models.RestaurantHours;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RestaurantActivity extends RedTitleBarActivity {

	protected static final String TAG = RestaurantActivity.class.getSimpleName();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.restaurant);
		Restaurant restaurant = API.restaurant;
		setCustomTitle(restaurant.getName());
		API.addCartButton(this);
		ImageView imageView = (ImageView) findViewById(R.id.restaurantImageView);
		new LoadMainlogoImageTask(restaurant.getMainlogo_image_url(), imageView).execute();
		TextView textView;
		textView = (TextView) findViewById(R.id.restaurantTextView);
		textView.setText(restaurant.getDescription());
		textView = (TextView) findViewById(R.id.restaurantAddressTextView1);
		textView.setText(restaurant.getAddress());
		textView = (TextView) findViewById(R.id.restaurantPhoneNumberTextView1);
		textView.setText(restaurant.getPhone_order());
		textView = (TextView) findViewById(R.id.restaurantHoursTextView1);
		textView.setText(todaysHours());
		textView = (TextView) findViewById(R.id.restaurantDeliveryTimeTextView1);
		textView.setText(restaurant.getDelivery_time().toString());
		textView = (TextView) findViewById(R.id.restaurantMinimumDeliveryTextView1);
		textView.setText(String.format("¥%d", restaurant.getMinimumDelivery()));
		setTitle(restaurant.getName());
		Button button = (Button) findViewById(R.id.restaurantButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RestaurantActivity.this, ItemListActivity.class));
			}
		});
	}
	
	private String todaysHours() {
		String h = getResources().getString(R.string.closed);
		String dayName = API.todaysDateEEE();
		ArrayList<String> hoursArray = new ArrayList<String>();
		for (RestaurantHours rh: API.restaurant.getHours()) {
			if (rh.getDay_name().equalsIgnoreCase(dayName)) {
				hoursArray.add(String.format("%s - %s", rh.getOpen_time(), rh.getClose_time()));
			}
		}
		int size = hoursArray.size();
		if (size > 0) {
			String[] sortedArray = hoursArray.toArray(new String[hoursArray.size()]);
			Arrays.sort(sortedArray, new Comparator<String>() {
				@Override
				public int compare(String a, String b) {
					int first = Integer.parseInt(a.substring(0, 2));
					int second = Integer.parseInt(b.substring(0, 2));
					if (first > second) {
						return 1;
					} else if (first < second) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			StringBuilder sb = new StringBuilder(sortedArray[0]);
			for (int i = 1; i < size; i++) {
				sb.append(", ").append(sortedArray[i]);
			}
			h = sb.toString();
		}
		return h;
	}
	
	private class LoadMainlogoImageTask extends AsyncTask<Void, Void, Drawable> {
		
		private final String mainlogoImageURL;
		private final ImageView imageView;
		
		public LoadMainlogoImageTask(String mainlogoImageURL, ImageView imageView) {
			this.mainlogoImageURL = mainlogoImageURL;
			this.imageView = imageView;
		}
		
		@Override
		protected Drawable doInBackground(Void... params) {
			try {
				return Drawable.createFromStream(API.getImage(RestaurantActivity.this, mainlogoImageURL), "src");
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Drawable result) {
			if (result != null) {
				imageView.setImageDrawable(result);
			}
		}
	
	}

}
