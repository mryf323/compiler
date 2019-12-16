package visitor.typeAnalysis.rule.unary;

import ast.node.expression.UnaryExpression;
import ast.type.primitiveType.IntType;
import visitor.typeAnalysis.AnalysedType;

public class MinusRule extends IntConsumer {

    public MinusRule(UnaryExpression expression) {
        super(expression);
    }

    @Override
    protected AnalysedType consumeInt(AnalysedType operand) {
        return new AnalysedType<>(new IntType(), false);
    }
}
