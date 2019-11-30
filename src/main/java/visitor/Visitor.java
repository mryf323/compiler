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

public interface Visitor<T> {

    T visit (Program program);

    //Declarations
    T visit (ActorDeclaration actorDeclaration);
    T visit (HandlerDeclaration handlerDeclaration);
    T visit (VarDeclaration varDeclaration);

    //main
    T visit(Main mainActors);
    T visit(ActorInstantiation actorInstantiation);

    //Expressions
    T visit(UnaryExpression unaryExpression);
    T visit(BinaryExpression binaryExpression);
    T visit(ArrayCall arrayCall);
    T visit(ActorVarAccess actorVarAccess);
    T visit(Identifier identifier);
    T visit(Self self);
    T visit(Sender sender);
    T visit(BooleanValue value);
    T visit(IntValue value);
    T visit(StringValue value);

    //Statements
    T visit(Block block);
    T visit(Conditional conditional);
    T visit(For loop);
    T visit(Break breakLoop);
    T visit(Continue continueLoop);
    T visit(MsgHandlerCall msgHandlerCall);
    T visit(Print print);
    T visit(Assign assign);
}