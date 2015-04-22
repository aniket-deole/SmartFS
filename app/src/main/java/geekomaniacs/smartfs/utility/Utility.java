package geekomaniacs.smartfs.utility;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import geekomaniacs.smartfs.MainActivity;
import geekomaniacs.smartfs.beans.SmartFSFile;

/**
 * Created by imbapumba on 4/20/15.
 */
public class Utility {

    public static final String SMART_FS_DIRECTORY = "/SmartFS";

    public static ArrayList<SmartFSFile> getFileList(){
        ArrayList<SmartFSFile> smartFSFiles = new ArrayList<SmartFSFile>();
        String path = Environment.getExternalStorageDirectory().toString() + SMART_FS_DIRECTORY;
        Log.d(MainActivity.TAG, "Path: " + path);
        File smartFSDirectory = new File(path);
        if(!smartFSDirectory.exists()) {
            if (!smartFSDirectory.mkdir()) {
                Log.d (MainActivity.TAG, "Cannot Create SmartFS Directory");
            }
        }
        File files[] = smartFSDirectory.listFiles();
        if (files == null)
            return smartFSFiles;
        Log.d(MainActivity.TAG, "Size: " + files.length);
        for(int i=0; i <files.length; i++){
            Log.d("Files", "FileName: "+files[i].getName());
            smartFSFiles.add(new SmartFSFile(files[i]));
        }
        return smartFSFiles;
    }
}
