package main.domain.mode;

/**
 * Created by rmbitiru on 12/29/15.
 */
public class BitAttackMode extends AttackMode {

    public BitAttackMode() {
        setAttackMode(Type.BIT);
    }

    public Type getAttackMode () {
        return attackMode ;
    }
}
