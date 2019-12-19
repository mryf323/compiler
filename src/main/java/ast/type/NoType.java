package ast.type;

import visitor.Visitor;

public class NoType extends Type {
    @Override
    public String toString() {
        return "NoType";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
