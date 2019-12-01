package symbolTable.symbolTableVariableItem;

import ast.node.declaration.VarDeclaration;
import ast.type.Type;

public class SymbolTableKnownActorItem extends SymbolTableVariableItem {
    
    public SymbolTableKnownActorItem(String name, Type type, int index)
    {
        super(name ,type ,index);
    }
    
    public SymbolTableKnownActorItem(VarDeclaration localVar)
    {
        super(localVar);
    }
}
