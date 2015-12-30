package main.timestamp;

import org.joda.time.DateTime;

/**
 * Gets the current timestamp
 * Created by rmbitiru on 12/29/15.
 */
public class Tic {

    private DateTime currTime ;

    public Tic() {
        setTime() ;
    }

    public long getTime () {
        return currTime.getMillis() ;
    }

    private void setTime() {
        currTime = new DateTime();
    }
}
