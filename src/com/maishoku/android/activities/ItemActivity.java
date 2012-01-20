package com.maishoku.android.activities;

import com.maishoku.android.API;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.R;
import com.maishoku.android.models.Cart;
import com.maishoku.android.models.Item;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ItemActivity extends RedTitleBarActivity {

	protected static final String TAG = ItemActivity.class.getSimpleName();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item);
		final Item item = API.item;
		setTitle(item.getName());
		TextView textView = (TextView) findViewById(R.id.itemPriceTextView);
		textView.append(String.valueOf(item.getPrice()));
		textView = (TextView) findViewById(R.id.itemCategoryTextView);
		textView.append(item.getName());
		final EditText editText = (EditText) findViewById(R.id.itemEditText);
		final TextView currentlyInCartTextView = (TextView) findViewById(R.id.itemCurrentlyInCartTextView);
		final Button checkoutButton = (Button) findViewById(R.id.itemCheckoutButton);
		Button button = (Button) findViewById(R.id.itemAddToCartButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int quantity = Integer.parseInt(editText.getText().toString());
				try {
					Cart.addToCart(item, quantity);
				} catch (IllegalArgumentException e) {
					return;
				}
				String text = ItemActivity.this.getResources().getString(R.string.quantity_in_cart);
				currentlyInCartTextView.setText(text + Cart.quantityForItem(item));
				checkoutButton.setEnabled(Cart.totalPrice() > 0);
				// Hide keyboard
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			}
		});
		checkoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ItemActivity.this, CartActivity.class));
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		String text = getResources().getString(R.string.quantity_in_cart);
		TextView currentlyInCartTextView = (TextView) findViewById(R.id.itemCurrentlyInCartTextView);
		currentlyInCartTextView.setText(text + Cart.quantityForItem(API.item));
		Button checkoutButton = (Button) findViewById(R.id.itemCheckoutButton);
		checkoutButton.setEnabled(Cart.totalPrice() > 0);
	}

}
