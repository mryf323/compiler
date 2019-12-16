package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import ast.type.Type;
import ast.type.primitiveType.IntType;
import visitor.typeAnalysis.AnalysedType;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.UNSUPPORTED_OPERATOR;

public abstract class BinaryIntConsumer extends BinaryConsumer {


    public BinaryIntConsumer(BinaryExpression expression) {
        super(expression);
    }

    protected abstract AnalysedType consumeInt(AnalysedType lhs, AnalysedType rhs);

    public final AnalysedType consume(AnalysedType lhs, AnalysedType rhs){

        Type lhsType = lhs.getType();
        Type rhsType = rhs.getType();
        if (!(lhsType instanceof IntType) || !(rhsType instanceof IntType)) {
            System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getBinaryOperator());
            return AnalysedType.NO_TYPE;
        } else
            return consumeInt(lhs, rhs);

    }
}
