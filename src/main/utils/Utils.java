package main.utils;

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
}
