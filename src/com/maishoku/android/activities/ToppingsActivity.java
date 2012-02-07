package com.maishoku.android.activities;

import java.util.List;

import com.maishoku.android.API;
import com.maishoku.android.RedTitleBarListActivity;
import com.maishoku.android.models.Item;
import com.maishoku.android.models.Position;
import com.maishoku.android.models.Topping;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ToppingsActivity extends RedTitleBarListActivity {

	protected static final String TAG = ToppingsActivity.class.getSimpleName();
	
	private ArrayAdapter<Topping> toppingsAdapter;
	private Item item;
	private Position position;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		item = API.toppingsItem;
		position = API.toppingsPosition;
		setCustomTitle(item.getName());
		toppingsAdapter = new ArrayAdapter<Topping>(this, android.R.layout.simple_list_item_multiple_choice, item.getToppings());
		final ListView toppingsListView = getListView();
		toppingsListView.setAdapter(toppingsAdapter);
		toppingsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		toppingsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Topping topping = item.getToppings()[position];
				List<Topping> positionToppings = ToppingsActivity.this.position.getToppings();
				if (toppingsListView.getCheckedItemPositions().get(position)) {
					if (!positionToppings.contains(topping)) {
						positionToppings.add(topping);
					}
				} else {
					if (positionToppings.contains(topping)) {
						positionToppings.remove(topping);
					}
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		ListView toppingsListView = getListView();
		List<Topping> positionToppings = position.getToppings();
		Topping[] itemToppings = item.getToppings();
		for (int i = 0, n = itemToppings.length; i < n; i++) {
			toppingsListView.setItemChecked(i, positionToppings.contains(itemToppings[i]));
		}
	}

}
