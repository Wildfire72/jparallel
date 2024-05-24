import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class HelloWorldVisitor extends JavaParserBaseVisitor<Void> {

    PrintWriter out;

    public HelloWorldVisitor(PrintWriter out) {
        this.out = out;
    }

//    @Override
//    public Void visitPubmod(JavaParser.PubmodContext ctx) {
//        out.print("public ");
//        return null;
//    }

    @Override
    public Void visitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        out.print("class " + ctx.identifier().getText() + " ");
        visit(ctx.classBody());
        out.println();
        return null;
    }

    @Override
    public Void visitClassBody(JavaParser.ClassBodyContext ctx) {
        out.print("{\n\t");
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

        out.print(") ");
        return null;
    }

}