package visitor.typeAnalysis.rule.unary;

import ast.node.expression.UnaryExpression;
import ast.type.primitiveType.BooleanType;
import visitor.typeAnalysis.AnalysedType;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.UNSUPPORTED_OPERATOR;

public class NotRule extends UnaryConsumer {

    public NotRule(UnaryExpression expression) {
        super(expression);
    }

    @Override
    protected AnalysedType consume(AnalysedType operand) {
        if (operand.getType() instanceof BooleanType)
            return new AnalysedType<>(new BooleanType(), false);
        System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getUnaryOperator());
        return AnalysedType.NO_TYPE;
    }
}
