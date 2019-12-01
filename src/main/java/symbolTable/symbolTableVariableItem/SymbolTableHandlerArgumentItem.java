package symbolTable.symbolTableVariableItem;

import ast.node.declaration.VarDeclaration;
import ast.type.Type;

public class SymbolTableHandlerArgumentItem extends SymbolTableVariableItem {
    
    public SymbolTableHandlerArgumentItem(String name, Type type, int index)
    {
        super(name, type, index);
    }

    public SymbolTableHandlerArgumentItem(VarDeclaration argDeclaration)
    {
        super(argDeclaration);
    }
}
