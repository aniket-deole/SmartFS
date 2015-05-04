package geekomaniacs.smartfs.background;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import geekomaniacs.smartfs.CustomHttpClient;
import geekomaniacs.smartfs.FilePartRequestUDPMessage;
import geekomaniacs.smartfs.beans.SmartFSFile;
import geekomaniacs.smartfs.message.FileMetadataRequestUDPMessage;
import geekomaniacs.smartfs.message.FileMetadataUDPMessage;
import geekomaniacs.smartfs.message.FilePartUDPMessage;
import geekomaniacs.smartfs.message.MessageResolver;
import geekomaniacs.smartfs.message.PayloadExceededException;
import geekomaniacs.smartfs.message.UDPMessage;
import geekomaniacs.smartfs.utility.Utility;

/**
 * Created by aniket on 5/2/15.
 */
public class MessageTransferServer extends Service {

    public static final String TAG = "SmartFS";
    private static final Integer SERVER_PORT = 10003;
    private volatile boolean detailsReceived;
    String foreignIp;
    String foreignPort;
    String username;
    String fileName;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String process = intent.getExtras().getString("process");
        if (process == null) {
            username = intent.getExtras().getString("username");
            fileName = intent.getExtras().getString("filename");
            Log.d(username, fileName);
            new FileTransfer().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                    null);
        } else {
            new ServerSocketListener().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    1);
        }

        return 0;
    }

    @Override
    public void onCreate () {




    }

    @Override
    public void onDestroy () {

    }


    public class ServerSocketListener extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            DatagramSocket socket;
            try {
                socket = new DatagramSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            while (true) {
                try {
                    Log.v(TAG, "SSLDB: Waiting for socket");

                    byte[] buf = new byte[UDPMessage.MAX_BLOCK_SIZE];


                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    buf = packet.getData();

                    UDPMessage receivedMessage = MessageResolver.createMessage(buf);
                    receivedMessage.setDestination(packet.getAddress());
                    receivedMessage.setPort(packet.getPort());
                    try {
                        receivedMessage.handle();
                    } catch (PayloadExceededException e) {
                        Log.v(TAG, "SSLDIB:", e);
                    }

                } catch (IOException e) {
                    Log.e(TAG, "SSLDB:", e);
                    break;
                }
            }
            if (socket != null)
                socket.close();

            return null;
        }
    }

    public class FileTransfer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                detailsReceived = false;
                new GetIpAndPortTask ().executeOnExecutor (AsyncTask.THREAD_POOL_EXECUTOR, null);
                while (!detailsReceived) {};

                Log.v (TAG, "Now starting to receive file.");

                UDPMessage requestFileDetails = new FileMetadataRequestUDPMessage(Integer.parseInt(foreignPort),
                        InetAddress.getByName(foreignIp),
                        fileName
                );

                FileMetadataUDPMessage fileDetails = (FileMetadataUDPMessage) requestFileDetails.sendAndWait();
                Log.v (TAG, "File received:" + fileDetails.fileName + " of size " + fileDetails.size);

                long totalParts = (fileDetails.size / SmartFSFile.BLOCK_SIZE)  + 1;

                for (int i = 0; i < totalParts; i++) {
                    UDPMessage requestFilePart = new FilePartRequestUDPMessage(Integer.parseInt(foreignPort),
                            InetAddress.getByName(foreignIp), fileDetails.fileName, i);

                    Log.v (TAG, "Sending request for part: " + i);
                    FilePartUDPMessage filePart = (FilePartUDPMessage) requestFilePart.sendAndWait();
                    filePart.setFileName (fileDetails.fileName);
                    filePart.setFileSize (fileDetails.size);
                    try {
                        filePart.handle();
                        Log.v (TAG, "Handled part:" + i);
                    } catch (PayloadExceededException e) {
                        Log.e (TAG, "FTDIB:", e);
                    }
                }



            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    private class GetIpAndPortTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                ArrayList<NameValuePair> postParameters = new  ArrayList<>();
                String fEmail;
                if (username.equalsIgnoreCase("aniket.deole@gmail.com")) {
                    fEmail = "shwetajo@buffalo.edu";
                }else {
                    fEmail = "aniket.deole@gmail.com";
                }
                postParameters.add(new BasicNameValuePair("email", fEmail));
                String response = CustomHttpClient.executeHttpPost("http://aniketdeole.in/sfs_getipport.php",
                        postParameters);

                Log.v(TAG, "PORTIP recieved:" + response);

                String strings[] = response.split(":");
                foreignIp = strings[0].trim ();
                foreignPort = strings[1].trim ();

                detailsReceived = true;

            } catch (Exception e) {

            }

            return null;
        }
    }

}

