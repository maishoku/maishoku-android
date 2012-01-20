package com.maishoku.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;

public class RedTitleBarActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = getWindow().findViewById(android.R.id.title);
		if (view != null) {
			ViewParent viewParent = view.getParent();
			if (viewParent != null && (viewParent instanceof View)) {
				((View)viewParent).setBackgroundColor(API.MAISHOKU_RED);
				setTitleColor(Color.WHITE);
			}
		}
	}

}
