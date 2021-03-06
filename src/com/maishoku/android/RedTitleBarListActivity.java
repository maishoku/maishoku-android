package com.maishoku.android;

import android.app.ListActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public abstract class RedTitleBarListActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(getListView());
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
		super.onCreate(savedInstanceState);
	}
	
	public void setCustomTitle(CharSequence title) {
		TextView textView = (TextView) findViewById(R.id.titleBarTextView);
		textView.setText(title);
	}
	
	public void setCustomTitle(int titleId) {
		Resources resources = getResources();
		if (resources == null) {
			setCustomTitle(null);
		} else {
			setCustomTitle(resources.getString(titleId));
		}
	}

}
