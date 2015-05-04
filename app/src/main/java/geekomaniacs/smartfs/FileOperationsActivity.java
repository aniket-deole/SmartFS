package geekomaniacs.smartfs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import geekomaniacs.smartfs.database.DatabaseOperations;
import geekomaniacs.smartfs.database.TableData;
import geekomaniacs.smartfs.mail.GmailSender;
import geekomaniacs.smartfs.utility.Utility;


public class FileOperationsActivity extends ActionBarActivity {

    TextView fileNameText;
    TextView fileSizeText;
    TextView dateModifiedText;
    EditText sharedToUsers;
    ListView listView;
    String fileName;
    String fileSize;
    String dateModified;
    Button share;
    Button delete;
    Button open;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_operations);
        context = this;

        Intent intent = getIntent();
        fileNameText = (TextView) findViewById(R.id.fileName);
        fileSizeText = (TextView) findViewById(R.id.fileSize);
        dateModifiedText = (TextView) findViewById(R.id.dateModified);
        listView = (ListView) findViewById(R.id.sharedWithUsers);
        sharedToUsers = (EditText) findViewById(R.id.user_email_id);
        share = (Button) findViewById(R.id.shareButton);
        delete = (Button) findViewById(R.id.deleteButton);
        open = (Button) findViewById(R.id.openButton);

        final DatabaseOperations dbo = Utility.dbo;

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] user_emails = sharedToUsers.getText().toString().split(",");
                if(dbo.insertIntoSharedUsersTable(dbo, fileName, user_emails) == 1) {
                    final GmailSender sender = new GmailSender(Utility.USERNAME, Utility.PASSWORD);
                    new AsyncTask<Void, Void, Void>() {
                        @Override public Void doInBackground(Void... arg) {
                            try {
                                sender.sendMail(Utility.SUBJECT + fileName, Utility.BODY +
                                        Utility.SHARED_FILE_LINK + MainActivity.username +
                                        Utility.FORWARD_SLASH + fileName + Utility.FORWARD_SLASH +
                                        fileSize + Utility.FORWARD_SLASH + dateModified, Utility.USERNAME,
                                        sharedToUsers.getText().toString());
                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                            }
                            return null;
                        }
                    }.execute();

                    }
                   Toast.makeText(context, "Shared with users successfully", Toast.LENGTH_LONG).show();
                }

        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] user_emails = sharedToUsers.getText().toString().split(",");
                if(dbo.deleteFromSharedUsersTables(dbo, fileName, user_emails) == 1)
                    Toast.makeText(context, "Unshared successfully", Toast.LENGTH_LONG).show();
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("file://" + Utility.path + Utility.FORWARD_SLASH + fileName);
                intent.setDataAndType(uri, "text/plain");
                startActivity(intent);
            }
        });

        setFieldValues(intent);
        dbo.insertIntoFilesTable(dbo, fileName, fileSize, dateModified);
        Cursor cr = dbo.getInformation(dbo, fileName);
        if(cr.getCount() != 0) {
            ListAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cr, new String[]{TableData.TableInformation.USER_EMAIL}, new int[]{android.R.id.text1});
            listView.setAdapter(adapter);
        }
    }

    /**
     * Set values for each field
     * @param intent
     */
    public void setFieldValues(Intent intent){
        fileName = intent.getExtras().getString(Utility.FILE_NAME);
        fileSize = String.valueOf(intent.getExtras().getLong(Utility.FILE_SIZE));
        dateModified = intent.getExtras().getString(Utility.DATE_MODIFIED);
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
