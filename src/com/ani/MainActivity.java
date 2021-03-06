package com.ani;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.ani.db.object.NetworkType;
import com.ani.log.Logger;
import com.ani.log.LoggerConstants;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		NetworkType type = NetworkType.APP_NAME;
		Intent i = new Intent(this, Logger.class);
		i.setAction(LoggerConstants.ACTION_COMMAND);
		i.putExtra(LoggerConstants.COMMAND, LoggerConstants.COMMAND_START);

		startService(i);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
