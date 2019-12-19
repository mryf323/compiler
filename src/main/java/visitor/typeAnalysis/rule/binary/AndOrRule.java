package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import ast.type.NoType;
import ast.type.Type;
import ast.type.primitiveType.BooleanType;
import visitor.typeAnalysis.AnalysedType;

import java.util.stream.Stream;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.UNSUPPORTED_OPERATOR;

public class AndOrRule extends BinaryConsumer {

    public AndOrRule(BinaryExpression expression) {
        super(expression);
    }

    public AnalysedType apply(AnalysedType lhs, AnalysedType rhs) {

        Type lhsType = lhs.getType();
        Type rhsType = rhs.getType();

        if (lhsType instanceof BooleanType && rhsType instanceof BooleanType)
            return new AnalysedType<>(new BooleanType(), false);
        boolean unsupportedType = Stream.of(lhsType, rhsType)
                .anyMatch(operand -> !(operand instanceof NoType) && !(operand instanceof BooleanType));
        if (unsupportedType)
            System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getBinaryOperator());
        return AnalysedType.NO_TYPE;
    }

}
