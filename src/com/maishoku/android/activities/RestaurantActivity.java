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
		API.addCartButton(this);
		TextView textView;
		textView = (TextView) findViewById(R.id.restaurantAddressTextView1);
		textView.setText(restaurant.getAddress());
		textView = (TextView) findViewById(R.id.restaurantAddressTextView2);
		textView.setText(R.string.address);
		textView = (TextView) findViewById(R.id.restaurantPhoneNumberTextView1);
		textView.setText(restaurant.getPhone_order());
		textView = (TextView) findViewById(R.id.restaurantPhoneNumberTextView2);
		textView.setText(R.string.phone_order);
		textView = (TextView) findViewById(R.id.restaurantDeliveryTimeTextView1);
		textView.setText(restaurant.getDelivery_time().toString());
		textView = (TextView) findViewById(R.id.restaurantDeliveryTimeTextView2);
		textView.setText(R.string.delivery_time);
		textView = (TextView) findViewById(R.id.restaurantMinimumOrderTextView1);
		textView.setText(String.format("¥%d", restaurant.getMinimum_order()));
		textView = (TextView) findViewById(R.id.restaurantMinimumOrderTextView2);
		textView.setText(R.string.minimum_delivery);
		textView = (TextView) findViewById(R.id.restaurantCuisinesTextView1);
		textView.setText(restaurant.getCommaSeparatedCuisines());
		textView = (TextView) findViewById(R.id.restaurantCuisinesTextView2);
		textView.setText(R.string.cuisines);
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
