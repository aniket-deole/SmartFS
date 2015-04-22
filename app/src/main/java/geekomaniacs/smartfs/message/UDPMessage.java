package geekomaniacs.smartfs.message;

import java.net.InetAddress;

/**
 * Created by aniket on 4/21/15.
 */
public abstract class UDPMessage implements Sendable {
    public Integer destinationPort;
    public InetAddress destinationAddress;

    protected UDPMessage(Integer destinationPort,
                         InetAddress destinationAddress) {
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }

    protected abstract void setPayload(char[] payload) throws PayloadExceededException;
}
