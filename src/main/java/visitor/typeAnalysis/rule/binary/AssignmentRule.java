package visitor.typeAnalysis.rule.binary;

import ast.node.expression.BinaryExpression;
import ast.type.NoType;
import ast.type.Type;
import ast.type.actorType.ActorType;
import ast.type.arrayType.ArrayType;
import visitor.nameAnalysis.ActorInheritanceService;
import visitor.typeAnalysis.AnalysedType;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.*;

public class AssignmentRule extends BinaryConsumer {


    private static final ActorInheritanceService inheritanceService = ActorInheritanceService.getInstance();

    public AssignmentRule(BinaryExpression expression) {
        super(expression);
    }

    public AnalysedType apply(AnalysedType lhs, AnalysedType rhs) {

        Type lhsType = lhs.getType();
        Type rhsType = rhs.getType();

        if (!lhs.islValue())
            System.out.printf(ASSIGN_LVAL, expression.getLine());
        if (lhsType instanceof ActorType && rhsType instanceof ActorType) {
            if (inheritanceService.isAssignable((ActorType) lhsType, (ActorType) rhsType)) {
                return new AnalysedType<>((ActorType) lhsType, false);
            }
        }
        else if (lhsType instanceof ArrayType && rhsType instanceof ArrayType) {
            if (((ArrayType) lhsType).getSize() != ((ArrayType) rhsType).getSize()) {
                System.out.printf(SAME_SIZE_ARRAY_ASSIGN, expression.getLine());
                return AnalysedType.NO_TYPE;
            } else {
                return new AnalysedType<>((ArrayType) lhsType, false);
            }
        } else if (rhsType.getClass().isAssignableFrom(lhsType.getClass())) {
            return new AnalysedType<>(lhsType, false);
        }
        if (!(lhsType instanceof NoType) && !(rhsType instanceof NoType))
            System.out.printf(UNSUPPORTED_OPERATOR, expression.getLine(), expression.getBinaryOperator());
        return AnalysedType.NO_TYPE;
    }
}
