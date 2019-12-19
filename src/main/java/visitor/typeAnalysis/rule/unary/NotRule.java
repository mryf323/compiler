package visitor.typeAnalysis.rule.unary;

import ast.node.expression.UnaryExpression;
import ast.type.NoType;
import ast.type.primitiveType.BooleanType;
import visitor.typeAnalysis.AnalysedType;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.UNSUPPORTED_OPERATOR;

public class NotRule extends UnaryConsumer {

    public NotRule(UnaryExpression expression) {
        super(expression);
    }

    @Override
    public AnalysedType apply(AnalysedType operand) {
        if (operand.getType() instanceof BooleanType)
            return new AnalysedType<>(new BooleanType(), false);
        else if (!(operand.getType() instanceof NoType))
            System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getUnaryOperator());
        return AnalysedType.NO_TYPE;
    }
}
