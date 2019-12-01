package ast.node.declaration.handler;

import ast.node.expression.Identifier;
import visitor.Visitor;

public class MsgHandlerDeclaration extends HandlerDeclaration {

    public MsgHandlerDeclaration(Identifier name) {
        this.name = name;
    }

	@Override
    public String toString() {
        return "MsgHandlerDeclaration";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
