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
import ast.type.NoType;
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

public class FirstPassSymbolTableInitializer implements Visitor<SymbolTable> {

    private VariableDeclarationZone variableDeclarationZone;

    @Override
    public SymbolTable visit(Program program) {


        SymbolTable.push(new SymbolTable());

        program.getActors().forEach(i -> i.accept(this));

        program.getMain().accept(this);
        return SymbolTable.pop();
    }

    @Override
    public SymbolTable visit(ActorDeclaration actorDeclaration) {

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
        return SymbolTable.pop();
    }

    @Override
    public SymbolTable visit(HandlerDeclaration handlerDeclaration) {

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
        
        return SymbolTable.pop();
    }

    @Override
    public SymbolTable visit(VarDeclaration varDeclaration) {
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
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(Main mainActors) {

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
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(ActorInstantiation actorInstantiation) {

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
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(UnaryExpression unaryExpression) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(BinaryExpression binaryExpression) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(ArrayCall arrayCall) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(ActorVarAccess actorVarAccess) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(Identifier identifier) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(Self self) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(Sender sender) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(BooleanValue value) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(IntValue value) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(StringValue value) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(Block block) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(Conditional conditional) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(For loop) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(Break breakLoop) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(Continue continueLoop) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(MsgHandlerCall msgHandlerCall) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(Print print) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(Assign assign) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(ArrayType arrayType) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(ActorType actorType) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(StringType stringType) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(IntType intType) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(BooleanType booleanType) {
        return SymbolTable.top;
    }

    @Override
    public SymbolTable visit(NoType noType) {
        return null;
    }
}

