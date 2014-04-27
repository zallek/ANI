package com.ani.db.entity;

import com.ani.db.object.NetworkType;

/**
 * BlackListItem are defined by a type and a string value "192.1.23.56" OR
 * "Facebook" They can be disabled
 * 
 * @author Nicolas
 * 
 */
public class BlackListItem {
	public final String URI = "BackListItem"; // TODO

	private NetworkType type;
	private String filter; // "192.1.23.56" OR "Facebook"
	private boolean active;

	public BlackListItem(NetworkType type, String filter, boolean active) {
		super();
		this.type = type;
		this.filter = filter;
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public NetworkType getType() {
		return type;
	}

	public String getFilter() {
		return filter;
	}
}
