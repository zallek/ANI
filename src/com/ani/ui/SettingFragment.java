package com.ani.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.ani.ANIApplication;
import com.ani.R;
import com.ani.db.entity.LogFilter;
import com.ani.db.object.NetworkType;
import com.ani.ui.view.AppItem;
import com.ani.ui.view.AppItemView;
import com.ani.ui.view.SettingItemView;
import com.ani.ui.view.adapter.AppItemAdapter;
import com.ani.utils.StaticANI;

public class SettingFragment extends Fragment {

	public static final int PAGE_SETTINGS_HOME = 0;
	public static final int PAGE_SETTINGS_FILTER = 1;
	private int page = PAGE_SETTINGS_HOME;

	private boolean isPoped = false;
	private PopupWindow popupWindow;
	private ListView popUpListview;
	private EditText appliedName;
	boolean isIP = false;

	private ViewGroup item_container, settings_layout;
	private Button addFilter;

	public SettingFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container,
				false);
		item_container = (ViewGroup) rootView
				.findViewById(R.id.settings_container);
		settings_layout = (ViewGroup) rootView
				.findViewById(R.id.settings_layout);
		addFilter = (Button) rootView.findViewById(R.id.settings_add_filter);
		addFilter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LayoutInflater layoutInflater = (LayoutInflater) getActivity()
						.getBaseContext().getSystemService(
								getActivity().LAYOUT_INFLATER_SERVICE);
				final View popupView = layoutInflater.inflate(
						R.layout.logfilter_popup, null);
				popupWindow = new PopupWindow(popupView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				final RadioGroup radio = (RadioGroup) popupView
						.findViewById(R.id.logfilterPopupRadioGroup);
				appliedName = (EditText) popupView
						.findViewById(R.id.logfilterappliedNameEditText);
				appliedName.addTextChangedListener(new SearchTextWatcher());
				Button addBlackList = (Button) popupView
						.findViewById(R.id.logfilterPopupAddButton);
				Button exitPopup = (Button) popupView
						.findViewById(R.id.logfilterPopupCancelButton);
				popUpListview = (ListView) popupView
						.findViewById(R.id.logfilter_pop_listview);
				final ViewGroup search_layout = (ViewGroup) popupView
						.findViewById(R.id.logfilter_pop_search);
				popUpListview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						appliedName.setText(((AppItemView) arg1).getName());
						refreshListview();
					}
				});

				// listener about buttons in the popup
				radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.logfilterselectApplicationName:
							isIP = false;
							search_layout.setVisibility(View.VISIBLE);
							break;
						case R.id.logfilterselectIPAddress:
							isIP = true;
							search_layout.setVisibility(View.GONE);
							break;
						}
					}
				});

				addBlackList.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						NetworkType type = isIP ? NetworkType.DESTINATION_IP
								: NetworkType.APP_NAME;
						String name = appliedName.getText().toString();

						toLogFilter(new LogFilter(type, name, true));
						dismissPopup();
					}
				});

				exitPopup.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dismissPopup();
					}
				});

				isPoped = true;
				settings_layout.setEnabled(false);
				settings_layout.setBackgroundColor(getActivity().getResources()
						.getColor(R.color.transparent));
				// popupWindow.setBackgroundDrawable(new
				// ColorDrawable(Color.WHITE));
				popupWindow.setFocusable(true);
				popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
			}
		});
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	public void refresh() {
		item_container.removeAllViews();
		if (page == PAGE_SETTINGS_HOME)
			refreshMainPage();
		else
			refreshFilterPage();
	}

	public void back() {
		setPage(SettingFragment.PAGE_SETTINGS_HOME);
		addFilter.setVisibility(View.GONE);
		refresh();
	}

	public void refreshMainPage() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		addSettingItemView(SettingItemView.TYPE_CATEG_SWITCH, "LOGS", null,
				pref.getBoolean(StaticANI.SERVICE_LOGS_ACTIVATE, true))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((SettingItemView) v).setSwitch(!((SettingItemView) v)
								.getSwitchi());
						SharedPreferences pref = PreferenceManager
								.getDefaultSharedPreferences(getActivity());
						Editor editor = pref.edit();
						editor.putBoolean(StaticANI.SERVICE_LOGS_ACTIVATE,
								((SettingItemView) v).getSwitchi());
						editor.commit();
						// TODO Activate/Unactivate logger
					}
				});
		addSettingItemView(SettingItemView.TYPE_NORMAL, "Filters")
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						setPage(PAGE_SETTINGS_FILTER);
					}
				});
		addSettingItemView(SettingItemView.TYPE_NORMAL, "Delete logs data")
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						LayoutInflater layoutInflater = (LayoutInflater) getActivity()
								.getBaseContext().getSystemService(
										Context.LAYOUT_INFLATER_SERVICE);
						final View popupView = layoutInflater.inflate(
								R.layout.setting_erase_logs_popup, null);
						popupWindow = new PopupWindow(popupView,
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						Button addApplySetting = (Button) popupView
								.findViewById(R.id.popup_setting_ok_button);
						Button exitPopup = (Button) popupView
								.findViewById(R.id.popup_setting_cancel_button);

						addApplySetting
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// TODO Remove all logs
										dismissPopup();
									}
								});

						exitPopup.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dismissPopup();
							}
						});

						isPoped = true;
						settings_layout.setEnabled(false);
						settings_layout.setBackgroundColor(getActivity()
								.getResources().getColor(R.color.transparent));
						// popupWindow.setBackgroundDrawable(new
						// ColorDrawable(Color.WHITE));
						popupWindow.setFocusable(true);
						popupWindow.showAtLocation(popupView, Gravity.CENTER,
								0, 0);
					}
				});
		;
		addSettingItemView(SettingItemView.TYPE_CATEG, "NOTIFICATIONS");
		addSettingItemView(SettingItemView.TYPE_NORMAL_SWITCH,
				"Data traffic notification", null,
				pref.getBoolean(StaticANI.NOTIFICATION_TRAFFIC_ACTIVATE, true))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((SettingItemView) v).setSwitch(!((SettingItemView) v)
								.getSwitchi());
						// TODO Activate/Unactivate traffic notification
						SharedPreferences pref = PreferenceManager
								.getDefaultSharedPreferences(getActivity());
						Editor editor = pref.edit();
						editor.putBoolean(
								StaticANI.NOTIFICATION_TRAFFIC_ACTIVATE,
								((SettingItemView) v).getSwitchi());
						editor.commit();
					}
				});
		addSettingItemView(
				SettingItemView.TYPE_NORMAL_SWITCH,
				"Blacklist notification",
				null,
				pref.getBoolean(StaticANI.NOTIFICATION_BLACKLIST_ACTIVATE, true))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((SettingItemView) v).setSwitch(!((SettingItemView) v)
								.getSwitchi());
						// TODO Activate/Unactivate blacklist notification
						SharedPreferences pref = PreferenceManager
								.getDefaultSharedPreferences(getActivity());
						Editor editor = pref.edit();
						editor.putBoolean(
								StaticANI.NOTIFICATION_BLACKLIST_ACTIVATE,
								((SettingItemView) v).getSwitchi());
						editor.commit();
					}
				});
	}

	public void refreshFilterPage() {
		// TODO finish this page
		addFilter.setVisibility(View.VISIBLE);
		List<LogFilter> list = ANIApplication.getANIDataBaseInterface()
				.getLogFilters();
		addSettingItemView(SettingItemView.TYPE_CATEG, "APPLICATIONS");
		Drawable icon = null;
		for (LogFilter logFilter : list) {
			icon = null;
			if (logFilter.getType() == NetworkType.APP_NAME) {
				for (int i = 0; i < ANIApplication.packages.size(); i++) {
					// Compare name with UID and package name
					if (((String) ((ANIApplication.packages.get(i) != null) ? getActivity()
							.getPackageManager().getApplicationLabel(
									ANIApplication.packages.get(i))
							: "Unknown App")).toLowerCase().equals(
							logFilter.getFilter().toLowerCase())) {

						// If match, change the name and the icon

						icon = getActivity().getPackageManager()
								.getApplicationIcon(
										ANIApplication.packages.get(i));
						// We don't need to continue the loop
						break;
					}
				}
				SettingItemView view = null;
				view = addSettingItemView(SettingItemView.TYPE_IMAGE,
						logFilter.getFilter(), icon, logFilter.isActive());
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((SettingItemView) v).setSwitch(!((SettingItemView) v)
								.getSwitchi());
						if (((SettingItemView) v).getSwitchi()) {
							ANIApplication.getANIDataBaseInterface()
									.disableLogFilter(
											new LogFilter(NetworkType.APP_NAME,
													((SettingItemView) v)
															.getName(),
													((SettingItemView) v)
															.getSwitchi()));
						} else {
							ANIApplication.getANIDataBaseInterface()
									.enableLogFilter(
											new LogFilter(NetworkType.APP_NAME,
													((SettingItemView) v)
															.getName(),
													((SettingItemView) v)
															.getSwitchi()));
						}
					}
				});
			}
		}
		addSettingItemView(SettingItemView.TYPE_CATEG, "DESTINATION IP");
		for (LogFilter logFilter : list) {
			if (logFilter.getType() == NetworkType.DESTINATION_IP) {
				SettingItemView view = null;
				view = addSettingItemView(SettingItemView.TYPE_IMAGE,
						logFilter.getFilter(), null, logFilter.isActive());
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((SettingItemView) v).setSwitch(!((SettingItemView) v)
								.getSwitchi());
						if (((SettingItemView) v).getSwitchi()) {
							ANIApplication.getANIDataBaseInterface()
									.disableLogFilter(
											new LogFilter(
													NetworkType.DESTINATION_IP,
													((SettingItemView) v)
															.getName(),
													((SettingItemView) v)
															.getSwitchi()));
						} else {
							ANIApplication.getANIDataBaseInterface()
									.enableLogFilter(
											new LogFilter(
													NetworkType.DESTINATION_IP,
													((SettingItemView) v)
															.getName(),
													((SettingItemView) v)
															.getSwitchi()));
						}
					}
				});
			}
		}
	}

	public SettingItemView addSettingItemView(int type, String name,
			Drawable icon, boolean b) {
		SettingItemView v = new SettingItemView(getActivity());
		item_container.addView(v);
		v.setItemView(type, name, icon);
		v.setSwitch(b);
		return v;
	}

	public SettingItemView addSettingItemView(int type, String name,
			Drawable icon) {
		SettingItemView v = new SettingItemView(getActivity());
		item_container.addView(v);
		v.setItemView(type, name, icon);
		return v;
	}

	public SettingItemView addSettingItemView(int type, String name) {
		return addSettingItemView(type, name, null);
	}

	public boolean isPoped() {
		return isPoped;
	}

	public void dismissPopup() {
		popupWindow.dismiss();
		isPoped = false;
		settings_layout.setEnabled(true);
		settings_layout.setBackgroundColor(Color.TRANSPARENT);
		refresh();
	}

	public void toLogFilter(LogFilter item) {
		String name = isConform(item);
		if (!filterAlreadyExist(item.getFilter()) && name != null) {
			ANIApplication.getANIDataBaseInterface().addLogFilter(
					new LogFilter(item.getType(), name, true));
		}
	}

	public String isConform(LogFilter item) {
		if (item.getType() == NetworkType.APP_NAME) {
			for (ApplicationInfo info : ANIApplication.packages) {
				if (((String) ((info != null) ? getActivity()
						.getPackageManager().getApplicationLabel(info)
						: "Unknown App")).toLowerCase().equals(
						item.getFilter().toLowerCase())) {
					return (String) ((info != null) ? getActivity()
							.getPackageManager().getApplicationLabel(info)
							: "Unknown App");
				}
			}
			Toast.makeText(getActivity(), "Incorrect Application name",
					Toast.LENGTH_SHORT).show();
		} else {
			Pattern pattern = Pattern
					.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
			Matcher matcher = pattern.matcher(item.getFilter());
			try {
				Log.d("ANI",
						"" + (matcher.find()) + "/"
								+ (matcher.group().equals(item.getFilter())));
			} catch (Exception e) {
				Toast.makeText(getActivity(), "Incorrect IP",
						Toast.LENGTH_SHORT).show();
				return null;
			}
			return item.getFilter();
		}
		return null;
	}

	public boolean filterAlreadyExist(String name) {
		List<LogFilter> filters = ANIApplication.getANIDataBaseInterface()
				.getLogFilters();
		for (LogFilter logFilter : filters) {
			if (logFilter.getFilter().equals(name)) {
				Toast.makeText(getActivity(), "Filter already exists",
						Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		return false;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
		refresh();
	}

	public void refreshListview() {

		AppItemAdapter adapter = new AppItemAdapter(getActivity(),
				new ArrayList<AppItem>());
		// Push the new list to the adapter
		adapter.clear();
		List<AppItem> items = new ArrayList<AppItem>();
		for (ApplicationInfo info : ANIApplication.packages) {
			if (((String) ((info != null) ? getActivity().getPackageManager()
					.getApplicationLabel(info) : "Unknown App")).toLowerCase()
					.contains(appliedName.getText().toString().toLowerCase())) {
				items.add(new AppItem(getActivity().getPackageManager()
						.getApplicationIcon(info),
						(String) ((info != null) ? getActivity()
								.getPackageManager().getApplicationLabel(info)
								: "Unknown App")));
			}
		}
		adapter.addAll(items);
		popUpListview.setAdapter(adapter);
	}

	/**
	 * For search EditText to retrieve the modification and update the list
	 */
	private class SearchTextWatcher implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!isIP)
				refreshListview();
		}
	}

}