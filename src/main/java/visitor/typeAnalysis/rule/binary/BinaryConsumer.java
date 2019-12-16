package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import ast.type.NoType;
import ast.type.Type;
import visitor.typeAnalysis.AnalysedType;

public abstract class BinaryConsumer {

    protected final BinaryExpression expression;

    public BinaryConsumer(BinaryExpression expression) {
        this.expression = expression;
    }


    public final AnalysedType apply(AnalysedType lhs, AnalysedType rhs) {

        Type lhsType = lhs.getType();
        Type rhsType = rhs.getType();
        if (lhsType instanceof NoType || rhsType instanceof NoType)
            return AnalysedType.NO_TYPE;

        return consume(lhs, rhs);
    }
    protected abstract AnalysedType consume(AnalysedType lhs, AnalysedType rhs);

}
