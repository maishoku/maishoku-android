package com.maishoku.android.activities;

import com.maishoku.android.API;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.R;
import com.maishoku.android.API.OrderMethod;
import com.maishoku.android.models.Cart;
import com.maishoku.android.models.Position;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CartActivity extends RedTitleBarActivity {

	protected static final String TAG = CartActivity.class.getSimpleName();
	
	private ArrayAdapter<Position> adapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.cart);
		setCustomTitle(R.string.cart);
		Button button = (Button) findViewById(R.id.cartCheckoutButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
			}
		});
		TextView textView = (TextView) findViewById(R.id.cartRestaurantTextView);
		textView.setText(API.restaurant.getName());
		adapter = new ArrayAdapter<Position>(this, R.layout.list_item);
		ListView listView = (ListView) findViewById(R.id.cartListView);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		reloadCart();
		adapter.clear();
		for (Position position: Cart.allPositions()) {
			adapter.add(position);
		}
	}
	
	private void reloadCart() {
		int totalPrice = Cart.totalPrice();
		int amountRemaining = API.restaurant.getMinimumDelivery() - totalPrice;
		TextView textView = (TextView) findViewById(R.id.cartAmountRemainingTextView);
		Button button = (Button) findViewById(R.id.cartCheckoutButton);
		if (API.orderMethod == OrderMethod.delivery && amountRemaining > 0) {
			textView.setText(String.format(getResources().getString(R.string.amount_remaining), amountRemaining));
			button.setEnabled(false);
		} else if (API.orderMethod == OrderMethod.pickup && totalPrice == 0) {
			textView.setText(null);
			button.setEnabled(false);
		} else {
			textView.setText(null);
			button.setEnabled(true);
		}
		textView = (TextView) findViewById(R.id.cartTotalPriceTextView);
		textView.setText(getResources().getString(R.string.total_price) + totalPrice);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.cart);
		menu.add(R.string.delete);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Position position = adapter.getItem(info.position);
		Cart.removePosition(position);
		adapter.remove(position);
		reloadCart();
		return true;
	}

}
