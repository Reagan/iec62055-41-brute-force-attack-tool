package main.domain.attack;

import main.domain.Replacement;
import main.timestamp.Tic;
import main.timestamp.Toc;

import java.math.BigInteger;

/**
 * This class is responsible for conducting brute force attacks against
 * 20 digit tokens
 * Created by rmbitiru on 12/29/15.
 */
public class BruteForceTokenAttacker extends Attacker {

    private final BigInteger RANGE_START = new BigInteger("0", 10);
    private final BigInteger RANGE_END = new BigInteger("99999999999999999", 10);

    public BruteForceTokenAttacker(Replacement[] replacements, String fileoutputPath) {
        setReplacements(replacements);
        setFileOutputPath(fileoutputPath);
    }

    /**
     * This method tests a number of tokens in the allowable range
     * to try and obtain valid tokens for a predefined range
     * The replacement values are inserted into the token to narrow
     * the values over which tokens can be guessed. Values that have been
     * set as replacement values are not modified over the course of
     * the tests
     */
    public void launchAttack() {
        Tic allProcessingTic = new Tic() ;
        String currOutputFile = "" ;
        for (BigInteger bi = RANGE_START; bi.compareTo(RANGE_END) <= 0; bi = bi.add(BigInteger.ONE)) {
            if (replacementsUnchanged(bi, replacements)) { // make sure that replacements not varied
                Tic specificTokenProcessingTic = new Tic() ;
                TokenParameters tokenParameters = getTokenParameters(bi);
                Toc specificTokenProcessingToc = new Toc(specificTokenProcessingTic) ;
                Toc allTokenProcessingToc = new Toc(allProcessingTic) ;
                currOutputFile = formatTokenParams(attackVector,
                                                    generatedToken,
                                                    tokenParameters,
                                                    validTokenStatus,
                                                    attackMode,
                                                    attackOrder,
                                                    specificTokenProcessingToc,
                                                    allTokenProcessingToc);

                saveToFile(currOutputFile, fileOutputPath);
            }
            System.out.println(bi);
        }
    }

    /**
     * This method checks that the newly determined attack vector 'val'
     * does not create a token that defies the rules specifies
     * in the replacement values and positions
     *
     * @param val          attack vector
     * @param replacements replacement values and positions
     * @return whether the newly created token defies any of the
     * replacement rules and positions
     */
    private boolean replacementsUnchanged(BigInteger val, Replacement[] replacements) {
        for (int index = 0; index < replacements.length; index++) {
            Replacement currReplacement = replacements[index];
            if (currReplacement.getReplacement() != indexOf(val, currReplacement.getPosition()))
                return false;
        }
        return true;
    }

    /**
     * This method supplies the generated token to a meter
     * interface to determine if it is valid
     *
     * @param bi represents the 20 digit token supplied
     * @return if the token is valid based on the generated
     * values from the generated token parameters
     */
    public TokenParameters getTokenParameters(BigInteger bi) {
        Meter meter = new Meter();
        return meter.decodeMeter(bi.toString());
    }

    private int indexOf(BigInteger val, int index) {
        String bigIntegerStr = val.toString();
        return Integer.parseInt(bigIntegerStr.charAt(index) + "");
    }
}
