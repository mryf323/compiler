package visitor;

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
import ast.type.actorType.ActorType;
import ast.type.arrayType.ArrayType;
import ast.type.primitiveType.BooleanType;
import ast.type.primitiveType.IntType;
import ast.type.primitiveType.StringType;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableActorItem;
import symbolTable.SymbolTableHandlerItem;
import symbolTable.SymbolTableMainItem;
import symbolTable.symbolTableVariableItem.*;

public class FirstPassSymbolTableInitializer implements Visitor<Void> {

    private VariableDeclarationZone variableDeclarationZone;

    private int latestActorSequenceNumber = 0;


    @Override
    public Void visit(Program program) {


        SymbolTable.push(new SymbolTable());

        program.getActors().forEach(i -> i.accept(this));

        program.getMain().accept(this);
        return null;
    }

    @Override
    public Void visit(ActorDeclaration actorDeclaration) {

        actorDeclaration.setSequenceNumber(latestActorSequenceNumber ++);
        SymbolTableActorItem item = new ForceSymbolTablePusher<SymbolTableActorItem>() {

            @Override
            protected SymbolTableActorItem push() {
                return new SymbolTableActorItem(actorDeclaration);
            }

            @Override
            protected SymbolTableActorItem onExistence() {
                actorDeclaration.setName(unifyRedundantIdentifier(actorDeclaration.getName()));
                return new SymbolTableActorItem(actorDeclaration);
            }
        }.forcePush(SymbolTable.top);

        SymbolTable actorSymbolTable = new SymbolTable(SymbolTable.top, actorDeclaration.getName().getName());
        SymbolTable.push(actorSymbolTable);
        item.setActorSymbolTable(actorSymbolTable);

        variableDeclarationZone = VariableDeclarationZone.KNOWN_ACTOR;
        actorDeclaration.getKnownActors().forEach(i -> i.accept(this));

        variableDeclarationZone = VariableDeclarationZone.ACTOR_VAR;
        actorDeclaration.getActorVars().forEach(i -> i.accept(this));

        if (actorDeclaration.getInitHandler() != null) {
            actorDeclaration.getInitHandler().accept(this);
        }
        actorDeclaration.getMsgHandlers().forEach(i -> i.accept(this));
        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(HandlerDeclaration handlerDeclaration) {

        SymbolTableHandlerItem item = new ForceSymbolTablePusher<SymbolTableHandlerItem>() {
            @Override
            protected SymbolTableHandlerItem push() {
                return new SymbolTableHandlerItem(handlerDeclaration);
            }

            @Override
            protected SymbolTableHandlerItem onExistence() {
                handlerDeclaration.setName(unifyRedundantIdentifier(handlerDeclaration.getName()));
                return new SymbolTableHandlerItem(handlerDeclaration);
            }
        }.forcePush(SymbolTable.top);

        SymbolTable.push(new SymbolTable(SymbolTable.top, handlerDeclaration.getName().getName()));
        item.setHandlerSymbolTable(SymbolTable.top);

        variableDeclarationZone = VariableDeclarationZone.HANDLER_ARG;
        handlerDeclaration.getArgs().forEach(arg -> arg.accept(this));

        variableDeclarationZone = VariableDeclarationZone.LOCAL;
        handlerDeclaration.getLocalVars().forEach(local -> local.accept(this));

        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(VarDeclaration varDeclaration) {
        new ForceSymbolTablePusher<SymbolTableVariableItem>() {
            private SymbolTableVariableItem nodeToItem(VarDeclaration declaration) {
                switch (variableDeclarationZone) {
                    case LOCAL:
                        return new SymbolTableLocalVariableItem(declaration);
                    case ACTOR_VAR:
                        return new SymbolTableActorVariableItem(declaration);
                    case HANDLER_ARG:
                        return new SymbolTableHandlerArgumentItem(declaration);
                    case KNOWN_ACTOR:
                        return new SymbolTableKnownActorItem(declaration);
                    default:
                        throw new RuntimeException("Unknown VariableDeclarationZone");
                }
            }

            @Override
            protected SymbolTableVariableItem push() {
                return nodeToItem(varDeclaration);
            }

            @Override
            protected SymbolTableVariableItem onExistence() {
                varDeclaration.setIdentifier(unifyRedundantIdentifier(varDeclaration.getIdentifier()));
                return nodeToItem(varDeclaration);
            }
        }.forcePush(SymbolTable.top);
        return null;
    }

    @Override
    public Void visit(Main mainActors) {

        SymbolTableMainItem item = new ForceSymbolTablePusher<SymbolTableMainItem>() {
            @Override
            public SymbolTableMainItem push() {
                return new SymbolTableMainItem(mainActors);
            }

            @Override
            public SymbolTableMainItem onExistence() {
                throw new RuntimeException("Unexpected Redundant Main");
            }
        }.forcePush(SymbolTable.top);

        SymbolTable symbolTable = new SymbolTable(SymbolTable.top, "main");
        SymbolTable.push(symbolTable);
        item.setMainSymbolTable(symbolTable);
        mainActors.getMainActors().forEach(ins -> ins.accept(this));
        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(ActorInstantiation actorInstantiation) {

        new ForceSymbolTablePusher<SymbolTableLocalVariableItem>() {

            @Override
            protected SymbolTableLocalVariableItem push() {
                return new SymbolTableLocalVariableItem(actorInstantiation);
            }

            @Override
            protected SymbolTableLocalVariableItem onExistence() {
                actorInstantiation.setIdentifier(unifyRedundantIdentifier(actorInstantiation.getIdentifier()));
                return new SymbolTableLocalVariableItem(actorInstantiation);
            }
        }.forcePush(SymbolTable.top);
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
}

