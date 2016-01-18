package utils;

import domain.Replacement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by rmbitiru on 12/29/15.
 */
public class Utils {

    private static String outputFilePath = "" ; // stores the output file path
    private static BufferedWriter bufferedWriter = null ;

    public static String convertToString(String[] args) {
        String concat = "" ;
        for(String s : args)
            concat += s + " " ;
        return concat ;
    }

    /**
     * Opens the file for reading
     * @param outputFilePath
     */
    public static void openFile (String outputFilePath)
        throws IOException {
        Utils.outputFilePath = outputFilePath ;
        File outputFile = new File(outputFilePath) ;
        if (!outputFile.exists())
            outputFile.createNewFile() ;
        FileWriter fileWriter = new FileWriter(outputFile.getAbsoluteFile()) ;
        bufferedWriter = new BufferedWriter(fileWriter) ;
    }

    /**
     * This method appends a token parameter log output to
     * the output file
     * @param tokenParameters  string to be written to output file
     */
    public static void writeToFile (String tokenParameters)
        throws IOException {
        bufferedWriter.write(tokenParameters);
    }

    /**
     * Closes the file after writing or reading
     */
    public static void closeFile()
        throws IOException {
        if (null != bufferedWriter)
            bufferedWriter.close();
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
            res += val.toString() + " " ;
        return res ;
    }
}
