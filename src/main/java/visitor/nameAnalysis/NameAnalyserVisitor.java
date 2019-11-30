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
import symbolTable.SymbolTable;
import symbolTable.symbolTableVariableItem.SymbolTableVariableItem;
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
            System.out.printf("%d:ErrorItemMessage: Redifinition of actor %s%n", actorDec.getName().getLine(), name);
            correct = false;
        }

        if (actorDec.getParentName() != null) {
            if (inheritanceService.transitiveParents(actorDec).size() == 0) {
                System.out.printf(
                        "%d:ErrorItemMessage: inherited actor %s not found%n",
                        actorDec.getParentName().getLine(),
                        actorDec.getParentName().getName()
                );
                correct = false;
            }
            if (inheritanceService.hasCycle(actorDec)) {
                System.out.printf(
                        "%d:ErrorItemMessage: Cyclic inheritance involving actor %s%n",
                        actorDec.getParentName().getLine(),
                        actorDec.getParentName().getName()
                );
                correct = false;
            }
        }

        if (actorDec.getQueueSize() == 0) {
            System.out.printf(
                    "%d:ErrorItemMessage: Queue size must be positive%n",
                    actorDec.getName().getLine()
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
                    "%d:ErrorItemMessage: Redefinition of variable %s%n",
                    varDeclaration.getIdentifier().getLine(), name
            );
            correct = false;
        }
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
        return binaryExpression.getLeft().accept(this) && binaryExpression.getRight().accept(this);
    }

    @Override
    public Boolean visit(ArrayCall arrayCall) {
        arrayCall.getArrayInstance().accept(this);
        return !(arrayCall.getIndex() instanceof IntValue) || ((IntValue) arrayCall.getIndex()).getConstant() > 0;
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
                conditional.getElseBody() == null ? true : conditional.getElseBody().accept(this);
    }

    @Override
    public Boolean visit(For loop) {

        return loop.getInitialize().accept(this) &&
                loop.getCondition().accept(this) &&
                loop.getUpdate().accept(this) &&
                loop.getBody().accept(this);
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
                        .map(i -> i.accept(this)).reduce(true, (i, j) -> i && j);
    }

    @Override
    public Boolean visit(Print print) {
        return true;
    }

    @Override
    public Boolean visit(Assign assign) {
        return assign.getlValue().accept(this) && assign.getrValue().accept(this);
    }


}
