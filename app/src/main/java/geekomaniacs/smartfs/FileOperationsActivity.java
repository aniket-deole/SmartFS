package geekomaniacs.smartfs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import geekomaniacs.smartfs.utility.Utility;


public class FileOperationsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_operations);

        Intent intent = getIntent();
        TextView fileNameText = (TextView) findViewById(R.id.fileName);
        TextView fileSizeText = (TextView) findViewById(R.id.fileSize);
        TextView dateModifiedText = (TextView) findViewById(R.id.dateModified);

        fileNameText.setText("File name: " + intent.getExtras().getString(Utility.FILENAME));
        fileSizeText.setText("File size: " + String.valueOf(intent.getExtras().getLong(Utility.FILESIZE) / 1000) + "kb");
        dateModifiedText.setText("Date modified: " + intent.getExtras().getString(Utility.DATEMODIFIED));

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
