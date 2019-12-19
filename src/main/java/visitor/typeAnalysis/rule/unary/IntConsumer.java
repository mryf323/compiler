package visitor.typeAnalysis.rule.unary;

import ast.node.expression.UnaryExpression;
import ast.type.NoType;
import ast.type.primitiveType.IntType;
import visitor.typeAnalysis.AnalysedType;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.UNSUPPORTED_OPERATOR;

public abstract class IntConsumer extends UnaryConsumer {

    public IntConsumer(UnaryExpression expression) {
        super(expression);
    }

    @Override
    public final AnalysedType apply(AnalysedType operand) {

        if (!(operand.getType() instanceof IntType) && !(operand.getType() instanceof NoType)) {
            System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getUnaryOperator());
        }

        AnalysedType analysedType = consumeInt(operand);
        if (operand.getType() instanceof IntType)
            return analysedType;
        else
            return AnalysedType.NO_TYPE;
    }
    protected abstract AnalysedType consumeInt(AnalysedType operand);
}
