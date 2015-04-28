package geekomaniacs.smartfs.message;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;

import geekomaniacs.smartfs.MainActivity;
import geekomaniacs.smartfs.beans.SmartFSFile;
import geekomaniacs.smartfs.utility.Utility;

/**
 * Created by aniket on 4/21/15.
 */
public class FilePartUDPMessage extends UDPMessage implements Sendable {
    public String fileName;
    public Integer partNumber;
    public Long fileSize;
    byte[] payload;
    byte[] buf;

    public FilePartUDPMessage(Integer destinationPort, InetAddress destinationAddress,
                              Integer partNumber, byte[] payload) {
        super(TYPE.FILE_PART, destinationPort, destinationAddress);
        this.buf = payload;
        this.partNumber = partNumber;
    }

    @Override
    public void setPayload(byte[] payload) throws PayloadExceededException {
        this.payload = payload;
    }

    @Override
    public void handle() throws PayloadExceededException, IOException {
        File file = new File(MainActivity.PATH + Utility.SMART_FS_DIRECTORY + File.separator + fileName);

        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(partNumber * SmartFSFile.BLOCK_SIZE);

        Log.v(MainActivity.TAG, "Data WRiting:" + partNumber + ":" +
                MainActivity.genHashWrapper(new String(buf, "UTF-8")));
        if (((partNumber * SmartFSFile.BLOCK_SIZE)  + SmartFSFile.BLOCK_SIZE)> fileSize) {
            byte[] bb;
            bb = Arrays.copyOfRange(buf, 0, (int)fileSize.intValue() % SmartFSFile.BLOCK_SIZE);
            raf.write (bb);
            Log.v (MainActivity.TAG, "Bytes Writtena: " + bb.length +":" +((partNumber * SmartFSFile.BLOCK_SIZE)  + SmartFSFile.BLOCK_SIZE)
                    + ":" + fileSize);
        }else {
            raf.write(buf);
            Log.v (MainActivity.TAG, "Bytes Writtenb: " + ":" + buf.length + ":" +((partNumber * SmartFSFile.BLOCK_SIZE)  + SmartFSFile.BLOCK_SIZE)
                    + ":" + fileSize);
        }
    }

    public void setFileSize (Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
//        try {
            return type.toString() + UDPMessage.SEPARATOR + partNumber + UDPMessage.SEPARATOR
                    ;
//        } catch (UnsupportedEncodingException e) {
//            Log.v(MainActivity.TAG, "FPUMTS:", e);
//            return null;
//        }
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
//        DatagramSocket socket = new DatagramSocket();
//        DatagramPacket packet = new DatagramPacket(payload, payload.length, destinationAddress,
//                destinationPort);
//        socket.send(packet);
//
//        byte[] buf = new byte[256];
//        packet = new DatagramPacket(buf, buf.length);
//        socket.receive(packet);
//
//        Log.v(MainActivity.TAG, "Data Received:" + new String(buf));
//
//        socket.close();
        return null;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static UDPMessage parse(String string, byte[] pl) {
        byte[] fullMessage = string.getBytes();
        byte[] firstPart = Arrays.copyOfRange(fullMessage, 0, 200);
        byte[] secondPart = new byte[0];
////        try {
//            secondPart = Arrays.copyOfRange(fullMessage, 200, 4096 + 200);
////            secondPart = Arrays.copyOfRange(new String(secondPart, "UTF-8").getBytes(), 0, 4096);
////        } catch (UnsupportedEncodingException e) {
////            e.printStackTrace();
////        }
        String[] strings = new String(firstPart).split(":");
        Integer partNumber = null;
        Log.v(MainActivity.TAG, "Total String parts: " + strings.length);
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                continue;
            } else if (i == 1) {
                partNumber = Integer.valueOf(strings[i]);
                break;
            }
        }
        return new FilePartUDPMessage(null, null, partNumber, Arrays.copyOfRange(pl, 200, 4296));

    }
}
