package geekomaniacs.smartfs.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import geekomaniacs.smartfs.MainActivity;
import geekomaniacs.smartfs.beans.SmartFSFile;
import geekomaniacs.smartfs.database.DatabaseOperations;
import geekomaniacs.smartfs.database.TableData;

/**
 * Created by imbapumba on 4/20/15.
 */
public class Utility {

    public static final String SMART_FS_DIRECTORY = "/SmartFS";
    public static final String SELECT_ACTION = "Select an action";
    public static final String SHARE = "Share";
    public static final String DELETE = "Delete";
    public static final String FILE_NAME = "fileName";
    public static final String FILE_SIZE = "fileSize";
    public static final String DATE_MODIFIED = "dateModified";
    public static final String COMMA = ",";
    public static final String USERNAME = "smartfs2015@gmail.com";
    public static final String PASSWORD = "geekomaniacs";
    public static final String SUBJECT = "SmartFS: A new file has been shared with you:- ";
    public static final String BODY = "Hello, a user has shared a file with you\n";
    public static final String SHARED_FILE_LINK = "Please click the following link to add the file to SmartFS\nhttp://www.aniketdeole.in/";
    public static final String FORWARD_SLASH = "/";
    public static final String DOWNLOAD = "Download";
    public static final String SPACE = " ";
    public static final String PERCENT_COMPLETED = "% Completed";
    public static String path;
    public static int position;
    public static DatabaseOperations dbo;
    public static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


    public static ArrayList<SmartFSFile> getFileList() {
        ArrayList<SmartFSFile> smartFSFiles = new ArrayList<SmartFSFile>();
        path = Environment.getExternalStorageDirectory().toString() + SMART_FS_DIRECTORY;
        Log.d(MainActivity.TAG, "Path: " + path);
        File smartFSDirectory = new File(path);
        if (!smartFSDirectory.exists()) {
            if (!smartFSDirectory.mkdir()) {
                Log.e(MainActivity.TAG, "Cannot Create SmartFS Directory");
            }
        }
        File files[] = smartFSDirectory.listFiles();
        if (files == null)
            return smartFSFiles;
        Log.d(MainActivity.TAG, "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.d(MainActivity.TAG, "FileName: " + files[i].getName());
            try {
                SmartFSFile newFile = new SmartFSFile(files[i]);
                double actualSize = getActualFileSize(files[i].getName());
                double currentSize = newFile.getFile().length();
                Log.d("Actual Size", String.valueOf(actualSize));
                Log.d("Current Size", String.valueOf(currentSize));
                String owner = getOwner(files[i].getName());
                if(owner != null){
                    newFile.setOwner(owner);
                }
                if(actualSize == -1){
                    dbo.insertIntoFilesTable(dbo, files[i].getName(), String.valueOf(currentSize),
                            sdf.format(files[i].lastModified()));
                    newFile.setDownloadSize(100);
                }else if(actualSize == 0){
                    newFile.setDownloadSize(0);
                }else{
                    newFile.setDownloadSize((currentSize/actualSize) * 100);
                }
                smartFSFiles.add(newFile);
            } catch (FileNotFoundException e) {
                Log.e(MainActivity.TAG, "UTGFL:", e);
            }
        }
        return smartFSFiles;
    }

    public static void createDatabaseObject(Context context){
        dbo = new DatabaseOperations(context);
    }

    public static String getOwner(String fileName){
        SQLiteDatabase sqlDB = dbo.getReadableDatabase();
        String[] columns = {TableData.TableInformation.ROWID, TableData.TableInformation.USER_EMAIL};
        Cursor cursor = sqlDB.query(TableData.TableInformation.SHARED_FILES, columns,
                TableData.TableInformation.FILE_NAME + " = '" + fileName + "'", null, null, null, null);
        if(cursor.moveToFirst() || cursor.getCount() >= 1){
            Log.d("Owner", cursor.getString(1));
            return cursor.getString(1);
        }
        return null;

    }

    public static int getActualFileSize(String fileName){
        SQLiteDatabase sqlDB = dbo.getReadableDatabase();
        String[] columns = {TableData.TableInformation.ROWID, TableData.TableInformation.FILE_SIZE};
        Cursor cursor = sqlDB.query(TableData.TableInformation.FILES, columns,
                TableData.TableInformation.FILE_NAME + " = '" + fileName + "'", null, null, null, null);
        Log.d("Cursor", String.valueOf(cursor == null));
        if(cursor.moveToFirst() || cursor.getCount() >= 1){
            Log.d("Size", String.valueOf(cursor.getInt(1)));
            return cursor.getInt(1);
        }else{
            return -1;
        }
    }
}
