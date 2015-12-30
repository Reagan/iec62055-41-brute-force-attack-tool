package main.domain.mode;

/**
 * This class is used to perform brute force attacks
 * on token digits
 * Created by rmbitiru on 12/29/15.
 */
public class TokenAttackMode extends  AttackMode {

    public TokenAttackMode () {
        setAttackMode(Type.TOKEN);
    }

    public Type getAttackMode() {
        return attackMode ;
    }
}
