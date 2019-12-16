package visitor.typeAnalysis.rule.unary;

import ast.node.expression.UnaryExpression;
import ast.type.primitiveType.IntType;
import visitor.typeAnalysis.AnalysedType;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.UNSUPPORTED_OPERATOR;

public abstract class IntConsumer extends UnaryConsumer {

    public IntConsumer(UnaryExpression expression) {
        super(expression);
    }

    @Override
    protected final AnalysedType consume(AnalysedType operand) {

        if (!(operand.getType() instanceof IntType)) {
            System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getUnaryOperator());
            return AnalysedType.NO_TYPE;
        } else
            return consumeInt(operand);
    }
    protected abstract AnalysedType consumeInt(AnalysedType operand);
}
