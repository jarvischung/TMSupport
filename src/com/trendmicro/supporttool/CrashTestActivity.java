package com.trendmicro.supporttool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class CrashTestActivity extends Activity {
	private final static String TAG = "CrashTestActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Account account = new Account("twmobile.sp@gmail.com", "com.google");
		String password = "19830823";
		AccountManager accountManager = AccountManager.get(this);
		accountManager.addAccountExplicitly(account, password, null);
		// Object nul = null;
		// nul.toString();

		/*try {
			String tmp[] = this.databaseList();
			Log.d(TAG, "XXXXXXXXX");
			new DataBaseHelper(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	class DataBaseHelper extends SQLiteOpenHelper {
		private Context mycontext;

		private String DB_PATH = "/data/data/org.acra.sampleapp/";
		private static final String DB_NAME = "downloads.db";

		public SQLiteDatabase myDataBase;

		public DataBaseHelper(Context context) throws IOException {
			super(context, DB_NAME, null, 1);
			this.mycontext = context;
			boolean dbexist = checkdatabase();
			if (true) {
				importDatabase("/data/data/org.acra.sampleapp/shared_prefs/org.acra.sampleapp_preferences.xml"); // /data/data/org.acra.sampleapp/databases/database.db
			} else {
				Log.d(TAG, "Database doesn't exist");
				//createdatabase();
			}

		}
		
		public static final String DB_FILEPATH = "/sdcard/database.xml";
		public boolean importDatabase(String dbPath){
		    close();
		    File newDb = new File(dbPath);
		    File oldDb = new File(DB_FILEPATH);
		    if (newDb.exists()) {
		    	try{
		    		copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
		        // Access the copied database so SQLiteHelper will cache it and mark
		        // it as created.
		        getWritableDatabase().close();
		        return true;
		    }else{
		    	Log.d(TAG, "No File!!");
		    }
		    return false;
		}
		
		public void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
	        FileChannel fromChannel = null;
	        FileChannel toChannel = null;
	        try {
	            fromChannel = fromFile.getChannel();
	            toChannel = toFile.getChannel();
	            fromChannel.transferTo(0, fromChannel.size(), toChannel);
	        } catch(Exception e){
	        	e.printStackTrace();
	        } finally {
	            try {
	                if (fromChannel != null) {
	                    fromChannel.close();
	                }
	            } finally {
	                if (toChannel != null) {
	                    toChannel.close();
	                }
	            }
	        }
	    }

		public void createdatabase() throws IOException {
			boolean dbexist = checkdatabase();
			if (dbexist) {
				// System.out.println(" Database exists.");
			} else {
				this.getReadableDatabase();
				try {
					copydatabase();
				} catch (IOException e) {
					throw new Error("Error copying database");
				}
			}
		}

		private boolean checkdatabase() {
			// SQLiteDatabase checkdb = null;
			boolean checkdb = false;
			try {
				String myPath = DB_PATH + DB_NAME;
				File dbfile = new File(myPath);
				// checkdb =
				// SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
				checkdb = dbfile.exists();
			} catch (SQLiteException e) {
				System.out.println("Database doesn't exist");
			}

			return checkdb;
		}

		private void copydatabase() throws IOException {

			// Open your local db as the input stream
			// InputStream myinput = new FileInputStream(
			// "/data/data/org.acra.sampleapp/databases/" +
			// DB_NAME);//mycontext.getAssets().open(DB_NAME);

			InputStream myinput = mycontext.getAssets().open(DB_NAME);

			// Path to the just created empty db
			String outfilename = DB_PATH + DB_NAME;

			// Open the empty db as the output stream
			OutputStream myoutput = new FileOutputStream(
					"/data/data/org.acra.sampleapp/databases/BLib.sqlite");

			// transfer byte to inputfile to outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myinput.read(buffer)) > 0) {
				myoutput.write(buffer, 0, length);
			}

			// Close the streams
			myoutput.flush();
			myoutput.close();
			myinput.close();

		}

		public void opendatabase() throws SQLException {
			// Open the database
			String mypath = DB_PATH + DB_NAME;
			myDataBase = SQLiteDatabase.openDatabase(mypath, null,
					SQLiteDatabase.OPEN_READONLY);

		}

		public synchronized void close() {
			if (myDataBase != null) {
				myDataBase.close();
			}
			super.close();
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}
	}
}
