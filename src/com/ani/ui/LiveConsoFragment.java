package com.ani.ui;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.ani.R;
import com.ani.task.LiveConsoTask;
import com.ani.ui.view.ConsumptionItem;

/**
 * Fragment that appears in the "content_frame", shows a planet
 */
public class LiveConsoFragment extends Fragment {

	private final int LIST_REFRESH_TIMER = 1000;
	private ListView listConso;
	private MainActivity activity;

	public LiveConsoFragment() {
		// Empty constructor required for fragment subclasses
		activity = (MainActivity) this.getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_liveconso,
				container, false);

		// Retrieving views
		listConso = (ListView) rootView.findViewById(R.id.conso_listview);

		listConso.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapt, View v,
					int position, long id) {
				String appName = ((ConsumptionItem) adapt
						.getItemAtPosition(position)).getName();
				// activity.openBlacklist(new
				// BlackListItem(NetworkType.APP_NAME, appName, true));
				// TODO
				return true;
			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		startAutoRefresh();
		super.onResume();
	}

	@Override
	public void onStop() {
		stopAutoRefresh();
		super.onStop();
	}

	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			refreshList();
			timerHandler.postDelayed(this, LIST_REFRESH_TIMER);
		}
	};

	private void startAutoRefresh() {
		timerHandler.postDelayed(timerRunnable, 0);
	}

	private void stopAutoRefresh() {
		timerHandler.removeCallbacks(timerRunnable);
	}

	/**
	 * Reload datas for the list and update it
	 */
	private void refreshList() {
		// Initialize variables to instantiate our own Executor for the thread
		// (The default executor seemed to block)
		int corePoolSize = 60;
		int maximumPoolSize = 80;
		int keepAliveTime = 10;
		// Instantiate queue
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(
				maximumPoolSize);
		// Creating executor
		Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
				maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
		// Execute task
		new LiveConsoTask(this).executeOnExecutor(threadPoolExecutor);
	}

	public ListView getListConso() {
		return listConso;
	}
}