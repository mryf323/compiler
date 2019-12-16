package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import ast.type.Type;
import ast.type.actorType.ActorType;
import visitor.nameAnalysis.ActorInheritanceService;
import visitor.typeAnalysis.AnalysedType;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.ASSIGN_LVAL;
import static visitor.typeAnalysis.TypeAnalyserMessageSource.UNSUPPORTED_OPERATOR;

public class AssignmentRule extends BinaryConsumer {


    private static final ActorInheritanceService inheritanceService = ActorInheritanceService.getInstance();

    public AssignmentRule(BinaryExpression expression) {
        super(expression);
    }

    public AnalysedType consume(AnalysedType lhs, AnalysedType rhs) {

        Type lhsType = lhs.getType();
        Type rhsType = rhs.getType();

        if (!lhs.islValue())
            System.out.printf(ASSIGN_LVAL, lhsType.getLine());
        if (lhsType instanceof ActorType && rhsType instanceof ActorType) {
            if (inheritanceService.isAssignable((ActorType) lhsType, (ActorType) rhsType)) {
                return new AnalysedType<>((ActorType) lhsType, false);
            }
        } else if (rhsType.getClass().isAssignableFrom(lhsType.getClass())) {
            return new AnalysedType<>(lhsType, false);
        }
        System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getBinaryOperator());
        return AnalysedType.NO_TYPE;
    }
}
