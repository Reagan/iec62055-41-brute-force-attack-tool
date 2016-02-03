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
    private final BigInteger RANGE_END = new BigInteger("99999999999999999999", RADIX);

    public BruteForceTokenAttacker(String startAttackToken,
                                   String endAttackToken,
                                   Replacement[] replacements,
                                   AttackMode attackMode,
                                   AttackOrder attackOrder, String fileoutputPath,
                                   String decoderKeyPath) {
        setReplacements(replacements);
        setLogFileOutputPath(fileoutputPath);
        setAttackMode (attackMode) ;
        setAttackOrder (attackOrder) ;
        setRangeStart(getRangeStart(startAttackToken)) ;
        setRangeEnd(getRangeEnd(endAttackToken)) ;
        setRadix(RADIX);
        setDecoderKeyPath(decoderKeyPath);
    }

    private BigInteger getRangeStart (String startAttackToken) {
        if (startAttackToken.trim().length() == 20 &&
                startAttackToken.matches("^[0-9]{0,20}$"))
            return new BigInteger(startAttackToken, 10) ;
        else
            return RANGE_START ;
    }

    private BigInteger getRangeEnd (String endAttackToken) {
        if (endAttackToken.trim().length() == 20 &&
                endAttackToken.matches("^[0-9]{0,20}$"))
            return new BigInteger(endAttackToken, 10) ;
        else
            return RANGE_END ;
    }
}
