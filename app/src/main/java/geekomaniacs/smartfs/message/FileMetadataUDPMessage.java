package geekomaniacs.smartfs.message;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import geekomaniacs.smartfs.MainActivity;

/**
 * Created by aniket on 4/26/15.
 */
public class FileMetadataUDPMessage extends UDPMessage implements Sendable {
    byte[] payload;

    public String fileName;
    public Long size;

    public FileMetadataUDPMessage(Integer destinationPort, InetAddress destinationAddress,
            String fileName, Long size) {
        super(TYPE.FILE_DETAILS, destinationPort, destinationAddress);
        this.fileName = fileName;
        this.size = size;
    }

    @Override
    public void setPayload(byte[] payload) throws PayloadExceededException {
        // DO NOT CALL THIS
        this.payload = payload;
    }

    @Override
    public void handle() {

    }

    public void setPayload(int fileId, int partId, char[] payload) throws PayloadExceededException {
        byte[] udpPayload = new byte[4 + 4 + payload.length];
        setPayload(udpPayload);
    }

    @Override
    public void send() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(payload, payload.length, destinationAddress,
                destinationPort);
        socket.send(packet);
        socket.close();
    }

    @Override
    public UDPMessage sendAndWait() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(payload, payload.length, destinationAddress,
                destinationPort);
        socket.send(packet);

        byte[] buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        Log.v(MainActivity.TAG, "Data Received:" + new String(buf));

        socket.close();
        return null;
    }

    @Override
    public String toString () {
        return type.toString() + UDPMessage.SEPARATOR + fileName + UDPMessage.SEPARATOR
                + size + UDPMessage.SEPARATOR;
    }

    public static UDPMessage parse(String string) {
        String[] strings = string.split(":");
        String s = null;
        Long size;
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                continue;
            } else if (i == 1) {
                s = strings[i];
            } else if (i == 2) {
                size = Long.valueOf(strings[i]);
                return new FileMetadataUDPMessage (null, null, s, size);
            } else {
                return null;
            }
        }
        return null;
    }
}
