package com.maishoku.android.activities;

import java.io.IOException;
import java.net.URI;
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
import com.maishoku.android.RedTitleBarListActivity;
import com.maishoku.android.R;
import com.maishoku.android.Result;
import com.maishoku.android.models.Address;
import com.maishoku.android.models.Cart;
import com.maishoku.android.models.Restaurant;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RestaurantListActivity extends RedTitleBarListActivity {

	protected static final String TAG = RestaurantListActivity.class.getSimpleName();
	
	private final AtomicBoolean restaurantsLoaded = new AtomicBoolean(false);
	private ArrayAdapter<Restaurant> adapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.restaurants);
		adapter = new ArrayAdapter<Restaurant>(this, R.layout.list_item);
		ListView listView = getListView();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Restaurant restaurant = adapter.getItem(position);
				if (restaurant != API.restaurant) {
					Cart.clear();
					API.restaurant = restaurant;
				}
				startActivity(new Intent(RestaurantListActivity.this, RestaurantActivity.class));
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (!restaurantsLoaded.get()) {
			new LoadRestaurantsTask().execute();
		}
	}
	
	private class LoadRestaurantsTask extends AsyncTask<Void, Void, Result<Restaurant[]>> {
		
		protected final String TAG = LoadRestaurantsTask.class.getSimpleName();
		
		private final AtomicReference<String> message;
		
		public LoadRestaurantsTask() {
			this.message = new AtomicReference<String>();
		}
		
		@Override
		protected Result<Restaurant[]> doInBackground(Void... params) {
			try {
				Address address = API.address;
				double lat = address.getLat();
				double lon = address.getLon();
				final URI url = API.getURL(String.format("/restaurants/search/%s?lat=%f&lon=%f", API.orderMethod.name(), lat, lon));
				HttpEntity<Void> httpEntity = API.getHttpEntity(RestaurantListActivity.this);
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
				ResponseEntity<Restaurant[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Restaurant[].class);
				return new Result<Restaurant[]>(responseEntity.getStatusCode() == HttpStatus.OK, message.get(), responseEntity.getBody());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				return new Result<Restaurant[]>(false, message.get(), null);
			}
		}
		
		@Override
		protected void onPostExecute(Result<Restaurant[]> result) {
			if (result.success) {
				Log.i(TAG, "Successfully loaded restaurants");
				for (Restaurant restaurant: result.resource) {
					adapter.add(restaurant);
				}
				restaurantsLoaded.set(true);
			} else {
				Log.e(TAG, "Failed to load restaurants");
			}
		}
	
	}

}
