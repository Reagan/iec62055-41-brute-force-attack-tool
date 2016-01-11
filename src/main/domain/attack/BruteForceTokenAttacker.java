package main.domain.attack;

import main.domain.Replacement;
import main.domain.mode.AttackMode;
import main.domain.order.AttackOrder;

import java.math.BigInteger;

/**
 * This class is responsible for conducting brute force attacks against
 * 20 digit tokens
 * Created by rmbitiru on 12/29/15.
 */
public class BruteForceTokenAttacker extends Attacker {

    private final BigInteger RANGE_START = new BigInteger("0", 10);
    private final BigInteger RANGE_END = new BigInteger("99999999999999999", 10);

    public BruteForceTokenAttacker(Replacement[] replacements, AttackMode attackMode,
                                   AttackOrder attackOrder, String fileoutputPath) {
        setReplacements(replacements);
        setFileOutputPath(fileoutputPath);
        setAttackMode (attackMode) ;
        setAttackOrder (attackOrder) ;
        setRangeStart(RANGE_START) ;
        setRangeEnd(RANGE_END) ;
    }
}
