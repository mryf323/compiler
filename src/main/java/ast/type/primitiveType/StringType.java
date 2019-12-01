package ast.type.primitiveType;

import ast.type.Type;
import visitor.Visitor;

public class StringType extends Type {
    @Override
    public String toString() {
        return "string";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
