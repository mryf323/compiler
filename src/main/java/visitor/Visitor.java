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

    //Types
    T visit(ArrayType arrayType);
    T visit(ActorType actorType);
    T visit(StringType stringType);
    T visit(IntType intType);
    T visit(BooleanType booleanType);
    T visit(NoType noType);
}
