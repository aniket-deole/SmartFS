package geekomaniacs.smartfs.database;

import android.provider.BaseColumns;

/**
 * Created by imbapumba on 4/24/15.
 */
public class TableData {

    public TableData(){
        //empty constructor to avoid erring instantiations
    }

    public static abstract class TableInformation implements BaseColumns{
        public static final String USER_IP = "user_ip";
        public static final String FILE_NAME = "file_name";
        public static final String DATABASE_NAME = "smartfs";
        public static final String SHARED_FILES = "shared_files";
        public static final String USERS = "users";
        public static final String FILES = "files";
        public static final String USER_EMAIL = "user_email";
        public static final String ROWID = "rowid _id";
        public static final String FILE_SIZE = "file_size";
        public static final String FILE_DATE_MODIFIED = "file_date_modified";
    }
}
