package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import ast.type.primitiveType.IntType;
import visitor.typeAnalysis.AnalysedType;

public class ArithmeticRule extends BinaryIntConsumer {


    public ArithmeticRule(BinaryExpression expression) {
        super(expression);
    }

    @Override
    protected AnalysedType consumeInt(AnalysedType lhs, AnalysedType rhs) {
        return new AnalysedType<>(new IntType(), false);
    }
}
