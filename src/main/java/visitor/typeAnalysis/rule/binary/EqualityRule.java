package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import ast.type.NoType;
import ast.type.Type;
import ast.type.primitiveType.BooleanType;
import visitor.typeAnalysis.AnalysedType;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.UNSUPPORTED_OPERATOR;

public class EqualityRule extends BinaryConsumer {


    public EqualityRule(BinaryExpression expression) {
        super(expression);
    }

    public AnalysedType apply(AnalysedType lhs, AnalysedType rhs) {

        Type lhsType = lhs.getType();
        Type rhsType = rhs.getType();

        if (rhsType.getClass().equals(lhsType.getClass()))
            return new AnalysedType<>(new BooleanType(), false);
        if (!(lhsType instanceof NoType) && !(rhsType instanceof NoType))
            System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getBinaryOperator());
        return AnalysedType.NO_TYPE;
    }
}
