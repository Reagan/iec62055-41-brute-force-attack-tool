package main.domain.attack;

import main.domain.Replacement;
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

    public BruteForceBitAttacker (Replacement[] replacements, String fileoutputPath) {
        setReplacements(replacements);
        setFileOutputPath(fileoutputPath);
    }

    /**
     * This method tests all possible bit combinations comprising the
     * 66 bit string used to generate a token while making sure
     * that the defined replacement bits are unchanged
     */
    public void launchAttack () {
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
     * This method checks that the created bitstring does not violate the
     * set of replacement rules specified
     * @param currValue curr big integer value
     * @param replacements set of replacements that should be modified in created bit string
     * @return whether the current value violates the replacement rules specified
     */
    private boolean replacementsUnchanged (BigInteger currValue, Replacement[] replacements) {
        String currValueBitString = currValue.toString(2);
        String bitString = "000000000000000000000000000000000000000000000000000000000000000000".substring(currValueBitString.length()) + currValueBitString ;
        for (int index = 0; index < replacements.length; index++) {
            Replacement currReplacement = replacements[index];
            if (currReplacement.getReplacement() != bitString.indexOf(currReplacement.getPosition()))
                return false;
        }
        return true;
    }
}
