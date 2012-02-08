package com.maishoku.android.activities;

import com.maishoku.android.API;
import com.maishoku.android.AuthenticateTask;
import com.maishoku.android.R;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.Result;
import com.maishoku.android.models.User;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends RedTitleBarActivity implements AuthenticateTask.Context {

	protected static final String TAG = LoginActivity.class.getSimpleName();
	private ProgressDialog progressDialog;
	
	@Override
	public void onPostExecute(Result<User> result) {
		progressDialog.dismiss();
		if (result.success) {
			Log.i(TAG, "Successfully authenticated");
			Editor editor = getSharedPreferences(API.PREFS, Context.MODE_PRIVATE).edit();
			editor.putString("username", result.resource.getUsername());
			editor.putString("password", result.resource.getPassword());
			editor.commit();
			startActivity(new Intent(this, LocationActivity.class));
			finish();
		} else {
			Log.i(TAG, "Failed to authenticate");
			new AlertDialog.Builder(this)
				.setTitle(R.string.login_failed)
				.setMessage(R.string.please_try_again)
				.setPositiveButton(R.string.ok, null)
				.show();
		}
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.login);
		setCustomTitle(R.string.login);
		final Button submitButton = (Button) findViewById(R.id.loginSubmitButton);
		final EditText usernameEditText = (EditText) findViewById(R.id.loginUsernameEditText);
		final EditText passwordEditText = (EditText) findViewById(R.id.loginPasswordEditText);
		TextWatcher tw = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				submitButton.setEnabled(usernameEditText.getText().length() > 0 && passwordEditText.getText().length() > 0);
			}
		};
		usernameEditText.addTextChangedListener(tw);
		passwordEditText.addTextChangedListener(tw);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = new ProgressDialog(LoginActivity.this);
				progressDialog.setTitle(R.string.loading);
				progressDialog.show();
				String username = usernameEditText.getText().toString();
				String password = passwordEditText.getText().toString();
				new AuthenticateTask(LoginActivity.this, username, password).execute();
			}
		});
		final Button signupButton = (Button) findViewById(R.id.loginSignUpButton);
		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
			}
		});
	}
	
	@Override
	public Context getAndroidContext() {
		return this;
	}

}
