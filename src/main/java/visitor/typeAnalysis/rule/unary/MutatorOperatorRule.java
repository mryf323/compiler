package visitor.typeAnalysis.rule.unary;

import ast.node.expression.UnaryExpression;
import visitor.typeAnalysis.AnalysedType;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.DEC_ON_RVALUE;
import static visitor.typeAnalysis.TypeAnalyserMessageSource.INC_ON_RVALUE;

public class MutatorOperatorRule extends IntConsumer {

    public MutatorOperatorRule(UnaryExpression expression) {
        super(expression);
    }

    @Override
    protected AnalysedType consumeInt(AnalysedType operand) {
        if (operand.islValue())
        return new AnalysedType<>(operand.getType(), false);
        else {
            switch (expression.getUnaryOperator()) {
                case preinc:
                case postinc:
                    System.out.printf(INC_ON_RVALUE, expression.getLine());
                case predec:
                case postdec:
                    System.out.printf(DEC_ON_RVALUE, expression.getLine());
            }
            return AnalysedType.NO_TYPE;
        }

    }
}
