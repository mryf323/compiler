package visitor.codeGenerator;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class UnaryOperatorCodeGenerator {

    private final PrintWriter currentWriter;
    private final AtomicInteger labelCounter;

    UnaryOperatorCodeGenerator(PrintWriter currentWriter, AtomicInteger labelCounter) {
        this.currentWriter = currentWriter;
        this.labelCounter = labelCounter;
    }

    void notStatement() {

        String prefix = "notUnary_" + labelCounter.getAndIncrement();
        String ifZero = prefix + "_ifZero";
        String statementEnd = prefix + "_end";

        currentWriter.printf("ifeq %s%n", ifZero);
        currentWriter.println("iconst_0");
        currentWriter.printf("goto %s%n", statementEnd);
        currentWriter.printf("%s:%niconst_1%n", ifZero);

        currentWriter.println(statementEnd + ":");
    }
}

