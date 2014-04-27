package com.ani.db;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ani.R;
import com.ani.db.entity.BlackListItem;
import com.ani.db.entity.LogFilter;
import com.ani.db.entity.NetLog;
import com.ani.db.object.NetLogAggregate;
import com.ani.db.object.Traffic;
import com.ani.utils.ANIUtils;

/****
 * 
 * @author Bertrand
 * 
 */
abstract public class DbModelInterface {

	protected SQLiteDatabase database;
	// the SQLiteOpenHelper
	protected GenericSQLiteHelper sqLiteHelper;
	protected String dataBasePath;
	protected Context context;

	/***
	 * Constructor the data base from file
	 * 
	 * @param context
	 * @param name
	 * @param version
	 */
	public DbModelInterface(Context context, String filename) {
		this.context = context;
		this.dataBasePath = "/data/data/" + context.getPackageName()
				+ "/databases/" + filename;
		Log.d("ANI", dataBasePath);
		this.sqLiteHelper = new GenericSQLiteHelper(context, filename, null, 1);
		SQLiteDatabase db = null;
		// Tried to create db before copying, so file should exist
		db = sqLiteHelper.getReadableDatabase();
		db.close();
		if (!isValidDataBase()) {
			ANIUtils.copyFileFromRaw(R.raw.datatest, dataBasePath);
		}
		open();
		clearLogs();

	}

	@Override
	public void finalize() {
		close();
	}

	/***
	 * Constructor the data base via the GenericSQLiteHelper
	 * 
	 * @param context
	 * @param name
	 * @param version
	 */
	public DbModelInterface(Context context, String name, int version) {
		// On créer la BDD et sa table
		sqLiteHelper = getSQLiteHelper(context, name, version);
	}

	public abstract GenericSQLiteHelper getSQLiteHelper(Context context,
			String name, int version);

	/***
	 * open the data base
	 */
	public void open() {
		try {
			// on ouvre la BDD en lecture/écriture
			if (database == null || database.isOpen() == false) {
				database = sqLiteHelper.getWritableDatabase();
				Log.d("ANI", "Opening : " + database.getPath());
			}
		} catch (Exception e) {
		}
	}

	/**
	 * close the database
	 */
	public void close() {
		// on ferme l'accès à la BDD
		try {
			if (database != null && database.isOpen()) {
				database.close();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * return the data base
	 * 
	 * @return
	 */
	public SQLiteDatabase getBDD() {
		return database;
	}

	/**
	 * Method for inserting an element in the database
	 * 
	 * @param table
	 * @param values
	 * @return
	 */
	public long insert(String table, ContentValues values) {
		long returnValue = -1;
		try {

			returnValue = database.insert(table, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return returnValue;
	}

	/**
	 * Method to modify an element in the dabatase
	 * 
	 * @param table
	 * @param whereClause
	 * @param values
	 * @return
	 */
	public int update(String table, String whereClause, ContentValues values) {
		int returnValue = -1;
		try {
			open();
			returnValue = database.update(table, values, whereClause, null);
		} catch (Exception e) {
			return -1;
		} finally {
			close();
		}
		return returnValue;
	}

	/**
	 * Method to supress an element in the dabatase
	 * 
	 * @param table
	 * @param whereClause
	 * @return
	 */
	public int delete(String table, String whereClause) {
		int returnValue = -1;
		try {
			open();
			// change point is only applied to blacklister because it can
			// operate correctly at the other parts
			if (table.equals("BlackList")) {
				database.execSQL(whereClause);
				returnValue = 0;
			} else
				returnValue = database.delete(table, whereClause, null);
		} catch (Exception e) {
			return -1;
		} finally {
			close();
		}
		return returnValue;
	}

	/**
	 * Execute a SQL query
	 * 
	 * @param query
	 * @return
	 */
	public Cursor executeQuery(String query) {
		Cursor c = null;
		try {
			open();
			c = database.rawQuery(query, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return c;
	}

	public final Context getContext() {
		return context;
	}

	public final void setContext(Context context) {
		this.context = context;
	}

	protected abstract boolean isValidDataBase();

	public class GenericSQLiteHelper extends SQLiteOpenHelper {

		public GenericSQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

	}

	// LOGS
	public abstract void addLog(NetLog log);

	public abstract List<NetLog> getLogs();

	public abstract List<NetLogAggregate> getLogAppsAgregate();

	public abstract List<NetLogAggregate> getLogAppsAgregate(Date beginDate,
			Date endDate);

	public abstract List<NetLogAggregate> getLogDesinationIPAgregate();

	public abstract List<NetLogAggregate> getLogDesinationIP(Date beginDate,
			Date endDate);

	public abstract void clearLogs();

	// TRAFFIC
	public abstract List<Traffic> getTrafics();

	// LOG FILTERS
	public abstract void addLogFilter(LogFilter filter);

	public abstract List<LogFilter> getLogFilters();

	public abstract void disableLogFilter(LogFilter filter);

	public abstract void enableLogFilter(LogFilter filter);

	public abstract void removeLogFilter(LogFilter filter);

	// BLACKLIST
	public abstract void addBlackListItem(BlackListItem blItem);

	public abstract List<BlackListItem> getBlackListItems();

	public abstract void disableBlackListItem(BlackListItem blItem);

	public abstract void enableBlackListItem(BlackListItem blItem);

	public abstract void removeBlackListItem(BlackListItem blItem);

}
