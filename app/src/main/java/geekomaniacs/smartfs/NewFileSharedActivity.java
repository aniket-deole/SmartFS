package geekomaniacs.smartfs;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import geekomaniacs.smartfs.database.DatabaseOperations;
import geekomaniacs.smartfs.utility.Utility;


public class NewFileSharedActivity extends ActionBarActivity {

    Uri data;
    String[] sharedBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_file_shared);
        Intent intent = getIntent();
        data = intent.getData();
        final DatabaseOperations dbo = new DatabaseOperations(this);
        String[] path = data.getPath().split(Utility.FORWARD_SLASH);
        String[] sharedBy = new String[1];
        sharedBy[0] = path[1];
        String fileName = path[2];
        dbo.insertIntoSharedUsersTable(dbo, fileName, sharedBy);
        TextView sharedByText = (TextView) findViewById(R.id.shared_by);
        sharedByText.setText("The user: " + sharedBy[0] + " has shared file: " + fileName + " with you.");
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
