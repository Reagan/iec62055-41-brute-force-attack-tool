package main.timestamp;

import org.joda.time.DateTime;

/**
 * Gets the difference between the current timestamp and the timestamp
 * created from the supplied tic
 * Created by rmbitiru on 12/29/15.
 */
public class Toc {

    private Tic startTime ;
    private DateTime currTime ;

    public Toc(Tic startTime) {
        setStartTime(startTime) ;
        setCurrentTime() ;
    }

    public Tic getStartTime () {
        return startTime ;
    }

    public void setStartTime(Tic startTime) {
        this.startTime = startTime ;
    }

    public long getTimeDiff () {
        return currTime.getMillis() - startTime.getTime() ;
    }

    private void setCurrentTime() {
        currTime = new DateTime();
    }
}
