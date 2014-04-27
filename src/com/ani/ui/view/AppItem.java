package com.ani.ui.view;

import android.graphics.drawable.Drawable;

public class AppItem {

	Drawable icon;
	String name;

	public AppItem() {
	}

	public AppItem(Drawable icon, String name) {
		this.icon = icon;
		this.name = name;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
