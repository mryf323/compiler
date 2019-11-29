package ast.node.expression;

import ast.node.expression.operators.UnaryOperator;
import visitor.Visitor;

public class UnaryExpression extends Expression {

    private UnaryOperator unaryOperator;
    private Expression operand;

    public UnaryExpression(UnaryOperator unaryOperator, Expression operand) {
        this.unaryOperator = unaryOperator;
        this.operand = operand;
    }

    public Expression getOperand() {
        return operand;
    }

    public void setOperand(Expression operand) {
        this.operand = operand;
    }

    public UnaryOperator getUnaryOperator() {
        return unaryOperator;
    }

    public void setUnaryOperator(UnaryOperator unaryOperator) {
        this.unaryOperator = unaryOperator;
    }

    @Override
    public String toString() {
        return "UnaryExpression " + unaryOperator.name();
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}