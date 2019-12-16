package visitor.typeAnalysis;

import ast.type.NoType;
import ast.type.Type;

public class AnalysedType<T extends Type> {
    private final T type;
    private final boolean lValue;

    public static final AnalysedType<NoType> NO_TYPE = new AnalysedType<>(new NoType(), true);

    public AnalysedType(T type, boolean lValue) {
        this.type = type;
        this.lValue = lValue;
    }

    public T getType() {
        return type;
    }

    public boolean islValue() {
        return lValue;
    }
}
