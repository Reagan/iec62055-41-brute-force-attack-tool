package domain.attack;

import domain.Replacement;
import domain.mode.AttackMode;
import domain.order.AttackOrder;
import java.math.BigInteger;

/**
 * This class is responsible for conducting brute force
 * attacks against the 66 bit string used to generate
 * the token
 * Created by rmbitiru on 12/29/15.
 */
public class BruteForceBitAttacker extends Attacker {

    private final int RADIX = 2 ;
    private final BigInteger RANGE_START = new BigInteger("000000000000000000000000000000000000000000000000000000000000000000", RADIX);
    private final BigInteger RANGE_END = new BigInteger("111111111111111111111111111111111111111111111111111111111111111111", RADIX);

    public BruteForceBitAttacker (Replacement[] replacements, AttackMode attackMode,
                                  AttackOrder attackOrder, String fileoutputPath) {
        setReplacements(replacements);
        setLogFileOutputPath(fileoutputPath);
        setAttackMode(attackMode);
        setAttackOrder(attackOrder);
        setRangeStart(RANGE_START) ;
        setRangeEnd(RANGE_END) ;
        setRadix(RADIX);
    }
}
