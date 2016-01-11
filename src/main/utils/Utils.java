package main.utils;

import main.domain.Replacement;

/**
 * Created by rmbitiru on 12/29/15.
 */
public class Utils {

    public static String convertToString(String[] args) {
        String concat = "" ;
        for(String s : args)
            concat += s + " " ;
        return concat ;
    }

    /**
     * This method appends a token parameter log output to
     * the output file
     * @param tokenParameters  string to be written to output file
     * @param outputFilePath output file path
     */
    public static void writeToFile (String tokenParameters, String outputFilePath) {

    }

    /**
     * This method concatenates a set of values in a string
     * array to a string
     * @param vals
     * @return
     */
    public static String concat(Replacement[] vals) {
        String res = "" ;
        for (Replacement val : vals)
            res += val.toString() ;
        return res ;
    }
}
