package main.domain.order;

/**
 * Created by rmbitiru on 12/29/15.
 */
public abstract class AttackOrder {

    protected Type attackOrder ;

    enum Type {
        RANDOM, SEQUENTIAL
    }

    public abstract Type getAttackOrder () ;

    protected void setAttackOrder (Type attackOrder) {
        this.attackOrder = attackOrder ;
    }
}
