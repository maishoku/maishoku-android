package com.maishoku.android;

import java.net.URI;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.maishoku.android.models.User;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

public class AuthenticateTask extends AsyncTask<Void, Void, Result<User>> {

	protected static final String TAG = AuthenticateTask.class.getSimpleName();
	
	public static interface Context {
		public void onPostExecute(Result<User> result);
		public PackageManager getPackageManager();
		public String getPackageName();
		public android.content.Context getAndroidContext();
	}
	
	private final Context ctx;
	private final String username;
	private final String password;
	
	public AuthenticateTask(Context ctx, String username, String password) {
		this.ctx = ctx;
		this.username = username;
		this.password = password;
	}
	
	@Override
	protected Result<User> doInBackground(Void... params) {
		try {
			final URI url = API.getURL("/authenticate");
			HttpBasicAuthentication hba = new HttpBasicAuthentication(username, password);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setAuthorization(hba);
			HttpEntity<?> httpEntity = API.getHttpEntity(ctx.getAndroidContext(), null, httpHeaders, false);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
			return new Result<User>(responseEntity.getStatusCode() == HttpStatus.OK, null, new User(username, password));
		} catch (Exception e) {
			Log.e(TAG, "Failed to authenticate", e);
		}
		return new Result<User>(false, null, new User(username, password));
	}
	
	@Override
	protected void onPostExecute(Result<User> result) {
		ctx.onPostExecute(result);
	}

}
