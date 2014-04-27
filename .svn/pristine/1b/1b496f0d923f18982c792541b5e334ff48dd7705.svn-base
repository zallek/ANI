package com.ani.ui.view.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.ani.ui.view.AppItem;
import com.ani.ui.view.AppItemView;

public class AppItemAdapter extends GenericItemAdapter<AppItem> {

	public AppItemAdapter(Context context, List<AppItem> objects) {
		super(context, objects);
	}

	@Override
	protected View createItemview(Context context, AppItem item) {
		AppItemView itemvw = new AppItemView(getContext());
		itemvw.setIcon(item.getIcon());
		itemvw.setName(item.getName());
		return itemvw;
	}

}
