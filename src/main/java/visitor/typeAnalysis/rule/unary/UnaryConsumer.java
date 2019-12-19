package visitor.typeAnalysis.rule.unary;

import ast.node.expression.UnaryExpression;
import visitor.typeAnalysis.AnalysedType;

public abstract class UnaryConsumer {

    protected final UnaryExpression expression;

    public UnaryConsumer(UnaryExpression expression) {
        this.expression = expression;
    }

    abstract protected AnalysedType apply(AnalysedType operand);
}
