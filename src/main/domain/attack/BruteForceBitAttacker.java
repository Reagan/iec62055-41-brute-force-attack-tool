package main.domain.attack;

import main.domain.Replacement;
import main.domain.mode.AttackMode;
import main.domain.order.AttackOrder;
import main.timestamp.Tic;
import main.timestamp.Toc;

import java.math.BigInteger;

/**
 * This class is responsible for conducting brute force
 * attacks against the 66 bit string used to generate
 * the token
 * Created by rmbitiru on 12/29/15.
 */
public class BruteForceBitAttacker extends Attacker {

    private final BigInteger RANGE_START = new BigInteger("000000000000000000000000000000000000000000000000000000000000000000", 2);
    private final BigInteger RANGE_END = new BigInteger("111111111111111111111111111111111111111111111111111111111111111111", 2);

    public BruteForceBitAttacker (Replacement[] replacements, AttackMode attackMode,
                                  AttackOrder attackOrder, String fileoutputPath) {
        setReplacements(replacements);
        setFileOutputPath(fileoutputPath);
        setAttackMode(attackMode);
        setAttackOrder(attackOrder);
        setRangeStart(RANGE_START) ;
        setRangeEnd(RANGE_END) ;
    }
}
