package com.ani.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ani.R;

public class AppItemView extends LinearLayout {

	private TextView name;
	private ImageView icon;

	public AppItemView(Context context) {
		this(context, null);

	}

	public AppItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate();
		initWidgets();
	}

	protected void inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_application, this);
	}

	protected void initWidgets() {
		name = (TextView) findViewById(R.id.view_app_name);
		icon = (ImageView) findViewById(R.id.view_app_image);
	}

	public String getName() {
		return name.getText().toString();
	}

	public void setName(String name) {
		this.name.setText(name);
	}

	public void setIcon(Drawable icon) {
		this.icon.setImageDrawable(icon);
	}

}
