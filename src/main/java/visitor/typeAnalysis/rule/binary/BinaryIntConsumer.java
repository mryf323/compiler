package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import ast.type.NoType;
import ast.type.primitiveType.IntType;
import visitor.typeAnalysis.AnalysedType;

import java.util.stream.Stream;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.UNSUPPORTED_OPERATOR;

public abstract class BinaryIntConsumer extends BinaryConsumer {


    public BinaryIntConsumer(BinaryExpression expression) {
        super(expression);
    }

    protected abstract AnalysedType consumeInt(AnalysedType lhs, AnalysedType rhs);

    public final AnalysedType apply(AnalysedType lhs, AnalysedType rhs){


        boolean unsupportedType = Stream.of(lhs.getType(), rhs.getType())
                .anyMatch(operand -> !(operand instanceof NoType) && !(operand instanceof IntType));
        if (unsupportedType)
            System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getBinaryOperator());

        if (lhs.getType() instanceof IntType && rhs.getType() instanceof IntType)
            return consumeInt(lhs, rhs);

        return AnalysedType.NO_TYPE;


    }
}
