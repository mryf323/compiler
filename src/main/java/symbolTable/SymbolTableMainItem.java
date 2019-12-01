package symbolTable;

import ast.node.Main;

public class SymbolTableMainItem extends SymbolTableItem {
        
    protected SymbolTable mainSymbolTable;
    protected Main programMain;
    public static final String STARTKEY = "Main_";
    public static String NAME = "src/main";


    public SymbolTableMainItem(Main programMain)
    {
        this.name = NAME;
        this.programMain = programMain;
    }
    
    public void setMainSymbolTable(SymbolTable mainSymbolTable)
    {
        this.mainSymbolTable = mainSymbolTable;
    }

    public SymbolTable getMainSymbolTable()
    {
        return mainSymbolTable;
    }

    public Main getProgramMain()
    {
        return programMain;
    }



    @Override
    public String getKey()
    {
        return SymbolTableMainItem.STARTKEY + this.name;
    }
}
