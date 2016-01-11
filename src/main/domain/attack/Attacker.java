package main.domain.attack;

import main.domain.Replacement;
import main.domain.mode.AttackMode;
import main.domain.mode.BitAttackMode;
import main.domain.mode.TokenAttackMode;
import main.domain.order.AttackOrder;
import main.domain.order.RandomAttackOrder;
import main.timestamp.Toc;
import main.utils.Utils;

/**
 * Created by rmbitiru on 12/29/15.
 */
public abstract class Attacker {

    protected Replacement[] replacements ;
    protected String fileOutputPath ;

    public abstract void launchAttack() ;

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
        return  attackVector + DELIMITER
                + generatedToken + DELIMITER
                + ((validTokenStatus) ? "T" : "F") + DELIMITER
                + formattedResultString += tokenParameters.toString() + DELIMITER
                + specificTokenProcessingToc.getTimeDiff() + DELIMITER
                + allTokenProcessingToc.getTimeDiff() + DELIMITER
                + ((attackMode instanceof TokenAttackMode) ? "Y" : "") + DELIMITER
                + (((attackMode instanceof TokenAttackMode) && (attackOrder instanceof RandomAttackOrder)) ? "R" : "S") + DELIMITER
                + Utils.concat(getReplacements()) + DELIMITER
                + ((attackMode instanceof BitAttackMode) ? "Y" : "") + DELIMITER
                + (((attackMode instanceof BitAttackMode) && (attackOrder instanceof RandomAttackOrder))  ? "R" : "S") + DELIMITER
                + ((attackMode instanceof BitAttackMode) ? Utils.concat(getReplacements()) : "" ) + DELIMITER ;
    }

    /**
     * Saves line to file
     * @param line line of file to be written
     * @param outputFilePath path to output file
     */
    public void saveToFile(String line, String outputFilePath) {
        Utils.writeToFile(line, outputFilePath) ;
    }

}
