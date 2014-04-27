package com.ani.ui.view;

import java.util.Calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ani.R;
import com.ani.utils.StaticANI;

public class ConsumptionUItemView extends LinearLayout {

	private TextView name;
	private TextView dest;
	private TextView date;
	private TextView weight;
	private TextView time;
	private ImageView icon;

	public ConsumptionUItemView(Context context) {
		this(context, null);

	}

	public ConsumptionUItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate();
		initWidgets();
	}

	protected void inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_unique_consumption, this);
	}

	protected void initWidgets() {
		name = (TextView) findViewById(R.id.view_u_name);
		dest = (TextView) findViewById(R.id.view_u_dest);
		weight = (TextView) findViewById(R.id.view_u_weight);
		date = (TextView) findViewById(R.id.view_u_date);
		time = (TextView) findViewById(R.id.view_u_time);
		icon = (ImageView) findViewById(R.id.view_u_icon);
	}

	public TextView getName() {
		return name;
	}

	public void setName(String name) {
		this.name.setText(name);
	}

	public TextView getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest.setText(dest);
	}

	public TextView getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date.setText(date);
	}

	public TextView getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight.setText(weight);
	}

	public TextView getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time.setText(time);
	}

	public ImageView getIcon() {
		return icon;
	}

	public void setIcon(Drawable resid) {
		if (icon != null) {
			icon.setImageDrawable(resid);
			icon.setVisibility(VISIBLE);
		}
	}

	public void setConsumptionItem(ConsumptionItem ci) {
		Calendar calendar = Calendar.getInstance();
		setName(ci.getName());
		setDest("Destination : " + ci.getDest());
		setWeight("Weight : " + ci.getUp());
		calendar.setTimeInMillis(ci.getTime());
		setDate(StaticANI.FORM_DATEFORMAT.format(calendar.getTime()));
		setTime(StaticANI.FORM_TIMEFORMAT.format(calendar.getTime()));
		setIcon(ci.getIcon());
	}

}
