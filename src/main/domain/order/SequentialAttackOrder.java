package domain.order;

/**
 * Created by rmbitiru on 12/29/15.
 */
public class SequentialAttackOrder extends AttackOrder {

    public SequentialAttackOrder () {
        setAttackOrder(attackOrder);
    }

    public Type getAttackOrder () {
        return attackOrder ;
    }
}
