package com.trendmicro.supporttool.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONObject;

import com.trendmicro.supporttool.R;
import com.trendmicro.supporttool.comm.impl.MailUtil;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

public class FileUtil {
	public final static String TAG = "FileUtil";
	private static final int ZIP_BUFFER = 2048;
	private Context context;
	private boolean mDumpMode = false;
	private int limitSize = 5;
	private final String FILE_DUMP_FOLDER = "/files/log/dump/";
	private final String COLLECT_DUMP_FOLDER = "/files/log/collect/";
	private final String CRASH_FOLDER = "/files/crash/";
	private final String ZIP_FOLDER = "/files/zip/";

	public FileUtil(Context context_) {
		context = context_;
		this.checkDumpFolder();
		this.checkCrashFolder();
		this.checkCollectFolder();
		
		// test
		//testPlan(context_);
		//testPlan(context_);
		/*testPlan(context_);
		testPlan(context_);
		testPlan(context_);
		testPlan(context_);
		testPlan(context_);
		
		File file[] = getZipFileList();
		
		checkLimit(5);*/
		//new MailUtil();
		
		//context.getResources();
		
		//dumpApplicationResource();
		
		/*try {
			copyFile(new FileInputStream("/data/data/com.trendmicro.supporttool/shared_prefs/test.xml"),
					new FileOutputStream("/data/data/com.trendmicro.supporttool/files/test.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	private void testPlan(Context context_) {
		try {
			// test-----
			//new DataBaseHelper(context_);//.testPlan(); //copy Databases and Preference
			save(new StringBuffer("1234567\r\n0987654321\r\n1234567890\r\n")); // For crash test
			compress();
			
			try{
				Thread.sleep(1000);
			}catch(Exception e){}
			
			// collect log
			ArrayList<String> listString = new ArrayList<String>();
			listString.add("test中文");
			listString.add("test2");
			listString.add("test3");
			listString.add("test4");
			save(listString);

			compress();
			
			//new MailUtil();
			// ---------
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//For testing.
	private void copyFile(FileInputStream fromFile, FileOutputStream toFile)
			throws IOException {
		FileChannel fromChannel = null;
		FileChannel toChannel = null;
		try {
			fromChannel = fromFile.getChannel();
			toChannel = toFile.getChannel();
			fromChannel.transferTo(0, fromChannel.size(), toChannel);
		} catch (Exception e) {
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
	
	public boolean dumpApplicationResource(){
		//How to get all res id.
		Class res = R.string.class;
		try {
			res = Class.forName(context.getPackageName() + ".R");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Class[] classTmp = res.getClasses();
		Field[] field = null;
		for(int i=0;i<classTmp.length;i++){
			Log.d(TAG, "" + classTmp[i].getName());
			if(classTmp[i].getName().indexOf("R$string")!=-1){
				field = classTmp[i].getFields(); //Get string resource id array.
			}
		}
		
		for(int i=0;i<field.length;i++){
			Log.d(TAG, "Resource :" + field[i].getName());
		}
		
		String android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
		Log.d(TAG, "" + android_id);
		//------
				
		return true;
	}

	//For crash mode.
	public boolean save(StringBuffer sb) {
		if(!mDumpMode)
			mDumpMode = false;
		FileOutputStream fos = null;
		File file = null;
		try {
			if (mDumpMode) { // Dump mode
				file = new File("/data/data/" + context.getPackageName()
						+ this.COLLECT_DUMP_FOLDER + "collect.log");
			} else { // Crash mode
				file = new File("/data/data/" + context.getPackageName()
						+ this.CRASH_FOLDER + "crash.log");
			}
			fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			return false;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}
	
	//For dump mode.
	public boolean save(List<String> list) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i) + "\r\n");
		}

		// copy database and prefs
		try {
			mDumpMode = true;
			new DataBaseHelper(context)/*.testPlan()*/;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return save(sb);
	}

	public boolean checkLimit(int limitSize_) {
		limitSize = limitSize_;
		File file = new File("/data/data/" + context.getPackageName()
				+ this.ZIP_FOLDER);
		if(file.listFiles().length > limitSize)
				deleteFileLimit(file.listFiles());
		
		/*if (mDumpMode) { // Dump mode
			file = new File("/data/data/" + context.getPackageName()
				+ "/files/zip/dump/");
			if(file.listFiles().length > limitSize)
				deleteFileLimit(file.listFiles());
		}else{ //Crash mode
			file = new File("/data/data/" + context.getPackageName()
					+ "/files/zip/crash/");
			if(file.listFiles().length > limitSize)
				deleteFileLimit(file.listFiles());
		}*/
		
		return true;
	}
	
	private void deleteFileLimit(File[] fileList){
		for(int i=0;i<(fileList.length - limitSize);i++){
			//Log.d( TAG, limitSize + " Delete File:" + fileList[i].getName() );
			fileList[i].delete();
		}
	}

	public void sizeControl(String filePath) {

	}

	public File compress() {
		checkZipFolder();
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
		String format = s.format(new Date());
		File filePath = null;
		if (mDumpMode) { // Dump mode
			filePath = new File("/data/data/" + context.getPackageName()
					+ this.ZIP_FOLDER + "dump_" + format + ".zip");
		} else { // Crash mode
			filePath = new File("/data/data/" + context.getPackageName()
					+ this.ZIP_FOLDER + "crash_" + format + ".zip");
		}

		if (zip(filePath)) { // compress
			if (mDumpMode){
				deleteDumpFile();
				mDumpMode = false;
			}else
				deleteCrashFile();
		}

		return filePath;
	}
	
	public File compressForCollect(){
		checkZipFolder();
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
		String format = s.format(new Date());
		File filePath = new File("/data/data/" + context.getPackageName()
			+ this.ZIP_FOLDER + "dump_" + format + ".zip");

		if (zip(filePath)) { // compress
			deleteDumpFile();
		}

		return filePath;
	}
	
	public File compressForCrash(){
		checkZipFolder();
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
		String format = s.format(new Date());
		File filePath = new File("/data/data/" + context.getPackageName()
			+ this.ZIP_FOLDER + "/crash_" + format + ".zip");

		if (zip(filePath)) { // compress
			deleteCrashFile();
		}

		return filePath;
	}

	public File[] getZipFileList() {
		File file = new File("/data/data/" + context.getPackageName()
				+ this.ZIP_FOLDER);
		
		/*if (mDumpMode){
			file = new File("/data/data/" + context.getPackageName()
				+ "/files/zip/dump/");
		}else{
			file = new File("/data/data/" + context.getPackageName()
				+ "/files/zip/crash/");
		}*/
		
		File tmp[] = file.listFiles();

		for (int i = 0; i < tmp.length; i++) {
			Log.d(TAG, "Zip file list:" + tmp[i].getPath());
		}
		return tmp;
	}

	private void deleteDumpFile() {
		// Delete collect folder file.
		File file[] = new File("/data/data/" + context.getPackageName()
				+ this.COLLECT_DUMP_FOLDER).listFiles();
		for (int i = 0; i < file.length; i++)
			file[i].delete();

		// Delete dump folder file.
		file = new File("/data/data/" + context.getPackageName()
				+ this.FILE_DUMP_FOLDER).listFiles();
		for (int i = 0; i < file.length; i++)
			file[i].delete();
	}

	private void deleteCrashFile() {
		// Delete crash file.
		File[] file = new File("/data/data/" + context.getPackageName()
				+ this.CRASH_FOLDER).listFiles();
		for (int i = 0; i < file.length; i++)
			file[i].delete();
	}

	private void checkZipFolder() {
		File file = new File("/data/data/" + context.getPackageName()
				+ this.ZIP_FOLDER);
		if (!file.exists())
			file.mkdirs();
	}
	
	private boolean checkDumpFolder() {
		File file = new File("/data/data/" + context.getPackageName()
				+ "/files/log/dump/");
		if (!file.exists()) {
			file.mkdirs();
			return false;
		} else {
			return true;
		}
	}

	private boolean checkCollectFolder() {
		File file = new File("/data/data/" + context.getPackageName()
				+ this.COLLECT_DUMP_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
			return false;
		} else {
			return true;
		}
	}

	private boolean checkCrashFolder() {
		File file = new File("/data/data/" + context.getPackageName()
				+ this.CRASH_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
			return false;
		} else {
			return true;
		}
	}

	private File[] getNeedCompressList() {
		File file = new File("/data/data/" + context.getPackageName()
				+ "/files/");
		File fileList[] = file.listFiles();

		return fileList;
	}

	private boolean zip(File zipFilePath) {
		Log.d(TAG, "Zip to: " + zipFilePath.getPath());

		File sourceDumpDir = null;
		if (mDumpMode){	//Dump mode
			sourceDumpDir = new File("/data/data/" + context.getPackageName()
				+ "/files/log/");
		}else{
			sourceDumpDir = new File("/data/data/" + context.getPackageName()
				+ this.CRASH_FOLDER);
		}
		if (!sourceDumpDir.exists()) {
			Log.d(TAG,
					"Can't find this path: " + sourceDumpDir.getAbsolutePath());
			return false;
		}

		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(zipFilePath.getPath())));
			zos = this.doZip(zos, sourceDumpDir, null);
		} catch (IOException e) {
			Log.d(TAG, e.getMessage());
			return false;
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private ZipOutputStream doZip(ZipOutputStream zos, File sourceDir,
			String folder) throws FileNotFoundException, IOException {
		File[] sourceFiles = sourceDir.listFiles();
		BufferedInputStream bis;
		ZipEntry entry;
		String path;
		for (int i = 0; i < sourceFiles.length; i++) {
			path = (folder == null ? "" : folder + "/")
					+ sourceFiles[i].getName();
			if (sourceFiles[i].isDirectory()) {
				Log.d(TAG, "Open a directory: " + sourceFiles[i].getAbsolutePath());
				this.doZip(zos, new File(sourceDir, sourceFiles[i].getName()),
						path);
			} else {
				Log.d(TAG, "Zip a file: " + sourceFiles[i].getAbsolutePath());
				bis = new BufferedInputStream(new FileInputStream(
						sourceFiles[i]));
				entry = new ZipEntry(path);
				zos.putNextEntry(entry);
				int data = 0;
				while ((data = bis.read()) != -1) {
					zos.write(data);
				}
				bis.close();
			}
		}
		zos.flush();
		return zos;
	}

	private File[] getDatabaseList() {
		// ContextWrapper cw = new ContextWrapper(context);
		File file = new File("/data/data/" + context.getPackageName()
				+ "/databases/");

		return file.listFiles();
	}

	private File[] getPreferenceList() {
		File file = new File("/data/data/" + context.getPackageName()
				+ "/shared_prefs/");

		return file.listFiles();
	}
	
	private ArrayList<File> fileAllList = new ArrayList<File>();
	private File[] getApplicationFile(String path, final String extensionName){
		File[] fileList = new File( path ).listFiles();
		
		for(int i=0;i<fileList.length;i++){
			if( fileList[i].isDirectory()){
				Log.d(TAG, "isDirectory: " + fileList[i].getAbsolutePath());
				getApplicationFile(fileList[i].getAbsolutePath(), extensionName);
			}else if( fileList[i].getName().indexOf(extensionName) !=-1){
				fileAllList.add(fileList[i]);
				Log.d(TAG, "File file: " + fileList[i].getAbsolutePath());
			}
		}
		
		/*FilenameFilter filter= new FilenameFilter() { 
            public boolean accept(File dir, String name) {
            	File currFile = new File(dir, name);
                if (currFile.isFile() && name.indexOf(extensionName) != -1) {
                    return true;
                } else {
                    return false;
                }
            }
		};*/

		return null;//file.listFiles(filter);
	}

	class DataBaseHelper extends SQLiteOpenHelper {
		private Context mContext;
		private static final String DB_NAME = "downloads.db";
		public SQLiteDatabase myDataBase;

		public DataBaseHelper(Context context) throws IOException {
			super(context, DB_NAME, null, 1);
			//testPlan();this.close();//test
			
			this.mContext = context;
			// boolean dbexist = checkdatabase();
			if (true) {

				File tmpDatabases[] = getDatabaseList();
				if (tmpDatabases != null)
					copyDatabaseFile(tmpDatabases);

				File tmpPreference[] = getPreferenceList();
				if (tmpPreference != null)
					copyPreferenceFile(tmpPreference);
				
				copyExtensionFile(".db");
			}

		}

		public void testPlan() {
			createDatabase(); // test
			createPreference(); // test
		}

		// Copy Databases file to temp folder.
		private void copyDatabaseFile(File list[]) {
			for (int i = 0; i < list.length; i++) {
				/*
				 * File fromFile = new File("/data/data/" +
				 * mContext.getPackageName() + "/databases/" + list[i]);
				 */
				File fromFile = new File(list[i].getPath());
				File toFile = new File("/data/data/"
						+ mContext.getPackageName() + "/files/log/dump/"
						+ list[i].getName());
				try {
					copyFile(new FileInputStream(fromFile),
							new FileOutputStream(toFile));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// Copy Preference file to temp folder.
		private void copyPreferenceFile(File list[]) {
			for (int i = 0; i < list.length; i++) {
				/*
				 * File fromFile = new File("/data/data/" +
				 * mContext.getPackageName() + "/shared_prefs/" + list[i]);
				 */
				File fromFile = new File(list[i].getPath());
				File toFile = new File("/data/data/"
						+ mContext.getPackageName() + "/files/log/dump/"
						+ list[i].getName());
				try {
					copyFile(new FileInputStream(fromFile),
							new FileOutputStream(toFile));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		//Copy totals file for extension file name. (ex: ini, db, xml)
		private void copyExtensionFile(String extensionName){
			File[] fileList = getApplicationFile("/data/data/"
					+ mContext.getPackageName() + "/", extensionName);
			
			for(int i=0;i<fileAllList.size();i++){
				Log.d(TAG, "ArrayList file: " + fileAllList.get(i).getAbsolutePath());
			}
		}

		private void copyFile(FileInputStream fromFile, FileOutputStream toFile)
				throws IOException {
			FileChannel fromChannel = null;
			FileChannel toChannel = null;
			try {
				fromChannel = fromFile.getChannel();
				toChannel = toFile.getChannel();
				fromChannel.transferTo(0, fromChannel.size(), toChannel);
			} catch (Exception e) {
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
		
		public void copyToSD(){
			
		}

		// For test
		public void createDatabase() {
			this.getReadableDatabase();
		}

		// For test
		public void createPreference() {
			Editor editor = mContext.getSharedPreferences("test",
					Context.MODE_PRIVATE).edit();
			editor.putInt("blue", 0);
			editor.putInt("green", 1);
			editor.commit();

			Editor editor2 = mContext.getSharedPreferences("test2",
					Context.MODE_PRIVATE).edit();
			editor.putInt("XX", 0);
			editor.putInt("DD", 1);
			editor.commit();
		}

		public synchronized void close() {
			if (myDataBase != null) {
				myDataBase.close();
			}
			super.close();
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		}
	}
}
