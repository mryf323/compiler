package ast.type.primitiveType;

import ast.type.Type;
import visitor.Visitor;

public class IntType extends Type {
    @Override
    public String toString() {
        return "int";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntType;
    }
}
