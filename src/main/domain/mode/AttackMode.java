package main.domain.mode;

/**
 * Created by rmbitiru on 12/29/15.
 */
public abstract class AttackMode {

    protected Type attackMode;

    public enum Type {
        TOKEN, BIT
    }

    public abstract Type getAttackMode()  ;

    protected void setAttackMode (Type attackMode) {
        this.attackMode = attackMode ;
    }
}
