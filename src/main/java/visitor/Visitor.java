package visitor;

import ast.node.*;
import ast.node.Program;
import ast.node.declaration.*;
import ast.node.declaration.handler.HandlerDeclaration;
import ast.node.declaration.VarDeclaration;
import ast.node.expression.*;
import ast.node.expression.values.BooleanValue;
import ast.node.expression.values.IntValue;
import ast.node.expression.values.StringValue;
import ast.node.statement.*;

public interface Visitor {

    void visit (Program program);

    //Declarations
    void visit (ActorDeclaration actorDeclaration);
    void visit (HandlerDeclaration handlerDeclaration);
    void visit (VarDeclaration varDeclaration);

    //main
    void visit(Main mainActors);
    void visit(ActorInstantiation actorInstantiation);

    //Expressions
    void visit(UnaryExpression unaryExpression);
    void visit(BinaryExpression binaryExpression);
    void visit(ArrayCall arrayCall);
    void visit(ActorVarAccess actorVarAccess);
    void visit(Identifier identifier);
    void visit(Self self);
    void visit(Sender sender);
    void visit(BooleanValue value);
    void visit(IntValue value);
    void visit(StringValue value);

    //Statements
    void visit(Block block);
    void visit(Conditional conditional);
    void visit(For loop);
    void visit(Break breakLoop);
    void visit(Continue continueLoop);
    void visit(MsgHandlerCall msgHandlerCall);
    void visit(Print print);
    void visit(Assign assign);
}
