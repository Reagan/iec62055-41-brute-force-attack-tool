package domain.order;

/**
 * Created by rmbitiru on 12/29/15.
 */
public class RandomAttackOrder extends AttackOrder {

    public RandomAttackOrder() {
        setAttackOrder(Type.RANDOM);
    }

    public Type getAttackOrder () {
        return attackOrder ;
    }
}
