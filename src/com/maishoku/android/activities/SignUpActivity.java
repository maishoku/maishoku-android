package com.maishoku.android.activities;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

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
import com.maishoku.android.IO;
import com.maishoku.android.R;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.Result;
import com.maishoku.android.models.User;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends RedTitleBarActivity {

	protected static final String TAG = SignUpActivity.class.getSimpleName();
	private ProgressDialog progressDialog;
	
	private static final Pattern emailPattern = Pattern.compile(
		"(?:[a-z0-9!#$%\\&'*+/=?\\^_`{|}~-]+(?:\\.[a-z0-9!#$%\\&'*+/=?\\^_`{|}" +
		"~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\" +
		"x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-" +
		"z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5" +
		"]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-" +
		"9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21" +
		"-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.signup);
		setCustomTitle(R.string.sign_up);
		final EditText usernameEditText = (EditText) findViewById(R.id.signupUsernameEditText);
		final EditText passwordEditText1 = (EditText) findViewById(R.id.signupPasswordEditText1);
		final EditText passwordEditText2 = (EditText) findViewById(R.id.signupPasswordEditText2);
		final EditText emailEditText = (EditText) findViewById(R.id.signupEmailEditText);
		final EditText phoneNumberEditText = (EditText) findViewById(R.id.signupPhoneNumberEditText);
		Button button = (Button) findViewById(R.id.signupSubmitButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = usernameEditText.getText().toString();
				String password1 = passwordEditText1.getText().toString();
				String password2 = passwordEditText2.getText().toString();
				String email = emailEditText.getText().toString();
				String phoneNumber = phoneNumberEditText.getText().toString();
				if (!password1.equals(password2)) {
					new AlertDialog.Builder(SignUpActivity.this)
						.setTitle(R.string.passwords_are_not_same)
						.setMessage(R.string.please_try_again)
						.setPositiveButton(R.string.ok, null)
						.show();
					return;
				}
				if (!phoneNumber.startsWith("0") || phoneNumber.length() < 10) {
					new AlertDialog.Builder(SignUpActivity.this)
						.setTitle(R.string.phone_number_invalid)
						.setMessage(R.string.phone_number_format)
						.setPositiveButton(R.string.ok, null)
						.show();
					return;
				}
				if (!emailPattern.matcher(email).matches()) {
					new AlertDialog.Builder(SignUpActivity.this)
						.setTitle(R.string.email_invalid)
						.setMessage(R.string.please_enter_valid_email)
						.setPositiveButton(R.string.ok, null)
						.show();
					return;
				}
				progressDialog = new ProgressDialog(SignUpActivity.this);
				progressDialog.setTitle(R.string.loading);
				progressDialog.show();
				new SignUpTask(username, password1, email, phoneNumber).execute();
			}
		});
	}
	
	private class SignUpTask extends AsyncTask<Void, Void, Result<User>> {
	
		protected final String TAG = SignUpTask.class.getSimpleName();
		
		private final String username;
		private final String password;
		private final String email;
		private final String phoneNumber;
		private final AtomicReference<String> message;
		
		public SignUpTask(String username, String password, String email, String phoneNumber) {
			this.username = username;
			this.password = password;
			this.email = email;
			this.phoneNumber = phoneNumber;
			this.message = new AtomicReference<String>();
		}
		
		@Override
		protected Result<User> doInBackground(Void... params) {
			try {
				final URI url = API.getURL("/user");
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				Map<String, String> map = new HashMap<String, String>();
				map.put("username", username);
				map.put("password", password);
				map.put("email", email);
				map.put("phone_number", phoneNumber);
				JSONObject json = new JSONObject(map);
				String body = json.toString();
				HttpEntity<?> httpEntity = API.getHttpEntity(SignUpActivity.this, body, httpHeaders, false);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.setErrorHandler(new ResponseErrorHandler() {
					@Override
					public boolean hasError(ClientHttpResponse response) throws IOException {
						return response.getStatusCode() != HttpStatus.CREATED;
					}
					@Override
					public void handleError(ClientHttpResponse response) throws IOException {
						message.set(IO.readString(response.getBody()));
					}
				});
				ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
				return new Result<User>(responseEntity.getStatusCode() == HttpStatus.CREATED, null, new User(username, password));
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				return new Result<User>(false, message.get(), new User(username, password));
			}
		}
		
		@Override
		protected void onPostExecute(Result<User> result) {
			progressDialog.dismiss();
			if (result.success) {
				Log.i(TAG, "Successfully created new user");
				Editor editor = getSharedPreferences(API.PREFS, Context.MODE_PRIVATE).edit();
				editor.putString("username", result.resource.getUsername());
				editor.putString("password", result.resource.getPassword());
				editor.commit();
				new AlertDialog.Builder(SignUpActivity.this)
					.setTitle(R.string.sign_up_complete)
					.setMessage(R.string.check_your_email)
					.setPositiveButton(R.string.ok, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SignUpActivity.this.finish();
						}
					}).show();
			} else {
				Log.i(TAG, "Failed to create new user");
				new AlertDialog.Builder(SignUpActivity.this)
					.setTitle(R.string.please_try_again)
					.setMessage(result.message)
					.setPositiveButton(R.string.ok, null)
					.show();
			}
		}
	
	}

}
