package geekomaniacs.smartfs;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.RandomAccess;

import geekomaniacs.smartfs.beans.SmartFSFile;
import geekomaniacs.smartfs.message.FileMetadataUDPMessage;
import geekomaniacs.smartfs.message.FilePartUDPMessage;
import geekomaniacs.smartfs.message.MessageResolver;
import geekomaniacs.smartfs.message.PayloadExceededException;
import geekomaniacs.smartfs.message.TYPE;
import geekomaniacs.smartfs.message.UDPMessage;
import geekomaniacs.smartfs.utility.Utility;

/**
 * Created by aniket on 4/27/15.
 */
public class FilePartRequestUDPMessage extends UDPMessage {
    public String fileName;
    public Integer partNumber;
    byte[] payload;
    public FilePartRequestUDPMessage(Integer serverPort, InetAddress byName, String fileName,
                                     Integer partNumber) {
        super(TYPE.GET_FILE_PART, serverPort, byName);
        this.fileName = fileName;
        this.partNumber = partNumber;
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

        RandomAccessFile raf = new RandomAccessFile(file,"r");
        raf.seek(partNumber * SmartFSFile.BLOCK_SIZE);
        byte[] buf;
        if ((partNumber * SmartFSFile.BLOCK_SIZE) > file.length()) {
            buf = new byte[(int)file.length() % SmartFSFile.BLOCK_SIZE];
        } else {
            buf = new byte[SmartFSFile.BLOCK_SIZE];

        }
        raf.read(buf);
        UDPMessage message = new FilePartUDPMessage(destinationPort,
                destinationAddress, partNumber, buf);

        message.setPayload(message.toString().getBytes());

        message.send();

    }

    @Override
    public void send() throws IOException {

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

        Log.v(MainActivity.TAG, "Data Received:" + partNumber+ ":" +
                MainActivity.genHashWrapper(new String (buf, "UTF-8")));

        socket.close();

        return MessageResolver.createMessage(buf);

    }

    @Override
    public String toString() {
        return type.toString() + UDPMessage.SEPARATOR + fileName + UDPMessage.SEPARATOR +
                partNumber + UDPMessage.SEPARATOR;
    }

    public static UDPMessage parse(String string) {
        String[] strings = string.split(UDPMessage.SEPARATOR);
        String name = null;
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                continue;
            } else if (i == 1) {
                name = strings[i];
            } else if (i == 2) {
                return new FilePartRequestUDPMessage(null, null, name, Integer.valueOf(strings[i]));
            }
        }
        return null;
    }
}
