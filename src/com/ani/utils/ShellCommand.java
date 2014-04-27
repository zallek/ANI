/* (C) 2012 Pragmatic Software
   This Source Code Form is subject to the terms of the Mozilla Public
   License, v. 2.0. If a copy of the MPL was not distributed with this
   file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package com.ani.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import android.content.Context;
import android.util.Log;

public class ShellCommand {
	Runtime rt;
	String[] command;
	String tag = "";
	Process process;
	BufferedReader stdout;
	public String error;
	public int exitval;

	public ShellCommand(String[] command, String tag) {
		this(command);
		this.tag = tag;
	}

	public ShellCommand(String[] command) {
		this.command = command;
		rt = Runtime.getRuntime();
	}

	public void start(boolean waitForExit) {
		Log.d("ShellCommand",
				"ShellCommand: starting [" + tag + "] "
						+ Arrays.toString(command));

		exitval = -1;
		error = null;

		try {
			process = new ProcessBuilder().command(command)
					.redirectErrorStream(true).start();

			stdout = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
		} catch (Exception e) {
			Log.e("ShellCommand", "Failure starting shell command [" + tag
					+ "]", e);
			error = e.getCause().getMessage();
			return;
		}

		if (waitForExit) {
			waitForExit();
		}
	}

	public void waitForExit() {
		while (checkForExit() == false) {
			if (stdoutAvailable()) {
				Log.e("ShellCommand", "ShellCommand waitForExit [" + tag
						+ "] discarding read: " + readStdout());

			} else {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					Log.d("ShellCommand", "waitForExit" + e);
				}
			}
		}
	}

	public void finish() {
		Log.d("ShellCommand",
				"ShellCommand: finishing [" + tag + "] "
						+ Arrays.toString(command));

		try {
			if (stdout != null) {
				stdout.close();
			}
		} catch (Exception e) {
			Log.e("ShellCommand", "Exception finishing [" + tag + "]", e);
		}

		process.destroy();
		process = null;
	}

	public boolean checkForExit() {
		try {
			exitval = process.exitValue();
			Log.d("ShellCommand", "ShellCommand exited: [" + tag + "] exit "
					+ exitval);
		} catch (IllegalThreadStateException e) {
			return false;
		}

		finish();
		return true;
	}

	public boolean stdoutAvailable() {
		try {
			/*
			 * if(Log.enabled) { Log.d("stdoutAvailable [" + tag + "]: " +
			 * stdout.ready()); }
			 */
			return stdout.ready();
		} catch (java.io.IOException e) {
			Log.e("ShellCommand", "stdoutAvailable error" + e);
			return false;
		}
	}

	public String readStdoutBlocking() {
		String line;

		if (stdout == null) {
			return null;
		}

		try {
			line = stdout.readLine();
		} catch (Exception e) {
			Log.e("ShellCommand", "readStdoutBlocking error" + e);
			return null;
		}

		if (line == null) {
			return null;
		} else {
			return line + "\n";
		}
	}

	public String readStdout() {

		if (stdout == null) {
			return null;
		}

		try {
			if (stdout.ready()) {
				String line = stdout.readLine();

				if (line == null) {
					return null;
				} else {
					return line + "\n";
				}
			} else {
				Log.d("ShellCommand", "readStdout [" + tag + "] no data");
				return "";
			}
		} catch (Exception e) {
			Log.e("ShellCommand", "readStdout error" + e);
			return null;
		}
	}

	public static boolean checkRoot(Context context) {

		ShellCommand cmd = new ShellCommand(new String[] { "su", "-c",
				"echo 42" }, "checkRoot");
		cmd.start(true);

		if (cmd.error != null) {
			Log.e("NetworkLog", "Failed check root (exit " + cmd.exitval
					+ "): " + cmd.error);
			return false;
		} else if (cmd.exitval != 0) {
			Log.e("NetworkLog", "Failed check root (exit " + cmd.exitval + ")");
			return false;
		} else {
			Log.e("NetworkLog", "Check root passed");
			return true;
		}

	}

}
