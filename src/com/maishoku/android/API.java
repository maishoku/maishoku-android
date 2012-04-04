package com.maishoku.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.maishoku.android.activities.CartActivity;
import com.maishoku.android.models.Address;
import com.maishoku.android.models.Item;
import com.maishoku.android.models.Position;
import com.maishoku.android.models.Restaurant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class API {

	public static final int MAISHOKU_RED = Color.rgb(180, 0, 0); // halfway between the 200-160 gradient used in the logo
	public static final Date NULL_DATE = new Date(0, 0, 0);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("EEE", Locale.US);
	public static final String BASE_URL;
	public static final String API_VERSION = "1.0";
	public static final String PREFS = "PREFS";
	public static Address address;
	public static OrderMethod orderMethod = OrderMethod.delivery;
	public static Item item;
	public static Item toppingsItem;
	public static Position toppingsPosition;
	public static Restaurant restaurant;
	public static String username;
	public static String password;
	
	public enum Language { en, ja }
	public enum OrderMethod { delivery, pickup }
	public enum PaymentMethod {
		card(1),
		cash(2);
		public final int value;
		PaymentMethod(int value) {
			this.value = value;
		}
	}
	
	private static final Object OBJECT = new Object();
	
	/*
	 * Cache image lookups to ensure that we don't hit the network more than once per activity lifecycle.
	 * Remove old entries to prevent a memory leak.
	 */
	private static final Map<String, Object> urls = Collections.synchronizedMap(new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = 6728185220878544083L;
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
			return size() > 100;
		}
	});
	
	static {
		if (Build.DEVICE.startsWith("generic")) {
			BASE_URL = "http://api-dev.maishoku.com";
		} else {
			BASE_URL = "https://api.maishoku.com";
		}
	}
	
	protected static final String TAG = API.class.getSimpleName();
	/*
	 * This class was inspired by http://pivotallabs.com/users/tyler/blog/articles/1754-android-image-caching
	 */
	private static class Cache {
		private static String escape(String url) {
			return url.replace("/", "-").replace(".", "-");
		}
		private static File getCacheFile(Context ctx, URI uri) {
			File cacheDir = ctx.getCacheDir();
			return new File(cacheDir, escape(uri.getPath()));
		}
		public static File get(Context ctx, URI uri) throws IOException {
			File file = getCacheFile(ctx, uri);
			if (file.exists()) {
				Log.i(TAG, "file exists: " + file.getAbsolutePath());
				return file;
			} else {
				Log.i(TAG, "file doesn't exist: " + file.getAbsolutePath());
				return null;
			}
		}
		public static void put(Context ctx, URI uri, InputStream inputStream) throws IOException {
			File file = getCacheFile(ctx, uri);
			Log.i(TAG, "putting file: " + file.getAbsolutePath());
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			int i;
			while ((i = inputStream.read()) != -1) {
				fileOutputStream.write(i);
			}
			fileOutputStream.close();
		};
	}
	
	public static InputStream getImage(Context ctx, String path) throws IOException {
		URI uri = URI.create(path);
		URL url = uri.toURL();
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		// First see whether the image is cached
		File file = Cache.get(ctx, uri);
		// If the image has been requested in this activity lifecycle, return the cached version
		if (urls.containsKey(path)) {
			return file == null ? null : new FileInputStream(file);
		} else {
			urls.put(path, OBJECT);
		}
		// If the image is cached but hasn't yet been requested in this activity lifecycle, set the If-Modified-Since header appropriately
		if (file != null) {
			long lastModified = file.lastModified();
			Log.i(TAG, "last modified: " + new Date(lastModified));
			connection.setIfModifiedSince(lastModified);
		}
		// Send an HTTP GET request to the server
		int responseCode = connection.getResponseCode();
		switch (responseCode) {
		case HttpURLConnection.HTTP_NOT_MODIFIED:
			Log.i(TAG, "Not modified - returning cached image");
			// Not modified - return the cached image
			return new FileInputStream(file);
		case HttpURLConnection.HTTP_OK:
			Log.i(TAG, "OK, returning response from the server");
			// Write the contents of the new image to the cache and return the newly cached image
			Cache.put(ctx, uri, connection.getInputStream());
			return new FileInputStream(Cache.get(ctx, uri));
		default:
			Log.i(TAG, "response code == " + responseCode + ", returning null");
			// Unexpected response code - return null
			return null;
		}
	}
	
	public static String todaysDateEEE() {
		return DAY_FORMAT.format(new Date()).toUpperCase();
	}
	
	public static Language getLanguage() {
		String language = Locale.getDefault().getLanguage();
		if (language.startsWith("ja")) {
			return Language.ja;
		} else {
			return Language.en;
		}
	}
	
	public static void addCartButton(final Activity activity) {
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		layoutParams.rightMargin = 5;
		ImageButton imageButton = new ImageButton(activity);
		imageButton.setBackgroundDrawable(activity.getResources().getDrawable(android.R.drawable.btn_default_small));
		imageButton.setImageResource(R.drawable.cart);
		imageButton.setLayoutParams(layoutParams);
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.startActivity(new Intent(activity, CartActivity.class));
			}
		});
		RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.titleBarRelativeLayout);
		relativeLayout.addView(imageButton);
	}
	
	public static URI getURL(String resource) throws URISyntaxException {
		return new URI(String.format("%s/%s%s", BASE_URL, API_VERSION, resource));
	}
	
	public static <T> HttpEntity<T> getHttpEntity(Context ctx) {
		return getHttpEntity(ctx, null, new HttpHeaders());
	}
	
	public static <T> HttpEntity<T> getHttpEntity(Context ctx, T body) {
		return getHttpEntity(ctx, body, new HttpHeaders());
	}
	
	public static <T> HttpEntity<T> getHttpEntity(Context ctx, HttpHeaders httpHeaders) {
		return getHttpEntity(ctx, null, httpHeaders);
	}
	
	public static <T> HttpEntity<T> getHttpEntity(Context ctx, T body, HttpHeaders httpHeaders) {
		return getHttpEntity(ctx, body, httpHeaders, true);
	}
	
	public static <T> HttpEntity<T> getHttpEntity(Context ctx, T body, HttpHeaders httpHeaders, boolean useBasicAuthentication) {
		String version;
		try {
			version = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
		} catch (Exception e) {
			version = "unknown";
		}
		httpHeaders.setUserAgent(String.format("Maishoku/%s Android/%d", version, Build.VERSION.SDK_INT));
		httpHeaders.setAcceptLanguage(Locale.getDefault().getLanguage());
		if (useBasicAuthentication) {
			SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
			String username = prefs.getString("username", null);
			String password = prefs.getString("password", null);
			httpHeaders.setAuthorization(new HttpBasicAuthentication(username, password));
		}
		return new HttpEntity<T>(body, httpHeaders);
	}

}
