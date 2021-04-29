package org.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
        String outputDir = "/Users/faustotnc/Documents/GitHub/Hinton-Lang/src/org/hinton_lang/Parser/AST/";

        // Expressions
        ArrayList<String> expressionList = new ArrayList<>();
        expressionList.add("Assign              : Token name, Expr value");
        expressionList.add("Binary              : Expr left, Token operator, Expr right");
        expressionList.add("Lambda              : List<Stmt.Parameter> params, List<Stmt> body");
        expressionList.add("Call                : Expr callee, Token paren, List<Expr.Argument> arguments");
        expressionList.add("Argument            : Token name, Expr value");
        expressionList.add("MemberAccess        : Expr object, Token name");
        expressionList.add("MemberSetter        : Expr object, Token name, Expr value");
        expressionList.add("Grouping            : Expr expression");
        expressionList.add("Literal             : Object value");
        expressionList.add("Logical             : Expr left, Token operator, Expr right");
        expressionList.add("Array               : List<Expr> expressions");
        expressionList.add("Indexing            : Token token, Expr arr, Expr index");
        expressionList.add("ArrayItemSetter     : Token token, Expr.Indexing target, Expr value");
        expressionList.add("Dictionary          : List<Expr.KeyValPair> members");
        expressionList.add("KeyValPair          : Token key, Expr val");
        expressionList.add("Unary               : Token operator, Expr right");
        expressionList.add("DeIn_crement        : Token operator, Expr operand, boolean isPre");
        expressionList.add("Variable            : Token name");
        defineAst(outputDir, "Expr", expressionList);

        // Statements
        ArrayList<String> statementList = new ArrayList<>();
        statementList.add("Import       : List<Stmt> statements");
        statementList.add("Block        : List<Stmt> statements");
        statementList.add("Expression   : Expr expression");
        statementList.add("Function     : Token name, List<Stmt.Parameter> params, List<Stmt> body");
        statementList.add("Parameter    : Token name, boolean isOptnl, Expr defVal");
        statementList.add("If           : Expr condition, Stmt thenBranch, Stmt elseBranch");
        statementList.add("Break        : Token keyword");
        statementList.add("Continue     : Token keyword");
        statementList.add("Return       : Token keyword, Expr value");
        statementList.add("While        : Expr condition, Stmt body");
        statementList.add("Var          : Token name, Expr initializer");
        statementList.add("Const        : Token name, Expr initializer");
        statementList.add("Enum         : Token name, List<Stmt.EnumMember> members");
        statementList.add("EnumMember   : Token name, HintonInteger idx");
        defineAst(outputDir, "Stmt", statementList);
    }

    private static void defineAst(String outputDir, String baseName, ArrayList<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package org.hinton_lang.Parser.AST;");
        writer.println();
        writer.println("import java.util.List;");
        if (baseName == "Stmt") {
            writer.println("import org.hinton_lang.Interpreter.HintonInteger.HintonInteger;");
        }
        writer.println("import org.hinton_lang.Tokens.Token;");
        writer.println();
        writer.println("public abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        // The base accept() method.
        writer.println();
        writer.println("    public abstract <R> R accept(Visitor<R> visitor);");

        // The AST classes.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, ArrayList<String> types) {
        writer.println();
        writer.println("    public interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        public R visit" + typeName + baseName + "(" + typeName + " "
                    + baseName.toLowerCase() + ");");
        }

        writer.println("    }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("\n");
        writer.println("    public static class " + className + " extends " + baseName + " {");
        // Store parameters in fields.
        String[] fields = fieldList.split(", ");

        // Fields.
        for (String field : fields) {
            writer.println("        public final " + field + ";");
        }
        writer.println();

        // Constructor.
        writer.println("        public " + className + "(" + fieldList + ") {");

        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");

        // Visitor pattern.
        writer.println();
        writer.println("        @Override");
        writer.println("        public <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");

        writer.println("    }");
    }
}