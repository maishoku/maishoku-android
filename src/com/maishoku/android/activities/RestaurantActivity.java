package com.maishoku.android.activities;

import com.maishoku.android.API;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.R;
import com.maishoku.android.models.Restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RestaurantActivity extends RedTitleBarActivity {

	protected static final String TAG = RestaurantActivity.class.getSimpleName();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.restaurant);
		Restaurant restaurant = API.restaurant;
		setCustomTitle(restaurant.getName());
		TextView textView = (TextView) findViewById(R.id.restaurantAddressTextView);
		textView.setText(restaurant.getAddress());
		textView = (TextView) findViewById(R.id.restaurantCuisinesTextView);
		textView.append(restaurant.getCommaSeparatedCuisines());
		textView = (TextView) findViewById(R.id.restaurantDeliveryTimeTextView);
		textView.append(restaurant.getDelivery_time().toString());
		textView = (TextView) findViewById(R.id.restaurantMinimumOrderTextView);
		textView.append(String.valueOf(restaurant.getMinimum_order()));
		textView = (TextView) findViewById(R.id.restaurantPhoneNumberTextView);
		textView.setText(restaurant.getPhone_contact());
		setTitle(restaurant.getName());
		Button button = (Button) findViewById(R.id.restaurantButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RestaurantActivity.this, ItemListActivity.class));
			}
		});
	}

}
