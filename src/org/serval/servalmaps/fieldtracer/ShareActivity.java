package org.serval.servalmaps.fieldtracer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.Vector;

import org.serval.servalmaps.fieldtracer.utils.Rhizome;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;



public class ShareActivity extends Activity {

//In an Activity
private String[] mFileList;
private File appPath = new File(Environment.getExternalStorageDirectory(),SettingsActivity.APP_NAME_PATH );
private String mChosenFile;
private static final String FTYPE = ""; //if we want an extension filter
private static final int DIALOG_LOAD_FILE = 1000;	

private TextView fileChoosed = null;  
private static final String TAG = "Debug";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		// Show the Up button in the action bar.
		setupActionBar();
		fileChoosed = (TextView) findViewById(R.id.textView1);   
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void loadFileList() {
	    try {
	        appPath.mkdirs();
	    }
	    catch(SecurityException e) {
	        Log.e(TAG, "unable to write on the sd card " + e.toString());
	    }
	    if(appPath.exists()) {
	        FilenameFilter filter = new FilenameFilter() {
	            public boolean accept(File dir, String filename) {
	                File sel = new File(dir, filename);
	                return filename.contains(FTYPE) || sel.isDirectory();
	            }
	        };
	        mFileList = appPath.list(filter);
	    }
	    else {
	        mFileList= new String[0];
	    }
	}

	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    AlertDialog.Builder builder = new Builder(this);

	    switch(id) {
	        case DIALOG_LOAD_FILE:
	            builder.setTitle("Choose your file");
	            if(mFileList == null) {
	                Log.e(TAG, "Showing file picker before loading the file list");
	                dialog = builder.create();
	                return dialog;
	            }
	            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    mChosenFile = mFileList[which];
	                    //you can do stuff with the file here too
	                    fileChoosed.setText(appPath + "/" + mChosenFile);   
	                }
	            });
	            break;
	    }
	    dialog = builder.show();
	    return dialog;
	}
	
	public void ButtonOnSelectFile(View v){
		// create alert dialog
		loadFileList();
		this.onCreateDialog(DIALOG_LOAD_FILE);
	}

	public void ButtonOnShareFile(View v){
		Rhizome.addFile(getBaseContext(),appPath + "/" + mChosenFile);
		 Builder dialog= new AlertDialog.Builder(this);
         dialog.setMessage("File added to Rhizome store");
         dialog.setPositiveButton("OK", null);
         dialog.show();
    }

	private Vector<File> findFile(String extension) {
		
		File[] entries = appPath.listFiles();
		Vector<File> selected = new Vector<File>();
		
        for (int i=0; i<entries.length;i++){
        	if (!entries[i].toString().contains((".manifest"))){        		        	
	        	if (entries[i].toString().endsWith(extension)){
	        		selected.add(entries[i]);
	        	}
        	}
        }		
		return selected;
	}
	
	public void ButtonOnShareAll(View v){	
		Vector<File> file_to_be_shared = new Vector<File>();
		file_to_be_shared.addAll(findFile(".poi"));
		file_to_be_shared.addAll(findFile(".trace"));
		file_to_be_shared.addAll(findFile(".gpx"));
		Iterator itr = file_to_be_shared.iterator();
		
		 while(itr.hasNext()){
			 File elem = (File)itr.next();
			 Rhizome.addFile(getBaseContext(),elem.toString());
			 Log.v(TAG, "File added to Rhizome Store :" + elem.toString());
		 }
						
		Builder dialog= new AlertDialog.Builder(this);
        dialog.setMessage("Files added to Rhizome store");
        dialog.setPositiveButton("OK", null);
        dialog.show();        
	}
}
