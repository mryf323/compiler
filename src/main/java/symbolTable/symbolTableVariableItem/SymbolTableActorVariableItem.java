package symbolTable.symbolTableVariableItem;

import ast.node.declaration.VarDeclaration;
import ast.type.Type;

public class SymbolTableActorVariableItem extends SymbolTableVariableItem{
    
    public SymbolTableActorVariableItem(String name, Type type, int index)
    {
        super(name, type, index);
    }
    
    public SymbolTableActorVariableItem(VarDeclaration actorVar)
    {
        super(actorVar);
    }

}