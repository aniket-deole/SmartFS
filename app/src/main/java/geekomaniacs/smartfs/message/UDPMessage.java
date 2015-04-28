package geekomaniacs.smartfs.message;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by aniket on 4/21/15.
 */
public abstract class UDPMessage implements Sendable {
    public static final java.lang.String SEPARATOR = ":";
    public Integer destinationPort;
    public InetAddress destinationAddress;
    public static Integer MAX_BLOCK_SIZE = 5120;

    public TYPE type;

    protected UDPMessage(TYPE type, Integer destinationPort,
                         InetAddress destinationAddress) {
        this.type = type;
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }

    public abstract void setPayload(byte[] payload) throws PayloadExceededException;

    public abstract void handle() throws PayloadExceededException, IOException;

    public void setDestination(InetAddress address) {this.destinationAddress = address;}

    public void setPort(int port) {
        this.destinationPort = port;
    }

}
