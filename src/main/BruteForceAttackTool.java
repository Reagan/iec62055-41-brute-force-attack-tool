package main;

import com.outstare.arg.processor.adapter.HyphenatedCharacterArgsAdapter;
import main.domain.Replacement;
import main.domain.attack.BruteForceBitAttacker;
import main.domain.attack.BruteForceTokenAttacker;
import main.domain.mode.AttackMode;
import main.domain.mode.BitAttackMode;
import main.domain.mode.TokenAttackMode;
import main.domain.order.AttackOrder;
import main.domain.order.RandomAttackOrder;
import main.domain.order.SequentialAttackOrder;
import main.exception.InconsistentReplacementsException;
import main.exception.InvalidAttackModeException;
import main.exception.InvalidAttackOrderException;
import main.exception.InvalidCommandLineArgumentException;
import main.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This program is used to generate brute force attacks
 * using an IEC62055-41:2014 compliant token generator and
 * token decoder to try and guess valid 20 digit tokens
 * an/or bit strings
 * Created by rmbitiru on 12/28/15.
 */
public class BruteForceAttackTool {

    private AttackMode attackMode ;
    private AttackOrder attackOrder ;
    private Replacement[] replacements ; // contains the replacement positions & values
    private String outputFilePath ;
    private HyphenatedCharacterArgsAdapter hyphenatedCharacterArgsAdapter;
    private String[] supportedCommandLineArgsFlags = {"m", "o", "r", "v", "f"};
    private String[] supportedCommandLineDefaultArgs = {"token", "sequential", "", "", "output.csv"};

    private final Logger logger = LogManager.getFormatterLogger("BruteForceAttackTool");

    private static final String VERSION_NO = "0.1";
    private static final String VERSION_STRING = "iec 62055-41 brute force attack tool version " + VERSION_NO ;
    private static final String AUTHOR = "Reagan Mbitiru <rmugo@andrew.cmu.edu>";

    /**
     * This method is responsible for launching the brute force attack tool.
     * @param args
     * -m <AttackMode> [token | bit] : specifies the brute force attack AttackMode. If 'token' attack
     *                           AttackMode is selected, then 20 digit tokens are attacked. If
     *                           'bit' attack AttackMode is selected, then the 66 bits are
     *                           attacked.
     *
     * -o <order> [random | sequential] : 1. 'random' <order> && 'token' <AttackMode>
     *                                       random values of the 20 digit
     *                                       token are generated for the brute force attack
     *                                    2. 'sequential' <order> && 'token' <AttackMode>
     *                                       sequential values of a token from the starting
     *                                       index are selected and used to generate the
     *                                       attack vector
     *                                    3. 'random' <order> and 'bit' <AttackMode>
     *                                       random bits are selected and used to create the
     *                                       attack vector
     *                                    4. 'sequential' <order> and 'bit' <AttackMode>
     *                                        a sequential number of bits is selected
     *                                        and a random value assigned to create an attack
     *                                        vector
     * -r <replacement_positions>   :   specifies the positions of tokens or bits at which values should be
     *                                  replaced
     * -v <replacement_values>      :   specifies the replacement values for bits and positions specified
     *                                  in the -r replacement flag positions. The number of values in the
     *                                  replacement positions and replacement values must be the same
     * -f <output_file>             :   specifies the output file to post the results
     *
     */
    public static void main(String[] args) {
        try {
            BruteForceAttackTool bruteForceAttackTool = new BruteForceAttackTool();
            bruteForceAttackTool.displayMeterInitializationMessage();
            bruteForceAttackTool.initializeCommandLineArgs(args);
            bruteForceAttackTool.launchAttack() ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayMeterInitializationMessage() {
        logger.info(VERSION_STRING);
        logger.info(AUTHOR);
    }


    private void initializeCommandLineArgs(String[] args)
            throws InvalidCommandLineArgumentException, InvalidAttackModeException,
                   InvalidAttackOrderException, InconsistentReplacementsException {
        hyphenatedCharacterArgsAdapter = new HyphenatedCharacterArgsAdapter(supportedCommandLineArgsFlags,
                supportedCommandLineDefaultArgs);
        hyphenatedCharacterArgsAdapter.extractArgs(Utils.convertToString(args));
        outputFilePath = extractOutputFilePath () ;
        attackMode = extractAttackMode() ;
        attackOrder = extractAttackOrder () ;
        replacements = extractReplacements();
    }

    private String extractOutputFilePath () {
        return hyphenatedCharacterArgsAdapter.getFlagValue(supportedCommandLineArgsFlags[4]);
    }

    private AttackMode extractAttackMode ()
        throws InvalidAttackModeException {
        String desiredAttackMode = hyphenatedCharacterArgsAdapter.getFlagValue(supportedCommandLineArgsFlags[0]);
        if (desiredAttackMode.trim().equalsIgnoreCase("token")) {
            return new TokenAttackMode() ;
        } else if (desiredAttackMode.trim().equalsIgnoreCase("bit")) {
            return new BitAttackMode() ;
        }
        else throw new InvalidAttackModeException(String.format("unrecognized attack mode %s", desiredAttackMode)) ;
    }

    private AttackOrder extractAttackOrder ()
        throws InvalidAttackOrderException {
        String desiredAttackOrder = hyphenatedCharacterArgsAdapter.getFlagValue(supportedCommandLineArgsFlags[1]);
        if (desiredAttackOrder.trim().equalsIgnoreCase("random")) {
            return new RandomAttackOrder() ;
        } else if (desiredAttackOrder.trim().equalsIgnoreCase("sequential")) {
            return new SequentialAttackOrder() ;
        } else throw new InvalidAttackOrderException (String.format("invalid attack order %s", desiredAttackOrder)) ;
    }

    private Replacement[] extractReplacements ()
        throws InconsistentReplacementsException {
        String desiredReplacementPositions = hyphenatedCharacterArgsAdapter.getFlagValue(supportedCommandLineArgsFlags[2]);
        String desiredReplacementValues = hyphenatedCharacterArgsAdapter.getFlagValue(supportedCommandLineArgsFlags[3]);
        String[] replacementPositions = desiredReplacementPositions.split(".") ;
        String[] replacementValues = desiredReplacementValues.split(".") ;
        Replacement[] rPositions = null ;
        if (desiredReplacementPositions.trim().equals(desiredReplacementValues.trim())
                && replacementPositions.length  == replacementValues.length) {
                rPositions = new Replacement[replacementPositions.length] ;
                for (int index =0 ; index < replacementPositions.length; index++) {
                    rPositions[index] = new Replacement(Integer.parseInt(replacementPositions[index].trim()),
                                                        Integer.parseInt(replacementValues[index].trim())) ;
                }
        } else throw new InconsistentReplacementsException("inconsistent replacement positions and values") ;
        return rPositions ;
    }

    /**
     * This code launches a brute force attack
     * based on command line parameters
     */
    private void launchAttack () {
        if (attackMode.getAttackMode() == AttackMode.Type.TOKEN) {
            BruteForceTokenAttacker bfat = new BruteForceTokenAttacker(replacements, outputFilePath) ;
            bfat.launchAttack() ;
        } else if (attackMode.getAttackMode() == AttackMode.Type.BIT) {
            BruteForceBitAttacker bfbt = new BruteForceBitAttacker(replacements, outputFilePath) ;
            bfbt.launchAttack() ;
        }
    }
}
