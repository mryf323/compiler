package ast.node.expression;

import visitor.Visitor;
import ast.node.expression.operators.BinaryOperator;

import javax.annotation.Nonnull;

public class BinaryExpression extends Expression {

    @Nonnull
    private Expression left;
    @Nonnull
    private Expression right;
    @Nonnull
    private BinaryOperator binaryOperator;

    public BinaryExpression(@Nonnull Expression left, @Nonnull Expression right, @Nonnull BinaryOperator binaryOperator) {
        this.left = left;
        this.right = right;
        this.binaryOperator = binaryOperator;
    }

    @Nonnull
    public Expression getLeft() {
        return left;
    }

    public void setLeft(@Nonnull Expression left) {
        this.left = left;
    }

    @Nonnull
    public Expression getRight() {
        return right;
    }

    public void setRight(@Nonnull Expression right) {
        this.right = right;
    }

    @Nonnull
    public BinaryOperator getBinaryOperator() {
        return binaryOperator;
    }

    public void setBinaryOperator(@Nonnull BinaryOperator binaryOperator) {
        this.binaryOperator = binaryOperator;
    }

    @Override
    public String toString() {
        return "BinaryExpression " + binaryOperator.name();
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}

