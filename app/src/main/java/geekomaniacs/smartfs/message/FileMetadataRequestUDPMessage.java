package geekomaniacs.smartfs.message;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import geekomaniacs.smartfs.MainActivity;
import geekomaniacs.smartfs.beans.SmartFSFile;
import geekomaniacs.smartfs.utility.Utility;

/**
 * Created by aniket on 4/27/15.
 */
public class FileMetadataRequestUDPMessage extends UDPMessage implements Sendable {

    String fileName;

    byte[] payload;

    public FileMetadataRequestUDPMessage(Integer destinationPort, InetAddress destinationAddress,
                                            String fileName) {
        super(TYPE.GET_FILE_DETAILS, destinationPort, destinationAddress);
        this.fileName = fileName;
    }

    @Override
    public void setPayload(byte[] payload) throws PayloadExceededException {
        this.payload = payload;
    }

    @Override
    public void handle() throws PayloadExceededException, IOException {
        // We open the file.
        // We get the the file details.
        // We return back.

        File file = new File(MainActivity.PATH + Utility.SMART_FS_DIRECTORY + File.separator + fileName);

        if (!file.exists())
            return;

        UDPMessage message = new FileMetadataUDPMessage(destinationPort,
                destinationAddress, fileName, file.length());

        message.setPayload(message.toString().getBytes());

        message.send();

    }

    @Override
    public void send() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(payload, payload.length,
                destinationAddress,
                destinationPort);
        socket.send(packet);
        socket.close();
    }

    @Override
    public String toString() {
        return type.toString() + UDPMessage.SEPARATOR + fileName + UDPMessage.SEPARATOR;
    }

    @Override
    public UDPMessage sendAndWait() throws IOException {

        String string = toString();

        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(string.getBytes("UTF-8"), string.getBytes().length,
                destinationAddress,
                destinationPort);
        socket.send(packet);

        byte[] buf = new byte[UDPMessage.MAX_BLOCK_SIZE];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        Log.v(MainActivity.TAG, "Data Received");

        socket.close();

        return MessageResolver.createMessage(buf);

    }

    public static UDPMessage parse(String string) {
        String[] strings = string.split(UDPMessage.SEPARATOR);
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                continue;
            } else if (i == 1) {
                return new FileMetadataRequestUDPMessage(null, null, strings[i]);
            }
        }
        return null;
    }
}
