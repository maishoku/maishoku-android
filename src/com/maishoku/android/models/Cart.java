package com.maishoku.android.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Cart {

	private static final LinkedList<Position> positions = new LinkedList<Position>();
	private static String instructions;
	
	public static void setInstructions(String instructions) {
		Cart.instructions = instructions;
	}
	
	public static String getInstructions() {
		return instructions;
	}
	
	public static void addPosition(Position position) {
		positions.add(position);
	}
	
	public static void removePosition(Position position) {
		positions.remove(position);
	}
	
	public static void clear() {
		instructions = null;
		positions.clear();
	}
	
	public static List<Position> allPositions() {
		return positions;
	}
	
	public static int size() {
		int size = 0;
		for (Position position: positions) {
			size += position.getQuantity();
		}
		return size;
	}
	
	public static int totalPrice() {
		int totalPrice = 0;
		for (Position position: positions) {
			int quantity = position.getQuantity();
			totalPrice += position.getItem().getPrice() * quantity;
			for (Option option: position.getOptions()) {
				totalPrice += option.getPrice_delta() * quantity;
			}
			for (Topping topping: position.getToppings()) {
				totalPrice += topping.getPrice_fixed() * quantity;
			}
		}
		return totalPrice;
	}
	
	public static JSONArray toJSONArray() {
		JSONArray jsonArray = new JSONArray();
		for (Position position: positions) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item_id", position.getItem().getId());
			map.put("quantity", position.getQuantity());
			if (position.getOptions().size() > 0) {
				JSONArray options = new JSONArray();
				for (Option option: position.getOptions()) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("id", option.getId());
					options.put(new JSONObject(m));
				}
				map.put("options", options);
			}
			if (position.getToppings().size() > 0) {
				JSONArray toppings = new JSONArray();
				for (Topping topping: position.getToppings()) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("id", topping.getId());
					toppings.put(new JSONObject(m));
				}
				map.put("toppings", toppings);
			}
			JSONObject jsonObject = new JSONObject(map);
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}

}
