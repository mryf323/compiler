package symbolTable.symbolTableVariableItem;

import ast.type.Type;
import ast.node.declaration.VarDeclaration;
import visitor.SymbolTableItemVisitor;

public class SymbolTableHandlerArgumentItem extends SymbolTableVariableItem {
    
    public SymbolTableHandlerArgumentItem(String name, Type type, int index)
    {
        super(name, type, index);
    }

    public SymbolTableHandlerArgumentItem(VarDeclaration argDeclaration)
    {
        super(argDeclaration);
    }

    @Override
    public <T> T accept(SymbolTableItemVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
