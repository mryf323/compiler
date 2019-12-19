package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import ast.type.primitiveType.BooleanType;
import visitor.typeAnalysis.AnalysedType;

public class GtLtRule extends BinaryIntConsumer {

    public GtLtRule(BinaryExpression expression) {
        super(expression);
    }

    @Override
    protected AnalysedType consumeInt(AnalysedType lhs, AnalysedType rhs) {
        return new AnalysedType<>(new BooleanType(), false);
    }
}
