package domain.attack;

import domain.Replacement;
import domain.mode.AttackMode;
import domain.order.AttackOrder;

import java.math.BigInteger;

/**
 * This class is responsible for conducting brute force attacks against
 * 20 digit tokens
 * Created by rmbitiru on 12/29/15.
 */
public class BruteForceTokenAttacker extends Attacker {

    private final int RADIX = 10 ;
    private final BigInteger RANGE_START = new BigInteger("10000000000000000000",  RADIX);
    // private final BigInteger RANGE_START = new BigInteger("10000000000001278799",  RADIX);
    private final BigInteger RANGE_END = new BigInteger("99999999999999999999", RADIX);

    public BruteForceTokenAttacker(Replacement[] replacements, AttackMode attackMode,
                                   AttackOrder attackOrder, String fileoutputPath) {
        setReplacements(replacements);
        setLogFileOutputPath(fileoutputPath);
        setAttackMode (attackMode) ;
        setAttackOrder (attackOrder) ;
        setRangeStart(RANGE_START) ;
        setRangeEnd(RANGE_END) ;
        setRadix(RADIX);
    }
}
