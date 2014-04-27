package com.ani.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class BinaryUtils {

	public static boolean installBinary(Context context, String binary,
			int resource) {
		boolean needsInstall = false;
		String path = context.getFilesDir().getAbsolutePath() + File.separator
				+ binary;
		File file = new File(path);

		if (!file.isFile()) {
			needsInstall = true;
		}

		if (needsInstall) {
			try {

				InputStream in = context.getResources().openRawResource(
						resource);
				FileOutputStream out = new FileOutputStream(path);

				byte buf[] = new byte[8192];
				int len;
				while ((len = in.read(buf)) != -1) {
					out.write(buf, 0, len);
				}

				out.close();
				in.close();

				Runtime.getRuntime().exec("chmod 755 " + path).waitFor();
			} catch (Exception e) {
				Log.e("ANI_Service", "Cannot install " + binary);
				return false;
			}
		}
		return true;
	}

}
