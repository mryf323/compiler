package visitor.codeGenerator;

import java.io.*;

public class PrintWriterFactory {
    public PrintWriter create(String fileName) throws FileNotFoundException {
        return new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName))), true);
    }
}
