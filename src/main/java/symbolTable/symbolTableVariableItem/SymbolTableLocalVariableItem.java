package symbolTable.symbolTableVariableItem;

import ast.type.Type;
import ast.node.declaration.VarDeclaration;
import visitor.SymbolTableItemVisitor;

public class SymbolTableLocalVariableItem extends SymbolTableVariableItem {
    
    public SymbolTableLocalVariableItem(String name, Type type, int index)
    {
        super(name ,type ,index);
    }
    
    public SymbolTableLocalVariableItem(VarDeclaration localVar)
    {
        super(localVar);
    }

    @Override
    public <T> T accept(SymbolTableItemVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
