package geekomaniacs.smartfs;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import geekomaniacs.smartfs.database.DatabaseOperations;
import geekomaniacs.smartfs.database.TableData;
import geekomaniacs.smartfs.utility.Utility;


public class FileOperationsActivity extends ActionBarActivity {

    TextView fileNameText;
    TextView fileSizeText;
    TextView dateModifiedText;
    ListView listView;
    String fileName;
    String fileSize;
    String dateModified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_operations);

        Intent intent = getIntent();
        fileNameText = (TextView) findViewById(R.id.fileName);
        fileSizeText = (TextView) findViewById(R.id.fileSize);
        dateModifiedText = (TextView) findViewById(R.id.dateModified);
        listView = (ListView) findViewById(R.id.sharedWithUsers);
        setFieldValues(intent);

        DatabaseOperations dbo = new DatabaseOperations(this);
//        dbo.insertIntoTable(dbo, fileName, "2");
        dbo.insertIntoFilesTable(dbo, fileName, fileSize, dateModified);
        Cursor cr = dbo.getInformation(dbo, fileName);
        if(cr.getCount() != 0) {
            cr.moveToFirst();
            do {
                Log.d("Column", cr.getString(0));
            } while (cr.moveToNext());
            cr.moveToFirst();

            ListAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cr, new String[]{TableData.TableInformation.USER_EMAIL}, new int[]{android.R.id.text1});
            listView.setAdapter(adapter);
        }
    }

    /**
     * Set values for each field
     * @param intent
     */
    public void setFieldValues(Intent intent){
        fileName = intent.getExtras().getString(Utility.FILENAME);
        fileSize = String.valueOf(intent.getExtras().getLong(Utility.FILESIZE) / 1000);
        dateModified = intent.getExtras().getString(Utility.DATEMODIFIED);
        fileNameText.setText("File name: " + fileName);
        fileSizeText.setText("File size: " + fileSize + "kb");
        dateModifiedText.setText("Date modified: " + dateModified);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_operations, menu);
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
