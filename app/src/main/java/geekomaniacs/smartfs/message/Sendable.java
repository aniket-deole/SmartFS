package geekomaniacs.smartfs.message;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Created by aniket on 4/21/15.
 */
public interface Sendable {
    public abstract void send() throws IOException;
    public abstract UDPMessage sendAndWait () throws IOException;
}
