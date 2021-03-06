package com.ani.ui.view.adapter;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

abstract public class GenericItemAdapter<E> extends ArrayAdapter<E> {

	@SuppressLint("UseSparseArrays")
	protected Map<Integer, WeakReference<View>> itemviews = new HashMap<Integer, WeakReference<View>>();
	protected boolean nocache = false;

	public GenericItemAdapter(Context context, List<E> objects) {
		this(context, 0, objects);
	}

	public GenericItemAdapter(Context context, List<E> objects, boolean nocache) {
		this(context, 0, objects);
		this.nocache = nocache;
	}

	public GenericItemAdapter(Context context, int textViewResourceId,
			List<E> objects) {
		super(context, textViewResourceId, objects);
	}

	public GenericItemAdapter(Context context, int textViewResourceId,
			List<E> objects, boolean nocache) {
		super(context, textViewResourceId, objects);
		this.nocache = nocache;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position + 1 >= getCount()) {
			downloadMoreResults();
		}
		if (itemviews != null && getContext() != null && !isEmpty()
				&& getCount() >= position && getItem(position) != null) {
			if (!nocache && itemviews.get(position) != null
					&& itemviews.get(position).get() != null) {
				return itemviews.get(position).get();
			} else {
				View view = createItemview(getContext(), getItem(position));
				itemviews.put(position, new WeakReference<View>(view));
				return view;
			}
		}
		return convertView;
	}

	abstract protected View createItemview(Context context, E item);

	protected void downloadMoreResults() {
	}

	@Override
	public void addAll(Collection<? extends E> list) {
		if (list != null) {
			for (E e : list) {
				add(e);
			}
		}
	};

	public void clearCache() {
		itemviews.clear();
		notifyDataSetChanged();
	}

	@Override
	public void clear() {
		super.clear();
		itemviews.clear();
	}

	@Override
	public void remove(E object) {
		super.remove(object);
		itemviews.clear();
	};

	@Override
	public void insert(E object, int index) {
		super.insert(object, index);
		itemviews.clear();
	};

}
