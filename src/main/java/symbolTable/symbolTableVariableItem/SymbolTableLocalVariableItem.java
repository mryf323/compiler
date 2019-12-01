package symbolTable.symbolTableVariableItem;

import ast.node.declaration.VarDeclaration;
import ast.type.Type;

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
