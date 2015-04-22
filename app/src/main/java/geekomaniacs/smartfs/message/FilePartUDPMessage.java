package geekomaniacs.smartfs.message;

import java.net.InetAddress;

/**
 * Created by aniket on 4/21/15.
 */
public class FilePartUDPMessage extends UDPMessage {
    char[] payload;

    public FilePartUDPMessage(Integer destinationPort, InetAddress destinationAddress) {
        super(destinationPort, destinationAddress);
    }

    @Override
    protected void setPayload(char[] payload) throws PayloadExceededException {
        this.payload = payload;
    }

    public void setPayload(int fileId, int partId, char[] payload) throws PayloadExceededException {
        char[] udpPayload = new char[4 + 4 + payload.length];
        setPayload(udpPayload);
    }

    @Override
    public void send() {
        // Should create a new socket and send the payload over.
    }
}
