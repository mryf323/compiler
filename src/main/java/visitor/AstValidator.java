package visitor;

import ast.node.Node;
import ast.node.Program;
import ast.node.declaration.ActorDeclaration;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class AstValidator {
    public void validate(Program p) throws IllegalAccessException {
        for (ActorDeclaration actor : p.getActors()) {
            validateNode(actor);
        }
        validateNode(p.getMain());
    }
    public void validateNode(Node node) throws IllegalAccessException {

        assert (node.getLine() > 0);
        for (Field field : node.getClass().getFields()) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(Node.class)){
                assert !field.isAnnotationPresent(Nonnull.class) || field.get(node) != null;
                if (field.get(node) != null) {
                    validateNode((Node) field.get(node));
                }
            }
            field.setAccessible(accessible);
        }

    }
}
