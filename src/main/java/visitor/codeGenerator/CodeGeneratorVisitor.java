package visitor.codeGenerator;

import ast.node.Main;
import ast.node.Node;
import ast.node.Program;
import ast.node.declaration.ActorDeclaration;
import ast.node.declaration.ActorInstantiation;
import ast.node.declaration.VarDeclaration;
import ast.node.declaration.handler.HandlerDeclaration;
import ast.node.declaration.handler.InitHandlerDeclaration;
import ast.node.expression.*;
import ast.node.expression.operators.BinaryOperator;
import ast.node.expression.values.BooleanValue;
import ast.node.expression.values.IntValue;
import ast.node.expression.values.StringValue;
import ast.node.statement.*;
import ast.type.NoType;
import ast.type.Type;
import ast.type.actorType.ActorType;
import ast.type.arrayType.ArrayType;
import ast.type.primitiveType.BooleanType;
import ast.type.primitiveType.IntType;
import ast.type.primitiveType.StringType;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableActorItem;
import symbolTable.SymbolTableHandlerItem;
import symbolTable.SymbolTableMainItem;
import symbolTable.itemException.ItemNotFoundException;
import symbolTable.symbolTableVariableItem.SymbolTableActorVariableItem;
import symbolTable.symbolTableVariableItem.SymbolTableKnownActorItem;
import symbolTable.symbolTableVariableItem.SymbolTableVariableItem;
import visitor.Visitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.union;
import static visitor.codeGenerator.JasminTypeConvector.getJasminType;

public class CodeGeneratorVisitor implements Visitor<Void> {

    private final SymbolTable global;

    private ActorDeclaration currentActor;
    private static PrintWriter currentWriter;
    private AtomicInteger labelCounter = new AtomicInteger();
    private Stack<String> continueLabel = new Stack<>();
    private Stack<String> breakLabel = new Stack<>();

    public CodeGeneratorVisitor(SymbolTable global) {
        this.global = global;
    }

    private void acceptList(List<? extends Node> list) {
        list.forEach(i -> i.accept(this));
    }


    private void declareVars(Pair<String, String> prior, boolean isStatic, String scopeBegin, String scopeEnd) {

        int firstArgIndex = isStatic ? 0 : 1;
        if (prior != null) {
            currentWriter.println(".var " + firstArgIndex + " is " + prior.getValue() + " " +
                    prior.getKey() + " from " +
                    scopeBegin + " to " + scopeEnd);
        }

        List<SymbolTableVariableItem> vars = SymbolTable.top.getSymbolTableItems().values().stream()
                .filter(i -> i instanceof SymbolTableVariableItem)
                .map(i -> (SymbolTableVariableItem) i)
                .collect(Collectors.toList());

        int nextIndex = (prior == null ? firstArgIndex : firstArgIndex + 1);
        for (int i = 0; i < vars.size(); i++) {
            currentWriter.println(".var " + (i + nextIndex) +
                    " is " + vars.get(i).getVarDeclaration().getIdentifier().getName() + " " +
                    getJasminType(vars.get(i).getType()) + " from " +
                    scopeBegin + " to " + scopeEnd);
            vars.get(i).setIndex(i + nextIndex);
        }

    }


    @Override
    public Void visit(Program program) {
        File dir = new File("./output");
        try {
            if (dir.exists())
                FileUtils.forceDelete(dir);
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs) {
                System.out.println("Can not create output dir");
                System.exit(-1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            FileUtils.copyDirectory(new File("./src/main/resources"), new File("./output"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SymbolTable.push(this.global);
        acceptList(program.getActors());
        program.getMain().accept(this);
        SymbolTable.pop();
        return null;
    }

    private void createQueueSizeConstructor() {
        currentWriter.println(".method public <init>(I)V");
        currentWriter.println(".limit stack 32");
        currentWriter.println(".limit locals 32");
        currentWriter.println("aload_0");
        currentWriter.println("iload_1");
        currentWriter.println("invokespecial Actor/<init>(I)V");
        setDefaultValues(true, currentActor.getActorVars());
        currentWriter.println("return");
        currentWriter.println(".end method");
        currentWriter.println();
    }

    private void createKnownActorsSetter(List<VarDeclaration> knownActors) {
        currentWriter.print(".method public setKnownActors(");
        knownActors.forEach(k -> currentWriter.print(getJasminType(k.getType())));
        currentWriter.println(")V");
        currentWriter.println(".limit stack 32");
        currentWriter.println(".limit locals 32");

        for (int i = 0; i < knownActors.size(); i++) {
            currentWriter.println("aload_0");
            currentWriter.printf("aload %d%n", i + 1);
            currentWriter.printf(
                    "putfield %s/%s %s%n",
                    currentActor.getName().getName(),
                    knownActors.get(i).getIdentifier().getName(),
                    getJasminType(knownActors.get(i).getType())
            );
        }

        currentWriter.println("return");
        currentWriter.println(".end method");
        currentWriter.println();

    }

    private void setDefaultValues(boolean actorVarScope, List<VarDeclaration> vars) {

        for (VarDeclaration localVar : vars) {

            try {
                if (actorVarScope)
                    currentWriter.println("aload_0");
                SymbolTableVariableItem symbolTableItem = (SymbolTableVariableItem) SymbolTable.top.get(
                        SymbolTableVariableItem.STARTKEY + localVar.getIdentifier().getName()
                );
                Type type = localVar.getType();
                if (type instanceof IntType || type instanceof BooleanType) {
                    currentWriter.println("iconst_0");
                } else if (type instanceof StringType) {
                    currentWriter.println("ldc \"\"");

                } else if (type instanceof ArrayType) {
                    currentWriter.printf("bipush %d%n", ((ArrayType) type).getSize());
                    currentWriter.println("newarray int");
                }
                if (actorVarScope) {
                    currentWriter.println("putfield " + currentActor.getName().getName() +
                            "/" + localVar.getIdentifier().getName() + " "
                            + getJasminType(type));
                } else
                    currentWriter.printf("%sstore %s%n", AOrI.eval(type) ,symbolTableItem.getIndex());

            } catch (ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Void visit(ActorDeclaration actorDeclaration) {
        try {
            currentWriter = new PrintWriterFactory().create("output/" + actorDeclaration.getName().getName() + ".j");
            currentWriter.println(".class public " + actorDeclaration.getName().getName());
            currentWriter.println(".super Actor");
            currentWriter.println();
            currentActor = actorDeclaration;

            SymbolTable symbolTable = ((SymbolTableActorItem)
                    SymbolTable.top.get(SymbolTableActorItem.STARTKEY + actorDeclaration.getName().getName()))
                    .getActorSymbolTable();

            SymbolTable.push(symbolTable);


            for (VarDeclaration filed : union(actorDeclaration.getKnownActors(), actorDeclaration.getActorVars()))
                currentWriter.printf(".field %s %s%n", filed.getIdentifier().getName(), getJasminType(filed.getType()));

            createQueueSizeConstructor();

            createKnownActorsSetter(actorDeclaration.getKnownActors());

            if (actorDeclaration.getInitHandler() != null)
                actorDeclaration.getInitHandler().accept(this);
            acceptList(actorDeclaration.getMsgHandlers());

            SymbolTable.pop();
            currentWriter.close();
        } catch (FileNotFoundException | ItemNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(HandlerDeclaration handlerDeclaration) {
        try {
            SymbolTableHandlerItem symbolTable = (SymbolTableHandlerItem) SymbolTable.top.get(
                    SymbolTableHandlerItem.STARTKEY +
                            handlerDeclaration.getName().getName()
            );
            SymbolTable.push(symbolTable.getHandlerSymbolTable());
            List<VarDeclaration> args = new ArrayList<>(handlerDeclaration.getArgs());

            Pair<String, String> firstArg = null;
            if (!handlerDeclaration.getName().getName().equals("initial")) {
                VarDeclaration sender = new VarDeclaration(new Identifier("sender"), new ActorType(new Identifier("Actor")));
                args.add(0, sender);
                firstArg = new Pair<>("LActor;", "sender");
            }

            currentWriter.print(".method public " + handlerDeclaration.getName().getName() + "(");
            args.forEach(arg -> currentWriter.print(getJasminType(arg.getType())));
            currentWriter.println(")V");
            currentWriter.println(".limit stack 32");
            currentWriter.println(".limit locals 32");


            String labelPrefix = handlerDeclaration.getName().getName() + labelCounter.getAndIncrement();
            String beginMethod = labelPrefix + "_begin";
            String endMethod = labelPrefix + "_end";

            declareVars(firstArg, false, beginMethod, endMethod);

            currentWriter.println(beginMethod + ":");
            setDefaultValues(false, handlerDeclaration.getLocalVars());
            handlerDeclaration.getBody().forEach(s -> s.accept(this));
            currentWriter.println(endMethod + ":");
            SymbolTable.pop();
            currentWriter.println("return");
            currentWriter.println(".end method");
            currentWriter.println();

            //helper method and message class
            if (!handlerDeclaration.getName().getName().equals("initial")) {
                currentWriter.printf(".method public send_%s(", handlerDeclaration.getName().getName());
                args.forEach(arg -> currentWriter.print(getJasminType(arg.getType())));
                currentWriter.println(")V");
                currentWriter.println(".limit stack 32");
                currentWriter.println(".limit locals 32");
                currentWriter.println("aload_0");
                currentWriter.printf("new %s_%s%n", currentActor.getName().getName(), handlerDeclaration.getName().getName());
                currentWriter.println("dup");
                currentWriter.println("aload_0");
                currentWriter.println("aload_1");

                for (int i = 1; i < args.size(); i++)
                    currentWriter.printf(
                            args.get(i).getType() instanceof IntType ||
                                    args.get(i).getType() instanceof BooleanType
                                    ? "iload %d%n"
                                    : "aload %d%n"
                            , i + 1
                    );

                currentWriter.printf(
                        "invokespecial %s_%s/<init>(L%s;",
                        currentActor.getName().getName(),
                        handlerDeclaration.getName().getName(),
                        currentActor.getName().getName()
                );
                args.forEach(arg -> currentWriter.print(getJasminType(arg.getType())));
                currentWriter.println(")V");
                currentWriter.printf("invokevirtual %s/send(LMessage;)V%n", currentActor.getName().getName());
                currentWriter.println("return");
                currentWriter.println(".end method");
                currentWriter.println();

                new MessageCodeGenerator(currentActor.getName().getName(), handlerDeclaration.getName().getName(), handlerDeclaration.getArgs())
                        .generateCodeForMethod();
            }
        } catch (ItemNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(VarDeclaration varDeclaration) {
        throw new NotImplementedException("Should not be called");
    }

    @Override
    public Void visit(Main mainActors) {

        try {
            SymbolTableMainItem symbolTable = (SymbolTableMainItem)
                    SymbolTable.top.get(SymbolTableMainItem.STARTKEY + SymbolTableMainItem.NAME);
            SymbolTable.push(symbolTable.getMainSymbolTable());

            currentWriter = new PrintWriterFactory().create("output/Main.j");
            currentWriter.println(".class public Main");
            currentWriter.println(".super java/lang/Object");
            currentWriter.println();
            currentWriter.println(".method public <init>()V");
            currentWriter.println(".limit stack 1");
            currentWriter.println(".limit locals 1");
            currentWriter.println("0: aload_0");
            currentWriter.println("1: invokespecial java/lang/Object/<init>()V");
            currentWriter.println("4: return");
            currentWriter.println(".end method");
            currentWriter.println();
            currentWriter.println(".method public static main([Ljava/lang/String;)V");
            currentWriter.println(".limit stack 32");
            currentWriter.println(".limit locals 32");
            String beginLabel = "main_begin";
            String endLabel = "main_end";
            declareVars(new Pair<>("[Ljava/lang/String;", "args"), true, beginLabel, endLabel);
            currentWriter.println(beginLabel + ":");
            visitActorInstantiations(mainActors.getMainActors());
            currentWriter.println(endLabel + ":");
            currentWriter.println("return");
            currentWriter.println(".end method");
            currentWriter.println();
            currentWriter.close();
        } catch (FileNotFoundException | ItemNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void visitActorInstantiations(List<ActorInstantiation> instantiations) {
        try {
            //new all

            for (int i = 0; i < instantiations.size(); i++) {

                ActorType actorType = (ActorType) instantiations.get(i).getType();

                SymbolTableActorItem actorSymbolTable =
                        ((SymbolTableActorItem) SymbolTable.top.get(SymbolTableActorItem.STARTKEY + actorType.getName().getName()));

                currentWriter.printf("new %s%n", actorType.getName().getName());
                currentWriter.println("dup");


                currentWriter.printf("ldc %d%n", actorSymbolTable.getActorDeclaration().getQueueSize());
                currentWriter.printf("invokespecial %s/<init>(I)V%n", actorType.getName().getName());
                currentWriter.printf("astore %d%n", i + 1);
            }

            //set known actors
            for (int i = 0; i < instantiations.size(); i++) {
                ActorType actorType = (ActorType) instantiations.get(i).getType();

                SymbolTableActorItem actorSymbolTable =
                        ((SymbolTableActorItem) SymbolTable.top.get(SymbolTableActorItem.STARTKEY + actorType.getName().getName()));

                currentWriter.printf("aload %d%n", i + 1);
                acceptList(instantiations.get(i).getKnownActors());
                currentWriter.printf("invokevirtual %s/setKnownActors(", actorType.getName().getName());

                actorSymbolTable.getActorDeclaration().getKnownActors()
                        .forEach(k -> currentWriter.print(getJasminType(k.getType())));
                currentWriter.println(")V");
            }

            //initials
            for (int i = 0; i < instantiations.size(); i++) {

                ActorType actorType = (ActorType) instantiations.get(i).getType();

                SymbolTableActorItem actorSymbolTable =
                        ((SymbolTableActorItem) SymbolTable.top.get(SymbolTableActorItem.STARTKEY + actorType.getName().getName()));
                InitHandlerDeclaration initHandler = actorSymbolTable.getActorDeclaration().getInitHandler();

                if (initHandler == null)
                    continue;

                currentWriter.printf("aload %d%n", i + 1);
                acceptList(instantiations.get(i).getInitArgs());
                currentWriter.printf("invokevirtual %s/initial(", actorType.getName().getName());
                initHandler.getArgs().forEach(a -> currentWriter.print(getJasminType(a.getType())));
                currentWriter.println(")V");
            }
            for (int i = 0; i < instantiations.size(); i++) {
                currentWriter.printf("aload %d%n", i + 1);
                currentWriter.printf("invokevirtual %s/start()V%n", ((ActorType) instantiations.get(i).getType()).getName().getName());

            }
        } catch (ItemNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Void visit(ActorInstantiation actorInstantiation) {
        throw new NotImplementedException("Should not be called");
    }

    @Override
    public Void visit(UnaryExpression unaryExpression) {

        switch (unaryExpression.getUnaryOperator()) {
            case not:
                unaryExpression.getOperand().accept(this);
                new UnaryOperatorCodeGenerator(currentWriter, labelCounter).notStatement();
                break;
            case minus:
                unaryExpression.getOperand().accept(this);
                currentWriter.println("ineg");
                break;
            case predec:
                new Assign(unaryExpression.getOperand(),
                        new BinaryExpression(
                                unaryExpression.getOperand(),
                                new IntValue(1, new IntType()),
                                BinaryOperator.sub
                        )
                ).accept(this);
                unaryExpression.getOperand().accept(this);
                break;
            case postdec:
                unaryExpression.getOperand().accept(this);
                new Assign(unaryExpression.getOperand(),
                        new BinaryExpression(
                                unaryExpression.getOperand(),
                                new IntValue(1, new IntType())
                                , BinaryOperator.sub
                        )
                ).accept(this);
                break;
            case preinc:
                new Assign(unaryExpression.getOperand(),
                        new BinaryExpression(
                                unaryExpression.getOperand(),
                                new IntValue(1, new IntType()),
                                BinaryOperator.add
                        )
                ).accept(this);
                unaryExpression.getOperand().accept(this);
                break;
            case postinc:
                unaryExpression.getOperand().accept(this);
                new Assign(unaryExpression.getOperand(),
                        new BinaryExpression(
                                unaryExpression.getOperand(),
                                new IntValue(1, new IntType())
                                , BinaryOperator.add
                        )
                ).accept(this);
                break;
        }

        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression) {
        Expression left = binaryExpression.getLeft();
        Expression right = binaryExpression.getRight();

        if (binaryExpression.getBinaryOperator() != BinaryOperator.assign) {
            left.accept(this);
            right.accept(this);
        }
        BinaryOperationCodeGenerator binaryOperationCodeGenerator = new BinaryOperationCodeGenerator(currentWriter, labelCounter);
        switch (binaryExpression.getBinaryOperator()) {
            case add:
                currentWriter.println("iadd");
                break;

            case sub:
                currentWriter.println("isub");
                break;

            case mult:
                currentWriter.println("imul");
                break;

            case div:
                currentWriter.println("idiv");
                break;

            case eq:
                binaryOperationCodeGenerator.checkEquality(left);
                break;

            case neq:
                binaryOperationCodeGenerator.checkNotEquality(left);
                break;

            case lt:
                binaryOperationCodeGenerator.compareStatements("lt", "lt");
                break;

            case gt:
                binaryOperationCodeGenerator.compareStatements("gt", "gt");
                break;

            case and:
                currentWriter.println("iand");
                break;

            case or:
                currentWriter.println("ior");
                break;
            case mod:
                currentWriter.println("irem");
                break;
            case assign:
                Assign tempAssign = new Assign(left, right);
                tempAssign.accept(this);
                left.accept(this);
                break;

        }
        return null;
    }

    @Override
    public Void visit(ArrayCall arrayCall) {
        Expression instance = arrayCall.getArrayInstance();
        Expression index = arrayCall.getIndex();
        instance.accept(this);
        index.accept(this);
        currentWriter.println("iaload");
        return null;
    }

    @Override
    public Void visit(ActorVarAccess actorVarAccess) {
        currentWriter.println("aload_0");
        String fieldKey = SymbolTableActorVariableItem.STARTKEY + actorVarAccess.getVariable().getName();
        try {
            SymbolTableActorVariableItem fieldItem = (SymbolTableActorVariableItem) SymbolTable.top.getPreSymbolTable().get(fieldKey);
            currentWriter.printf("getfield %s/%s %s%n",
                    currentActor.getName().getName(),
                    actorVarAccess.getVariable().getName(),
                    getJasminType(fieldItem.getType())
            );
        } catch (ItemNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        try {
            SymbolTableVariableItem item = (SymbolTableVariableItem) SymbolTable.top.get(
                    SymbolTableVariableItem.STARTKEY + identifier.getName()
            );
            Type type = item.getType();
            boolean isField = item instanceof SymbolTableActorVariableItem || item instanceof SymbolTableKnownActorItem;

            if (isField) {
                currentWriter.println("aload_0");
                currentWriter.println("getfield " + currentActor.getName().getName() +
                        "/" + identifier.getName() + " "
                        + getJasminType(type));
            } else {
                if (type instanceof IntType || type instanceof BooleanType)
                    currentWriter.printf("iload %d%n", item.getIndex());

                if (type instanceof ActorType || type instanceof ArrayType || type instanceof StringType)
                    currentWriter.printf("aload %d%n", item.getIndex());
            }
        } catch (ItemNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(Self self) {
        currentWriter.println("aload_0");
        return null;
    }

    @Override
    public Void visit(Sender sender) {
        currentWriter.println("aload_1");
        return null;
    }

    @Override
    public Void visit(BooleanValue value) {
        currentWriter.println("iconst_" + (value.getConstant() ? 1 : 0));
        return null;
    }

    @Override
    public Void visit(IntValue value) {
        currentWriter.println("bipush " + value.getConstant());
        return null;
    }

    @Override
    public Void visit(StringValue value) {
        currentWriter.println("ldc " + value.getConstant());
        return null;
    }

    @Override
    public Void visit(Block block) {
        acceptList(block.getStatements());
        return null;
    }

    @Override
    public Void visit(Conditional conditional) {
        Expression expression = conditional.getExpression();
        Statement consequenceBody = conditional.getThenBody();
        Statement alternativeBody = conditional.getElseBody();
        int label = labelCounter.getAndIncrement();
        String scopeBegin = "if_" + label;
        String scopeEnd = "end_if_" + label;
        String statementEnd = "else_" + label;

        expression.accept(this);

        currentWriter.println("ifeq " + scopeEnd);

        currentWriter.println(scopeBegin + ":");
        consequenceBody.accept(this);
        currentWriter.println("goto " + statementEnd);

        currentWriter.println(scopeEnd + ":");

        if (alternativeBody != null)
            alternativeBody.accept(this);

        currentWriter.println(statementEnd + ":");
        return null;
    }

    @Override
    public Void visit(For loop) {
        Expression condition = loop.getCondition();
        Statement body = loop.getBody();

        if (loop.getInitialize() != null)
            loop.getInitialize().accept(this);

        String prefix = "loop_" + labelCounter.getAndIncrement();
        String start = prefix + "_begin";
        continueLabel.push(prefix + "update");
        breakLabel.push(prefix + "_end");
        currentWriter.println(start + ":");
        if (condition != null)
            condition.accept(this);
        else
            currentWriter.println("iconst_1");
        currentWriter.println("ifeq " + breakLabel.peek());
        body.accept(this);
        currentWriter.println(prefix + "update:");
        if (loop.getUpdate() != null) {
            loop.getUpdate().accept(this);
        }
        currentWriter.println("goto " + start);
        currentWriter.println(breakLabel.peek() + ":");
        continueLabel.pop();
        breakLabel.pop();
        return null;
    }

    @Override
    public Void visit(Break breakLoop) {
        currentWriter.println("goto " + breakLabel.peek());
        return null;
    }

    @Override
    public Void visit(Continue continueLoop) {
        currentWriter.println("goto " + continueLabel.peek());
        return null;
    }

    @Override
    public Void visit(MsgHandlerCall msgHandlerCall) {
        msgHandlerCall.getInstance().accept(this);
        currentWriter.println("aload_0");
        acceptList(msgHandlerCall.getArgs());

        Type targetType = msgHandlerCall.getInstance().getType();
        assert (targetType instanceof ActorType);

        currentWriter.printf("invokevirtual %s/send_%s(LActor;", ((ActorType) targetType).getName().getName(),
                msgHandlerCall.getMsgHandlerName().getName()
        );
        msgHandlerCall.getArgs().stream()
                .map(Expression::getType)
                .forEach(type -> currentWriter.print(getJasminType(type)));
        currentWriter.println(")V");

        return null;
    }

    @Override
    public Void visit(Print print) {
        currentWriter.println("getstatic java/lang/System/out Ljava/io/PrintStream;");
        print.getArg().accept(this);
        Type type = print.getArg().getType();
        if (type instanceof ArrayType) {
            currentWriter.println("invokestatic java/util/Arrays/toString([I)Ljava/lang/String;");
            currentWriter.println("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
        }
        else
            currentWriter.printf("invokevirtual java/io/PrintStream/println(%s)V%n", getJasminType(type));
        return null;
    }

    public void visitArrayCallByReference(ArrayCall arrayCall) {
        Expression instance = arrayCall.getArrayInstance();
        Expression index = arrayCall.getIndex();
        instance.accept(this);
        index.accept(this);
    }

    @Override
    public Void visit(Assign assign) {

        Expression lValue = assign.getlValue();
        Expression rValue = assign.getrValue();

        if (lValue instanceof ArrayCall) {

            visitArrayCallByReference((ArrayCall) lValue);
            rValue.accept(this);
            currentWriter.println("iastore");

        } else if (lValue instanceof Identifier) {
            try {
                SymbolTableVariableItem item = (SymbolTableVariableItem) SymbolTable.top.get(
                        SymbolTableVariableItem.STARTKEY + ((Identifier) lValue).getName()
                );

                Type type = item.getType();
                boolean isField = item instanceof SymbolTableActorVariableItem || item instanceof SymbolTableKnownActorItem;
                if (isField) {
                    currentWriter.println("aload_0");
                    rValue.accept(this);
                    currentWriter.println("putfield " + currentActor.getName().getName() +
                            "/" + ((Identifier) lValue).getName() + " "
                            + getJasminType(type));
                } else {
                    if (type instanceof IntType || type instanceof BooleanType) {
                        rValue.accept(this);
                        currentWriter.println("istore " + item.getIndex());
                    } else if (type instanceof ActorType || type instanceof StringType) {
                        rValue.accept(this);
                        currentWriter.println("astore " + item.getIndex());
                    }
                }

            } catch (ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    // Types -----------------------------------------------------------------------------------------------------------
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
