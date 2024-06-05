import java.io.*;
import java.util.ArrayList;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class HelloWorldVisitor extends JavaParserBaseVisitor<Void> {

    PrintWriter out;
    int tabs;

    public HelloWorldVisitor(PrintWriter out) {
        this.out = out;
        this.tabs = 0;
    }

    public int getTabs() {
        return this.tabs;
    }

    public void printTabs() {
        for (int i = 0; i < this.tabs; i++) {
            out.print("\t");
        }
    }

    public void addTab() {
        this.tabs++;
    }

    public void remTab() {
        if (this.tabs > 0) {
            this.tabs--;
        }
    }

//    @Override
//    public Void visitPubmod(JavaParser.PubmodContext ctx) {
//        out.print("public ");
//        return null;
//    }

    @Override
    public Void visitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        out.print("class " + ctx.identifier().getText() + "1 ");
        visit(ctx.classBody());
        out.println();
        return null;
    }

    @Override
    public Void visitClassBody(JavaParser.ClassBodyContext ctx) {
        out.print("{\n");
        addTab();
        printTabs();
        visit(ctx.classBodyDeclaration(0));
        out.println("\n}");
        return null;
    }

    @Override
    public Void visitClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
        visit(ctx.modifier(0));
        visit(ctx.modifier(1));
        visit(ctx.memberDeclaration());
        return null;
    }

    @Override
    public Void visitModifier(JavaParser.ModifierContext ctx) {
        visit(ctx.classOrInterfaceModifier());
        return null;
    }

    @Override
    public Void visitClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        if (ctx.PUBLIC() != null) {
            out.print(ctx.getText() + " ");
        } else if (ctx.STATIC() != null) {
            out.print(ctx.getText() + " ");
        }
        return null;
    }

    @Override
    public Void visitMemberDeclaration(JavaParser.MemberDeclarationContext ctx) {
        if (ctx.methodDeclaration() != null) {
            visit(ctx.methodDeclaration());
        }
        return null;
    }

    @Override
    public Void visitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        visit(ctx.typeTypeOrVoid());
        out.print(ctx.identifier().getText() + " ");
        visit(ctx.formalParameters());
        if (ctx.LBRACK(0) != null) {
            for (int i = 0; i < ctx.LBRACK().size(); i++) {
                out.print(ctx.LBRACK(i));
                out.print(ctx.RBRACK(i));
            }
        }
        if (ctx.qualifiedNameList() != null) {
            out.print("throws");
            visit(ctx.qualifiedNameList());
        }
        visit(ctx.methodBody());
        return null;
    }

    @Override
    public Void visitMethodBody(JavaParser.MethodBodyContext ctx) {
        if (ctx.block() != null) {
            visit(ctx.block());
        } else {
            out.print(";\n");
        }
        return null;
    }

    @Override
    public Void visitBlock(JavaParser.BlockContext ctx) {
        addTab();
        out.print("{\n");
        printTabs();
        if (ctx.blockStatement(0) != null) {
            for (JavaParser.BlockStatementContext stmt: ctx.blockStatement()) {
                visit(stmt);
            }
        }
        remTab();
        out.print("\n");
        printTabs();
        out.print("}");
        remTab();
        return null;
    }

    @Override
    public Void visitBlockStatement(JavaParser.BlockStatementContext ctx) {
        if (ctx.localVariableDeclaration() != null) {
            visit(ctx.localVariableDeclaration());
            out.print(";\n");
            printTabs();
        } else if (ctx.localTypeDeclaration() != null) {
            visit(ctx.localTypeDeclaration());
        } else {
            visit(ctx.statement());
        }
        return null;
    }

    @Override
    public Void visitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        if (ctx.variableModifier(0) != null) {
            for (JavaParser.VariableModifierContext modifier: ctx.variableModifier()) {
                visit(modifier);
            }
        }
        if (ctx.identifier() != null && ctx.expression() != null) {
            out.print("var ");
            visit(ctx.identifier());
            out.print(" = ");
            out.print(ctx.expression());
        } else {
            visit(ctx.typeType());
            visit(ctx.variableDeclarators());
        }
        return null;
    }

    @Override
    public Void visitLocalTypeDeclaration(JavaParser.LocalTypeDeclarationContext ctx) {
        if (ctx.classOrInterfaceModifier(0) != null) {
            for (JavaParser.ClassOrInterfaceModifierContext modifierContext: ctx.classOrInterfaceModifier()) {
                visit(modifierContext);
            }
        }
        if (ctx.classDeclaration() != null) {
            visit(ctx.classDeclaration());
        }
        return null;
    }

    @Override
    public Void visitVariableDeclarators(JavaParser.VariableDeclaratorsContext ctx) {
        int i = 0;
        visit(ctx.variableDeclarator(i));
        i++;
        while(ctx.variableDeclarator(i) != null) {
            out.print(", ");
            visit(ctx.variableDeclarator(i));
        }
        return null;
    }

    @Override
    public Void visitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        visit(ctx.variableDeclaratorId());
        if(ctx.variableInitializer() != null) {
            out.print(" = ");
            visit(ctx.variableInitializer());
        }
        return null;
    }

    @Override
    public Void visitStatement(JavaParser.StatementContext ctx) {
        if(ctx.blockLabel != null) {
            visit(ctx.block());
        } else if(ctx.ASSERT() != null) {
            out.print(ctx.ASSERT().getText());
            visit(ctx.expression(0));
            if (ctx.expression(1) != null) {
                out.print(":");
                visit(ctx.expression(1));
            }
            out.print(";");
        } else if(ctx.IF() != null) {
            out.print("if ");
            visit((ctx.parExpression()));
            visit(ctx.statement(0));
            if (ctx.ELSE() != null) {
                out.print("else ");
                visit(ctx.statement(1));
            }
        } else if(ctx.FOR() != null) {
            out.print("for (");
            visit(ctx.forControl());
            out.print(") ");
            visit(ctx.statement(0));
        } else if (ctx.DO() == null && ctx.WHILE() != null) {
            out.print("while ");
            visit(ctx.parExpression());
            visit(ctx.statement(0));
        } else if (ctx.DO() != null) {
            out.print("do");
            visit(ctx.statement(0));
            out.print("while");
            visit(ctx.parExpression());
            out.print(";");
        } else if (ctx.TRY() != null && ctx.resourceSpecification() == null) {
            out.print("try ");
            visit(ctx.block());
            if (ctx.catchClause(0) != null) {
                for (JavaParser.CatchClauseContext clause : ctx.catchClause()) {
                    visit(clause);
                }
                if (ctx.finallyBlock() != null) {
                    visit(ctx.finallyBlock());
                }
            } else {
                visit(ctx.finallyBlock());
            }
        } else if (ctx.TRY() != null) {
            out.print("try ");
            visit(ctx.resourceSpecification());
            visit(ctx.block());
            if (ctx.catchClause(0) != null) {
                for (JavaParser.CatchClauseContext clause : ctx.catchClause()) {
                    visit(clause);
                }
            }
            if (ctx.finallyBlock() != null) {
                visit(ctx.finallyBlock());
            }
        } else if (ctx.SWITCH() != null) {
            out.print("switch ");
            visit(ctx.parExpression());
            out.print("{");
            addTab();
            printTabs();
            if (ctx.switchBlockStatementGroup(0) != null) {
                for (JavaParser.SwitchBlockStatementGroupContext group : ctx.switchBlockStatementGroup()) {
                    visit(group);
                }
            }
            if (ctx.switchLabel(0) != null) {
                for (JavaParser.SwitchLabelContext label : ctx.switchLabel()) {
                    visit(label);
                }
            }
            remTab();
            printTabs();
            out.print("}");
        } else if (ctx.SYNCHRONIZED() != null) {
            out.print("synchronized ");
            visit(ctx.parExpression());
            visit(ctx.block());
        } else if (ctx.RETURN() != null) {
            out.print("return ");
            if (ctx.expression() != null) {
                visit(ctx.expression(0));
                out.print(";");
            }
        } else if (ctx.THROW() != null) {
            out.print("throw ");
            visit(ctx.expression(0));
            out.print(";");
        } else if (ctx.BREAK() != null) {
            out.print("break ");
            if (ctx.identifier() != null) {
                visit(ctx.identifier());
                out.print(";");
            }
        } else if (ctx.CONTINUE() != null) {
            out.print("continue ");
            if (ctx.identifier() != null) {
                visit(ctx.identifier());
                out.print(";");
            }
        } else if (ctx.YIELD() != null) {
            out.print("yield ");
            visit(ctx.expression(0));
            out.print(";");
        } else if (ctx.statementExpression != null) {
            visit(ctx.expression(0));
            out.print(";");
        } else if (ctx.switchExpression() != null) {
            visit(ctx.switchExpression());
            if (ctx.getText().contains(";")) {
                out.print(";");
            }
        } else if (ctx.identifierLabel != null) {
            visit(ctx.identifier());
            out.print(":");
            visit(ctx.statement(0));
        } else if (ctx.SEMI() != null) {
            out.print(ctx.SEMI().getText());
        }
        return null;
    }

//    @Override
//    public Void visitForControl(JavaParser.ForControlContext ctx) {
//
//        return null;
//    }

//    @Override
//    public Void visitResourceSpecification(JavaParser.ResourceSpecificationContext ctx) {
//
//        return null;
//    }

//    @Override
//    public Void visitCatchClause(JavaParser.CatchClauseContext ctx) {
//
//        return null;
//    }

//    @Override
//    public Void visitFinallyBlock(JavaParser.FinallyBlockContext ctx) {
//
//        return null;
//    }

//    @Override
//    public Void visitSwitchLabel(JavaParser.SwitchLabelContext ctx) {
//
//        return null;
//    }

//    @Override
//    public Void visitSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) {
//
//        return null;
//    }

    @Override
    public Void visitExpressionList(JavaParser.ExpressionListContext ctx) {
        visit(ctx.expression(0));
        if (ctx.expression(1) != null) {
            for (int i = 1; i < ctx.expression().size(); i++) {
                out.print(", ");
                visit(ctx.expression(i));
            }
        }
        return null;
    }

    @Override
    public Void visitPrimaryExpr(JavaParser.PrimaryExprContext ctx) {
        visit(ctx.primary());
        return null;
    }

    @Override
    public Void visitBrackExpr(JavaParser.BrackExprContext ctx) {
        visit(ctx.expression(0));
        out.print("[");
        visit(ctx.expression(1));
        out.print("]");
        return null;
    }

    @Override
    public Void visitBopExpr(JavaParser.BopExprContext ctx) {
        visit(ctx.expression());
        out.print(".");
        if (ctx.identifier() != null) {
            visit(ctx.identifier());
        } else if(ctx.methodCall() != null) {
            visit(ctx.methodCall());
        } else if(ctx.THIS() != null) {
            out.print(ctx.THIS().getText());
        } else if(ctx.NEW() != null) {
            out.print(ctx.NEW().getText());
            if (ctx.nonWildcardTypeArguments() != null) {
                visit(ctx.nonWildcardTypeArguments());
            }
            visit(ctx.innerCreator());
        } else if (ctx.SUPER() != null) {
            out.print(ctx.SUPER().getText());
            visit(ctx.superSuffix());
        } else if(ctx.explicitGenericInvocation() != null) {
            visit(ctx.explicitGenericInvocation());
        }
        return null;
    }

    @Override
    public Void visitMethCallExpr(JavaParser.MethCallExprContext ctx) {
        visit(ctx.methodCall());
        return null;
    }

    @Override
    public Void visitMethRef1Expr(JavaParser.MethRef1ExprContext ctx) {
        visit(ctx.expression());
        out.print("::");
        if (ctx.typeArguments() != null) {
            visit(ctx.typeArguments());
        }
        visit(ctx.identifier());
        return null;
    }

    @Override
    public Void visitMethRef2Expr(JavaParser.MethRef2ExprContext ctx) {
        visit(ctx.typeType());
        out.print("::");
        if (ctx.identifier() != null) {
            if(ctx.typeArguments() != null) {
                visit(ctx.typeArguments());
            }
            visit(ctx.identifier());
        } else {
            out.print(ctx.NEW().getText());
        }
        return null;
    }

    @Override
    public Void visitMethRef3Expr(JavaParser.MethRef3ExprContext ctx) {
        visit(ctx.classType());
        out.print("::");
        if(ctx.typeArguments() != null) {
            visit(ctx.typeArguments());
        }
        out.print(ctx.NEW().getText());
        return null;
    }
    @Override
    public Void visitSwtchExpr(JavaParser.SwtchExprContext ctx) {
        visit(ctx.switchExpression());
        return null;
    }

    @Override
    public Void visitIncDecOpExpr(JavaParser.IncDecOpExprContext ctx) {
        visit(ctx.expression());
        out.print(ctx.postfix.getText());
        return null;
    }

    @Override
    public Void visitUnaryOpExpr(JavaParser.UnaryOpExprContext ctx) {
        out.print(ctx.prefix.getText());
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitCastExpr(JavaParser.CastExprContext ctx) {
        out.print("(");
        for(JavaParser.AnnotationContext annot : ctx.annotation()) {
            visit(annot);
        }
        visit(ctx.typeType(0));
        if (ctx.typeType(1) != null) {
            for(int i = 1; i < ctx.typeType().size(); i++) {
                out.print("&");
                visit(ctx.typeType(i));
            }
        }
        out.print(")");
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitObjCreateExpr(JavaParser.ObjCreateExprContext ctx) {
        out.print(ctx.NEW().getText());
        visit(ctx.creator());
        return null;
    }

    @Override
    public Void visitMultOpExpr(JavaParser.MultOpExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitAddOpExpr(JavaParser.AddOpExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitShiftOpExpr(JavaParser.ShiftOpExprContext ctx) {
        visit(ctx.expression(0));
        if (ctx.getText().contains(">>>")) {
            out.print(">>>");
        } else if (ctx.getText().contains(">>")) {
            out.print(">>");
        } else {
            out.print("<<");
        }
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitRelOpExpr(JavaParser.RelOpExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitInstOfOpExpr(JavaParser.InstOfOpExprContext ctx) {
        visit(ctx.expression());
        out.print(ctx.bop.getText());
        if (ctx.typeType() != null) {
            visit(ctx.typeType());
        } else {
            visit(ctx.pattern());
        }
        return null;
    }

    @Override
    public Void visitEquaOpExpr(JavaParser.EquaOpExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitBitAndOpExpr(JavaParser.BitAndOpExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitBitXorOpExpr(JavaParser.BitXorOpExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitBitOrExpr(JavaParser.BitOrExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitLogAndExpr(JavaParser.LogAndExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitLogOrExpr(JavaParser.LogOrExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitTernExpr(JavaParser.TernExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        out.print(":");
        visit(ctx.expression(2));
        return null;
    }

    @Override
    public Void visitAssignExpr(JavaParser.AssignExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText());
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitLambdaExpr(JavaParser.LambdaExprContext ctx) {
        visit(ctx.lambdaExpression());
        return null;
    }

    @Override
    public Void visitPattern(JavaParser.PatternContext ctx) {
        for (JavaParser.VariableModifierContext var : ctx.variableModifier()) {
            visit(var);
        }
        visit(ctx.typeType());
        for (JavaParser.AnnotationContext annot : ctx.annotation()) {
            visit(annot);
        }
        visit(ctx.identifier());
        return null;
    }

    @Override
    public Void visitLambdaExpression(JavaParser.LambdaExpressionContext ctx) {
        visit(ctx.lambdaParameters());
        out.print("->");
        visit(ctx.lambdaBody());
        return null;
    }

    @Override
    public Void visitLambIdent(JavaParser.LambIdentContext ctx) {
        visit(ctx.identifier());
        return null;
    }

    @Override
    public Void visitLambList(JavaParser.LambListContext ctx) {
        out.print("(");
        if (ctx.formalParameterList() != null) {
            visit(ctx.formalParameterList());
        }
        out.print(")");
        return null;
    }

    @Override
    public Void visitLambIdent2(JavaParser.LambIdent2Context ctx) {
        out.print("(");
        visit(ctx.identifier(0));
        if (ctx.identifier(1) != null) {
            for (int i = 1; i < ctx.identifier().size(); i++) {
                out.print(", ");
                visit(ctx.identifier(i));
            }
        }
        out.print(")");
        return null;
    }

    @Override
    public Void visitLambLvti(JavaParser.LambLvtiContext ctx) {
        out.print("(");
        if (ctx.lambdaLVTIList() != null) {
            visit(ctx.lambdaLVTIList());
        }
        out.print(")");
        return null;
    }

    @Override
    public Void visitLambBodyExpr(JavaParser.LambBodyExprContext ctx) {
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitLambBodyBloc(JavaParser.LambBodyBlocContext ctx) {
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitPrimExpr(JavaParser.PrimExprContext ctx) {
        out.print("(");
        visit(ctx.expression());
        out.print(")");
        return null;
    }

    @Override
    public Void visitPrimThis(JavaParser.PrimThisContext ctx) {
        out.print(ctx.THIS().getText());
        return null;
    }

    @Override
    public Void visitPrimSuper(JavaParser.PrimSuperContext ctx) {
        out.print(ctx.SUPER().getText());
        return null;
    }

    @Override
    public Void visitPrimLit(JavaParser.PrimLitContext ctx) {
        visit(ctx.literal());
        return null;
    }

    @Override
    public Void visitPrimIdent(JavaParser.PrimIdentContext ctx) {
        visit(ctx.identifier());
        return null;
    }

    @Override
    public Void visitPrimClass(JavaParser.PrimClassContext ctx) {
        visit(ctx.typeTypeOrVoid());
        out.print(".");
        out.print(ctx.CLASS().getText());
        return null;
    }

    @Override
    public Void visitPrimWild(JavaParser.PrimWildContext ctx) {
        visit(ctx.nonWildcardTypeArguments());
        if (ctx.explicitGenericInvocationSuffix() != null) {
            visit(ctx.explicitGenericInvocationSuffix());
        } else {
            out.print(ctx.THIS().getText());
            visit(ctx.arguments());
        }
        return null;
    }

    @Override
    public Void visitIntLit(JavaParser.IntLitContext ctx) {
        visit(ctx.integerLiteral());
        return null;
    }

    @Override
    public Void visitFloatLit(JavaParser.FloatLitContext ctx) {
        visit(ctx.floatLiteral());
        return null;
    }

    @Override
    public Void visitCharLit(JavaParser.CharLitContext ctx) {
        out.print(ctx.CHAR_LITERAL().getText());
        return null;
    }

    @Override
    public Void visitStringLit(JavaParser.StringLitContext ctx) {
        out.print(ctx.STRING_LITERAL().getText());
        return null;
    }

    @Override
    public Void visitBoolLit(JavaParser.BoolLitContext ctx) {
        out.print(ctx.BOOL_LITERAL().getText());
        return null;
    }

    @Override
    public Void visitNullLit(JavaParser.NullLitContext ctx) {
        out.print(ctx.NULL_LITERAL().getText());
        return null;
    }

    @Override
    public Void visitTxtBlocLit(JavaParser.TxtBlocLitContext ctx) {
        out.print(ctx.TEXT_BLOCK().getText());
        return null;
    }

    @Override
    public Void visitTypeTypeOrVoid(JavaParser.TypeTypeOrVoidContext ctx) {
        if (ctx.typeType() != null) {
            visit(ctx.typeType());
        } else if (ctx.VOID() != null) {
            out.print(ctx.getText() + " ");
        }
        return null;
    }

    @Override
     public Void visitFormalParameters(JavaParser.FormalParametersContext ctx) {
        out.print("(");
        if (ctx.receiverParameter() != null) {
            visit(ctx.receiverParameter());
            if (ctx.formalParameterList() != null) {
                out.print(", ");
                visit(ctx.formalParameterList());
            }
        } else if(ctx.formalParameterList() != null) {
            visit(ctx.formalParameterList());
        }
        out.print(") ");
        return null;
    }

    @Override
    public Void visitReceiverParameter(JavaParser.ReceiverParameterContext ctx) {
        visit(ctx.typeType());
        int i = 0;
        if (ctx.identifier(i) != null) {
            visit(ctx.identifier(i));
            i++;
            while (ctx.identifier(i) != null) {
                out.print(".");
                visit(ctx.identifier(i));
                i++;
            }
        }
        out.print("this");
        return null;
    }

    @Override
    public Void visitFormalParameterList(JavaParser.FormalParameterListContext ctx) {
        if (ctx.formalParameter(0) != null) {
            int i = 0;
            visit(ctx.formalParameter(i));

            i++;
            while (ctx.formalParameter(i) != null) {
                out.print(", ");
                visit(ctx.formalParameter(i));
                i++;
            }

            if (ctx.lastFormalParameter() != null) {
                visit(ctx.lastFormalParameter());
            }
        } else {
            visit(ctx.lastFormalParameter());
        }
        return null;

    }

    @Override
    public Void visitFormalParameter(JavaParser.FormalParameterContext ctx) {
        if (ctx.variableModifier(0) != null) {
            for (JavaParser.VariableModifierContext modifier: ctx.variableModifier()) {
                visit(modifier);

            }
        }
        visit(ctx.typeType());
        visit(ctx.variableDeclaratorId());
        return null;
    }

    @Override
    public Void visitQualifiedName(JavaParser.QualifiedNameContext ctx) {
        int i = 0;
        visit(ctx.identifier(i));
        i++;
        while (ctx.identifier(i) != null) {
            out.print(".");
            visit(ctx.identifier(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
        out.print(" " + ctx.getText());
        if (ctx.getText().contains("[")) {
            out.print("[]");
        }
        return null;
    }

    @Override
    public Void visitLastFormalParameter(JavaParser.LastFormalParameterContext ctx) {
        if (ctx.variableModifier(0) != null) {
            for (JavaParser.VariableModifierContext modifier: ctx.variableModifier()) {
                visit(modifier);
            }
        }
        if (ctx.annotation(0) != null) {
            for (JavaParser.AnnotationContext annotation: ctx.annotation()) {
                visit(annotation);
            }
        }
        visit(ctx.typeType());
        visit(ctx.variableDeclaratorId());
        return null;
    }

    @Override
    public Void visitVariableModifier(JavaParser.VariableModifierContext ctx) {
        if (ctx.annotation() != null) {
            visit(ctx.annotation());
        } else {
            out.print("FINAL ");
        }
        return null;
    }

    @Override
    public Void visitAnnotation(JavaParser.AnnotationContext ctx) {
        // may not need this right now
        return null;
    }

    @Override
    public Void visitTypeType(JavaParser.TypeTypeContext ctx) {
        if (ctx.annotation(0) != null) {            // may need to separate these out
            for (JavaParser.AnnotationContext annotation: ctx.annotation()) {
                visit(annotation);
            }
        }
        if (ctx.classOrInterfaceType() != null) {
            visit(ctx.classOrInterfaceType());
        } else {
            visit(ctx.primitiveType());
        }
        if (ctx.getText().contains("[")) {
            out.print("[]");
        }
        return null;
    }

    @Override
    public Void visitClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
        int i = 0;
        if (ctx.identifier(0) != null) {
            visit(ctx.identifier(i));
            i++;
            while (ctx.identifier(i) != null && i < ctx.identifier().size() - 1) {
                out.print(" ");
                visit(ctx.identifier(i));
                if (ctx.typeArguments(i) != null) {
                    out.print(" ");
                    visit(ctx.typeArguments(i));
                }
                i++;
            }
            out.print(".");
        }
        visit(ctx.typeIdentifier());
        if (ctx.typeArguments(i) != null) {
            visit(ctx.typeArguments(i));
        }
        return null;
    }

    @Override
    public Void visitIdentifier(JavaParser.IdentifierContext ctx) {
        out.print(ctx.getText());
        return null;
    }

    @Override
    public Void visitTypeArguments(JavaParser.TypeArgumentsContext ctx) {
        out.print("<");
        for (JavaParser.TypeArgumentContext argument: ctx.typeArgument()) {
            visit(argument);
            out.print(", ");
        }
        out.print(">");
        return null;
    }

    @Override
    public Void visitTypeArgument(JavaParser.TypeArgumentContext ctx) {
        if (ctx.getText().contains("?")) {
            if (ctx.annotation(0) != null) {
                for (JavaParser.AnnotationContext annotation : ctx.annotation()) {
                    visit(annotation);
                }
            }
            out.print("? ");
            if (ctx.SUPER() != null) {
                out.print("SUPER");
            } else {
                out.print("EXTENDS");
            }
        }
        visit(ctx.typeType());
        return null;
    }

    @Override
    public Void visitPrimitiveType(JavaParser.PrimitiveTypeContext ctx) {
        out.print(ctx.getText() + " ");
        return null;
    }

    @Override
    public Void visitTypeIdentifier(JavaParser.TypeIdentifierContext ctx) {
        out.print(ctx.getText());
        return null;
    }

    @Override
    public Void visitArguments(JavaParser.ArgumentsContext ctx) {
        out.print("(");
        if (ctx.expressionList() != null) {
            visit(ctx.expressionList());
        }
        out.print(")");
        return null;
    }
}