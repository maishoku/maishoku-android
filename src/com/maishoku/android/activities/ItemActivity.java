package com.maishoku.android.activities;

import com.maishoku.android.API;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.R;
import com.maishoku.android.models.Cart;
import com.maishoku.android.models.Item;

import android.content.Context;
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
		super.onCreate(savedInstanceState, R.layout.item);
		final Item item = API.item;
		setCustomTitle(item.getName());
		API.addCartButton(this);
		TextView textView = (TextView) findViewById(R.id.itemPriceTextView);
		textView.append(String.valueOf(item.getPrice()));
		textView = (TextView) findViewById(R.id.itemCategoryTextView);
		textView.append(item.getName());
		final EditText editText = (EditText) findViewById(R.id.itemEditText);
		final TextView currentlyInCartTextView = (TextView) findViewById(R.id.itemCurrentlyInCartTextView);
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
				// Hide keyboard
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		String text = getResources().getString(R.string.quantity_in_cart);
		TextView currentlyInCartTextView = (TextView) findViewById(R.id.itemCurrentlyInCartTextView);
		currentlyInCartTextView.setText(text + Cart.quantityForItem(API.item));
	}

}
