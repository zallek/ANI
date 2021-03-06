package com.ani.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ani.R;

public class ConsumptionMItemView extends LinearLayout {

	private TextView name;
	private TextView dest;
	private TextView up;
	private TextView down;
	private ImageView icon;

	public ConsumptionMItemView(Context context) {
		this(context, null);

	}

	public ConsumptionMItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate();
		initWidgets();
	}

	protected void inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_multiple_consumption, this);
	}

	protected void initWidgets() {
		name = (TextView) findViewById(R.id.view_m_name);
		dest = (TextView) findViewById(R.id.view_m_dest);
		up = (TextView) findViewById(R.id.view_m_up);
		down = (TextView) findViewById(R.id.view_m_down);
		icon = (ImageView) findViewById(R.id.view_m_icon);
	}

	public TextView getNameTextView() {
		return name;
	}

	public void setName(String name) {
		if (this.name != null)
			this.name.setText(name);
	}

	public TextView getDestTextView() {
		return dest;
	}

	public void setDest(String dest) {
		if (this.dest != null)
			this.dest.setText(dest);
	}

	public TextView getUpTextView() {
		return up;
	}

	public void setUp(String up) {
		if (this.up != null)
			this.up.setText(up);
	}

	public TextView getDownTextView() {
		return down;
	}

	public void setDown(String down) {
		if (this.down != null)
			this.down.setText(down);
	}

	public ImageView getIconImageView() {
		return icon;
	}

	public void setIcon(Drawable resid) {
		if (icon != null) {
			icon.setImageDrawable(resid);
			icon.setVisibility(VISIBLE);
		}
	}

	public void setConsumptionItem(ConsumptionItem ci) {
		setName(ci.getName());
		setDest("Destination : " + ci.getDest());
		setUp("" + ci.getUp());
		setDown("" + ci.getDown());
		setIcon(ci.getIcon());
	}
}
