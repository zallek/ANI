package com.ani.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.ani.ANIApplication;
import com.ani.db.DbModel;

/**
 * used to write and read file
 * 
 * @author Bertrand
 * 
 */
public class ANIUtils {

	static final public DbModel getDatabase() {// TODO
		return null;
		// return
		// ANIApplication.getApplicationInstance().getDataBaseInterface();
	}

	static final public void copyFileFromRaw(Integer rawsource,
			String destfilePath) {

		File file = new File(destfilePath);
		if (file != null && file.exists()) {
			try {
				InputStream input = ANIApplication.getApplicationInstance()
						.getResources().openRawResource(rawsource);
				// Open the empty database as the output stream
				OutputStream output = new FileOutputStream(destfilePath);
				// transfer byte to input file to output file
				byte[] buffer = new byte[1024];
				int length;
				while ((length = input.read(buffer)) > 0) {
					output.write(buffer, 0, length);
				}
				// Close the streams
				output.flush();
				output.close();
				input.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
