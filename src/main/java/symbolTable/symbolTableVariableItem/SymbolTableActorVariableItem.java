package symbolTable.symbolTableVariableItem;

import ast.type.Type;
import ast.node.declaration.VarDeclaration;
import visitor.SymbolTableItemVisitor;

public class SymbolTableActorVariableItem extends SymbolTableVariableItem{
    
    public SymbolTableActorVariableItem(String name, Type type, int index)
    {
        super(name, type, index);
    }
    
    public SymbolTableActorVariableItem(VarDeclaration actorVar)
    {
        super(actorVar);
    }

    @Override
    public <T> T accept(SymbolTableItemVisitor<T> visitor) {
        return visitor.visit(this);
    }
}