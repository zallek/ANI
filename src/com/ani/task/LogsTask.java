package com.ani.task;

import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.ani.ANIApplication;
import com.ani.R;
import com.ani.db.entity.NetLog;
import com.ani.ui.LogsFragment;
import com.ani.ui.view.ConsumptionItem;
import com.ani.ui.view.adapter.ConsumptionMItemAdapter;
import com.ani.ui.view.adapter.ConsumptionUItemAdapter;
import com.ani.ui.view.adapter.GenericItemAdapter;
import com.ani.utils.StaticANI;

public class LogsTask extends
		AsyncTask<LogsFragment, Void, List<ConsumptionItem>> {

	private SoftReference<LogsFragment> fragment;
	private List<ConsumptionItem> items = new ArrayList<ConsumptionItem>();
	private boolean isFullLogOneApp;
	private String search;

	public LogsTask(LogsFragment fragment) {
		this.fragment = new SoftReference<LogsFragment>(fragment);
		isFullLogOneApp = LogsFragment.isFullLogOneApp;
	}

	@Override
	protected void onPreExecute() {
		if (fragment.get() != null) {
			// Make the wheel appear
			fragment.get().enable(false);
		}
	}

	@Override
	protected List<ConsumptionItem> doInBackground(LogsFragment... params) {
		if (fragment.get() != null) {// Trying if we are in the fragment
			search = fragment.get().getSearch().getText().toString();
			Calendar calendar = Calendar.getInstance();
			long start = 0, end = 0;
			// Getting time in millisecond from a string in the EditText
			try {
				calendar.setTime(StaticANI.COMPLETE_TIMEFORMAT.parse(fragment
						.get().getTimeS().getText().toString()));
				start = calendar.getTimeInMillis();
			} catch (ParseException e) {
				start = 0;
			}
			// Getting time in millisecond from a string in the EditText
			try {
				calendar.setTime(StaticANI.COMPLETE_TIMEFORMAT.parse(fragment
						.get().getTimeE().getText().toString()));
				end = calendar.getTimeInMillis();
			} catch (ParseException e) {
				end = System.currentTimeMillis();
			}
			// Launch database query to retrieve the datas
			List<NetLog> list = ANIApplication.getANIDataBaseInterface()
					.getLogs(
							start,
							end,
							isFullLogOneApp ? fragment.get().getCurrentApp()
									: null,
							isFullLogOneApp ? fragment.get().getCurrentDest()
									: null);
			// After query reorganization of datas
			items = getItemsFromLogs(list);
		}
		return items;
	}

	/**
	 * This method search the title of apps, merge logs in order to get donwload
	 * AND upload and filter the result for the search filter (Cannot be done in
	 * the database because we need the final name of apps).
	 * 
	 * @param logs
	 *            : list from the database
	 * @return Final list that can be displayed
	 */
	private List<ConsumptionItem> getItemsFromLogs(List<NetLog> logs) {
		List<ConsumptionItem> items = new ArrayList<ConsumptionItem>();
		/***
		 * TEST !//This is just testing values, don't worry about it
		 */
		NetLog lig = new NetLog();
		lig.setAppName("com.adobe.air");
		lig.setDestination("blop");
		lig.setMessageSize(15);
		lig.setTimeStamp(64500000);
		logs.add(lig);
		NetLog lug = new NetLog();
		lug.setAppName("blip");
		lug.setDestination("com.adobe.air");
		lug.setMessageSize(15);
		lug.setTimeStamp(64500000);
		logs.add(lug);
		if (logs != null && logs.size() > 0) {
			for (NetLog log : logs) {
				// Creating the items with the logs (0 -> Download, null ->
				// Icon)
				ConsumptionItem item = new ConsumptionItem(
						log.getMessageSize(), 0, log.getTimeStamp(), null,
						log.getDestination(), log.getAppName());
				// If in the second screen, we don't have to merge logs
				if (isFullLogOneApp) {
					// Retrieve info like labeled name and icon when possible
					item = retrieveInfo(item);
					// Search filter on destination or name
					if (search == null
							|| search == ""
							|| item.getName().toLowerCase()
									.contains(search.toLowerCase())
							|| item.getDest().toLowerCase()
									.contains(search.toLowerCase())) {
						items.add(item);
					}
				} else { // First screen case
					// Initialize boolean to see if we have to add it or merge
					// with
					// another
					boolean b = false;
					// Loop over previous items in the lsit to see if there
					// isn't any double or mergeable items
					for (ConsumptionItem consumptionItem : items) {
						if (log.getAppName() == null
								|| consumptionItem.getName() == null)
							continue;

						Log.e("Tsotron",
								"" + log + " " + log.getAppName() + " "
										+ consumptionItem + " "
										+ consumptionItem.getDest());
						if ((log.getAppName().equals(consumptionItem.getDest()) && log
								.getDestination().equals(
										consumptionItem.getName()))) {
							// Can be merge (Reverse column case)
							consumptionItem.setDown(consumptionItem.getDown()
									+ item.getUp());
							// Set as modified
							b = true;
							break;
						}
						if ((log.getDestination().equals(
								consumptionItem.getDest()) && log.getAppName()
								.equals(consumptionItem.getName()))) {
							// Double entry (Same column case)
							consumptionItem.setDown(consumptionItem.getDown()
									+ item.getDown());
							consumptionItem.setUp(consumptionItem.getUp()
									+ item.getUp());
							// Set as modified
							b = true;
							break;
						}
					}
					if (!b) {// If there isn't mergeable other items, add it
						// Retrieve info like labeled name and icon when
						// possible
						item = retrieveInfo(item);

						// Search filter on destination or name
						if (item.getName() != null
								&& (search == null
										|| search == ""
										|| item.getName().toLowerCase()
												.contains(search.toLowerCase()) || item
										.getDest().toLowerCase()
										.contains(search.toLowerCase()))) {
							items.add(item);
						}
					}
				}
			}
		}
		// Finally return corrected list !
		return items;
	}

	/**
	 * Search for icon and labeled name for an item when possible (Compare with
	 * the package name and the UID)
	 * 
	 * @param item
	 *            : The item to modify
	 * @return Item with good icon and label when possible
	 */
	private ConsumptionItem retrieveInfo(ConsumptionItem item) {
		for (int i = 0; i < ANIApplication.packages.size(); i++) {
			// Compare name with UID and package name
			if (item.getName() == null)
				continue;
			if ((item.getName().equals(ANIApplication.packages.get(i).uid) || item
					.getName().equals(
							ANIApplication.packages.get(i).packageName))
					&& fragment.get() != null) {
				// If match, change the name and the icon
				item.setName((String) ((ANIApplication.packages.get(i) != null) ? fragment
						.get().getActivity().getPackageManager()
						.getApplicationLabel(ANIApplication.packages.get(i))
						: "Unknown App"));
				item.setIcon(fragment.get().getActivity().getPackageManager()
						.getApplicationIcon(ANIApplication.packages.get(i)));
				// We don't need to continue the loop
				break;
			}
			// Compare destination with UID and package name
			if ((item.getDest().equals(ANIApplication.packages.get(i).uid) || item
					.getDest().equals(
							ANIApplication.packages.get(i).packageName))
					&& fragment.get() != null) {
				// If match, change the name and the icon and reverse the name
				// and destination (And the upload/download) in first list case

				// In second list case, we just get the icon
				if (!isFullLogOneApp && fragment.get() != null) {
					String tmp = item.getName();
					item.setName((String) ((ANIApplication.packages.get(i) != null) ? fragment
							.get()
							.getActivity()
							.getPackageManager()
							.getApplicationLabel(ANIApplication.packages.get(i))
							: "Unknown App"));
					item.setDest(tmp);
					float tmpsize = item.getDown();
					item.setDown(item.getUp());
					item.setUp(tmpsize);
				}
				item.setIcon(fragment.get().getActivity().getPackageManager()
						.getApplicationIcon(ANIApplication.packages.get(i)));
				// We don't need to continue
				break;
			}
		}
		return item;
	}

	@Override
	protected void onPostExecute(List<ConsumptionItem> result) {
		// If the fragment is alive
		GenericItemAdapter<ConsumptionItem> adapter;
		if (fragment.get() != null) {
			// Choose the right adapter for the right view
			if (!isFullLogOneApp) {
				adapter = new ConsumptionMItemAdapter(fragment.get()
						.getActivity(), new ArrayList<ConsumptionItem>());
				fragment.get().getActivity().setTitle(R.string.logs_title);
			} else {
				adapter = new ConsumptionUItemAdapter(fragment.get()
						.getActivity(), new ArrayList<ConsumptionItem>());
				fragment.get()
						.getActivity()
						.setTitle(
								fragment.get().getResources()
										.getString(R.string.logs_title)
										+ " : "
										+ fragment.get().getCurrentApp());
			}
			// Push the new list to the adapter
			adapter.clear();
			adapter.addAll(items);
			fragment.get().getListlog().setAdapter(adapter);
			// Make the wheel disappear
			fragment.get().enable(true);
		}
	}

}
