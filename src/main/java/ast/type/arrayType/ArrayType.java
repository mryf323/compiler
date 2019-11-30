package ast.type.arrayType;

import ast.type.Type;
import visitor.Visitor;

public class ArrayType extends Type {
    private int size;

    public ArrayType(int size){
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    @Override
    public String toString() {
        return "int[]";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
