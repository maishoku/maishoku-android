package com.maishoku.android.activities;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.maishoku.android.API;
import com.maishoku.android.API.PaymentMethod;
import com.maishoku.android.IO;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.R;
import com.maishoku.android.Result;
import com.maishoku.android.API.OrderMethod;
import com.maishoku.android.models.Cart;
import com.maishoku.android.models.CreditCard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class CheckoutActivity extends RedTitleBarActivity {

	protected static final String TAG = CheckoutActivity.class.getSimpleName();
	
	private ArrayAdapter<CreditCard> adapter;
	private CreditCard selectedCreditCard;
	private ProgressDialog progressDialog;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.checkout);
		setCustomTitle(R.string.checkout);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.loading);
		final Button checkoutButton = (Button) findViewById(R.id.checkoutButton);
		final Button addInstructionsButton = (Button) findViewById(R.id.checkoutAddInstructionsButton);
		final EditText cardNumberEditText = (EditText) findViewById(R.id.checkoutCardNumberEditText);
		final EditText expirationDateEditText = (EditText) findViewById(R.id.checkoutExpirationDateEditText);
		final ListView listView = (ListView) findViewById(R.id.checkoutListView);
		final TextView textView = (TextView) findViewById(R.id.checkoutExpirationDateTextView);
		final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.checkoutToggleButton);
		checkoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog.show();
				checkoutButton.setEnabled(false);
				addInstructionsButton.setEnabled(false);
				new OrderTask().execute();
			}
		});
		addInstructionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CheckoutActivity.this, AddInstructionsActivity.class));
			}
		});
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.checkoutRadioGroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				checkoutButton.setEnabled(false);
				RadioButton cardRadioButton = (RadioButton) findViewById(R.id.checkoutCardRadioButton);
				if (checkedId == cardRadioButton.getId()) { // credit card payment
					String saved_card = getResources().getString(R.string.saved_card);
					String new_card = getResources().getString(R.string.new_card);
					String[] items = {saved_card, new_card};
					AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
					builder.setTitle(R.string.credit_card);
					builder.setItems(items, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int item) {
							selectedCreditCard = null;
							switch (item) {
							case 0: // saved card
								progressDialog.setTitle(R.string.loading);
								progressDialog.show();
								new LoadCreditCardsTask().execute();
								listView.setVisibility(View.VISIBLE);
								cardNumberEditText.setVisibility(View.INVISIBLE);
								expirationDateEditText.setVisibility(View.INVISIBLE);
								textView.setVisibility(View.INVISIBLE);
								toggleButton.setVisibility(View.INVISIBLE);
								checkoutButton.setEnabled(false);
								break;
							case 1: // new card
								listView.setVisibility(View.INVISIBLE);
								cardNumberEditText.setVisibility(View.VISIBLE);
								expirationDateEditText.setVisibility(View.VISIBLE);
								textView.setVisibility(View.VISIBLE);
								toggleButton.setVisibility(View.VISIBLE);
								checkoutButton.setEnabled(true);
								break;
							default:
								setViewsInvisible();
								break;
							}
						}
					});
					builder.create().show();
				} else { // cash payment
					setViewsInvisible();
					checkoutButton.setEnabled(true);
				}
			}
		});
		adapter = new ArrayAdapter<CreditCard>(this, R.layout.list_item);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedCreditCard = adapter.getItem(position);
				checkoutButton.setEnabled(true);
				for (int i = 0, n = listView.getChildCount(); i < n; i++) {
					View childView = listView.getChildAt(i);
					if (childView == view) {
						childView.setBackgroundColor(Color.WHITE);
					} else {
						childView.setBackgroundColor(Color.TRANSPARENT);
					}
				}
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				selectedCreditCard = adapter.getItem(position);
				return false;
			}
		});
		registerForContextMenu(listView);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.credit_card);
		menu.add(R.string.delete);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		CreditCard creditCard = adapter.getItem(info.position);
		try {
			// The selected context item is always 'delete'
			URI url = API.getURL("/credit_cards/" + creditCard.getId());
			HttpEntity<Void> httpEntity = API.getHttpEntity(this);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, null);
			// Should 'always' succeed, so don't care about the response - just remove here
			adapter.remove(selectedCreditCard);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Could not delete credit card", e);
			return false;
		}
	}
	
	private void setViewsInvisible() {
		ListView listView = (ListView) findViewById(R.id.checkoutListView);
		listView.setVisibility(View.INVISIBLE);
		EditText editText = (EditText) findViewById(R.id.checkoutCardNumberEditText);
		editText.setVisibility(View.INVISIBLE);
		editText = (EditText) findViewById(R.id.checkoutExpirationDateEditText);
		editText.setVisibility(View.INVISIBLE);
		TextView textView = (TextView) findViewById(R.id.checkoutExpirationDateTextView);
		textView.setVisibility(View.INVISIBLE);
		ToggleButton toggleButton = (ToggleButton) findViewById(R.id.checkoutToggleButton);
		toggleButton.setVisibility(View.INVISIBLE);
	}
	
	private class OrderTask extends AsyncTask<Void, Void, Result<String>> {
		
		protected final String TAG = OrderTask.class.getSimpleName();
		
		private final AtomicReference<String> message;
		
		public OrderTask() {
			this.message = new AtomicReference<String>();
		}
		
		@Override
		protected Result<String> doInBackground(Void... params) {
			try {
				final URI url = API.getURL("/orders");
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("restaurant_id", API.restaurant.getId());
				RadioGroup radioGroup = (RadioGroup) findViewById(R.id.checkoutRadioGroup);
				RadioButton cardButton = (RadioButton) findViewById(R.id.checkoutCardRadioButton);
				int id = PaymentMethod.cash.value;
				if (radioGroup.getCheckedRadioButtonId() == cardButton.getId()) {
					id = PaymentMethod.card.value;
					if (selectedCreditCard == null) {
						EditText cardNumberEditText = (EditText) findViewById(R.id.checkoutCardNumberEditText);
						EditText expirationDateEditText = (EditText) findViewById(R.id.checkoutExpirationDateEditText);
						map.put("card_number", cardNumberEditText.getText().toString());
						map.put("expiration_date", expirationDateEditText.getText().toString());
						ToggleButton toggleButton = (ToggleButton) findViewById(R.id.checkoutToggleButton);
						if (toggleButton.isChecked()) {
							map.put("save_card", true);
						}
					} else {
						map.put("credit_card_id", selectedCreditCard.getId());
					}
				}
				String instructions = Cart.getInstructions();
				if (instructions != null) {
					map.put("instructions", instructions);
				}
				map.put("payment_method", id);
				map.put("is_delivery", API.orderMethod == OrderMethod.delivery);
				map.put("address_id", API.address.getId());
				map.put("items", Cart.toJSONArray());
				JSONObject json = new JSONObject(map);
				String body = json.toString();
				HttpEntity<?> httpEntity = API.getHttpEntity(CheckoutActivity.this, body, httpHeaders, true);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.setErrorHandler(new ResponseErrorHandler() {
					@Override
					public boolean hasError(ClientHttpResponse response) throws IOException {
						return response.getStatusCode() != HttpStatus.CREATED;
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
				ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
				return new Result<String>(responseEntity.getStatusCode() == HttpStatus.CREATED, message.get(), responseEntity.getBody());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				return new Result<String>(false, message.get(), null);
			}
		}
		
		@Override
		protected void onPostExecute(Result<String> result) {
			progressDialog.dismiss();
			if (result.success) {
				Cart.clear();
				setViewsInvisible();
				RadioGroup radioGroup = (RadioGroup) findViewById(R.id.checkoutRadioGroup);
				radioGroup.setVisibility(View.INVISIBLE);
				TextView textView = (TextView) findViewById(R.id.checkoutTextView);
				textView.setText(getResources().getString(R.string.order_confirmed) + API.restaurant.getDelivery_time().toString());
			} else {
				Button button = (Button) findViewById(R.id.checkoutButton);
				button.setEnabled(true);
				button = (Button) findViewById(R.id.checkoutAddInstructionsButton);
				button.setEnabled(true);
				new AlertDialog.Builder(CheckoutActivity.this)
					.setTitle(R.string.order_failed)
					.setMessage(result.message)
					.setPositiveButton(R.string.ok, null)
					.show();
			}
		}
	
	}
	
	private class LoadCreditCardsTask extends AsyncTask<Void, Void, Result<CreditCard[]>> {
		
		protected final String TAG = LoadCreditCardsTask.class.getSimpleName();
		
		private final AtomicReference<String> message;
		
		public LoadCreditCardsTask() {
			this.message = new AtomicReference<String>();
		}
		
		@Override
		protected Result<CreditCard[]> doInBackground(Void... params) {
			try {
				final URI url = API.getURL("/user/credit_cards");
				HttpEntity<?> httpEntity = API.getHttpEntity(CheckoutActivity.this);
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
				ResponseEntity<CreditCard[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, CreditCard[].class);
				return new Result<CreditCard[]>(responseEntity.getStatusCode() == HttpStatus.OK, message.get(), responseEntity.getBody());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				return new Result<CreditCard[]>(false, message.get(), null);
			}
		}
		
		@Override
		protected void onPostExecute(Result<CreditCard[]> result) {
			progressDialog.dismiss();
			for (CreditCard creditCard: result.resource) {
				adapter.add(creditCard);
			}
		}
	
	}

}
