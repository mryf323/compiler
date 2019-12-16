package visitor.typeAnalysis.rule.unary;

import ast.node.expression.UnaryExpression;
import ast.type.NoType;
import visitor.typeAnalysis.AnalysedType;

public abstract class UnaryConsumer {

    protected final UnaryExpression expression;

    public UnaryConsumer(UnaryExpression expression) {
        this.expression = expression;
    }

    public final AnalysedType apply(AnalysedType operand) {
        if (operand.getType() instanceof NoType)
            return AnalysedType.NO_TYPE;
        else
            return consume(operand);
    }
    abstract protected AnalysedType consume(AnalysedType operand);
}
