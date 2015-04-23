package geekomaniacs.smartfs.beans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * Created by imbapumba on 4/20/15.
 */
public class SmartFSFile {
    private File file;
    private RandomAccessFile rafFile;
    private String metadata;

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

    public RandomAccessFile getRAFFile () { return this.rafFile; }
}
