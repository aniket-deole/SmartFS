package geekomaniacs.smartfs.utility;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import geekomaniacs.smartfs.MainActivity;
import geekomaniacs.smartfs.beans.SmartFSFile;

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
    public static final String SHARED_FILE_LINK = "Please click the following link to add the file to SmartFS\nhttp://www.aniketdeole.in/file/";
    public static final String FORWARD_SLASH = "/";

    public static int position;

    public static ArrayList<SmartFSFile> getFileList() {
        ArrayList<SmartFSFile> smartFSFiles = new ArrayList<SmartFSFile>();
        String path = Environment.getExternalStorageDirectory().toString() + SMART_FS_DIRECTORY;
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
                smartFSFiles.add(new SmartFSFile(files[i]));
            } catch (FileNotFoundException e) {
                Log.e(MainActivity.TAG, "UTGFL:", e);
            }
        }
        return smartFSFiles;
    }
}
