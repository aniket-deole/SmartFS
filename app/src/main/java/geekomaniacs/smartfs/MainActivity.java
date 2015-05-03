package geekomaniacs.smartfs;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import org.apache.http.NameValuePair;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import geekomaniacs.smartfs.adapters.MyAdapter;
import geekomaniacs.smartfs.background.MessageTransferServer;
import geekomaniacs.smartfs.beans.SmartFSFile;
import geekomaniacs.smartfs.message.FileMetadataRequestUDPMessage;
import geekomaniacs.smartfs.message.FileMetadataUDPMessage;
import geekomaniacs.smartfs.message.FilePartUDPMessage;
import geekomaniacs.smartfs.message.MessageResolver;
import geekomaniacs.smartfs.message.PayloadExceededException;
import geekomaniacs.smartfs.message.UDPMessage;
import geekomaniacs.smartfs.utility.Utility;


public class MainActivity extends Activity {
    public static final String IP1 = "192.168.1.18";
    public static final String IP2 = "192.168.1.10";
    public static final String TAG = "SmartFS";
    private static final Integer SERVER_PORT = 10003;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static String PATH;
    private Integer messageCounter = 0;
    private int myPort;
    ArrayList<SmartFSFile> mDataset;
    public static String username;

    String myIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        username = intent.getExtras().getString("username");
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

/*        mRecyclerView.addOnItemTouchListener(
                new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                        Log.v(TAG, "onInterceptTouchEvent");
                        showPopUp(recyclerView);
                        onTouchEvent(recyclerView, motionEvent);
                        return true;
                    }

                    @Override
                    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                        Log.v(TAG, "onTouchEvent");
                        showPopUp(recyclerView);

                    }
                }
        );*/

        // specify an adapter (see also next example)
//        ArrayList<SmartFSFile> mDataset = Utility.getFileList();
        mDataset = new ArrayList<SmartFSFile>();
        try{
            mDataset.add(new SmartFSFile(new File("ABC")));
            mDataset.add(new SmartFSFile(new File("BCD")));
        }catch(FileNotFoundException e){
            Log.e("No file","No file");
        }

        mAdapter = new MyAdapter(mDataset, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        myIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        PATH = Environment.getExternalStorageDirectory().toString();


        new RegisterTask ().executeOnExecutor (AsyncTask.SERIAL_EXECUTOR, null);

        Intent i = new Intent(getApplicationContext(), MessageTransferServer.class);
        i.putExtra("process", "listen");
        getApplicationContext().startService(i);

        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MessageTransferServer.class);
                i.putExtra("username", username);
                getApplicationContext().startService(i);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void showPopUp(View view) {
        PopupMenu popUp = new PopupMenu(this, view);
        popUp.inflate(R.menu.pop_up_menu);
        popUp.show();
    }


    public static String genHashWrapper(String input) {
        String hashed = "";
        try {
            hashed = genHash(input);
        } catch (NoSuchAlgorithmException e) {
        }
        return hashed;
    }

    private static String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        java.util.Formatter formatter = new java.util.Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getItemId() == R.id.share){
            Log.d(MainActivity.TAG, mDataset.get(Utility.position).getFile().getName());
        }
        return true;
    }

    private class RegisterTask extends AsyncTask <Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                ArrayList<NameValuePair> postParameters = new  ArrayList<>();
                postParameters.add(new BasicNameValuePair("email", username));
                postParameters.add(new BasicNameValuePair("ip", myIp));
                postParameters.add(new BasicNameValuePair("port", SERVER_PORT.toString()));
                String response = CustomHttpClient.executeHttpPost("http://aniketdeole.in/sfs_register.php",
                        postParameters);

                Log.v (TAG, "Registered:" + response);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
