package geekomaniacs.smartfs.message;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import geekomaniacs.smartfs.FilePartRequestUDPMessage;
import geekomaniacs.smartfs.MainActivity;

/**
 * Created by aniket on 4/27/15.
 */
public class MessageResolver {
    public static UDPMessage createMessage(byte[] buf) {
        String string = null;
//        try {
            string = new String (buf);
//        } catch (UnsupportedEncodingException e) {
//            Log.v(MainActivity.TAG, "UDPCM:", e);
//        }
        int i = 0;
        TYPE type = null;

        for (String s : string.split(UDPMessage.SEPARATOR)) {
            if (i == 0) {
                type = TYPE.valueOf(TYPE.class, s);
            } else {
                return parse(type, string, buf);
            }
            i++;
        }
        return null;
    }

    private static UDPMessage parse (TYPE type, String string, byte[] buf) {
        if (type == TYPE.GET_FILE_DETAILS)
            return FileMetadataRequestUDPMessage.parse(string);
        else if (type == TYPE.FILE_DETAILS)
            return FileMetadataUDPMessage.parse (string);
        else if (type == TYPE.GET_FILE_PART)
            return FilePartRequestUDPMessage.parse (string);
        else if (type == TYPE.FILE_PART)
            return FilePartUDPMessage.parse (string, buf);
            return null;
    }
}
