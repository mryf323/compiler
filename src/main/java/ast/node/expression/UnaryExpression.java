package ast.node.expression;

import ast.node.expression.operators.UnaryOperator;
import visitor.Visitor;

import javax.annotation.Nonnull;

public class UnaryExpression extends Expression {
    @Nonnull
    private UnaryOperator unaryOperator;
    @Nonnull
    private Expression operand;

    public UnaryExpression(@Nonnull UnaryOperator unaryOperator, @Nonnull Expression operand) {
        this.unaryOperator = unaryOperator;
        this.operand = operand;
    }

    @Nonnull
    public Expression getOperand() {
        return operand;
    }

    public void setOperand(@Nonnull Expression operand) {
        this.operand = operand;
    }

    @Nonnull
    public UnaryOperator getUnaryOperator() {
        return unaryOperator;
    }

    public void setUnaryOperator(@Nonnull UnaryOperator unaryOperator) {
        this.unaryOperator = unaryOperator;
    }

    @Override
    public String toString() {
        return "UnaryExpression " + unaryOperator.name();
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}