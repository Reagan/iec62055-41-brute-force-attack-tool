package main.domain.attack;

import main.domain.Replacement;
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
     * This method is responsible for formatting the output string
     * @param tokenParameters token parameters from attack vector
     * @param specificTokenProcessingToc current end processing time for all tokens
     * @param allTokenProcessingToc end processing time for current tokens
     * @return CSV string with generated parameters
     */
    public abstract String formatTokenParams (TokenParameters tokenParameters,
                                      Toc specificTokenProcessingToc,
                                      Toc allTokenProcessingToc) ;
    /**
     * Saves line to file
     * @param tokenParameters generated token parameters
     * @param outputFilePath path to output file
     * @param specificTokenProcessingToc end processing toc for all tokens being processed
     * @param allTokenProcessingToc end processing time for specific token being processed
     */
    public void saveToFile(TokenParameters tokenParameters, String outputFilePath,
                            Toc specificTokenProcessingToc, Toc allTokenProcessingToc) {
        Utils.writeToFile(formatTokenParams(tokenParameters, specificTokenProcessingToc, allTokenProcessingToc),
                outputFilePath) ;
    }

}
