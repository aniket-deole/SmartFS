package geekomaniacs.smartfs.message;

/**
 * Created by aniket on 4/21/15.
 */
public class PayloadExceededException extends Exception {
    public PayloadExceededException(String message) {
        super(message);
    }
}
