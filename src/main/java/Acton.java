import ast.node.Program;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parsers.actonLexer;
import parsers.actonParser;
import symbolTable.SymbolTable;
import visitor.FirstPassSymbolTableInitializer;
import visitor.codeGenerator.CodeGeneratorVisitor;
import visitor.codeGenerator.DefaultActorGenerator;
import visitor.typeAnalysis.TypeAnalyserVisitor;

import java.io.IOException;

// Visit https://stackoverflow.com/questions/26451636/how-do-i-use-antlr-generated-parser-and-lexer
public class Acton {


    public static void main(String[] args) throws IOException {
        CharStream reader = CharStreams.fromFileName(args[1]); // arg[ 1 ] : acton source file
        actonLexer lexer = new actonLexer(reader);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        actonParser parser = new actonParser(tokens);
        Program program = parser.program().p; /* assuming that the name of the Program ast node that the program rule returns is p */
        SymbolTable global = new FirstPassSymbolTableInitializer().visit(program);
        //new InheritedSymbolTableFiller(global).fill(program.getActors());
        new TypeAnalyserVisitor(global).visit(program);
        new CodeGeneratorVisitor(global).visit(program);
        new DefaultActorGenerator(global).visit(program);
    }
}