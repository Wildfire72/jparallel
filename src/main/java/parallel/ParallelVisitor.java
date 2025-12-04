//package parallel;

import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.ArrayList;

public class ParallelVisitor extends JavaParserBaseVisitor<Void> {

    static boolean inForLoop = false;
    static int totalThreads = 0;
    static int numThreads;
    static int threadStart = 0;
    PrintWriter out;
    int tabs;
    static ArrayList<String> localVariables = new ArrayList<String>();
    static ArrayList<String> assignedVariables = new ArrayList<String>();

    public ParallelVisitor(PrintWriter out) {
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
//    public Void visitCompUnitPack(JavaParser.CompUnitPackContext ctx) {
//        if (ctx.packageDeclaration() != null) {
//            visit(ctx.packageDeclaration());
//        }
//
//        return null;
//    }

    @Override
    public Void visitCompUnitEOF(JavaParser.CompUnitEOFContext ctx) {
        visit(ctx.moduleDeclaration());
        out.print(ctx.EOF().getText());
        return null;
    }

    @Override
    public Void visitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        for (JavaParser.AnnotationContext annot : ctx.annotation()) {
            visit(annot);
        }
        out.print(ctx.PACKAGE().getText() + " ");
        visit(ctx.qualifiedName());
        out.print(";\n");
        return null;
    }

    @Override
    public Void visitImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        out.print(ctx.IMPORT().getText() + " ");
        if (ctx.STATIC() != null) {
            out.print(ctx.STATIC().getText() + " ");
        }
        visit(ctx.qualifiedName());
        if (ctx.DOT() != null) {
            out.print(".*");
        }
        out.print(";\n");
        return null;
    }

    @Override
    public Void visitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        for (JavaParser.ClassOrInterfaceModifierContext mod : ctx.classOrInterfaceModifier()) {
            visit(mod);
        }
        if (ctx.classDeclaration() != null) {
            visit(ctx.classDeclaration());
        } else if (ctx.enumDeclaration() != null) {
            visit(ctx.enumDeclaration());
        } else if (ctx.interfaceDeclaration() != null) {
            visit(ctx.interfaceDeclaration());
        } else if (ctx.annotationTypeDeclaration() != null) {
            visit(ctx.annotationTypeDeclaration());
        } else {
            visit(ctx.recordDeclaration());
        }
        return null;
    }

    @Override
    public Void visitClassOrIntMod(JavaParser.ClassOrIntModContext ctx) {
        visit(ctx.classOrInterfaceModifier());
        return null;
    }

    @Override
    public Void visitNativeMod(JavaParser.NativeModContext ctx) {
        out.print(ctx.NATIVE().getText() + " ");
        return null;
    }

    @Override
    public Void visitSyncMod(JavaParser.SyncModContext ctx) {
        out.print(ctx.SYNCHRONIZED().getText() + " ");
        return null;
    }

    @Override
    public Void visitTransMod(JavaParser.TransModContext ctx) {
        out.print(ctx.TRANSIENT().getText() + " ");
        return null;
    }

    @Override
    public Void visitVolaMod(JavaParser.VolaModContext ctx) {
        out.print(ctx.VOLATILE().getText() + " ");
        return null;
    }

    @Override
    public Void visitAnnotate(JavaParser.AnnotateContext ctx) {
        visit(ctx.annotation());
        return null;
    }

    @Override
    public Void visitPubMod(JavaParser.PubModContext ctx) {
        out.print(ctx.PUBLIC().getText() + " ");
        return null;
    }

    @Override
    public Void visitProtMod(JavaParser.ProtModContext ctx) {
        out.print(ctx.PROTECTED().getText() + " ");
        return null;
    }

    @Override
    public Void visitPrivMod(JavaParser.PrivModContext ctx) {
        out.print(ctx.PRIVATE().getText() + " ");
        return null;
    }

    @Override
    public Void visitStatMod(JavaParser.StatModContext ctx) {
        out.print(ctx.STATIC().getText() + " ");
        return null;
    }

    @Override
    public Void visitAbstrMod(JavaParser.AbstrModContext ctx) {
        out.print(ctx.ABSTRACT().getText() + " ");
        return null;
    }

    @Override
    public Void visitFinMod(JavaParser.FinModContext ctx) {
        out.print(ctx.FINAL().getText() + " ");
        return null;
    }

    @Override
    public Void visitStriMod(JavaParser.StriModContext ctx) {
        out.print(ctx.STRICTFP().getText() + " ");
        return null;
    }

    @Override
    public Void visitSealMod(JavaParser.SealModContext ctx) {
        out.print(ctx.SEALED().getText() + " ");
        return null;
    }

    @Override
    public Void visitNsealMod(JavaParser.NsealModContext ctx) {
        out.print(ctx.NON_SEALED().getText() + " ");
        return null;
    }

    @Override
    public Void visitFinVarMod(JavaParser.FinVarModContext ctx) {
        out.print(ctx.FINAL().getText() + " ");
        return null;
    }

    @Override
    public Void visitAnnotVarMod(JavaParser.AnnotVarModContext ctx) {
        visit(ctx.annotation());
        return null;
    }

    @Override
    public Void visitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        out.print(ctx.CLASS().getText() + " " + ctx.identifier().getText() + " ");
        if (ctx.typeParameters() != null) {
            visit(ctx.typeParameters());
        }
        if (ctx.EXTENDS() != null) {
            out.print(ctx.EXTENDS().getText() + " ");
            visit(ctx.typeType());
        }
        if (ctx.IMPLEMENTS() != null) {
            out.print(ctx.IMPLEMENTS().getText() + " ");
            visit(ctx.typeList(0));
        }
        if (ctx.PERMITS() != null) {
            out.print(ctx.PERMITS().getText() + " ");
            visit(ctx.typeList(1));
        }
        visit(ctx.classBody());
        return null;
    }

    @Override
    public Void visitTypeParameters(JavaParser.TypeParametersContext ctx) {
        out.print("<");
        int i = 0;
        visit(ctx.typeParameter(i));
        i++;
        while (ctx.typeParameter(i) != null) {
            out.print(", ");
            visit(ctx.typeParameter(i));
        }
        out.print(">");
        return null;
    }

    @Override
    public Void visitTypeParameter(JavaParser.TypeParameterContext ctx) {
        for (JavaParser.FirstAnnotationContext fAnnot : ctx.firstAnnotation()) {
            visit(fAnnot);
        }
        visit(ctx.identifier());
        if (ctx.EXTENDS() != null) {
            out.print(ctx.EXTENDS().getText() + " ");
            for (JavaParser.AnnotationContext annot : ctx.annotation()) {
                visit(annot);
            }
            visit(ctx.typeBound());
        }
        return null;
    }

    @Override
    public Void visitFirstAnnotation(JavaParser.FirstAnnotationContext ctx) {
        visit(ctx.annotation());
        return null;
    }

    @Override
    public Void visitTypeBound(JavaParser.TypeBoundContext ctx) {
        int i = 0;
        visit(ctx.typeType(i));
        while (ctx.typeType(i) != null) {
            out.print("&");
            visit(ctx.typeType(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
        out.print(ctx.ENUM().getText() + " ");
        visit(ctx.identifier());
        if (ctx.IMPLEMENTS() != null) {
            out.print(ctx.IMPLEMENTS().getText() + " ");
            visit(ctx.typeList());
        }
        out.print("{\n");
        addTab();
        printTabs();
        if (ctx.enumConstants() != null) {
            visit(ctx.enumConstants());
        }
        if (ctx.COMMA() != null) {
            out.print(ctx.COMMA().getText() + " ");
        }
        if (ctx.enumBodyDeclarations() != null) {
            visit(ctx.enumBodyDeclarations());
        }
        remTab();
        out.print("\n");
        printTabs();
        out.print("}\n");
        return null;
    }

    @Override
    public Void visitEnumConstants(JavaParser.EnumConstantsContext ctx) {
        int i = 0;
        visit(ctx.enumConstant(i));
        i++;
        while (ctx.enumConstant(i) != null) {
            out.print(", ");
            visit(ctx.enumConstant(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitEnumConstant(JavaParser.EnumConstantContext ctx) {
        for (JavaParser.AnnotationContext annot : ctx.annotation()) {
            visit(annot);
        }
        visit(ctx.identifier());
        if (ctx.arguments() != null) {
            visit(ctx.arguments());
        }
        if (ctx.classBody() != null) {
            visit(ctx.classBody());
        }
        return null;
    }

    @Override
    public Void visitEnumBodyDeclarations(JavaParser.EnumBodyDeclarationsContext ctx) {
        out.print(";");
        for (JavaParser.ClassBodyDeclarationContext body : ctx.classBodyDeclaration()) {
            visit(body);
        }
        return null;
    }

    @Override
    public Void visitInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        out.print(ctx.INTERFACE().getText() + " ");
        visit(ctx.identifier());
        if (ctx.typeParameters() != null) {
            visit(ctx.typeParameters());
        }
        if (ctx.EXTENDS() != null) {
            out.print(ctx.EXTENDS().getText() + " ");
            visit(ctx.typeList(0));
        }
        if (ctx.PERMITS() != null) {
            out.print(ctx.PERMITS().getText() + " ");
            visit(ctx.typeList(1));
        }
        visit(ctx.interfaceBody());
        return null;
    }

    @Override
    public Void visitClassBody(JavaParser.ClassBodyContext ctx) {
        out.print("{\n");
        addTab();
        printTabs();
        for (JavaParser.ClassBodyDeclarationContext body : ctx.classBodyDeclaration()) {
            visit(body);
        }
        out.println("}\n");
        return null;
    }

    @Override
    public Void visitInterfaceBody(JavaParser.InterfaceBodyContext ctx) {
        out.print("{\n");
        addTab();
        printTabs();
        for (JavaParser.InterfaceBodyDeclarationContext body : ctx.interfaceBodyDeclaration()) {
            visit(body);
        }
        remTab();
        out.print("\n");
        printTabs();
        out.print("}\n");
        return null;
    }

    @Override
    public Void visitClassBodyDeclSemi(JavaParser.ClassBodyDeclSemiContext ctx) {
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitClassBodyDeclStatic(JavaParser.ClassBodyDeclStaticContext ctx) {
        if (ctx.STATIC() != null) {
            out.print(ctx.STATIC().getText() + " ");
        }
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitClassBodyDeclMod(JavaParser.ClassBodyDeclModContext ctx) {
        for (JavaParser.ModifierContext mod : ctx.modifier()) {
            visit(mod);
        }
        visit(ctx.memberDeclaration());
        return null;
    }

    @Override
    public Void visitRecDecl(JavaParser.RecDeclContext ctx) {
        visit(ctx.recordDeclaration());
        return null;
    }

    @Override
    public Void visitMethDecl(JavaParser.MethDeclContext ctx) {
        visit(ctx.methodDeclaration());
        return null;
    }

    @Override
    public Void visitGenMethDecl(JavaParser.GenMethDeclContext ctx) {
        visit(ctx.genericMethodDeclaration());
        return null;
    }

    @Override
    public Void visitFieldDecl(JavaParser.FieldDeclContext ctx) {
        visit(ctx.fieldDeclaration());
        return null;
    }

    @Override
    public Void visitConsDecl(JavaParser.ConsDeclContext ctx) {
        visit(ctx.constructorDeclaration());
        return null;
    }

    @Override
    public Void visitGenConsDecl(JavaParser.GenConsDeclContext ctx) {
        visit(ctx.genericConstructorDeclaration());
        return null;
    }

    @Override
    public Void visitIntDecl(JavaParser.IntDeclContext ctx) {
        visit(ctx.interfaceDeclaration());
        return null;
    }

    @Override
    public Void visitAnnotTypeDecl(JavaParser.AnnotTypeDeclContext ctx) {
        visit(ctx.annotationTypeDeclaration());
        return null;
    }

    @Override
    public Void visitClassDecl(JavaParser.ClassDeclContext ctx) {
        visit(ctx.classDeclaration());
        return null;
    }

    @Override
    public Void visitEnumDecl(JavaParser.EnumDeclContext ctx) {
        visit(ctx.enumDeclaration());
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
            out.print(ctx.THROWS().getText() + " ");
            visit(ctx.qualifiedNameList());
        }
        visit(ctx.methodBody());
        return null;
    }

    @Override
    public Void visitMethBodyBlock(JavaParser.MethBodyBlockContext ctx) {
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitMethBodySemi(JavaParser.MethBodySemiContext ctx) {
        out.print(";\n");
        return null;
    }

    @Override
    public Void visitTyType(JavaParser.TyTypeContext ctx) {
        visit(ctx.typeType());
        return null;
    }

    @Override
    public Void visitVoid(JavaParser.VoidContext ctx) {
        out.print(ctx.VOID().getText() + " ");
        return null;
    }

    @Override
    public Void visitGenericMethodDeclaration(JavaParser.GenericMethodDeclarationContext ctx) {
        visit(ctx.typeParameters());
        visit(ctx.methodDeclaration());
        return null;
    }

    @Override
    public Void visitGenericConstructorDeclaration(JavaParser.GenericConstructorDeclarationContext ctx) {
        visit(ctx.typeParameters());
        visit(ctx.constructorDeclaration());
        return null;
    }

    @Override
    public Void visitConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        visit(ctx.identifier());
        visit(ctx.formalParameters());
        if (ctx.qualifiedNameList() != null) {
            out.print(ctx.THROWS().getText() + " ");
            visit(ctx.qualifiedNameList());
        }
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitCompactConstructorDeclaration(JavaParser.CompactConstructorDeclarationContext ctx) {
        for (JavaParser.ModifierContext mod : ctx.modifier()) {
            visit(mod);
        }
        visit(ctx.identifier());
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        visit(ctx.typeType());
        visit(ctx.variableDeclarators());
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitIntBodyMod(JavaParser.IntBodyModContext ctx) {
        for (JavaParser.ModifierContext mod : ctx.modifier()) {
            visit(mod);
        }
        visit(ctx.interfaceMemberDeclaration());
        return null;
    }

    @Override
    public Void visitIntBodySemi(JavaParser.IntBodySemiContext ctx) {
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitIntRecDecl(JavaParser.IntRecDeclContext ctx) {
        visit(ctx.recordDeclaration());
        return null;
    }

    @Override
    public Void visitIntConsDecl(JavaParser.IntConsDeclContext ctx) {
        visit(ctx.constDeclaration());
        return null;
    }

    @Override
    public Void visitIntMethDecl(JavaParser.IntMethDeclContext ctx) {
        visit(ctx.interfaceMethodDeclaration());
        return null;
    }

    @Override
    public Void visitGenIntMethDecl(JavaParser.GenIntMethDeclContext ctx) {
        visit(ctx.genericInterfaceMethodDeclaration());
        return null;
    }

    @Override
    public Void visitIntIntDecl(JavaParser.IntIntDeclContext ctx) {
        visit(ctx.interfaceDeclaration());
        return null;
    }

    @Override
    public Void visitAnnotDecl(JavaParser.AnnotDeclContext ctx) {
        visit(ctx.annotationTypeDeclaration());
        return null;
    }

    @Override
    public Void visitIntClassDecl(JavaParser.IntClassDeclContext ctx) {
        visit(ctx.classDeclaration());
        return null;
    }

    @Override
    public Void visitIntEnumDecl(JavaParser.IntEnumDeclContext ctx) {
        visit(ctx.enumDeclaration());
        return null;
    }

    @Override
    public Void visitConstDeclaration(JavaParser.ConstDeclarationContext ctx) {
        visit(ctx.typeType());
        int i = 0;
        visit(ctx.constantDeclarator(i));
        i++;
        while (ctx.constantDeclarator(i) != null) {
            out.print(", ");
            visit(ctx.constantDeclarator(i));
            i++;
        }
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitConstantDeclarator(JavaParser.ConstantDeclaratorContext ctx) {
        visit(ctx.identifier());
        for (int i = 0; i < ctx.LBRACK().size(); i++) {
            out.print("[]");
        }
        out.print(" = ");
        visit(ctx.variableInitializer());
        return null;
    }

    @Override
    public Void visitInterfaceMethodDeclaration(JavaParser.InterfaceMethodDeclarationContext ctx) {
        for (JavaParser.InterfaceMethodModifierContext mod : ctx.interfaceMethodModifier()) {
            visit(mod);
        }
        visit(ctx.interfaceCommonBodyDeclaration());
        return null;
    }

    @Override
    public Void visitIntAnnotMod(JavaParser.IntAnnotModContext ctx) {
        visit(ctx.annotation());
        return null;
    }

    @Override
    public Void visitIntPubMod(JavaParser.IntPubModContext ctx) {
        out.print(ctx.PUBLIC().getText() + " ");
        return null;
    }

    @Override
    public Void visitIntAbsMod(JavaParser.IntAbsModContext ctx) {
        out.print(ctx.ABSTRACT().getText() + " ");
        return null;
    }

    @Override
    public Void visitIntDefMod(JavaParser.IntDefModContext ctx) {
        out.print(ctx.DEFAULT().getText() + " ");
        return null;
    }

    @Override
    public Void visitIntStatMod(JavaParser.IntStatModContext ctx) {
        out.print(ctx.STATIC().getText() + " ");
        return null;
    }

    @Override
    public Void visitIntStriMod(JavaParser.IntStriModContext ctx) {
        out.print(ctx.STRICTFP().getText() + " ");
        return null;
    }

    @Override
    public Void visitGenericInterfaceMethodDeclaration(JavaParser.GenericInterfaceMethodDeclarationContext ctx) {
        for (JavaParser.InterfaceMethodModifierContext mod : ctx.interfaceMethodModifier()) {
            visit(mod);
        }
        visit(ctx.typeParameters());
        visit(ctx.interfaceCommonBodyDeclaration());
        return null;
    }

    @Override
    public Void visitInterfaceCommonBodyDeclaration(JavaParser.InterfaceCommonBodyDeclarationContext ctx) {
        for (JavaParser.AnnotationContext annot : ctx.annotation()) {
            visit(annot);
        }
        visit(ctx.typeTypeOrVoid());
        visit(ctx.identifier());
        visit(ctx.formalParameters());
        for (int i = 0; i < ctx.LBRACK().size(); i++) {
            out.print("[]");
        }
        if (ctx.qualifiedNameList() != null) {
            out.print(ctx.THROWS().getText() + " ");
            visit(ctx.qualifiedNameList());
        }
        visit(ctx.methodBody());
        return null;
    }

    @Override
    public Void visitVariableDeclarators(JavaParser.VariableDeclaratorsContext ctx) {
        int i = 0;
        visit(ctx.variableDeclarator(i));
        i++;
        while (ctx.variableDeclarator(i) != null) {
            out.print(", ");
            visit(ctx.variableDeclarator(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        visit(ctx.variableDeclaratorId());
        if (ctx.variableInitializer() != null) {
            out.print(" = ");
            visit(ctx.variableInitializer());
        }
        return null;
    }

    @Override
    public Void visitVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
        visit(ctx.identifier());
        for (int i = 0; i < ctx.LBRACK().size(); i++) {
            out.print(ctx.LBRACK(i).getText() + ctx.RBRACK(i).getText());
        }
        return null;
    }

    @Override
    public Void visitArrayInit(JavaParser.ArrayInitContext ctx) {
        visit(ctx.arrayInitializer());
        return null;
    }

    @Override
    public Void visitVarExprInit(JavaParser.VarExprInitContext ctx) {
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitArrayInitializer(JavaParser.ArrayInitializerContext ctx) {
        out.print("{");
        int i = 0;
        if (ctx.variableInitializer(i) != null) {
            visit(ctx.variableInitializer(i));
            i++;
            while (ctx.variableInitializer(i) != null) {
                out.print(", ");
                visit(ctx.variableInitializer(i));
                i++;
            }
            if (ctx.COMMA(0) != null) {
                out.print(ctx.COMMA(0).getText());
            }
        }
        out.print("}");
        return null;
    }

    @Override
    public Void visitClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
        for (JavaParser.LeadingPartContext lead : ctx.leadingPart()) {
            visit(lead);
        }
        visit(ctx.typeIdentifier());
        if (ctx.typeArguments() != null) {
            visit(ctx.typeArguments());
        }
        return null;
    }

    @Override
    public Void visitLeadingPart(JavaParser.LeadingPartContext ctx) {
        visit(ctx.identifier());
        if (ctx.typeArguments() != null) {
            visit(ctx.typeArguments());
        }
        out.print(".");
        return null;
    }

    @Override
    public Void visitTyTyTypeArg(JavaParser.TyTyTypeArgContext ctx) {
        visit(ctx.typeType());
        return null;
    }

    @Override
    public Void visitTyTyAnnot(JavaParser.TyTyAnnotContext ctx) {
        for (JavaParser.AnnotationContext annot : ctx.annotation()) {
            visit(annot);
        }
        out.print("?");
        if (ctx.EXTENDS() != null) {
            out.print(ctx.EXTENDS().getText() + " ");
        } else {
            out.print(ctx.SUPER().getText() + " ");
        }
        if (ctx.typeType() != null) {
            visit(ctx.typeType());
        }
        return null;
    }

    @Override
    public Void visitQualifiedNameList(JavaParser.QualifiedNameListContext ctx) {
        int i = 0;
        visit(ctx.qualifiedName(i));
        i++;
        while (ctx.qualifiedName(i) != null) {
            out.print(", ");
            visit(ctx.qualifiedName(i));
            i++;
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
        } else if (ctx.formalParameterList() != null) {
            visit(ctx.formalParameterList());
        }
        out.print(") ");
        return null;
    }

    @Override
    public Void visitReceiverParameter(JavaParser.ReceiverParameterContext ctx) {
        visit(ctx.typeType());
        int i = 0;
        while (ctx.identifier(i) != null) {
            visit(ctx.identifier(i));
            out.print(".");
            i++;
        }
        out.print(ctx.THIS().getText() + " ");
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
                out.print(", ");
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
            for (JavaParser.VariableModifierContext modifier : ctx.variableModifier()) {
                visit(modifier);
            }
        }
        visit(ctx.typeType());
        visit(ctx.variableDeclaratorId());
        return null;
    }

    @Override
    public Void visitLastFormalParameter(JavaParser.LastFormalParameterContext ctx) {
        if (ctx.variableModifier(0) != null) {
            for (JavaParser.VariableModifierContext modifier : ctx.variableModifier()) {
                visit(modifier);
            }
        }
        visit(ctx.typeType());
        if (ctx.annotation(0) != null) {
            for (JavaParser.AnnotationContext annotation : ctx.annotation()) {
                visit(annotation);
            }
        }
        out.print("...");
        visit(ctx.variableDeclaratorId());
        return null;
    }

    @Override
    public Void visitLambdaLVTIList(JavaParser.LambdaLVTIListContext ctx) {
        int i = 0;
        visit(ctx.lambdaLVTIParameter(i));
        i++;
        while (ctx.lambdaLVTIParameter(i) != null) {
            out.print(", ");
            visit(ctx.lambdaLVTIParameter(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitLambdaLVTIParameter(JavaParser.LambdaLVTIParameterContext ctx) {
        for (JavaParser.VariableModifierContext mod : ctx.variableModifier()) {
            visit(mod);
        }
        out.print(ctx.VAR().getText() + " ");
        visit(ctx.identifier());
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
    public Void visitDecLit(JavaParser.DecLitContext ctx) {
        out.print(ctx.DECIMAL_LITERAL().getText());
        return null;
    }

    @Override
    public Void visitHexLit(JavaParser.HexLitContext ctx) {
        out.print(ctx.HEX_LITERAL().getText());
        return null;
    }

    @Override
    public Void visitOctLit(JavaParser.OctLitContext ctx) {
        out.print(ctx.OCT_LITERAL().getText());
        return null;
    }

    @Override
    public Void visitBinLit(JavaParser.BinLitContext ctx) {
        out.print(ctx.BINARY_LITERAL().getText());
        return null;
    }

    @Override
    public Void visitActFloatLit(JavaParser.ActFloatLitContext ctx) {
        out.print(ctx.FLOAT_LITERAL().getText());
        return null;
    }

    @Override
    public Void visitHexFloatLit(JavaParser.HexFloatLitContext ctx) {
        out.print(ctx.HEX_FLOAT_LITERAL().getText());
        return null;
    }

    @Override
    public Void visitAltAnnotationQualifiedName(JavaParser.AltAnnotationQualifiedNameContext ctx) {
        int i = 0;
        while (ctx.DOT(i) != null) {
            visit(ctx.identifier(0));
            out.print(ctx.DOT(i).getText());
            i++;
        }
        out.print("@");
        visit(ctx.identifier(i));
        return null;
    }

    @Override
    public Void visitAnnotation(JavaParser.AnnotationContext ctx) {
        if (ctx.qualifiedName() != null) {
            out.print("@");
            visit(ctx.qualifiedName());
        } else if (ctx.altAnnotationQualifiedName() != null) {
            out.print("@");
            visit(ctx.altAnnotationQualifiedName());
        }
        if (ctx.LPAREN() != null) {
            out.print(ctx.LPAREN().getText());
            if (ctx.elementValuePairs() != null) {
                visit(ctx.elementValuePairs());
            } else if (ctx.elementValue() != null) {
                visit(ctx.elementValue());
            }
            out.print(ctx.RPAREN().getText());
        }
        return null;
    }

    @Override
    public Void visitElementValuePairs(JavaParser.ElementValuePairsContext ctx) {
        int i = 0;
        visit(ctx.elementValuePair(i));
        i++;
        while (ctx.elementValuePair(i) != null) {
            out.print(", ");
            visit(ctx.elementValuePair(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitElementValuePair(JavaParser.ElementValuePairContext ctx) {
        visit(ctx.identifier());
        out.print(" = ");
        visit(ctx.elementValue());
        return null;
    }

    @Override
    public Void visitExprElVal(JavaParser.ExprElValContext ctx) {
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitAnnotElVal(JavaParser.AnnotElValContext ctx) {
        visit(ctx.annotation());
        return null;
    }

    @Override
    public Void visitArrayElVal(JavaParser.ArrayElValContext ctx) {
        visit(ctx.elementValueArrayInitializer());
        return null;
    }

    @Override
    public Void visitElementValueArrayInitializer(JavaParser.ElementValueArrayInitializerContext ctx) {
        out.print("{");
        int i = 0;
        if (ctx.elementValue(i) != null) {
            visit(ctx.elementValue(i));
            i++;
        }
        while (ctx.elementValue(i) != null) {
            out.print(", ");
            visit(ctx.elementValue(i));
            i++;
        }
        if (ctx.COMMA(i - 1) != null) {
            out.print(ctx.COMMA(i - 1).getText());
        }
        out.print("}");
        return null;
    }

    @Override
    public Void visitAnnotationTypeDeclaration(JavaParser.AnnotationTypeDeclarationContext ctx) {
        out.print("@");
        out.print(ctx.INTERFACE().getText());
        visit(ctx.identifier());
        visit(ctx.annotationTypeBody());
        return null;
    }

    @Override
    public Void visitAnnotationTypeBody(JavaParser.AnnotationTypeBodyContext ctx) {
        out.print("{\n");
        addTab();
        printTabs();
        for (JavaParser.AnnotationTypeElementDeclarationContext element : ctx.annotationTypeElementDeclaration()) {
            visit(element);
        }
        remTab();
        printTabs();
        out.print("}\n");
        return null;
    }

    @Override
    public Void visitAnnotationTypeElementDeclaration(JavaParser.AnnotationTypeElementDeclarationContext ctx) {
        if (ctx.annotationTypeElementRest() != null) {
            for (JavaParser.ModifierContext mod : ctx.modifier()) {
                visit(mod);
            }
            visit(ctx.annotationTypeElementRest());
        } else {
            out.print(";\n");
            printTabs();
        }
        return null;
    }

    @Override
    public Void visitAnnotTyTy(JavaParser.AnnotTyTyContext ctx) {
        visit(ctx.typeType());
        visit(ctx.annotationMethodOrConstantRest());
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitAnnotClass(JavaParser.AnnotClassContext ctx) {
        visit(ctx.classDeclaration());
        if (ctx.SEMI() != null) {
            out.print(ctx.SEMI().getText() + "\n");
        }
        return null;
    }

    @Override
    public Void visitAnnotInt(JavaParser.AnnotIntContext ctx) {
        visit(ctx.interfaceDeclaration());
        if (ctx.SEMI() != null) {
            out.print(ctx.SEMI().getText() + "\n");
        }
        return null;
    }

    @Override
    public Void visitAnnotEnum(JavaParser.AnnotEnumContext ctx) {
        visit(ctx.enumDeclaration());
        if (ctx.SEMI() != null) {
            out.print(ctx.SEMI().getText() + "\n");
        }
        return null;
    }

    @Override
    public Void visitAnnotType(JavaParser.AnnotTypeContext ctx) {
        visit(ctx.annotationTypeDeclaration());
        if (ctx.SEMI() != null) {
            out.print(ctx.SEMI().getText() + "\n");
        }
        return null;
    }

    @Override
    public Void visitAnnotRec(JavaParser.AnnotRecContext ctx) {
        visit(ctx.recordDeclaration());
        if (ctx.SEMI() != null) {
            out.print(ctx.SEMI().getText() + "\n");
        }
        return null;
    }

    @Override
    public Void visitAnnotMeth(JavaParser.AnnotMethContext ctx) {
        visit(ctx.annotationMethodRest());
        return null;
    }

    @Override
    public Void visitAnnotConst(JavaParser.AnnotConstContext ctx) {
        visit(ctx.annotationConstantRest());
        return null;
    }

    @Override
    public Void visitAnnotationMethodRest(JavaParser.AnnotationMethodRestContext ctx) {
        visit(ctx.identifier());
        out.print("()");
        if (ctx.defaultValue() != null) {
            visit(ctx.defaultValue());
        }
        return null;
    }

    @Override
    public Void visitAnnotationConstantRest(JavaParser.AnnotationConstantRestContext ctx) {
        visit(ctx.variableDeclarators());
        return null;
    }

    @Override
    public Void visitDefaultValue(JavaParser.DefaultValueContext ctx) {
        out.print(ctx.DEFAULT().getText());
        visit(ctx.elementValue());
        return null;
    }

    @Override
    public Void visitModuleDeclaration(JavaParser.ModuleDeclarationContext ctx) {
        if (ctx.OPEN() != null) {
            out.print(ctx.OPEN().getText());
        }
        out.print(ctx.MODULE().getText());
        visit(ctx.qualifiedName());
        visit(ctx.moduleBody());
        return null;
    }

    @Override
    public Void visitModuleBody(JavaParser.ModuleBodyContext ctx) {
        out.print("{\n");
        addTab();
        printTabs();
        for (JavaParser.ModuleDirectiveContext dir : ctx.moduleDirective()) {
            visit(dir);
        }
        remTab();
        printTabs();
        out.print("}\n");
        return null;
    }

    @Override
    public Void visitModReq(JavaParser.ModReqContext ctx) {
        out.print(ctx.REQUIRES().getText() + " ");
        for (JavaParser.RequiresModifierContext req : ctx.requiresModifier()) {
            visit(req);
        }
        visit(ctx.qualifiedName());
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitModExp(JavaParser.ModExpContext ctx) {
        out.print(ctx.EXPORTS().getText() + " ");
        visit(ctx.qualifiedName(0));
        if (ctx.qualifiedName(1) != null) {
            out.print(ctx.TO().getText());
            visit(ctx.qualifiedName(1));
        }
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitModOpen(JavaParser.ModOpenContext ctx) {
        out.print(ctx.OPENS().getText() + " ");
        visit(ctx.qualifiedName(0));
        if (ctx.qualifiedName(1) != null) {
            out.print(ctx.TO().getText());
            visit(ctx.qualifiedName(1));
        }
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitModUses(JavaParser.ModUsesContext ctx) {
        out.print(ctx.USES().getText() + " ");
        visit(ctx.qualifiedName());
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitModProv(JavaParser.ModProvContext ctx) {
        out.print(ctx.PROVIDES().getText() + " ");
        visit(ctx.qualifiedName(0));
        out.print(ctx.WITH().getText() + " ");
        visit(ctx.qualifiedName(1));
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitReqTrans(JavaParser.ReqTransContext ctx) {
        out.print(ctx.TRANSITIVE().getText() + " ");
        return null;
    }

    @Override
    public Void visitReqStat(JavaParser.ReqStatContext ctx) {
        out.print(ctx.STATIC().getText() + " ");
        return null;
    }

    @Override
    public Void visitRecordDeclaration(JavaParser.RecordDeclarationContext ctx) {
        out.print(ctx.RECORD().getText() + " ");
        visit(ctx.identifier());
        if (ctx.typeParameters() != null) {
            visit(ctx.typeParameters());
        }
        visit(ctx.recordHeader());
        if (ctx.typeList() != null) {
            out.print(ctx.IMPLEMENTS().getText() + " ");
            visit(ctx.typeList());
            visit(ctx.recordBody());
        }
        return null;
    }

    @Override
    public Void visitRecordHeader(JavaParser.RecordHeaderContext ctx) {
        out.print("(");
        if (ctx.recordComponentList() != null) {
            visit(ctx.recordComponentList());
        }
        out.print(")");
        return null;
    }

    @Override
    public Void visitRecordComponentList(JavaParser.RecordComponentListContext ctx) {
        int i = 0;
        visit(ctx.recordComponent(i));
        i++;
        while (ctx.recordComponent(i) != null) {
            out.print(", ");
            visit(ctx.recordComponent(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitRecordComponent(JavaParser.RecordComponentContext ctx) {
        visit(ctx.typeType());
        visit(ctx.identifier());
        return null;
    }

    @Override
    public Void visitRecordBody(JavaParser.RecordBodyContext ctx) {
        out.print("{\n");
        addTab();
        printTabs();
        int i = 0;
        while (ctx.classBodyDeclaration(i) != null || ctx.compactConstructorDeclaration(i) != null) {
            if (ctx.classBodyDeclaration(i) != null) {
                visit(ctx.classBodyDeclaration(i));
                i++;
            } else if (ctx.compactConstructorDeclaration(i) != null) {
                visit(ctx.compactConstructorDeclaration(i));
                i++;
            }
        }
        remTab();
        printTabs();
        out.print("}\n");
        return null;
    }

    @Override
    public Void visitBlock(JavaParser.BlockContext ctx) {
        out.print("{\n");
        addTab();
        if (ctx.blockStatement(0) != null) {
            for (JavaParser.BlockStatementContext stmt : ctx.blockStatement()) {
                printTabs();
                visit(stmt);
            }
        }
        remTab();
        printTabs();
        out.print("}\n\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitBlockStatement(JavaParser.BlockStatementContext ctx) {
        if (ctx.localVariableDeclaration() != null) {
            visit(ctx.localVariableDeclaration());
            //out.print(";\n"); handled in declaration
        } else if (ctx.localTypeDeclaration() != null) {
            visit(ctx.localTypeDeclaration());
        } else if (ctx.paraBlock() != null) {
          visit(ctx.paraBlock());
        } else {
            visit(ctx.statement());
        }
        return null;
    }

    @Override
    public Void visitParaBlock(JavaParser.ParaBlockContext ctx) {
        numThreads = 0;
        threadStart = totalThreads;
        int i;
        if (ctx.paraBlockStatements() != null) {
            visit(ctx.paraBlockStatements());
            for (i = 0; i < numThreads; i++) {
                printTabs();
                out.println("Runnable" + (threadStart + i) + " run" + 
                        (threadStart + i) + " = new Runnable" +
                        (threadStart + i) + "(" + (threadArgs()) +
                        ");");
                out.println("Thread t" + (threadStart + i) + 
                        " = new Thread(run" + (threadStart + i) +
                        ");");
            }

            for (i = 0; i < numThreads; i++) {
                printTabs();
                out.println("t" + (threadStart + i) + ".start();");
            }

            printTabs();
            out.println("try {");
            addTab();

            for (i = 0; i < numThreads; i++) {
                printTabs();
                out.println("t" + (threadStart + i) + ".join();");
            }
            for (i =0; i < numThreads; i++) {
                printTabs();
                out.println(assignedVariables.get(i) + " = run" + 
                        (threadStart + i) + "." + 
                        assignedVariables.get(i) + ";");
            }
            remTab();
            printTabs();
            out.println("} catch (InterruptedException e) {\n");
            printTabs();
            out.println("}");
        }
        return null;
    }

    private String threadArgs(){
        if (localVariables == null){ 
            return ""; 
        }

        String paras = "";
        for (String s : localVariables){
            String temp = getVarName(s);
            temp += ", ";
            paras += temp;
        }
        paras = paras.substring(0,paras.length()-2);
        return paras;
    }

    private String getVarName(String s){
        if (!s.contains(",")){
            String val = removeEquals(s);
            val = val.replace(";","");
            String[] parts = val.split(" ");
            return parts[1];
        }
        s = s.replace(";","");
        String[] parts = s.split(",");
        String[] first = parts[0].split(" ");
        String val = removeEquals(first[1]) + ", ";
        for (int i = 1; i < parts.length; i++){
            val += removeEquals(parts[i]) + ", ";
        }
        return val.substring(0,val.length()-2); //get rid of , and ;
    }

    private String varsToParameters(String s){
        if (!s.contains(",")){
            String val = removeEquals(s);
            return val.substring(0,val.length()-1);
        }
        String[] parts = s.split(",");
        String[] first = parts[0].split(" ");
        String type = first[0]+" ";
        String val = removeEquals(parts[0]) + ",";
        for (int i=1;i<parts.length;i++){
            val += type + removeEquals(parts[i]) + ",";
        }
        return val.substring(0,val.length()-1); //get rid of , and ;
    }

    private String removeEquals(String s){
        if (!s.contains("=")){
            return s;
        }
        String[] parts = s.split("=");
        return parts[0];
    }

    private String varsToInstVars(String s){
        if (!s.contains(",")){
            String[] parts = s.split(" ");
            String name = removeEquals(parts[1]);
            name = name.substring(0,name.length()); 
            String val = "this."+ name + " = " + name +";";
            return val;
        }
        String[] parts = s.split(",");
        String[] first = parts[0].split(" ");
        String name = removeEquals(first[1]);
        String val = "this." + name + " = " + name + ";";
        for (int i=1;i<parts.length;i++){
            name = removeEquals(parts[i]);
            String temp = "\nthis." + name + " = " + name + ";";
            val+=temp;
        }
        return val;
    }

    @Override
    public Void visitParaBlockStatements(JavaParser.ParaBlockStatementsContext ctx) {
        for (JavaParser.BlockStatementContext stmt : ctx.blockStatement()) {
            //numThreads++;
            //totalThreads++;
            printTabs();
            out.println("class Runnable" + totalThreads + " implements Runnable {\n");
            addTab();
            for (String s : localVariables){
                out.println("public " + s);
            }
            //localVariables=new ArrayList<String>();
            printTabs();
            String args="Runnable" + totalThreads + "(";
            for (String s : localVariables){
                args+=varsToParameters(s)+",";
            }
            args=args.substring(0,args.length()-1);
            args+=") {";
            out.println(args);
            //set instance variables
            for (String s : localVariables){
                out.println(varsToInstVars(s));
            }
            out.println("}"); 
            out.println("public void run() {");
            addTab();
            printTabs();
            out.println("System.out.println(Thread.currentThread().getName() + \", executing run() method!\");");
            printTabs();
            visit(stmt);
            remTab();
            printTabs();
            out.println("}");
            remTab();
            printTabs();
            out.println("}");
            numThreads++;
            totalThreads++;
        }
        return null;
    }

    @Override
    public Void visitLocalVariableDeclaration(
            JavaParser.LocalVariableDeclarationContext ctx) {
        
        String type;
        if (ctx.VAR() != null){
            type = "var";
        } else{
            type = ctx.typeType().getText();
        }
        
        for (JavaParser.VariableDeclaratorContext decl :
        ctx.variableDeclarators().variableDeclarator()){
            String vName = decl.variableDeclaratorId().getText();
            String initText = null;

            if (decl.variableInitializer() !=null){
                initText = decl.variableInitializer().getText();
            }

            if (initText == null){
                initText = defaultVal(type);
            }

            String line = type + " " + vName + " = " + initText;
            if (!inForLoop){
               line+=";";
               localVariables.add(line);
            }
            out.println(line);
        }
        return null;
    }

    private String defaultVal(String s){
        if (s.equals("int") || s.equals("byte") || s.equals("short") ||
                s.equals("long") || s.equals("char")){
            return "0";
                }
        if (s.equals("double")){
            return "0.0";
        }
        if (s.equals("float")){
            return "0.0f";
        }
        if (s.equals("boolean")){
            return "false";
        }
        return "null";
    }

    @Override
    public Void visitIdentifier(JavaParser.IdentifierContext ctx) {
        if (ctx.getText().contains(".")) {
            out.print(ctx.getText());
        } else {
            out.print(ctx.getText() + " ");
        }
        return null;
    }

    @Override
    public Void visitTypeIdentifier(JavaParser.TypeIdentifierContext ctx) {
        out.print(ctx.getText() + " ");
        return null;
    }

    @Override
    public Void visitLocalTypeDeclaration(JavaParser.LocalTypeDeclarationContext ctx) {
        if (ctx.classOrInterfaceModifier(0) != null) {
            for (JavaParser.ClassOrInterfaceModifierContext modifierContext : ctx.classOrInterfaceModifier()) {
                visit(modifierContext);
            }
        }
        if (ctx.classDeclaration() != null) {
            visit(ctx.classDeclaration());
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(JavaParser.BlockStmtContext ctx) {
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitAssertStmt(JavaParser.AssertStmtContext ctx) {
        out.print(ctx.ASSERT().getText());
        visit(ctx.expression(0));
        if (ctx.expression(1) != null) {
            out.print(":");
            visit(ctx.expression(1));
        }
        out.print(";\n");
        return null;
    }

    @Override
    public Void visitIfStmt(JavaParser.IfStmtContext ctx) {
        out.print(ctx.IF().getText());
        visit(ctx.parExpression());
        visit(ctx.statement(0));
        if (ctx.statement(1) != null) {
            out.print(ctx.ELSE().getText() + " ");
            visit(ctx.statement(1));
        }
        return null;
    }

    @Override
    public Void visitForStmt(JavaParser.ForStmtContext ctx) {
        out.print("for(");
        inForLoop = true;
        visit(ctx.forControl());
        out.print(") ");
        inForLoop = false;
        visit(ctx.statement());
        return null;
    }

    @Override
    public Void visitWhileStmt(JavaParser.WhileStmtContext ctx) {
        out.print(ctx.WHILE().getText());
        visit(ctx.parExpression());
        visit(ctx.statement());
        return null;
    }

    @Override
    public Void visitDoStmt(JavaParser.DoStmtContext ctx) {
        out.print(ctx.DO().getText());
        visit(ctx.statement());
        out.print(ctx.WHILE().getText());
        visit(ctx.parExpression());
        out.print(";\n");
        return null;
    }

    @Override
    public Void visitTryBlockStmt(JavaParser.TryBlockStmtContext ctx) {
        out.print(ctx.TRY().getText());
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
        return null;
    }

    @Override
    public Void visitTryStmt(JavaParser.TryStmtContext ctx) {
        out.print(ctx.TRY().getText());
        visit(ctx.resourceSpecification());
        visit(ctx.block());
        for (JavaParser.CatchClauseContext clause : ctx.catchClause()) {
            visit(clause);
        }
        if (ctx.finallyBlock() != null) {
            visit(ctx.finallyBlock());
        }
        return null;
    }

    @Override
    public Void visitSwitchStmt(JavaParser.SwitchStmtContext ctx) {
        out.print(ctx.SWITCH().getText());
        visit(ctx.parExpression());
        out.print("{\n");
        addTab();
        printTabs();
        for (JavaParser.SwitchBlockStatementGroupContext group : ctx.switchBlockStatementGroup()) {
            visit(group);
        }
        for (JavaParser.SwitchLabelContext label : ctx.switchLabel()) {
            visit(label);
        }
        remTab();
        printTabs();
        out.print("}\n");
        return null;
    }

    @Override
    public Void visitSyncStmt(JavaParser.SyncStmtContext ctx) {
        out.print(ctx.SYNCHRONIZED().getText());
        visit(ctx.parExpression());
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitReturnStmt(JavaParser.ReturnStmtContext ctx) {
        out.print(ctx.RETURN().getText() + " ");
        if (ctx.expression() != null) {
            visit(ctx.expression());
        }
        out.print(";\n");
        return null;
    }

    @Override
    public Void visitThrowStmt(JavaParser.ThrowStmtContext ctx) {
        out.print(ctx.THROW().getText() + " ");
        visit(ctx.expression());
        out.print(";\n");
        return null;
    }

    @Override
    public Void visitBreakStmt(JavaParser.BreakStmtContext ctx) {
        out.print(ctx.BREAK().getText());
        if (ctx.identifier() != null) {
            visit(ctx.identifier());
        }
        out.print(";\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitContStmt(JavaParser.ContStmtContext ctx) {
        out.print(ctx.CONTINUE().getText());
        if (ctx.identifier() != null) {
            visit(ctx.identifier());
        }
        out.print(";\n");
        return null;
    }

    @Override
    public Void visitYieldStmt(JavaParser.YieldStmtContext ctx) {
        out.print(ctx.YIELD().getText());
        visit(ctx.expression());
        out.print(";\n");
        return null;
    }

    @Override
    public Void visitSemiStmt(JavaParser.SemiStmtContext ctx) {
        out.print(ctx.SEMI().getText() + "\n");
        printTabs();
        return null;
    }

    @Override
    public Void visitExprStmt(JavaParser.ExprStmtContext ctx) {
        //printTabs();
        visit(ctx.expression());
        out.print(";\n");
        return null;
    }

    @Override
    public Void visitSwtchExprStmt(JavaParser.SwtchExprStmtContext ctx) {
        visit(ctx.switchExpression());
        if (ctx.SEMI() != null) {
            out.print(ctx.SEMI().getText());
        }
        return null;
    }

    @Override
    public Void visitIdentStmt(JavaParser.IdentStmtContext ctx) {
        visit(ctx.identifier());
        out.print(":");
        visit(ctx.statement());
        return null;
    }

    @Override
    public Void visitCatchClause(JavaParser.CatchClauseContext ctx) {
        out.print(ctx.CATCH().getText());
        out.print("(");
        for (JavaParser.VariableModifierContext mod : ctx.variableModifier()) {
            visit(mod);
        }
        visit(ctx.catchType());
        visit(ctx.identifier());
        out.print(")");
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitCatchType(JavaParser.CatchTypeContext ctx) {
        int i = 0;
        visit(ctx.qualifiedName(i));
        i++;
        while (ctx.qualifiedName(i) != null) {
            out.print("|");
            visit(ctx.qualifiedName(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitFinallyBlock(JavaParser.FinallyBlockContext ctx) {
        out.print(ctx.FINALLY().getText());
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitResourceSpecification(JavaParser.ResourceSpecificationContext ctx) {
        out.print("(");
        visit(ctx.resources());
        if (ctx.SEMI() != null) {
            out.print(ctx.SEMI().getText());
        }
        out.print(")");
        return null;
    }

    @Override
    public Void visitResources(JavaParser.ResourcesContext ctx) {
        int i = 0;
        visit(ctx.resource(i));
        i++;
        while (ctx.resource(i) != null) {
            out.print(";");
            visit(ctx.resource(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitVarRes(JavaParser.VarResContext ctx) {
        for (JavaParser.VariableModifierContext mod : ctx.variableModifier()) {
            visit(mod);
        }
        if (ctx.VAR() == null) {
            visit(ctx.classOrInterfaceType());
            visit(ctx.variableDeclaratorId());
        } else {
            out.print(ctx.VAR().getText() + " ");
            visit(ctx.identifier());
        }
        out.print(" = ");
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitQualRes(JavaParser.QualResContext ctx) {
        visit(ctx.qualifiedName());
        return null;
    }

    @Override
    public Void visitSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) {
        for (JavaParser.SwitchLabelContext label : ctx.switchLabel()) {
            visit(label);
        }
        for (JavaParser.BlockStatementContext block : ctx.blockStatement()) {
            visit(block);
        }
        return null;
    }

    @Override
    public Void visitCaseSwLbl(JavaParser.CaseSwLblContext ctx) {
        out.print(ctx.CASE().getText() + " ");
        if (ctx.expression() != null) {
            visit(ctx.expression());
        } else if (ctx.IDENTIFIER() != null) {
            out.print(ctx.IDENTIFIER().getText());
        } else {
            visit(ctx.typeType());
            visit(ctx.identifier());
        }
        out.print(":\n");
        addTab();
        printTabs();
        remTab();
        return null;
    }

    @Override
    public Void visitDefSwLbl(JavaParser.DefSwLblContext ctx) {
        out.print(ctx.DEFAULT().getText());
        out.print(":\n");
        addTab();
        printTabs();
        remTab();
        return null;
    }

    @Override
    public Void visitEnhanForCtrl(JavaParser.EnhanForCtrlContext ctx) {
        visit(ctx.enhancedForControl());
        return null;
    }

    @Override
    public Void visitForInitForCtrl(JavaParser.ForInitForCtrlContext ctx) {
        if (ctx.forInit() != null) {
            visit(ctx.forInit());
        }
        out.print("; ");
        if (ctx.expression() != null) {
            visit(ctx.expression());
        }
        out.print("; ");
        if (ctx.expressionList() != null) {
            visit(ctx.expressionList());
        }
        return null;
    }

    @Override
    public Void visitForInitVar(JavaParser.ForInitVarContext ctx) {
        visit(ctx.localVariableDeclaration());
        return null;
    }

    @Override
    public Void visitForInitExprL(JavaParser.ForInitExprLContext ctx) {
        visit(ctx.expressionList());
        return null;
    }

    @Override
    public Void visitEnhancedForControl(JavaParser.EnhancedForControlContext ctx) {
        for (JavaParser.VariableModifierContext mod : ctx.variableModifier()) {
            visit(mod);
        }
        if (ctx.typeType() != null) {
            visit(ctx.typeType());
        } else {
            out.print(ctx.VAR().getText());
        }
        visit(ctx.variableDeclaratorId());
        out.print(":");
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitParExpression(JavaParser.ParExpressionContext ctx) {
        out.print("(");
        visit(ctx.expression());
        out.print(")");
        return null;
    }

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
    public Void visitMethodCall(JavaParser.MethodCallContext ctx) {
        if (ctx.identifier() != null) {
            visit(ctx.identifier());
        } else if (ctx.THIS() != null) {
            out.print(ctx.THIS().getText());
        } else {
            out.print(ctx.SUPER().getText());
        }
        visit(ctx.arguments());
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
        } else if (ctx.methodCall() != null) {
            visit(ctx.methodCall());
        } else if (ctx.THIS() != null) {
            out.print(ctx.THIS().getText());
        } else if (ctx.NEW() != null) {
            out.print(ctx.NEW().getText() + " ");
            if (ctx.nonWildcardTypeArguments() != null) {
                visit(ctx.nonWildcardTypeArguments());
            }
            visit(ctx.innerCreator());
        } else if (ctx.SUPER() != null) {
            out.print(ctx.SUPER().getText());
            visit(ctx.superSuffix());
        } else if (ctx.explicitGenericInvocation() != null) {
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
            if (ctx.typeArguments() != null) {
                visit(ctx.typeArguments());
            }
            visit(ctx.identifier());
        } else {
            out.print(ctx.NEW().getText() + " ");
        }
        return null;
    }

    @Override
    public Void visitMethRef3Expr(JavaParser.MethRef3ExprContext ctx) {
        visit(ctx.classType());
        out.print("::");
        if (ctx.typeArguments() != null) {
            visit(ctx.typeArguments());
        }
        out.print(ctx.NEW().getText() + " ");
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
        for (JavaParser.AnnotationContext annot : ctx.annotation()) {
            visit(annot);
        }
        visit(ctx.typeType(0));
        if (ctx.typeType(1) != null) {
            for (int i = 1; i < ctx.typeType().size(); i++) {
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
        out.print(ctx.NEW().getText() + " ");
        visit(ctx.creator());
        return null;
    }

    @Override
    public Void visitMultOpExpr(JavaParser.MultOpExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText() + " ");
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitAddOpExpr(JavaParser.AddOpExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText() + " ");
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
        out.print(ctx.bop.getText() + " ");
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitInstOfOpExpr(JavaParser.InstOfOpExprContext ctx) {
        visit(ctx.expression());
        out.print(ctx.bop.getText() + " ");
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
        out.print(ctx.bop.getText() + " ");
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitBitAndOpExpr(JavaParser.BitAndOpExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText() + " ");
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitBitXorOpExpr(JavaParser.BitXorOpExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText() + " ");
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitBitOrExpr(JavaParser.BitOrExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText() + " ");
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitLogAndExpr(JavaParser.LogAndExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText() + " ");
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitLogOrExpr(JavaParser.LogOrExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText() + " ");
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Void visitTernExpr(JavaParser.TernExprContext ctx) {
        visit(ctx.expression(0));
        out.print(ctx.bop.getText() + " ");
        visit(ctx.expression(1));
        out.print(":");
        visit(ctx.expression(2));
        return null;
    }

    @Override
    public Void visitAssignExpr(JavaParser.AssignExprContext ctx) {
        visit(ctx.expression(0));
        assignedVariables.add(ctx.expression(0).getText());
        //out.print(" "+ctx.expression(0).getText()+" ");
        out.print(ctx.bop.getText() + " ");
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
    public Void visitSwitchExpression(JavaParser.SwitchExpressionContext ctx) {
        out.print(ctx.SWITCH().getText());
        visit(ctx.parExpression());
        out.print("{\n");
        addTab();
        printTabs();
        for (JavaParser.SwitchLabeledRuleContext rule : ctx.switchLabeledRule()) {
            visit(rule);
        }
        remTab();
        printTabs();
        out.print("}\n");
        return null;
    }

    @Override
    public Void visitCaseSwLblRule(JavaParser.CaseSwLblRuleContext ctx) {
        out.print(ctx.CASE().getText());
        if (ctx.expressionList() != null) {
            visit(ctx.expressionList());
        } else if (ctx.NULL_LITERAL() != null) {
            out.print(ctx.NULL_LITERAL().getText());
        } else {
            visit(ctx.guardedPattern());
        }
        if (ctx.ARROW() != null) {
            out.print(ctx.ARROW().getText());
        } else {
            out.print(ctx.COLON().getText());
        }
        visit(ctx.switchRuleOutcome());
        return null;
    }

    @Override
    public Void visitDefSwLblRule(JavaParser.DefSwLblRuleContext ctx) {
        out.print(ctx.DEFAULT().getText());
        if (ctx.ARROW() != null) {
            out.print(ctx.ARROW().getText());
        } else {
            out.print(ctx.COLON().getText());
        }
        visit(ctx.switchRuleOutcome());
        return null;
    }

    @Override
    public Void visitGuardPatt(JavaParser.GuardPattContext ctx) {
        out.print("(");
        visit(ctx.guardedPattern());
        out.print(")");
        return null;
    }

    @Override
    public Void visitVarModGrdPatt(JavaParser.VarModGrdPattContext ctx) {
        for (JavaParser.VariableModifierContext mod : ctx.variableModifier()) {
            visit(mod);
        }
        visit(ctx.typeType());
        for (JavaParser.AnnotationContext annot : ctx.annotation()) {
            visit(annot);
        }
        visit(ctx.identifier());
        int i = 0;
        while (ctx.expression(i) != null) {
            out.print(" && ");
            visit(ctx.expression(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitExprGrdPatt(JavaParser.ExprGrdPattContext ctx) {
        visit(ctx.guardedPattern());
        out.print(" && ");
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitBlckSwRuleOut(JavaParser.BlckSwRuleOutContext ctx) {
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitBlckStmtSwRuleOut(JavaParser.BlckStmtSwRuleOutContext ctx) {
        for (JavaParser.BlockStatementContext block : ctx.blockStatement()) {
            visit(block);
        }
        return null;
    }

    @Override
    public Void visitClassType(JavaParser.ClassTypeContext ctx) {
        if (ctx.classOrInterfaceType() != null) {
            visit(ctx.classOrInterfaceType());
            out.print(".");
        }
        for (JavaParser.AnnotationContext annot : ctx.annotation()) {
            visit(annot);
        }
        visit(ctx.identifier());
        if (ctx.typeArguments() != null) {
            visit(ctx.typeArguments());
        }
        return null;
    }

    @Override
    public Void visitNonWildCreate(JavaParser.NonWildCreateContext ctx) {
        if (ctx.nonWildcardTypeArguments() != null) {
            visit(ctx.nonWildcardTypeArguments());
        }
        visit(ctx.createdName());
        visit(ctx.classCreatorRest());
        return null;
    }

    @Override
    public Void visitArrayCreate(JavaParser.ArrayCreateContext ctx) {
        visit(ctx.createdName());
        visit(ctx.arrayCreatorRest());
        return null;
    }

    @Override
    public Void visitIdentCreate(JavaParser.IdentCreateContext ctx) {
        int i = 0;
        int j = 0;
        visit(ctx.identifier(i));
        i++;
        if (ctx.typeArgumentsOrDiamond(j) != null) {
            visit(ctx.typeArgumentsOrDiamond(j));
        }
        j++;
        while (ctx.identifier(i) != null) {
            out.print(".");
            visit(ctx.identifier(i));
            i++;
            if (ctx.typeArgumentsOrDiamond(j) != null) {
                visit(ctx.typeArgumentsOrDiamond(j));
            }
            j++;
        }
        return null;
    }

    @Override
    public Void visitPrimCreate(JavaParser.PrimCreateContext ctx) {
        visit(ctx.primitiveType());
        return null;
    }

    @Override
    public Void visitInnerCreator(JavaParser.InnerCreatorContext ctx) {
        visit(ctx.identifier());
        if (ctx.nonWildcardTypeArgumentsOrDiamond() != null) {
            visit(ctx.nonWildcardTypeArgumentsOrDiamond());
        }
        visit(ctx.classCreatorRest());
        return null;
    }

    @Override
    public Void visitArrayInitCreate(JavaParser.ArrayInitCreateContext ctx) {
        for (int i = 0; i < ctx.LBRACK().size(); i++) {
            out.print("[] ");
        }
        visit(ctx.arrayInitializer());
        return null;
    }

    @Override
    public Void visitArrayExprCreate(JavaParser.ArrayExprCreateContext ctx) {
        for (JavaParser.ExpressionContext expr : ctx.expression()) {
            out.print("[");
            visit(expr);
            out.print("] ");
        }
        for (int i = 0; i < ctx.LBRACK().size(); i++) {
            out.print("[] ");
        }
        return null;
    }

    @Override
    public Void visitClassCreatorRest(JavaParser.ClassCreatorRestContext ctx) {
        visit(ctx.arguments());
        if (ctx.classBody() != null) {
            visit(ctx.classBody());
        }
        return null;
    }

    @Override
    public Void visitExplicitGenericInvocation(JavaParser.ExplicitGenericInvocationContext ctx) {
        visit(ctx.nonWildcardTypeArguments());
        if (ctx.explicitGenericInvocationSuffix() != null) {
            visit(ctx.explicitGenericInvocationSuffix());
        }
        return null;
    }

    @Override
    public Void visitDiamond(JavaParser.DiamondContext ctx) {
        out.print("<>");
        return null;
    }

    @Override
    public Void visitTypeArgs(JavaParser.TypeArgsContext ctx) {
        visit(ctx.typeArguments());
        return null;
    }

    @Override
    public Void visitNonWildDiamond(JavaParser.NonWildDiamondContext ctx) {
        out.print("<>");
        return null;
    }

    @Override
    public Void visitNonWildTypeArgs(JavaParser.NonWildTypeArgsContext ctx) {
        visit(ctx.nonWildcardTypeArguments());
        return null;
    }

    @Override
    public Void visitNonWildcardTypeArguments(JavaParser.NonWildcardTypeArgumentsContext ctx) {
        out.print("<");
        visit(ctx.typeList());
        out.print(">");
        return null;
    }

    @Override
    public Void visitTypeList(JavaParser.TypeListContext ctx) {
        int i = 0;
        visit(ctx.typeType(i));
        i++;
        while (ctx.typeType(i) != null) {
            out.print(",");
            visit(ctx.typeType(i));
            i++;
        }
        return null;
    }

    @Override
    public Void visitTypeType(JavaParser.TypeTypeContext ctx) {
        for (JavaParser.FirstAnnotationContext fAnnot : ctx.firstAnnotation()) {
            visit(fAnnot);
        }
        if (ctx.classOrInterfaceType() != null) {
            visit(ctx.classOrInterfaceType());
        } else {
            visit(ctx.primitiveType());
        }
        int i = 0;
        while (ctx.LBRACK(i) != null) {
            visit(ctx.annotationList(i));
            out.print("[]");
            i++;
        }
        return null;
    }

    @Override
    public Void visitAnnotationList(JavaParser.AnnotationListContext ctx) {
        for (JavaParser.AnnotationContext annot : ctx.annotation()) {
            visit(annot);
        }
        return null;
    }

    @Override
    public Void visitPrimitiveType(JavaParser.PrimitiveTypeContext ctx) {
        out.print(ctx.getText() + " ");
        return null;
    }

    @Override
    public Void visitTypeArguments(JavaParser.TypeArgumentsContext ctx) {
        out.print("<");
        int i = 0;
        visit(ctx.typeArgument(i));
        i++;
        while (ctx.typeArgument(i) != null) {
            out.print(", ");
            visit(ctx.typeArgument(i));
            i++;
        }
        out.print(">");
        return null;
    }

    @Override
    public Void visitArgsSupSuffix(JavaParser.ArgsSupSuffixContext ctx) {
        visit(ctx.arguments());
        return null;
    }

    @Override
    public Void visitTypeArgsSupSuffix(JavaParser.TypeArgsSupSuffixContext ctx) {
        out.print(".");
        if(ctx.typeArguments() != null){
            visit(ctx.typeArguments());
        }
        visit(ctx.identifier());
        if(ctx.arguments() != null) {
            visit(ctx.arguments());
        }
        return null;
    }

    @Override
    public Void visitSuperInvoSuffix(JavaParser.SuperInvoSuffixContext ctx) {
        out.print(ctx.SUPER().getText());
        visit(ctx.superSuffix());
        return null;
    }

    @Override
    public Void visitIdentInvoSuffix(JavaParser.IdentInvoSuffixContext ctx) {
        visit(ctx.identifier());
        visit(ctx.arguments());
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

