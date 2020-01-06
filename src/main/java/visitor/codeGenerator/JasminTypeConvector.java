package visitor.codeGenerator;

import ast.type.Type;
import ast.type.actorType.ActorType;
import ast.type.arrayType.ArrayType;
import ast.type.primitiveType.BooleanType;
import ast.type.primitiveType.IntType;
import ast.type.primitiveType.StringType;

public class JasminTypeConvector {
    public static String getJasminType(Type type) {
        if (type instanceof IntType) {
            return "I";
        } else if (type instanceof BooleanType) {
            return "Z";
        } else if (type instanceof ArrayType) {
            return "[I";
        } else if (type instanceof StringType) {
            return "Ljava/lang/String;";
        } else if (type instanceof ActorType) {
            String className = ((ActorType) type).getName().getName();
            return "L" + className + ";";
        }
        throw new RuntimeException("Invalid given Type :"+type+" to convert to Jasmin.");
    }
}
