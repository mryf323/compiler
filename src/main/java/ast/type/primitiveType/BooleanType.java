package ast.type.primitiveType;

import ast.type.Type;
import visitor.Visitor;

public class BooleanType extends Type {
    @Override
    public String toString() {
        return "boolean";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
