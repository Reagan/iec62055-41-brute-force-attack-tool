package main.domain;

import main.exception.InvalidReplacementsException;

/**
 * Created by rmbitiru on 12/29/15.
 */
public class Replacement {

    private int position;
    private int replacement;
    protected int currReplacement ;

    public Replacement() {}

    public Replacement(int position, int replacement) {
            setPosition(position);
            setReplacement(replacement);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getReplacement() {
        return replacement;
    }

    public void setReplacement(int replacement) {
        this.replacement = replacement;
    }
}
