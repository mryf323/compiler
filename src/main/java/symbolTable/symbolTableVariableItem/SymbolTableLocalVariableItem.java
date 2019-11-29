package symbolTable.symbolTableVariableItem;

import ast.type.Type;
import ast.node.declaration.VarDeclaration;

public class SymbolTableLocalVariableItem extends SymbolTableVariableItem {
    
    public SymbolTableLocalVariableItem(String name, Type type, int index)
    {
        super(name ,type ,index);
    }
    
    public SymbolTableLocalVariableItem(VarDeclaration localVar)
    {
        super(localVar);
    }
}
