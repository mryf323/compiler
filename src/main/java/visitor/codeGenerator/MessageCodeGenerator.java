package visitor.codeGenerator;

import ast.node.declaration.VarDeclaration;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import static visitor.codeGenerator.JasminTypeConvector.getJasminType;

public class MessageCodeGenerator {
    private final String className;
    private final String methodName;
    private final List<VarDeclaration> args;

    public MessageCodeGenerator(String className, String methodName, List<VarDeclaration> declarations) {
        this.className = className;
        this.methodName = methodName;
        this.args = declarations;
    }

    public void generateCodeForMethod() {
        try {
            // Class and fields
            PrintWriter writer = new PrintWriterFactory().create("output/" + className + "_" + methodName + ".j");
            writer.printf(".class public %s_%s%n", className, methodName);
            writer.println(".super Message");
            writer.println();
            for (VarDeclaration arg : args)
                writer.printf(".field private %s %s%n", arg.getIdentifier().getName(), getJasminType(arg.getType()));
            writer.printf(".field private receiver L%s;%n", className);
            writer.println(".field private sender LActor;");

            //Constructor
            writer.printf(".method public <init>(L%s;LActor;", className);
            args.forEach(arg -> writer.print(getJasminType(arg.getType())));
            writer.println(")V");
            writer.println(".limit stack 32");
            writer.println(".limit locals 32");
            writer.println("aload_0");
            writer.println("invokespecial Message/<init>()V");
            writer.println("aload_0");
            writer.println("aload_1");
            writer.printf("putfield %s_%s/receiver L%s;%n", className, methodName, className);
            writer.println("aload_0");
            writer.println("aload_2");
            writer.printf("putfield %s_%s/sender LActor;%n", className, methodName);

            for (int i = 0; i < args.size(); i++) {
                writer.println("aload_0");

                writer.printf("%sload %d%n",AOrI.eval(args.get(i).getType()), i + 3);
                writer.printf("putfield %s_%s/%s %s%n",
                        className, methodName,
                        args.get(i).getIdentifier().getName(),
                        getJasminType(args.get(i).getType())
                );
            }
            writer.println("return");
            writer.println(".end method");
            writer.println();

            //execute
            writer.println(".method public execute()V");
            writer.println(".limit stack 3");
            writer.println(".limit locals 1");
            writer.println("aload_0");
            writer.printf("getfield %s_%s/receiver L%s;%n", className, methodName, className);
            writer.println("aload_0");
            writer.printf("getfield %s_%s/sender LActor;%n", className, methodName);

            for (VarDeclaration arg : args) {
                writer.println("aload_0");
                writer.printf("getfield %s_%s/%s %s%n", className, methodName, arg.getIdentifier().getName(), getJasminType(arg.getType()));
            }

            writer.printf("invokevirtual %s/%s(LActor;", className, methodName);
            for (VarDeclaration arg : args)
                writer.print(getJasminType(arg.getType()));

            writer.println(")V");
            writer.println("return");
            writer.println(".end method");
            writer.println();

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
