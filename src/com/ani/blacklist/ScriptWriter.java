package com.ani.blacklist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.ani.R;
import com.ani.db.DbModel;
import com.ani.db.entity.BlackListItem;

/**
 * Write script in the iptables for blocking specific packet
 * 
 * @author Kichan
 * 
 */
public class ScriptWriter extends BlackListerAbstract {
	static StringBuilder script = new StringBuilder();
	Context ctx;
	DbModel db;

	public ScriptWriter(Context context) {
		super(context);
		ctx = context;
		db = new DbModel(ctx);
	}

	@Override
	/**
	 * add blItem to iptables and database
	 */
	protected void enable(BlackListItem blItem) {
		// exception handling
		if (blItem.isActive() == true) {
			Toast.makeText(ctx, "this is already enabled", Toast.LENGTH_SHORT)
					.show();
		}

		switch (blItem.getType()) {
		case APP_NAME:
			enableScript(ctx, blItem.getFilter(), false);
			break;
		case DESTINATION_IP:
			enableScript(ctx, blItem.getFilter(), true);
			break;
		}
		blItem.setActive(true);
		db.addBlackListItem(blItem);
	}

	/**
	 * delete blItem from iptables and database
	 */
	@Override
	protected void disable(BlackListItem blItem) {
		// exception handling
		if (blItem.isActive() == false) {
			Toast.makeText(ctx, "this is already disabled", Toast.LENGTH_SHORT)
					.show();
		}

		switch (blItem.getType()) {
		case APP_NAME:
			disableScript(ctx, blItem.getFilter(), false);
			break;
		case DESTINATION_IP:
			disableScript(ctx, blItem.getFilter(), true);
			break;
		}
		blItem.setActive(false);
		db.removeBlackListItem(blItem);
	}

	public void checkPrerequisite(Context ctx) {
		script = new StringBuilder();

		final String dir = ctx.getDir("bin", 0).getAbsolutePath();
		final String myiptables = dir + "/iptables_armv5";
		script.append("" + "IPTABLES=iptables\n" + "BUSYBOX=busybox\n"
				+ "GREP=grep\n" + "ECHO=echo\n" +
				// "# Try to find busybox\n" +
				"if "
				+ dir
				+ "/busybox_g1 --help >/dev/null 2>/dev/null ; then\n"
				+ "	BUSYBOX="
				+ dir
				+ "/busybox_g1\n"
				+ "	GREP=\"$BUSYBOX grep\"\n"
				+ "	ECHO=\"$BUSYBOX echo\"\n"
				+ "elif busybox --help >/dev/null 2>/dev/null ; then\n"
				+ "	BUSYBOX=busybox\n"
				+ "elif /system/xbin/busybox --help >/dev/null 2>/dev/null ; then\n"
				+ "	BUSYBOX=/system/xbin/busybox\n"
				+ "elif /system/bin/busybox --help >/dev/null 2>/dev/null ; then\n"
				+ "	BUSYBOX=/system/bin/busybox\n"
				+ "fi\n"
				+
				// "# Try to find grep\n" +
				"if ! $ECHO 1 | $GREP -q 1 >/dev/null 2>/dev/null ; then\n"
				+ "	if $ECHO 1 | $BUSYBOX grep -q 1 >/dev/null 2>/dev/null ; then\n"
				+ "		GREP=\"$BUSYBOX grep\"\n"
				+ "	fi\n"
				+
				// "	# Grep is absolutely required\n" +
				"	if ! $ECHO 1 | $GREP -q 1 >/dev/null 2>/dev/null ; then\n"
				+ "		$ECHO The grep command is required. aniwall will not work.\n"
				+ "		exit 1\n"
				+ "	fi\n"
				+ "fi\n"
				+
				// "# Try to find iptables\n" +
				"if "
				+ myiptables
				+ " --version >/dev/null 2>/dev/null ; then\n"
				+ "	IPTABLES="
				+ myiptables + "\n" + "fi\n" + "");
	}

	public void scriptHeader(Context ctx) {
		// TODO: check whether our chain is made (if this, then end this method)
		assertBinaries(ctx);

		checkPrerequisite(ctx);

		final String ITFS_3G[] = { "rmnet+", "pdp+", "ppp+", "uwbr+", "wimax+",
				"vsnet+", "ccmni+", "usb+" };

		script.append(""
				+ "$IPTABLES --version || exit 1\n"
				+
				// "# Create the aniwall chains if necessary\n" +
				"$IPTABLES -L aniwall >/dev/null 2>/dev/null || $IPTABLES --new aniwall || exit 2\n"
				+ "$IPTABLES -L aniwall-3g >/dev/null 2>/dev/null || $IPTABLES --new aniwall-3g || exit 3\n"
				+ "$IPTABLES -L aniwall-reject >/dev/null 2>/dev/null || $IPTABLES --new aniwall-reject || exit 5\n"
				+
				// "# Add aniwall chain to OUTPUT chain if necessary\n" +
				"$IPTABLES -L OUTPUT | $GREP -q aniwall || $IPTABLES -A OUTPUT -j aniwall || exit 6\n"
				+ "");
		// it will be used when the process is add(delete) list file -> apply
		// elements in this file
		/*
		 * script.append("" + "$IPTABLES --version || exit 1\n" +
		 * "# Create the aniwall chains if necessary\n" +
		 * "$IPTABLES -L aniwall >/dev/null 2>/dev/null || $IPTABLES --new aniwall || exit 2\n"
		 * +
		 * "$IPTABLES -L aniwall-3g >/dev/null 2>/dev/null || $IPTABLES --new aniwall-3g || exit 3\n"
		 * +
		 * "$IPTABLES -L aniwall-reject >/dev/null 2>/dev/null || $IPTABLES --new aniwall-reject || exit 5\n"
		 * + "# Add aniwall chain to OUTPUT chain if necessary\n" +
		 * "$IPTABLES -L OUTPUT | $GREP -q aniwall || $IPTABLES -A OUTPUT -j aniwall || exit 6\n"
		 * + "# Flush existing rules\n" + "$IPTABLES -F aniwall || exit 7\n" +
		 * "$IPTABLES -F aniwall-3g || exit 8\n" +
		 * "$IPTABLES -F aniwall-reject || exit 10\n" + "");
		 */

		script.append(""
				+
				// "# Create the reject rule (log disabled)\n" +
				"$IPTABLES -F aniwall || exit 7\n"
				+ "$IPTABLES -F aniwall-3g || exit 8\n"
				+ "$IPTABLES -F aniwall-reject || exit 10\n"
				+ "$IPTABLES -A aniwall-reject -j REJECT || exit 11\n" + "");
		// script.append("# Main rules (per interface)\n");
		for (final String itf : ITFS_3G) {
			script.append("$IPTABLES -A aniwall -o ").append(itf)
					.append(" -j aniwall-3g || exit\n");
		}

		try {
			runScriptAsRoot(ctx, script.toString(), new StringBuilder());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disableScript(Context ctx, String target, Boolean type) {
		checkPrerequisite(ctx);

		if (type == false) // application
		{
			int uid = findUid(ctx, target);
			if (uid == -1) {
				Toast.makeText(ctx, "There is no application",
						Toast.LENGTH_SHORT).show();
			} else {
				script.append("$IPTABLES -D aniwall-3g -m owner --uid-owner ")
						.append(uid).append(" -j aniwall-reject\n");
				try {
					runScriptAsRoot(ctx, script.toString(), new StringBuilder());
					Toast.makeText(ctx, "disable complete", Toast.LENGTH_SHORT)
							.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else // IP address
		{
			script.append("$IPTABLES -D INPUT -s " + target + " -j REJECT");
			try {
				runScriptAsRoot(ctx, script.toString(), new StringBuilder());
				Toast.makeText(ctx, "disable complete", Toast.LENGTH_SHORT)
						.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// script removing all rules
		/*
		 * script.append("" + "# Flush existing rules\n" +
		 * "$IPTABLES -F aniwall || exit 7\n" +
		 * "$IPTABLES -F aniwall-3g || exit 8\n" +
		 * "$IPTABLES -F aniwall-reject || exit 10\n" + "");
		 * 
		 * script.append("$IPTABLES -F INPUT");
		 */
	}

	/**
	 * Add script which apply blocking iptables instruction
	 * 
	 * @param target
	 *            application name or IP address
	 * @param isIP
	 *            0=application name, 1=IP address
	 */
	public int enableScript(Context ctx, String target, Boolean isIP) {
		checkPrerequisite(ctx);
		script.append("# Filtering rules\n");

		if (isIP == false) // add application
		{
			int uid = findUid(ctx, target);
			if (uid == -1) {
				Toast.makeText(ctx, "There is no application",
						Toast.LENGTH_SHORT).show();
				return -1;
			} else {
				script.append("$IPTABLES -D aniwall-3g -m owner --uid-owner ")
						.append(uid).append(" -j aniwall-reject\n");
				script.append("$IPTABLES -A aniwall-3g -m owner --uid-owner ")
						.append(uid).append(" -j aniwall-reject\n");
				try {
					runScriptAsRoot(ctx, script.toString(), new StringBuilder());
				} catch (IOException e) {
					e.printStackTrace();
				}
				Toast.makeText(ctx, "enable complete", Toast.LENGTH_SHORT)
						.show();
				return 0;
			}
		} else // add IPaddress
		{
			// check whether it is IPaddress form -> if not, pop toast and
			// return -1
			Pattern IPpattern = Pattern
					.compile("^(1|2)?[0-9]?[0-9]([.](1|2)?[0-9]?[0-9]){3}$");
			Matcher IPmatcher = IPpattern.matcher(target);
			if (IPmatcher.find() == false) {
				Toast.makeText(ctx, "It isn't IP form", Toast.LENGTH_SHORT)
						.show();
				return -1;
			}

			script.append("$IPTABLES -D INPUT -s " + target + " -j REJECT\n");
			script.append("$IPTABLES -A INPUT -s " + target + " -j REJECT");
			try {
				runScriptAsRoot(ctx, script.toString(), new StringBuilder());
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(ctx, "enable complete", Toast.LENGTH_SHORT).show();
			return 0;
		}
	}

	private int findUid(Context ctx, String appName) {
		PackageManager manager = ctx.getPackageManager();
		List<ApplicationInfo> appList = manager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		ApplicationInfo app;
		// File testfile = new File(ctx.getDir("bin",0), "test.txt");

		// FileWriter writer = new FileWriter(testfile);
		// PrintWriter out = new PrintWriter(writer);

		int nSize = appList.size();
		for (int i = 0; i < nSize; i++) {
			app = appList.get(i);

			// out.println(app.packageName);

			// packagename�� �����ϴ� ��Ű�� �̸�, processname�� �������� ��Ű�� �̸�
			// if(app.packageName.equals(applicationName.getText().toString()))

			if (((String) ctx.getPackageManager().getApplicationLabel(app))
					.equals(appName)) {
				return app.uid;
			}
			// out.close();
		}
		return -1;
	}

	public boolean assertBinaries(Context ctx) {
		try {
			// Check iptables_armv5
			File file = new File(ctx.getDir("bin", 0), "iptables_armv5");
			if (!file.exists() || file.length() != 198652) {
				copyRawFile(ctx, R.raw.iptables_armv5, file, "755");
			}
			// Check busybox
			file = new File(ctx.getDir("bin", 0), "busybox_g1");
			if (!file.exists()) {
				copyRawFile(ctx, R.raw.busybox_g1, file, "755");
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void copyRawFile(Context ctx, int resid, File file, String mode)
			throws IOException, InterruptedException {
		final String abspath = file.getAbsolutePath();
		// Write the iptables binary
		final FileOutputStream out = new FileOutputStream(file);
		final InputStream is = ctx.getResources().openRawResource(resid);
		byte buf[] = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		is.close();
		// Change the permissions
		Runtime.getRuntime().exec("chmod " + mode + " " + abspath).waitFor();
	}

	public static int runScriptAsRoot(Context ctx, String script,
			StringBuilder res, long timeout) {
		return runScript(ctx, script, res, timeout, true);
	}

	public static int runScriptAsRoot(Context ctx, String script,
			StringBuilder res) throws IOException {
		return runScriptAsRoot(ctx, script, res, 40000);
	}

	public static int runScript(Context ctx, String script, StringBuilder res)
			throws IOException {
		return runScript(ctx, script, res, 40000, false);
	}

	public static int runScript(Context ctx, String script, StringBuilder res,
			long timeout, boolean asroot) {
		final File file = new File(ctx.getDir("bin", 0), "aniwall.sh");
		final ScriptRunner runner = new ScriptRunner(file, script, res, asroot);
		runner.start();
		try {
			if (timeout > 0) {
				runner.join(timeout);
			} else {
				runner.join();
			}
			if (runner.isAlive()) {
				// Timed-out
				runner.interrupt();
				runner.join(150);
				runner.destroy();
				runner.join(50);
			}
		} catch (InterruptedException ex) {
		}
		return runner.exitcode;
	}

	private static final class ScriptRunner extends Thread {
		private final File file;
		private final String script;
		private final StringBuilder res;
		private final boolean asroot;
		public int exitcode = -1;
		private Process exec;

		/**
		 * Creates a new script runner.
		 * 
		 * @param file
		 *            temporary script file
		 * @param script
		 *            script to run
		 * @param res
		 *            response output
		 * @param asroot
		 *            if true, executes the script as root
		 */
		public ScriptRunner(File file, String script, StringBuilder res,
				boolean asroot) {
			this.file = file;
			this.script = script;
			this.res = res;
			this.asroot = asroot;
		}

		@Override
		public void run() {
			try {
				file.createNewFile();
				final String abspath = file.getAbsolutePath();
				// make sure we have execution permission on the script file
				Runtime.getRuntime().exec("chmod 777 " + abspath).waitFor();
				// Write the script to be executed
				final OutputStreamWriter out = new OutputStreamWriter(
						new FileOutputStream(file));
				if (new File("/system/bin/sh").exists()) {
					out.write("#!/system/bin/sh\n");
				}
				out.write(script);
				if (!script.endsWith("\n"))
					out.write("\n");
				out.write("exit\n");
				out.flush();
				out.close();
				if (this.asroot) {
					// Create the "su" request to run the script
					exec = Runtime.getRuntime().exec("su -c " + abspath);
				} else {
					// Create the "sh" request to run the script
					exec = Runtime.getRuntime().exec("sh " + abspath);
				}
				final InputStream stdout = exec.getInputStream();
				final InputStream stderr = exec.getErrorStream();
				final byte buf[] = new byte[8192];
				int read = 0;
				while (true) {
					final Process localexec = exec;
					if (localexec == null)
						break;
					try {
						// get the process exit code - will raise
						// IllegalThreadStateException if still running
						this.exitcode = localexec.exitValue();
					} catch (IllegalThreadStateException ex) {
						// The process is still running
					}
					// Read stdout
					if (stdout.available() > 0) {
						read = stdout.read(buf);
						if (res != null)
							res.append(new String(buf, 0, read));
					}
					// Read stderr
					if (stderr.available() > 0) {
						read = stderr.read(buf);
						if (res != null)
							res.append(new String(buf, 0, read));
					}
					if (this.exitcode != -1) {
						// finished
						break;
					}
					// Sleep for the next round
					Thread.sleep(50);
				}
			} catch (InterruptedException ex) {
				if (res != null)
					res.append("\nOperation timed-out");
			} catch (Exception ex) {
				if (res != null)
					res.append("\n" + ex);
			} finally {
				destroy();
			}
		}

		/**
		 * Destroy this script runner
		 */
		@Override
		public synchronized void destroy() {
			if (exec != null)
				exec.destroy();
			exec = null;
		}
	}

}
