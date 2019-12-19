package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import visitor.typeAnalysis.AnalysedType;

public abstract class BinaryConsumer {

    protected final BinaryExpression expression;

    public BinaryConsumer(BinaryExpression expression) {
        this.expression = expression;
    }

    protected abstract AnalysedType apply(AnalysedType lhs, AnalysedType rhs);

}
