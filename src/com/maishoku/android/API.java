package com.maishoku.android;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.maishoku.android.activities.CartActivity;
import com.maishoku.android.models.Address;
import com.maishoku.android.models.Item;
import com.maishoku.android.models.Restaurant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class API {

	public static final int MAISHOKU_RED = Color.rgb(180, 0, 0); // halfway between the 200-160 gradient used in the logo
	public static final Date NULL_DATE = new Date(0, 0, 0);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String BASE_URL;
	public static final String API_VERSION = "1.0";
	public static final String PREFS = "PREFS";
	public static Address address;
	public static Item item;
	public static OrderMethod orderMethod = OrderMethod.delivery;
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
	
	static {
		if (Build.DEVICE.startsWith("generic")) {
			BASE_URL = "http://api-dev.maishoku.com";
		} else {
			BASE_URL = "https://api.maishoku.com";
		}
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
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams.rightMargin = 5;
		ImageButton imageButton = new ImageButton(activity);
		imageButton.setBackgroundColor(Color.TRANSPARENT);
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
