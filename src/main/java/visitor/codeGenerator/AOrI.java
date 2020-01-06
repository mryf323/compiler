package visitor.codeGenerator;

import ast.type.Type;
import ast.type.primitiveType.BooleanType;
import ast.type.primitiveType.IntType;

public class AOrI {

    public static String eval(Type type){
        return type instanceof IntType ||
                type instanceof BooleanType
                ? "i" : "a";
    }
}
