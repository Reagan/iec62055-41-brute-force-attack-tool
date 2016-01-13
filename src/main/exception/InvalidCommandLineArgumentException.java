package exception;

/**
 * Created by rmbitiru on 12/29/15.
 */
public class InvalidCommandLineArgumentException extends Exception {

    public InvalidCommandLineArgumentException (String message) {
        super(message) ;
    }
}