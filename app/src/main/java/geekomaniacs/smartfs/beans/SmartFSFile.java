package geekomaniacs.smartfs.beans;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by imbapumba on 4/20/15.
 */
public class SmartFSFile {
    private File file;
    private String metadata;

    public SmartFSFile(File file){
        this.file = file;
        //initialize metadata
    }

    public File getFile(){
        return this.file;
    }

    public String getMetadata(){
        return this.metadata;
    }

    public void setFile(File file){
        this.file = file;
    }

    public void setMetadata(String metadata){
        this.metadata = metadata;
    }
}
