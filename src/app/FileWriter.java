package app;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;

/**
 * Created by gaura on 2016-07-22.
 */
public class FileWriter {
    public static String createFile(String baseFileName) throws Exception {

        String filename = baseFileName + new SimpleDateFormat("yyyyMMddhhmm'.txt'").format(new Date());
        File f =  new File(filename);
        f.createNewFile();
        return filename;
    }

    public static void writeToFile(ArrayList<String> list, String fileName) throws Exception {

        BufferedWriter bw  = new BufferedWriter(new java.io.FileWriter(fileName,true));
        for (String data: list){
            bw.write(data);
            bw.newLine();
        }
        bw.close();
    }



}
