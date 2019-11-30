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
import symbolTable.SymbolTable;
import visitor.ForceSymbolTablePusher;
import visitor.Visitor;

public class NameAnalyserVisitor implements Visitor<Boolean> {

    private ActorInheritanceService inheritanceService = ActorInheritanceService.getInstance();

    @Override
    public Boolean visit(Program program) {
        return true;
    }

    @Override
    public Boolean visit(ActorDeclaration actorDec) {

        boolean correct = true;
        String name = actorDec.getName().getName();
        if (ForceSymbolTablePusher.isUnified(name)) {
            name = ForceSymbolTablePusher.strip(name);
            System.out.printf("Line:%d:Redefinition of actor %s%n", actorDec.getLine(), name);
            correct = false;
        }

        if (actorDec.getParentName() != null && inheritanceService.hasCycle(actorDec)) {
            System.out.printf(
                    "Line:%d:Cyclic inheritance involving actor %s%n",
                    actorDec.getLine(),
                    actorDec.getName().getName()
            );
            correct = false;
        }

        if (actorDec.getQueueSize() == 0) {
            System.out.printf(
                    "Line:%d:Queue size must be positive%n",
                    actorDec.getLine()
            );
            correct = false;

        }
        return correct;
    }

    @Override
    public Boolean visit(HandlerDeclaration handlerDec) {
        return true;
    }

    @Override
    public Boolean visit(VarDeclaration varDeclaration) {
        boolean correct = true;
        String name = varDeclaration.getIdentifier().getName();
        if (ForceSymbolTablePusher.isUnified(name)) {
            name = ForceSymbolTablePusher.strip(name);
            System.out.printf(
                    "Line:%d:Redefinition of variable %s%n",
                    varDeclaration.getLine(), name
            );
            correct = false;
        }
        correct &= varDeclaration.getType().accept(this);
        return correct;
    }

    @Override
    public Boolean visit(Main mainActors) {
        return true;
    }

    @Override
    public Boolean visit(ActorInstantiation actorInstantiation) {

        return visit((VarDeclaration) actorInstantiation) &&
                actorInstantiation.getKnownActors().stream()
                        .map(i -> i.accept(this))
                        .reduce(true, (i, j) -> i && j) &&
                actorInstantiation.getInitArgs().stream()
                        .map(i -> i.accept(this))
                        .reduce(true, (i, j) -> i && j);

    }

    @Override
    public Boolean visit(UnaryExpression unaryExpression) {
        return unaryExpression.getOperand().accept(this);
    }

    @Override
    public Boolean visit(BinaryExpression binaryExpression) {
        return binaryExpression.getLeft().accept(this) &&
                binaryExpression.getRight().accept(this);
    }

    @Override
    public Boolean visit(ArrayCall arrayCall) {
        return arrayCall.getArrayInstance().accept(this) &&
                arrayCall.getIndex().accept(this);
    }

    @Override
    public Boolean visit(ActorVarAccess actorVarAccess) {
        return true;
    }

    @Override
    public Boolean visit(Identifier identifier) {
        return true;
    }

    @Override
    public Boolean visit(Self self) {
        return true;
    }

    @Override
    public Boolean visit(Sender sender) {
        return true;
    }

    @Override
    public Boolean visit(BooleanValue value) {
        return true;
    }

    @Override
    public Boolean visit(IntValue value) {
        return true;
    }

    @Override
    public Boolean visit(StringValue value) {
        return true;
    }

    @Override
    public Boolean visit(Block block) {

        SymbolTable.push(new SymbolTable(SymbolTable.top, String.valueOf(block.getLine())));
        Boolean correct = block.getStatements().stream()
                .map(i -> i.accept(this))
                .reduce(true, (i, j) -> i && j);
        SymbolTable.pop();
        return correct;
    }

    @Override
    public Boolean visit(Conditional conditional) {

        return conditional.getExpression().accept(this) &&
                conditional.getThenBody().accept(this) &&
                conditional.getElseBody() != null ? conditional.getElseBody().accept(this) : true;
    }

    @Override
    public Boolean visit(For loop) {
        boolean correct = true;
        if (loop.getInitialize() != null) {
            correct = loop.getInitialize().accept(this);
        }
        if (loop.getCondition() != null) {
            correct &= loop.getCondition().accept(this);
        }

        if (loop.getUpdate() != null) {
            correct &= loop.getUpdate().accept(this);
        }
        correct &= loop.getBody().accept(this);
        return correct;
    }

    @Override
    public Boolean visit(Break breakLoop) {
        return true;
    }

    @Override
    public Boolean visit(Continue continueLoop) {
        return true;
    }

    @Override
    public Boolean visit(MsgHandlerCall msgHandlerCall) {

        return msgHandlerCall.getInstance().accept(this) &&
                msgHandlerCall.getArgs().stream()
                        .map(i -> i.accept(this))
                        .reduce(true, (i, j) -> i && j);
    }

    @Override
    public Boolean visit(Print print) {
        return true;
    }

    @Override
    public Boolean visit(Assign assign) {
        return assign.getlValue().accept(this) && assign.getrValue().accept(this);
    }

    @Override
    public Boolean visit(ArrayType arrayType) {
        if (arrayType.getSize() == 0){
            System.out.printf("Line:%d:Array size must be positive%n", arrayType.getLine());
            return false;
        }
        return true;
    }

    @Override
    public Boolean visit(ActorType actorType) {
        return true;
    }

    @Override
    public Boolean visit(StringType stringType) {
        return true;
    }

    @Override
    public Boolean visit(IntType intType) {
        return true;
    }

    @Override
    public Boolean visit(BooleanType booleanType) {
        return true;
    }

}
