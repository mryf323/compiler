package symbolTable.symbolTableVariableItem;

import ast.type.Type;
import ast.node.declaration.VarDeclaration;
import visitor.SymbolTableItemVisitor;

public class SymbolTableKnownActorItem extends SymbolTableVariableItem {
    
    public SymbolTableKnownActorItem(String name, Type type, int index)
    {
        super(name ,type ,index);
    }
    
    public SymbolTableKnownActorItem(VarDeclaration localVar)
    {
        super(localVar);
    }

    @Override
    public <T> T accept(SymbolTableItemVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
