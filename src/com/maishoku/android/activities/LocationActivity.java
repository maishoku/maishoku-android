package com.maishoku.android.activities;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

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
import com.maishoku.android.API.OrderMethod;
import com.maishoku.android.IO;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.R;
import com.maishoku.android.Result;
import com.maishoku.android.models.Address;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class LocationActivity extends RedTitleBarActivity {

	protected static final String TAG = LocationActivity.class.getSimpleName();
	
	private Address selectedAddress;
	private ArrayAdapter<Address> adapter;
	private ProgressDialog progressDialog;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.location);
		setCustomTitle(R.string.app_name);
		Button button = (Button) findViewById(R.id.locationLogoutButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = getSharedPreferences(API.PREFS, Context.MODE_PRIVATE).edit();
				editor.remove("username");
				editor.remove("password");
				editor.commit();
				startActivity(new Intent(LocationActivity.this, LoginActivity.class));
				LocationActivity.this.finish();
			}
		});
		button = (Button) findViewById(R.id.locationAddNewAddressButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LocationActivity.this, NewAddressActivity.class));
			}
		});
		final ImageButton imageButton = (ImageButton) findViewById(R.id.locationGpsButton);
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				final AlertDialog alertDialog = new AlertDialog.Builder(LocationActivity.this).setMessage(R.string.getting_location).show();
				final LocationListener locationListener = new LocationListener() {
					public void onLocationChanged(Location location) {
						if (location.getAccuracy() < 100.0) {
							Address address = new Address();
							address.setId(1);
							address.setLat(location.getLatitude());
							address.setLon(location.getLongitude());
							API.address = address;
							alertDialog.dismiss();
							startActivity(new Intent(LocationActivity.this, RestaurantListActivity.class));
						}
					}
					public void onStatusChanged(String provider, int status, Bundle extras) {
					}
					public void onProviderEnabled(String provider) {
					}
					public void onProviderDisabled(String provider) {
					}
				};
				alertDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						locationManager.removeUpdates(locationListener);
					}
				});
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			}
		});
		final RadioButton deliveryButton = (RadioButton) findViewById(R.id.locationDeliveryRadioButton);
		final RadioButton pickupButton = (RadioButton) findViewById(R.id.locationPickupRadioButton);
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.locationRadioGroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == deliveryButton.getId()) {
					imageButton.setVisibility(View.INVISIBLE);
					API.orderMethod = OrderMethod.delivery;
				} else if (checkedId == pickupButton.getId()) {
					imageButton.setVisibility(View.VISIBLE);
					API.orderMethod = OrderMethod.pickup;
				}
			}
		});
		adapter = new ArrayAdapter<Address>(this, R.layout.list_item);
		ListView listView = (ListView) findViewById(R.id.locationListView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Address address = adapter.getItem(position);
				API.address = address;
				startActivity(new Intent(LocationActivity.this, RestaurantListActivity.class));
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				selectedAddress = adapter.getItem(position);
				return false;
			}
		});
		registerForContextMenu(listView);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		adapter.clear();
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.loading);
		progressDialog.show();
		new LoadAddressesTask().execute();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.address);
		menu.add(R.string.delete);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Address address = adapter.getItem(info.position);
		try {
			// The selected context item is always 'delete'
			URI url = API.getURL("/addresses/" + address.getId());
			HttpEntity<Void> httpEntity = API.getHttpEntity(this);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, null);
			// Should 'always' succeed, so don't care about the response - just remove here
			adapter.remove(selectedAddress);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Could not delete address", e);
			return false;
		}
	}
	
	private class LoadAddressesTask extends AsyncTask<Void, Void, Result<Address[]>> {
		
		protected final String TAG = LoadAddressesTask.class.getSimpleName();
		
		private final AtomicReference<String> message;
		
		public LoadAddressesTask() {
			this.message = new AtomicReference<String>();
		}
		
		@Override
		protected Result<Address[]> doInBackground(Void... params) {
			try {
				final URI url = API.getURL("/user/addresses");
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<Void> httpEntity = API.getHttpEntity(LocationActivity.this, httpHeaders);
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
				ResponseEntity<Address[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Address[].class);
				return new Result<Address[]>(responseEntity.getStatusCode() == HttpStatus.OK, message.get(), responseEntity.getBody());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				return new Result<Address[]>(false, message.get(), null);
			}
		}
		
		@Override
		protected void onPostExecute(Result<Address[]> result) {
			progressDialog.dismiss();
			if (result.success) {
				for (Address address: result.resource) {
					adapter.add(address);
				}
			} else {
				Log.e(TAG, "Failed to load addresses");
			}
		}
	
	}

}
