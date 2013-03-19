package minicompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import minicompiler.misc.FileUtils;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntegrationTest {
    private static File inputFile;
    private static File outputFile;
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        inputFile = File.createTempFile("minicompiler-input", ".txt");
        inputFile.deleteOnExit();
        outputFile = File.createTempFile("minicompiler-output", ".txt");
        outputFile.deleteOnExit();
    }
    
    @AfterClass
    public static void tearDownClass() {
        inputFile.delete();
        outputFile.delete();
    }
    
    @Test
    public void testHelloWorld() throws Exception {
        compileExample("hello-world");
        assertArrayEquals(new int[] { 1, 2, 3 }, runExample("hello-world"));
    }
    
    
    @Test
    public void testFactorial() throws Exception {
        compileExample("factorial");
        assertArrayEquals(new int[] { 6 }, runExample("factorial", 3));
        assertArrayEquals(new int[] { 5040 }, runExample("factorial", 7));
        assertArrayEquals(new int[] { 3628800 }, runExample("factorial", 10));
    }
    
    @Test
    public void testSum() throws Exception {
        compileExample("sum");
        assertArrayEquals(new int[] { 77 }, runExample("sum", 33, 44));
        assertArrayEquals(new int[] { -3 }, runExample("sum", -5, 2));
        assertArrayEquals(new int[] { -5 }, runExample("sum", 2, -7));
    }
    
    private void compileExample(String name) throws Exception {
        String command = "./compile.sh " + name + ".mini";
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(command, null, new File("./examples"));
        if (proc.waitFor() != 0) {
            fail(command + " exited with " + proc.exitValue());
        }
    }
    
    private String runExample(String name, String input) throws Exception {
        FileUtils.writeTextFile(inputFile, input);
        
        String[] command = new String[] {
            "/bin/sh",
            "-e",
            "-c",
            "./executable-" + name + " < " + inputFile + " > " + outputFile
        };
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(command, null, new File("./examples"));
        if (proc.waitFor() != 0) {
            fail("executable-" + name + " exited with " + proc.exitValue());
        }
        
        return FileUtils.readTextFile(outputFile);
    }
    
    private int[] runExample(String name, int... input) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int x : input) {
            sb.append(x).append("\n");
        }
        
        String output = runExample(name, sb.toString());
        
        Scanner scanner = new Scanner(output);
        ArrayList<Integer> outputInts = new ArrayList<Integer>();
        while (scanner.hasNextInt()) {
            outputInts.add(scanner.nextInt());
        }
        
        int[] outputIntArray = new int[outputInts.size()];
        for (int i = 0; i < outputInts.size(); ++i) {
            outputIntArray[i] = outputInts.get(i);
        }
        return outputIntArray;
    }
}
