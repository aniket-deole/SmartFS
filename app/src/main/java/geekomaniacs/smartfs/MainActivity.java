package geekomaniacs.smartfs;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import geekomaniacs.smartfs.beans.SmartFSFile;
import geekomaniacs.smartfs.utility.Utility;


public class MainActivity extends Activity {

    public static final String TAG = "SmartFS";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final Integer SERVER_PORT = 10000;
    private int myPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        mRecyclerView.addOnItemTouchListener(
//                new RecyclerView.OnItemTouchListener() {
//                    @Override
//                    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
//                        Log.v(TAG, "onInterceptTouchEvent");
//                        showPopUp(recyclerView);
//                        onTouchEvent(recyclerView, motionEvent);
//                        return true;
//                    }
//
//                    @Override
//                    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
//                        Log.v(TAG, "onTouchEvent");
//                        showPopUp(recyclerView);
//
//                    }
//                }
//        );

        // specify an adapter (see also next example)
        ArrayList<SmartFSFile> mDataset = Utility.getFileList();
//        ArrayList<SmartFSFile> mDataset = new ArrayList<SmartFSFile>();
//        mDataset.add(new SmartFSFile(new File("ABC")));
//        mDataset.add(new SmartFSFile(new File("BCD")));

        mAdapter = new MyAdapter(mDataset, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String sMyPort = String.valueOf((Integer.parseInt(portStr) * 2));
        myPort = Integer.parseInt(sMyPort);

        Toast.makeText(getApplicationContext(), "Hi" + myPort, Toast.LENGTH_SHORT).show();

        if (myPort == 11108) {
            new FileTransfer().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                   null );
        } else {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                Log.v (TAG, "ServerSocket error", e);
            }
            new ServerSocketListener().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    serverSocket);
        }

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

    public void showPopUp(View view){
        PopupMenu popUp = new PopupMenu(this, view);
        popUp.inflate(R.menu.pop_up_menu);
        popUp.show();
    }

    public class ServerSocketListener extends AsyncTask <ServerSocket, Void, Void> {

        @Override
        protected Void doInBackground(ServerSocket... params) {
            while (true) {
                try {
                    Log.v (TAG, "SSLDB: Waiting for socket");
                    Socket socket = params[0].accept();
                    File file = new File (Environment.getExternalStorageDirectory().toString()
                            + Utility.SMART_FS_DIRECTORY +
                            File.separator + "testReceived");
                    Log.v (TAG, "SSLDB: File Created");
                    byte [] mybytearray  = new byte [10485760];
                    InputStream is = socket.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int bytesRead = is.read(mybytearray,0,mybytearray.length);
                    int current = bytesRead;

                    do {
                        bytesRead =
                                is.read(mybytearray, current, (mybytearray.length-current));
                        if(bytesRead >= 0) current += bytesRead;
                        Log.v (TAG, "SSLDB: " + bytesRead + " bytes written");
                    } while(bytesRead > 0);

                    bos.write(mybytearray, 0 , current);
                    bos.flush();
                    Log.v (TAG, "SSLDB: File Write Done");

                    socket.close();
                    bos.close();

                } catch (IOException e) {
                    Log.e (TAG, "SSLDB:", e);
                }
            }
        }
    }

    public class FileTransfer extends AsyncTask <Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Log.v (TAG, "FTDIB: ", e);
            }

            File file = new File (Environment.getExternalStorageDirectory().toString()
                    + Utility.SMART_FS_DIRECTORY +
                    File.separator + "test");
            if (!file.exists()) {
                Log.v (TAG, "The file cannot be found.");
                return null;
            } else {
                Log.v (TAG, "The test file exists. Beginning to transfer file.");
            }

            try {
                Socket socket = new Socket (InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        11112);

                byte [] mybytearray  = new byte [(int)file.length()];
                BufferedInputStream bis = new BufferedInputStream(
                        new FileInputStream(file));
                int result = bis.read(mybytearray,0,mybytearray.length);
                Log.v (TAG, "FTDIB: Number of bytes read/shoudldRead:" + result + ":" +
                    file.length ());

                socket.getOutputStream().write(mybytearray,
                        0, mybytearray.length);
                socket.getOutputStream().flush();
                Log.v (TAG, "FTDIB: File Writing Done.");
                socket.close();

                bis.close();

                return null;

            } catch (IOException e) {
                Log.e (TAG, "FTDIB:", e);
            }

            return null;
        }
    }
}
