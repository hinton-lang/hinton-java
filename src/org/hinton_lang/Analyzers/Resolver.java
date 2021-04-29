package org.hinton_lang.Analyzers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.hinton_lang.Hinton;
import org.hinton_lang.Envornment.DecType;
import org.hinton_lang.Interpreter.Interpreter;
import org.hinton_lang.Parser.*;
import org.hinton_lang.Scanner.Token;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    // Keeps track of the type of function being resolved
    // when resolving a function/method.
    private FunctionType currentFunction = FunctionType.NONE;
    // True when resolving a loop
    private boolean isInsideLoop = false;

    /** The types of functions that can exist in Hinton */
    public enum FunctionType {
        NONE, FUNCTION, LAMBDA
    }

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    public void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    public void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    public void resolve(Expr expr) {
        expr.accept(this);
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    private void endScope() {
        scopes.pop();
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name, DecType.VARIABLE);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitConstStmt(Stmt.Const stmt) {
        declare(stmt.name, DecType.CONSTANT);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    private void declare(Token name, DecType decType) {
        if (scopes.isEmpty())
            return;

        Map<String, Boolean> scope = scopes.peek();

        if (scope.containsKey(name.lexeme)) {
            String type;
            if (decType == DecType.VARIABLE) {
                type = "Variable";
            } else if (decType == DecType.CONSTANT) {
                type = "Constant";
            } else {
                type = "Function";
            }

            Hinton.error(name, type + " with this name already exists in this scope.");
        }

        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty())
            return;
        scopes.peek().put(name.lexeme, true);
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Hinton.error(expr.name, "Can't read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name, DecType.FUNCTION);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Stmt param : function.params) {
            resolve(param);
        }
        resolve(function.body);
        endScope();

        currentFunction = enclosingFunction;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null)
            resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Hinton.error(stmt.keyword, "Can't return from top-level code.");
        }

        if (stmt.value != null) {
            resolve(stmt.value);
        }

        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        boolean wasInLoop = isInsideLoop;
        isInsideLoop = true;
        resolve(stmt.condition);
        resolve(stmt.body);

        isInsideLoop = wasInLoop;
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);

        for (Expr argument : expr.arguments) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitImportStmt(Stmt.Import stmt) {
        resolve(stmt.statements);
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        if (!isInsideLoop) {
            Hinton.error(stmt.keyword, "Can't break from outside of a loop.");
        }
        return null;
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        if (!isInsideLoop) {
            Hinton.error(stmt.keyword, "Can't continue from outside of a loop.");
        }
        return null;
    }

    @Override
    public Void visitLambdaExpr(Expr.Lambda expr) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = FunctionType.LAMBDA;

        beginScope();
        for (Stmt.Parameter param : expr.params) {
            declare(param.name, DecType.VARIABLE);
            define(param.name);
        }
        resolve(expr.body);
        endScope();

        currentFunction = enclosingFunction;

        return null;
    }

    @Override
    public Void visitArrayExpr(Expr.Array expr) {
        for (Expr expression : expr.expressions) {
            resolve(expression);
        }
        return null;
    }

    @Override
    public Void visitIndexingExpr(Expr.Indexing expr) {
        resolve(expr.arr);
        resolve(expr.index);
        return null;
    }

    @Override
    public Void visitMemberAccessExpr(Expr.MemberAccess expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitMemberSetterExpr(Expr.MemberSetter expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitArrayItemSetterExpr(Expr.ArrayItemSetter expr) {
        resolve(expr.target);
        resolve(expr.value);
        return null;
    }

    @Override
    public Void visitEnumStmt(Stmt.Enum stmt) {
        declare(stmt.name, DecType.ENUMERABLE);
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitEnumMemberStmt(Stmt.EnumMember stmt) {
        resolve(stmt);
        return null;
    }

    @Override
    public Void visitDictionaryExpr(Expr.Dictionary expr) {
        for (Expr.KeyValPair element : expr.members) {
            resolve(element);
        }
        return null;
    }

    @Override
    public Void visitKeyValPairExpr(Expr.KeyValPair expr) {
        resolve(expr.val);
        return null;
    }

    @Override
    public Void visitDeIn_crementExpr(Expr.DeIn_crement expr) {
        if (expr.operand instanceof Expr.Variable) {
            Expr.Variable var = (Expr.Variable) expr.operand;
            resolve(var);
            resolveLocal(var, var.name);
        } else {
            resolve(expr.operand);
        }

        return null;
    }

    @Override
    public Void visitParameterStmt(Stmt.Parameter param) {
        declare(param.name, DecType.VARIABLE);
        define(param.name);
        resolve(param.defVal);
        return null;
    }

    @Override
    public Void visitArgumentExpr(Expr.Argument arg) {
        resolve(arg.value);
        return null;
    }
}
