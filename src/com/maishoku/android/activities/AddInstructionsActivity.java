package com.maishoku.android.activities;

import com.maishoku.android.R;
import com.maishoku.android.RedTitleBarActivity;
import com.maishoku.android.models.Cart;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddInstructionsActivity extends RedTitleBarActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.add_instructions);
		setCustomTitle(R.string.add_instructions);
		final EditText editText = (EditText) findViewById(R.id.addInstructionsEditText);
		Button button = (Button) findViewById(R.id.addInstructionsCancelButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddInstructionsActivity.this.finish();
			}
		});
		button = (Button) findViewById(R.id.addInstructionsDoneButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Cart.setInstructions(editText.getText().toString());
				AddInstructionsActivity.this.finish();
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		EditText editText = (EditText) findViewById(R.id.addInstructionsEditText);
		editText.setText(Cart.getInstructions());
	}

}
