package com.ani.ui.view.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.ani.ui.view.ConsumptionItem;
import com.ani.ui.view.ConsumptionMItemView;

public class ConsumptionMItemAdapter extends
		GenericItemAdapter<ConsumptionItem> {

	public ConsumptionMItemAdapter(Context context,
			List<ConsumptionItem> objects) {
		super(context, objects);
	}

	@Override
	protected View createItemview(Context context, ConsumptionItem item) {

		ConsumptionMItemView itemvw = new ConsumptionMItemView(getContext());
		itemvw.setConsumptionItem(item);
		return itemvw;
	}

}
