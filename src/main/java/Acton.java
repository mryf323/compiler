import ast.node.Program;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parsers.actonLexer;
import parsers.actonParser;
import symbolTable.SymbolTable;
import visitor.FirstPassSymbolTableInitializer;
import visitor.nameAnalysis.NameAnalyserVisitor;
import visitor.typeAnalysis.InheritedSymbolTableFiller;
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
        new InheritedSymbolTableFiller(global).fill(program.getActors());
        boolean success = new NameAnalyserVisitor(global).visit(program);
        /*if(success)
            new AstPrinter().visit(program);*/
        new TypeAnalyserVisitor(global).visit(program);
    }
}