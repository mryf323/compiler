package visitor.codeGenerator;

import ast.node.expression.Expression;
import ast.type.Type;
import ast.type.actorType.ActorType;
import ast.type.arrayType.ArrayType;
import ast.type.primitiveType.BooleanType;
import ast.type.primitiveType.IntType;
import ast.type.primitiveType.StringType;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class BinaryOperationCodeGenerator {

    private final PrintWriter currentWriter;
    private final AtomicInteger labelCounter;
    private final UnaryOperatorCodeGenerator unaryOperatorCodeGenerator;

    public BinaryOperationCodeGenerator(PrintWriter currentWriter, AtomicInteger labelCounter) {
        this.currentWriter = currentWriter;
        this.labelCounter = labelCounter;
        this.unaryOperatorCodeGenerator = new UnaryOperatorCodeGenerator(currentWriter, labelCounter);
    }

    void checkEquality(Expression value) {
        Type type = value.getType();

        if (type instanceof BooleanType || type instanceof IntType)
            compareStatements("eq", "eq");

        else if (type instanceof StringType)
            currentWriter.println("invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z");


        else if (type instanceof ActorType)
            currentWriter.println("invokevirtual java/lang/Object/equals(Ljava/lang/Object;)Z");


        else if (type instanceof ArrayType)
            currentWriter.println("invokevirtual java/lang/Object/equals(Ljava/lang/Object;)Z");


    }

    void compareStatements(String operator, String condition) {

        String prefix = operator + "_" + labelCounter.getAndIncrement();
        String operatorBegin = prefix + "_begin";
        String satisfied = prefix + "_satisfied";
        String scopeEnd = prefix + "_end";

        currentWriter.println("if_icmp" + condition + " " + satisfied);
        currentWriter.println(operatorBegin + ":");
        currentWriter.println("iconst_0");
        currentWriter.println("goto " + scopeEnd);
        currentWriter.println(satisfied + ":");
        currentWriter.println("iconst_1");
        currentWriter.println(scopeEnd + ":");
    }

    void checkNotEquality(Expression value) {
        Type type = value.getType();

        if (type instanceof BooleanType || type instanceof IntType)
            compareStatements("neq", "ne");

        if (type instanceof StringType) {
            currentWriter.println("invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z");
            unaryOperatorCodeGenerator.notStatement();
        }

        if (type instanceof ActorType) {
            currentWriter.println("invokevirtual java/lang/Object/equals(Ljava/lang/Object;)Z");
            unaryOperatorCodeGenerator.notStatement();
        }

        if (type instanceof ArrayType) {
            currentWriter.println("invokevirtual java/lang/Object/equals(Ljava/lang/Object;)Z");
            unaryOperatorCodeGenerator.notStatement();
        }

    }
}

