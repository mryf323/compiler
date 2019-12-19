package visitor.typeAnalysis;

public class TypeAnalyserMessageSource {

    /*01*/ static final String UNDECLARED_VAR = "Line:%d:variable %s is not declared%n";
    /*02*/ public static final String UNSUPPORTED_OPERATOR = "Line:%d:unsupported operand type for %s%n";
    /*03*/ static final String BOOL_CONDITION = "Line:%d:condition type must be boolean%n";
    /*04*/ static final String UNDECLARED_ACTOR = "Line:%d:actor %s is not declared%n";
    /*05*/ static final String UNDECLARED_HANDLER = "Line:%d:there is no msghandler named %s in actor %s%n";
    /*06*/ static final String PRINT_UNSUPPORTED_TYPE = "Line:%d:unsupported type for print%n";
    /*07*/ public static final  String ASSIGN_LVAL = "Line:%d:left side of assignment must be a valid lvalue%n";
    /*08*/ static final String KNOWN_ACTOR_MISMATCH = "Line:%d:knownactors do not match with definition%n";
    /*09*/ static final String NON_CALLABLE_VAR = "Line:%d:variable %s is not callable%n";
    /*10*/ static final String BREAK_WITHOUT_LOOP = "Line:%d:break statement not within loop%n";
    /*10*/ static final String CONTINUE_WITHOUT_LOOP = "Line:%d:continue statement not within loop%n";
    /*11*/ static final String NO_SENDER_IN_INITIAL = "Line:%d:no sender in initial msghandler%n";
    /*12*/ public static final String INC_ON_RVALUE = "Line:%d:lvalue required as increment operand%n";
    /*12*/ public static final String DEC_ON_RVALUE = "Line:%d:lvalue required as decrement operand%n";
           static final String MSG_HANDLER_ARG_MATCH = "Line:%d:arguments do not match with definition%n";
           static final String SELF_OUT_OF_ACTOR = "Line:%d:self doesn't refer to any actor%n";
           public static final String SAME_SIZE_ARRAY_ASSIGN = "Line:%d:operation assign requires equal array sizes%n";

}
