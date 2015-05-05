package geekomaniacs.smartfs;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import geekomaniacs.smartfs.database.DatabaseOperations;
import geekomaniacs.smartfs.utility.Utility;


public class NewFileSharedActivity extends ActionBarActivity {

    Uri data;
    String[] sharedBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_file_shared);
        Utility.createDatabaseObject(this);
        Intent intent = getIntent();
        data = intent.getData();
        final DatabaseOperations dbo = new DatabaseOperations(this);
        String toDecode = data.getPath().substring(data.getPath().lastIndexOf(Utility.FORWARD_SLASH) + 1);
        String temp = new String(Utility.decode(toDecode));
        String[] sharedBy = new String[1];
        String path[] = temp.split(Utility.FORWARD_SLASH);
        sharedBy[0] = path[0];
        String fileName = path[1];
        String fileSize = path[2];
        String dateModified = path[3] + "-" + path[4] + "-" + path[5];
        Log.d("Date", dateModified);
        dbo.insertIntoSharedUsersTable(dbo, fileName, sharedBy);
        TextView sharedByText = (TextView) findViewById(R.id.shared_by);
        sharedByText.setText("The user: " + sharedBy[0] + " has shared file: " + fileName + " with you.");
        File file = new File(Environment.getExternalStorageDirectory().toString() +
                Utility.SMART_FS_DIRECTORY + Utility.FORWARD_SLASH + fileName);
        Utility.dbo.insertIntoFilesTable(Utility.dbo, fileName, fileSize, dateModified);
        try {
            if(file.createNewFile()){
                Log.d("Created", "file");
            }
            else
                Log.d("Not created", "file");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_file_shared, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
