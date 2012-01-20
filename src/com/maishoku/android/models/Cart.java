package com.maishoku.android.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Cart {

	private static final Map<Integer, Position> cart = new HashMap<Integer, Position>(); // {item.id: position}
	
	public static void addToCart(Item item, int quantity) {
		if (quantity < 1) {
			throw new IllegalArgumentException("quantity must be greater than zero");
		}
		int id = item.getId();
		Position position = cart.get(id);
		if (position == null) {
			position = new Position(item, quantity);
			cart.put(id, position);
		} else {
			position.setQuantity(position.getQuantity() + quantity);
		}
	}
	
	public static void removeFromCart(Position position) {
		cart.remove(position.getItem().getId());
	}
	
	public static void removeFromCart(Item item) {
		cart.remove(item.getId());
	}
	
	public static void updateQuantity(Item item, int quantity) {
		if (quantity < 1) {
			throw new IllegalArgumentException("quantity must be greater than zero");
		}
		int id = item.getId();
		Position position = cart.get(id);
		if (position == null) {
			position = new Position(item, quantity);
			cart.put(id, position);
		} else {
			position.setQuantity(quantity);
		}
	}
	
	public static void clear() {
		cart.clear();
	}
	
	public static int quantityForItem(Item item) {
		Position position = cart.get(item.getId());
		if (position == null) {
			return 0;
		} else {
			return position.getQuantity();
		}
	}
	
	public static int totalPrice() {
		int totalPrice = 0;
		for (Position position: cart.values()) {
			totalPrice += position.getItem().getPrice() * position.getQuantity();
		}
		return totalPrice;
	}
	
	public static Collection<Position> allPositions() {
		return cart.values();
	}
	
	public static List<Item> allItems() {
		List<Item> items = new LinkedList<Item>();
		for (Position position: cart.values()) {
			items.add(position.getItem());
		}
		return items;
	}
	
	public static JSONArray toJSONArray() {
		JSONArray jsonArray = new JSONArray();
		for (Position position: cart.values()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item_id", position.getItem().getId());
			map.put("quantity", position.getQuantity());
			JSONObject jsonObject = new JSONObject(map);
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}

}
