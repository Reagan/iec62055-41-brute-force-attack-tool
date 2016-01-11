package main.domain.attack;

import edu.cmu.iec62055simulator.domain.TokenParameters;
import edu.cmu.iec62055simulator.pos.Meter;
import main.domain.Replacement;
import main.domain.mode.AttackMode;
import main.domain.mode.BitAttackMode;
import main.domain.mode.TokenAttackMode;
import main.domain.order.AttackOrder;
import main.domain.order.RandomAttackOrder;
import main.timestamp.Tic;
import main.timestamp.Toc;
import main.utils.Utils;

import java.math.BigInteger;

/**
 * Created by rmbitiru on 12/29/15.
 */
public abstract class Attacker {

    protected Replacement[] replacements ;
    protected String fileOutputPath ;
    private AttackMode attackMode ;
    private AttackOrder attackOrder ;
    private BigInteger rangeStart = new BigInteger("0", 10);
    private BigInteger rangeEnd = new BigInteger("0", 10);

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
        String currOutputFile = "" ; // output file to capture the log of the attack
        String attackVector = "" ; // bits or token digits used for the brute force attack
        String generatedToken = "" ; // token generated from the attack vector
        boolean validTokenStatus = true ; // specifies the results of validating the attack vector as valid
        TokenParameters generatedTokenParameters ;
        for (BigInteger bi = getRangeStart(); bi.compareTo(getRangeEnd()) <= 0; bi = bi.add(BigInteger.ONE)) {
            if (replacementsUnchanged(bi, replacements)) { // make sure that replacements are not varied
                attackVector = generatedToken = bi.toString() ;
                Tic specificTokenProcessingTic = new Tic() ;
                try {
                    generatedTokenParameters = getTokenParameters(attackVector);
                } catch (Exception e) {
                    validTokenStatus = false  ;
                    continue;
                }
                Toc specificTokenProcessingToc = new Toc(specificTokenProcessingTic) ;
                Toc allTokenProcessingToc = new Toc(allProcessingTic) ;
                currOutputFile = formatTokenParams(attackVector,
                        generatedToken,
                        generatedTokenParameters,
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


    public Replacement[] getReplacements() {
        return replacements;
    }

    public void setReplacements(Replacement[] replacements) {
        this.replacements = replacements;
    }

    public String getFileOutputPath() {
        return fileOutputPath;
    }

    public void setFileOutputPath(String fileOutputPath) {
        this.fileOutputPath = fileOutputPath;
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
     * This method formats the attack vector and parameter details
     * to a CSV string that can be serialized to a file
     *
     * <h4>CSV File output format</h4>
     * <pre>
     *     attack_vector,                               // the token or 66 bit APDU used for brute force attack
     *     generated token,                             // the generated token from the attack vector (useful when 66 bit APDU used)
     *     valid,                                       // flag indicating whether no token processing errors thrown
     *     valid_token_details,                         // the generated token details (class, subclass, TID, amount & CRC
     *     time_to_generate,                            // total time taken to process attack vector
     *     time_from_start_of_code_run,                 // specific time taken to decode 66 bit APDU or 20 digit token
     *     token_attack_mode,                           // flag specifying whether attack mode is token
     *     token_attack_order_random_or_sequential,     // flag specifying whether token attack mode's attack order is sequential or random
     *     token_attack_bits_known,                     // specific token digits known in attack vector
     *     bit_attack_mode,                             // flag specifying whether attack mode is bit
     *     bit_attack_sequential_random_token_blocks,   // flag specifying whether bit attack mode is sequential or random
     *     bit_attack_bits_or_tokens_known              // set of bits known in attack vector
     * </pre>
     *
     * @param tokenParameters parameters from decoded token
     * @param specificTokenProcessingToc Toc for processing specific token
     * @param allTokenProcessingToc Toc for processing all tokens
     * @return
     */
    public String formatTokenParams (String attackVector, String generatedToken,
                                      TokenParameters tokenParameters, boolean validTokenStatus,
                                      AttackMode attackMode, AttackOrder attackOrder,
                                      Toc specificTokenProcessingToc,
                                      Toc allTokenProcessingToc) {
        final String DELIMITER = "," ;
        final String EOL = "\n" ;
        return  attackVector + DELIMITER
                + generatedToken + DELIMITER
                + ((validTokenStatus) ? "T" : "F") + DELIMITER
                + tokenParameters.toString() + DELIMITER
                + specificTokenProcessingToc.getTimeDiff() + DELIMITER
                + allTokenProcessingToc.getTimeDiff() + DELIMITER
                + ((attackMode instanceof TokenAttackMode) ? "Y" : "") + DELIMITER
                + (((attackMode instanceof TokenAttackMode) && (attackOrder instanceof RandomAttackOrder)) ? "R" : "S") + DELIMITER
                + Utils.concat(getReplacements()) + DELIMITER
                + ((attackMode instanceof BitAttackMode) ? "Y" : "") + DELIMITER
                + (((attackMode instanceof BitAttackMode) && (attackOrder instanceof RandomAttackOrder))  ? "R" : "S") + DELIMITER
                + ((attackMode instanceof BitAttackMode) ? Utils.concat(getReplacements()) : "" ) + EOL ;
    }

    /**
     * This method supplies the generated token to a meter
     * interface to determine if it is valid
     *
     * @param _20DigitToken represents the 20 digit token supplied
     * @return if the token is valid based on the generated
     * values from the generated token parameters
     */
    public TokenParameters getTokenParameters(String _20DigitToken)
        throws Exception {
        Meter meter = new Meter(); // uses default location for Decoder key
        return meter.processToken(_20DigitToken);
    }

    /**
     * Saves line to file
     * @param line line of file to be written
     * @param outputFilePath path to output file
     */
    public void saveToFile(String line, String outputFilePath) {
        Utils.writeToFile(line, outputFilePath) ;
    }

    /**
     * Finds the location of a bit or digit in a 66 bit string
     * ot 20 digit token
     * @param haystack value from which to find distinct digit
     * @param position oosition at which to search for value in haysytack
     * @return whether the desired value was found in the entire string
     */
    private int indexOf(BigInteger haystack, int position) {
        String bigIntegerStr = haystack.toString();
        return Integer.parseInt(bigIntegerStr.charAt(position) + "");
    }

    /** Accessors for attack mode and order **/
    public AttackMode getAttackMode() {
        return attackMode;
    }

    public void setAttackMode(AttackMode attackMode) {
        this.attackMode = attackMode;
    }

    public AttackOrder getAttackOrder() {
        return attackOrder;
    }

    public void setAttackOrder(AttackOrder attackOrder) {
        this.attackOrder = attackOrder;
    }

    public BigInteger getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(BigInteger rangeStart) {
        this.rangeStart = rangeStart ;
    }

    public BigInteger getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(BigInteger rangeEnd) {
        this.rangeEnd = rangeEnd ;
    }
}
