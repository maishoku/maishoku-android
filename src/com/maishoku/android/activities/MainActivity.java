package com.maishoku.android.activities;

import com.maishoku.android.API;
import com.maishoku.android.AuthenticateTask;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.Result;
import com.maishoku.android.models.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class MainActivity extends RedTitleBarActivity implements AuthenticateTask.Context {

	protected static final String TAG = MainActivity.class.getSimpleName();
	private ProgressDialog progressDialog;
	
	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences prefs = getSharedPreferences(API.PREFS, Context.MODE_PRIVATE);
		String username = prefs.getString("username", null);
		String password = prefs.getString("password", null);
		if (username == null || password == null) {
			Log.i(TAG, "username and password not stored - starting LoginActivity");
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		} else {
			Log.i(TAG, "username and password stored - executing AuthenticateTask");
			progressDialog = new ProgressDialog(this);
			progressDialog.show();
			new AuthenticateTask(this, username, password).execute();
		}
	}
	
	@Override
	public void onPostExecute(Result<User> result) {
		progressDialog.dismiss();
		if (result.success) {
			startActivity(new Intent(this, LocationActivity.class));
		} else {
			startActivity(new Intent(this, LoginActivity.class));
		}
		finish();
	}
	
	@Override
	public Context getAndroidContext() {
		return this;
	}

}