package minicompiler.misc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static void writeTextFile(File file, String text) throws IOException {
        FileWriter fw = new FileWriter(file);
        try {
            fw.write(text);
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
    
    public static String readTextFile(File file) throws IOException {
        FileReader fr = new FileReader(file);
        try {
            return StreamUtils.readAll(fr);
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
}
