package visitor.nameAnalysis;

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
import visitor.Visitor;

public class AstPrinter implements Visitor<Void> {
    @Override
    public Void visit(Program program) {
        System.out.println(program);
        program.getActors().forEach(i -> i.accept(this));
        program.getMain().accept(this);
        return null;
    }

    @Override
    public Void visit(ActorDeclaration actorDeclaration) {
        System.out.println(actorDeclaration);
        actorDeclaration.getName().accept(this);
        if (actorDeclaration.getParentName() != null) {
            actorDeclaration.getParentName().accept(this);
        }
        actorDeclaration.getKnownActors().forEach(i -> i.accept(this));
        actorDeclaration.getActorVars().forEach(i -> i.accept(this));
        if (actorDeclaration.getInitHandler() != null) {
            actorDeclaration.getInitHandler().accept(this);
        }
        actorDeclaration.getMsgHandlers().forEach(i -> i.accept(this));
        return null;
    }

    @Override
    public Void visit(HandlerDeclaration handlerDeclaration) {
        System.out.println(handlerDeclaration);
        handlerDeclaration.getName().accept(this);
        handlerDeclaration.getArgs().forEach(i -> i.accept(this));
        handlerDeclaration.getLocalVars().forEach(i -> i.accept(this));
        handlerDeclaration.getBody().forEach(i -> i.accept(this));
        return null;
    }

    @Override
    public Void visit(VarDeclaration varDeclaration) {
        System.out.println(varDeclaration);
        varDeclaration.getType().accept(this);
        varDeclaration.getIdentifier().accept(this);
        return null;
    }

    @Override
    public Void visit(Main mainActors) {
        System.out.println(mainActors);
        mainActors.getMainActors().forEach(i -> i.accept(this));
        return null;
    }

    @Override
    public Void visit(ActorInstantiation actorInstantiation) {
        System.out.println(actorInstantiation);
        actorInstantiation.getType().accept(this);
        actorInstantiation.getIdentifier().accept(this);
        actorInstantiation.getKnownActors().forEach(i -> i.accept(this));
        actorInstantiation.getInitArgs().forEach(i -> i.accept(this));
        return null;
    }

    @Override
    public Void visit(UnaryExpression unaryExpression) {
        System.out.println(unaryExpression);
        unaryExpression.getOperand().accept(this);
        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression) {
        System.out.println(binaryExpression);
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);
        return null;
    }

    @Override
    public Void visit(ArrayCall arrayCall) {
        System.out.println(arrayCall);
        arrayCall.getArrayInstance().accept(this);
        arrayCall.getIndex().accept(this);
        return null;
    }

    @Override
    public Void visit(ActorVarAccess actorVarAccess) {
        System.out.println(actorVarAccess);
        actorVarAccess.getSelf().accept(this);
        actorVarAccess.getVariable().accept(this);
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        System.out.println(identifier);
        return null;
    }

    @Override
    public Void visit(Self self) {
        System.out.println(self);
        return null;
    }

    @Override
    public Void visit(Sender sender) {
        System.out.println(sender);
        return null;
    }

    @Override
    public Void visit(BooleanValue value) {
        System.out.println(value);
        return null;
    }

    @Override
    public Void visit(IntValue value) {
        System.out.println(value);
        return null;
    }

    @Override
    public Void visit(StringValue value) {
        System.out.println(value);
        return null;
    }

    @Override
    public Void visit(Block block) {
        System.out.println(block);
        block.getStatements().forEach(i -> i.accept(this));
        return null;
    }

    @Override
    public Void visit(Conditional conditional) {
        System.out.println(conditional);
        conditional.getExpression().accept(this);
        conditional.getThenBody().accept(this);
        if (conditional.getElseBody() != null) {
            conditional.getElseBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(For loop) {
        System.out.println(loop);
        if (loop.getInitialize() != null) {
            loop.getInitialize().accept(this);
        }
        if (loop.getCondition() != null) {
            loop.getCondition().accept(this);
        }
        if (loop.getUpdate() != null) {
            loop.getUpdate().accept(this);
        }
        loop.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(Break breakLoop) {
        System.out.println(breakLoop);
        return null;
    }

    @Override
    public Void visit(Continue continueLoop) {
        System.out.println(continueLoop);
        return null;
    }

    @Override
    public Void visit(MsgHandlerCall msgHandlerCall) {
        System.out.println(msgHandlerCall);
        msgHandlerCall.getInstance().accept(this);
        msgHandlerCall.getMsgHandlerName().accept(this);
        msgHandlerCall.getArgs().forEach(i ->i.accept(this));
        return null;
    }

    @Override
    public Void visit(Print print) {
        System.out.println(print);
        print.getArg().accept(this);
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        System.out.println(assign);
        assign.getlValue().accept(this);
        assign.getrValue().accept(this);
        return null;
    }

    @Override
    public Void visit(ArrayType arrayType) {
        System.out.println(arrayType);
        return null;
    }

    @Override
    public Void visit(ActorType actorType) {
        System.out.println(actorType);
        actorType.getName().accept(this);
        return null;
    }

    @Override
    public Void visit(StringType stringType) {
        System.out.println(stringType);
        return null;
    }

    @Override
    public Void visit(IntType intType) {
        System.out.println(intType);
        return null;
    }

    @Override
    public Void visit(BooleanType booleanType) {
        System.out.println(booleanType);
        return null;
    }
}
