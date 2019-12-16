package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import ast.type.Type;
import ast.type.primitiveType.BooleanType;
import visitor.typeAnalysis.AnalysedType;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.UNSUPPORTED_OPERATOR;

public class AndOrRule extends BinaryConsumer {

    public AndOrRule(BinaryExpression expression) {
        super(expression);
    }

    public AnalysedType consume(AnalysedType lhs, AnalysedType rhs) {

        Type lhsType = lhs.getType();
        Type rhsType = rhs.getType();

        if (lhsType instanceof BooleanType && rhsType instanceof BooleanType)
            return new AnalysedType<>(new BooleanType(), false);

        System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getBinaryOperator());
        return AnalysedType.NO_TYPE;
    }

}
