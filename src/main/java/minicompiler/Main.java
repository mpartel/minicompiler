package minicompiler;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final String usage =
            "Usage: java -jar minicompiler-dev.jar input-file output-file\n" +
            "   or  java -jar minicompiler-dev.jar < input-file > output-file";
    
    public static void main(String[] args) throws IOException {
        List<String> argList = Arrays.asList(args);
        Reader reader = null;
        Writer writer = null;
        
        if (argList.contains("-h") || argList.contains("--help")) {
            System.out.println(usage);
            System.exit(0);
        }
        
        if (args.length == 0) {
            reader = new InputStreamReader(System.in, "UTF-8");
            writer = new OutputStreamWriter(System.out, "UTF-8");
        } else if (args.length == 1) {
            reader = new FileReader(args[0]);
            writer = new OutputStreamWriter(System.out, "UTF-8");
        } else if (args.length == 2) {
            reader = new FileReader(args[0]);
            writer = new FileWriter(args[1]);
        } else {
            System.err.println(usage);
            System.exit(1);
        }
        
        Compiler.compile(reader, writer);
        writer.close();
        reader.close();
    }
}
