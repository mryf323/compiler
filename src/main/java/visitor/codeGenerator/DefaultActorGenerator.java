package visitor.codeGenerator;

import ast.node.Main;
import ast.node.Program;
import ast.node.declaration.ActorDeclaration;
import ast.node.declaration.ActorInstantiation;
import ast.node.declaration.VarDeclaration;
import ast.node.declaration.handler.HandlerDeclaration;
import ast.node.expression.*;
import ast.node.expression.values.BooleanValue;
import ast.node.expression.values.IntValue;
import ast.node.expression.values.StringValue;
import ast.node.statement.*;
import ast.type.NoType;
import ast.type.actorType.ActorType;
import ast.type.arrayType.ArrayType;
import ast.type.primitiveType.BooleanType;
import ast.type.primitiveType.IntType;
import ast.type.primitiveType.StringType;
import symbolTable.SymbolTable;
import visitor.Visitor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static visitor.codeGenerator.JasminTypeConvector.getJasminType;

public class DefaultActorGenerator implements Visitor<Void> {

    private final SymbolTable global;
    private PrintWriter writer;

    public DefaultActorGenerator(SymbolTable global) {
        this.global = global;
    }

    @Override
    public Void visit(Program program) {

        try {
            writer = new PrintWriterFactory().create("output/DefaultActor.j");
            writer.println(".class public DefaultActor");
            writer.println(".super java/lang/Thread");
            writer.println();
            writer.println(".method public <init>()V");
            writer.println(".limit stack 1");
            writer.println(".limit locals 1");
            writer.println("aload_0");
            writer.println("invokespecial java/lang/Thread/<init>()V");
            writer.println("return");
            writer.println(".end method");
            writer.println();
            SymbolTable.push(global);
            program.getActors().forEach(a -> a.accept(this));
            SymbolTable.pop();
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(ActorDeclaration actorDeclaration) {
        actorDeclaration.getMsgHandlers().forEach(h -> h.accept(this));
        return null;
    }

    @Override
    public Void visit(HandlerDeclaration handlerDeclaration) {
        writer.printf(".method public send_%s(LActor;", handlerDeclaration.getName().getName());
        handlerDeclaration.getArgs().forEach(a -> writer.print(getJasminType(a.getType())));
        writer.println(")V");
        writer.println(".limit stack 32");
        writer.println(".limit locals 32");
        writer.println("getstatic java/lang/System/out Ljava/io/PrintStream;");
        writer.println("ldc \"there is no msghandler named bar in sender\"");
        writer.println("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
        writer.println("return");
        writer.println(".end method");
        writer.println();
        return null;
    }

    @Override
    public Void visit(VarDeclaration varDeclaration) {
        return null;
    }

    @Override
    public Void visit(Main mainActors) {
        return null;
    }

    @Override
    public Void visit(ActorInstantiation actorInstantiation) {
        return null;
    }

    @Override
    public Void visit(UnaryExpression unaryExpression) {
        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression) {
        return null;
    }

    @Override
    public Void visit(ArrayCall arrayCall) {
        return null;
    }

    @Override
    public Void visit(ActorVarAccess actorVarAccess) {
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        return null;
    }

    @Override
    public Void visit(Self self) {
        return null;
    }

    @Override
    public Void visit(Sender sender) {
        return null;
    }

    @Override
    public Void visit(BooleanValue value) {
        return null;
    }

    @Override
    public Void visit(IntValue value) {
        return null;
    }

    @Override
    public Void visit(StringValue value) {
        return null;
    }

    @Override
    public Void visit(Block block) {
        return null;
    }

    @Override
    public Void visit(Conditional conditional) {
        return null;
    }

    @Override
    public Void visit(For loop) {
        return null;
    }

    @Override
    public Void visit(Break breakLoop) {
        return null;
    }

    @Override
    public Void visit(Continue continueLoop) {
        return null;
    }

    @Override
    public Void visit(MsgHandlerCall msgHandlerCall) {
        return null;
    }

    @Override
    public Void visit(Print print) {
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        return null;
    }

    @Override
    public Void visit(ArrayType arrayType) {
        return null;
    }

    @Override
    public Void visit(ActorType actorType) {
        return null;
    }

    @Override
    public Void visit(StringType stringType) {
        return null;
    }

    @Override
    public Void visit(IntType intType) {
        return null;
    }

    @Override
    public Void visit(BooleanType booleanType) {
        return null;
    }

    @Override
    public Void visit(NoType noType) {
        return null;
    }
}
