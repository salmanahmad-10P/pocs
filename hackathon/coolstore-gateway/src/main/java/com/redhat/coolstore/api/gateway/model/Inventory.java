package com.redhat.coolstore.api.gateway.model;

public class Inventory {

	public String itemId;

	public int quantity;

	public String location;

	public String link;

	public Inventory() {
	}

	public Inventory(String itemId, int quantity, String location, String link) {
		this.itemId = itemId;
		this.quantity = quantity;
		this.location = location;
		this.link = link;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String toString() {
		return ("+{\"id\":\"" + itemId + "\", \"quantity\":\"" + quantity
				+ "\", \"location\":\"" + location + "\", \"link\": \"" + link + "\"");
	}

}
