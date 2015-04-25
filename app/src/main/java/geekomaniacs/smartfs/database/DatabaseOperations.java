package geekomaniacs.smartfs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import geekomaniacs.smartfs.utility.Utility;

/**
 * Created by imbapumba on 4/24/15.
 */
public class DatabaseOperations extends SQLiteOpenHelper{
    public static final int DB_VERSION = 1;
    public static final String CREATE_TABLE_FILES = "CREATE TABLE IF NOT EXISTS " +
            TableData.TableInformation.FILES + "(" + TableData.TableInformation.FILE_NAME +
            " TEXT PRIMARY KEY" + Utility.COMMA + TableData.TableInformation.FILE_SIZE + " TEXT"+
            Utility.COMMA + TableData.TableInformation.FILE_DATE_MODIFIED +" TEXT);";

    public static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " +
            TableData.TableInformation.USERS + "(" + TableData.TableInformation.USER_EMAIL +
            " TEXT PRIMARY KEY" + Utility.COMMA + TableData.TableInformation.USER_IP + " TEXT);";

    public static final String CREATE_TABLE_SHARED_FILES = "CREATE TABLE " +
            TableData.TableInformation.SHARED_FILES + "(" + TableData.TableInformation.FILE_NAME +
            " TEXT" + Utility.COMMA + TableData.TableInformation.USER_EMAIL + " TEXT" +
            Utility.COMMA + "FOREIGN KEY(" + TableData.TableInformation.FILE_NAME + ") REFERENCES "
            + TableData.TableInformation.FILES + "(" + TableData.TableInformation.FILE_NAME +
            ") ON DELETE CASCADE" + Utility.COMMA + "FOREIGN KEY(" +
            TableData.TableInformation.USER_EMAIL + ") REFERENCES " +
            TableData.TableInformation.USERS + "(" + TableData.TableInformation.USER_EMAIL +
            ") ON DELETE CASCADE);";


    public DatabaseOperations(Context context){
        super(context, TableData.TableInformation.DATABASE_NAME, null, DB_VERSION);
        System.out.println(CREATE_TABLE_SHARED_FILES);
        Log.d("Data", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FILES);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_SHARED_FILES);
        Log.d("Table", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Insert a row in the shared_files table
     * @param dbo
     * @param fileName
     * @param userEmail
     */
    public void insertIntoTable(DatabaseOperations dbo, String fileName, String userEmail){
        SQLiteDatabase sqlDB = dbo.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableData.TableInformation.FILE_NAME, fileName);
        values.put(TableData.TableInformation.USER_EMAIL, userEmail);
        long status = sqlDB.insert(TableData.TableInformation.SHARED_FILES, null, values);
        Log.d("Insert", String.valueOf(status));
    }

    public void insertIntoFilesTable(DatabaseOperations dbo, String fileName, String fileSize,
                                      String dateModified){
        SQLiteDatabase sqlDB = dbo.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableData.TableInformation.FILE_NAME, fileName);
        values.put(TableData.TableInformation.FILE_SIZE, fileSize);
        values.put(TableData.TableInformation.FILE_DATE_MODIFIED, dateModified);
        long status = sqlDB.insert(TableData.TableInformation.FILES, null, values);
    }

    /**
     * Return list of users with which the file is shared
     * @param dbo
     * @param fileName
     * @return
     */
    public Cursor getInformation(DatabaseOperations dbo, String fileName){
        SQLiteDatabase sqlDB = dbo.getReadableDatabase();
        String[] columns = {TableData.TableInformation.ROWID, TableData.TableInformation.USER_EMAIL};
        Cursor cursor = sqlDB.query(TableData.TableInformation.SHARED_FILES, columns,
                TableData.TableInformation.FILE_NAME + "= '"+ fileName +"'", null, null, null, null);
        Log.d("Cursor", String.valueOf(cursor == null));
        return cursor;
    }


}
