package geekomaniacs.smartfs.beans;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by imbapumba on 4/20/15.
 */
public class SmartFSFile {
    private File file;
    private RandomAccessFile rafFile;
    private String metadata;
    public static final Integer BLOCK_SIZE = 4096;
    private static final String TAG = "SmartFS";

    public SmartFSFile(File file) throws FileNotFoundException {
        // If synchronous required, check file modes from here:
        // http://docs.oracle.com/javase/7/docs/api/java/io/
        // RandomAccessFile.html#RandomAccessFile(java.io.File,%20java.lang.String)
        this.rafFile = new RandomAccessFile(file, "rw");
        this.file = file;
        //initialize metadata
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public byte[] getDataBlock (int block_number) {
        try {
            rafFile.seek(block_number * BLOCK_SIZE);
            byte[] buf = new byte[BLOCK_SIZE];
            int i = rafFile.read(buf);
            return buf;
        } catch (IOException e) {
            Log.e(TAG, "SFS_GDB: IOException", e);
            e.printStackTrace();
            return null;
        }
    }
}
