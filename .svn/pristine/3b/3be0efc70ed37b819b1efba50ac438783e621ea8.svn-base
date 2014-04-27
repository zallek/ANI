package com.ani.ui;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import com.ani.R;
import com.ani.task.LogsTask;
import com.ani.ui.view.ConsumptionItem;
import com.ani.utils.StaticANI;

/**
 * Fragment that appears in the "content_frame", shows a planet
 */
public class LogsFragment extends Fragment implements OnDateSetListener,
		OnTimeSetListener {

	private ListView listlog;
	private EditText timeS, timeE, search, currentEditText;
	private String tmp_Time, currentApp, currentDest;
	private View waiting;
	public static boolean isFullLogOneApp = false;

	public LogsFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_logs, container,
				false);
		// Retrieving views
		timeS = (EditText) rootView.findViewById(R.id.logs_timeStart);
		timeE = (EditText) rootView.findViewById(R.id.logs_timeEnd);
		search = (EditText) rootView.findViewById(R.id.logs_searchfilter);
		listlog = (ListView) rootView.findViewById(R.id.logs_listview);
		waiting = rootView.findViewById(R.id.logs_waiting);
		search.addTextChangedListener(new SearchTextWatcher());

		timeE.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dateOnClick(v);
				}
			}
		});

		timeS.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					dateOnClick(v);
			}
		});

		listlog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapt, View v, int position,
					long id) {
				if (!isFullLogOneApp) {
					currentApp = ((ConsumptionItem) adapt
							.getItemAtPosition(position)).getName();
					currentDest = ((ConsumptionItem) adapt
							.getItemAtPosition(position)).getDest();
					isFullLogOneApp = true;
					refresh();
				}
			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Returning to first list
		isFullLogOneApp = false;
		refresh();
	}

	/**
	 * Reload datas for the list and update it
	 */
	private void refresh() {
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
		new LogsTask(this).executeOnExecutor(threadPoolExecutor);
	}

	/**
	 * Method to execute when hitting return button.
	 */
	public void back() {
		refresh();
	}

	/**
	 * <b>Method from <i>OnDateSetListener</i></b></br> What to do when click on
	 * the EditText with correspond to a date
	 * 
	 * @param v
	 */
	public void dateOnClick(View v) {
		currentEditText = (EditText) v;
		Date date = new Date();
		try {
			// Try to get the date in the EditText to initialize date dialog box
			if (currentEditText.getText().toString() != null
					&& currentEditText.getText().toString().length() > 0)
				date = StaticANI.FORM_DATEFORMAT.parse(currentEditText
						.getText().toString());
		} catch (ParseException e) {
			System.out.println(currentEditText.getText().toString());
			e.printStackTrace();
		}
		// Creating calendar
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// Creating dialog box
		DatePickerDialog dpd = new DatePickerDialog(getActivity(), this,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		dpd.show();
		// Giving focus to another view to avoid the user to be able to type
		// something in the EditText after
		listlog.requestFocus();
	}

	/**
	 * <b>Method from <i>OnDateSetListener</i></b></br> What to do when
	 * finishing editing text
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// Get calendar and update it
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		String dateDate = StaticANI.FORM_DATEFORMAT.format(calendar.getTime());
		// Storing the part of the date in a variable
		tmp_Time = dateDate;
		// Creating time dialog
		TimePickerDialog tpd = new TimePickerDialog(getActivity(), this,
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), true);
		tpd.show();
	}

	/**
	 * <b>Method from <i>OnTimeSetListener</i></b></br> What to do when
	 * finishing editing text
	 */
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Set the calendar
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		// Complete the date string
		String dateDate = StaticANI.FORM_TIMEFORMAT.format(calendar.getTime());
		currentEditText.setText(tmp_Time + dateDate);
		// Refresh the datas
		refresh();
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
			refresh();
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// /////////////////////////// GETTER //////////////////////////////////////

	public EditText getSearch() {
		return search;
	}

	public String getCurrentApp() {
		return currentApp;
	}

	public String getCurrentDest() {
		return currentDest;
	}

	public EditText getTimeS() {
		return timeS;
	}

	public EditText getTimeE() {
		return timeE;
	}

	public ListView getListlog() {
		return listlog;
	}

	/**
	 * Activate the loading wheel
	 * 
	 * @param enable
	 *            (<b>True</b> -> <b>Gone</b> and <b>False</b> ->
	 *            <b>Visible</b>)
	 */
	public void enable(boolean enable) {
		waiting.setVisibility(enable ? View.GONE : View.VISIBLE);
	}

}