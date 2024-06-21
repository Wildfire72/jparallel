/*
 [The "BSD licence"]
 Copyright (c) 2013 Terence Parr, Sam Harwell
 Copyright (c) 2017 Ivan Kochurkin (upgrade to Java 8)
 Copyright (c) 2021 Michał Lorek (upgrade to Java 11)
 Copyright (c) 2022 Michał Lorek (upgrade to Java 17)
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

parser grammar JavaParser;

options { tokenVocab=JavaLexer; }

compilationUnit
    : packageDeclaration? (importDeclaration | ';')* (typeDeclaration | ';')*                   #compUnitPack
    | moduleDeclaration EOF                                                                     #compUnitEOF
    ;

packageDeclaration
    : annotation* PACKAGE qualifiedName ';'
    ;

importDeclaration
    : IMPORT STATIC? qualifiedName (DOT MUL)? ';'
    ;

typeDeclaration
    : classOrInterfaceModifier*
      (classDeclaration | enumDeclaration | interfaceDeclaration | annotationTypeDeclaration | recordDeclaration)
    ;

modifier
    : classOrInterfaceModifier                                              #classOrIntMod
    | NATIVE                                                                #nativeMod
    | SYNCHRONIZED                                                          #syncMod
    | TRANSIENT                                                             #transMod
    | VOLATILE                                                              #volaMod
    ;

classOrInterfaceModifier
    : annotation                                                            #annotate
    | PUBLIC                                                                #pubMod
    | PROTECTED                                                             #protMod
    | PRIVATE                                                               #privMod
    | STATIC                                                                #statMod
    | ABSTRACT                                                              #abstrMod
    | FINAL    /* FINAL for class only -- does not apply to interfaces */   #finMod
    | STRICTFP                                                              #striMod
    | SEALED /* Java17 */                                                   #sealMod
    | NON_SEALED /* Java17 */                                               #nsealMod
    ;

variableModifier
    : FINAL                                                                 #finVarMod
    | annotation                                                            #annotVarMod
    ;

classDeclaration
    : CLASS identifier typeParameters?
      (EXTENDS typeType)?
      (IMPLEMENTS typeList)?
      (PERMITS typeList)? // Java17
      classBody
    ;

typeParameters
    : '<' typeParameter (',' typeParameter)* '>'
    ;

typeParameter
    : annotation* identifier (EXTENDS annotation* typeBound)?
    ;

typeBound
    : typeType ('&' typeType)*
    ;

enumDeclaration
    : ENUM identifier (IMPLEMENTS typeList)? '{' enumConstants? COMMA? enumBodyDeclarations? '}'
    ;

enumConstants
    : enumConstant (',' enumConstant)*
    ;

enumConstant
    : annotation* identifier arguments? classBody?
    ;

enumBodyDeclarations
    : ';' classBodyDeclaration*
    ;

interfaceDeclaration
    : INTERFACE identifier typeParameters? (EXTENDS typeList)? (PERMITS typeList)? interfaceBody
    ;

classBody
    : '{' classBodyDeclaration* '}'
    ;

interfaceBody
    : '{' interfaceBodyDeclaration* '}'
    ;

classBodyDeclaration
    : ';'                                       #classBodyDeclSemi
    | STATIC? block                             #classBodyDeclStatic
    | modifier* memberDeclaration               #classBodyDeclMod
    ;

memberDeclaration
    : recordDeclaration /*Java17*/              #recDecl
    | methodDeclaration                         #methDecl
    | genericMethodDeclaration                  #genMethDecl
    | fieldDeclaration                          #fieldDecl
    | constructorDeclaration                    #consDecl
    | genericConstructorDeclaration             #genConsDecl
    | interfaceDeclaration                      #intDecl
    | annotationTypeDeclaration                 #annotTypeDecl
    | classDeclaration                          #classDecl
    | enumDeclaration                           #enumDecl
    ;

/* We use rule this even for void methods which cannot have [] after parameters.
   This simplifies grammar and we can consider void to be a type, which
   renders the [] matching as a context-sensitive issue or a semantic check
   for invalid return type after parsing.
 */
methodDeclaration
    : typeTypeOrVoid identifier formalParameters (LBRACK RBRACK)*
      (THROWS qualifiedNameList)?
      methodBody
    ;

methodBody
    : block                         #methBodyBlock
    | ';'                           #methBodySemi
    ;

typeTypeOrVoid
    : typeType                      #tyType
    | VOID                          #void
    ;

genericMethodDeclaration
    : typeParameters methodDeclaration
    ;

genericConstructorDeclaration
    : typeParameters constructorDeclaration
    ;

constructorDeclaration
    : identifier formalParameters (THROWS qualifiedNameList)? constructorBody=block
    ;

compactConstructorDeclaration
    : modifier* identifier constructorBody=block
    ;

fieldDeclaration
    : typeType variableDeclarators ';'
    ;

interfaceBodyDeclaration
    : modifier* interfaceMemberDeclaration                          #intBodyMod
    | ';'                                                           #intBodySemi
    ;

interfaceMemberDeclaration
    : recordDeclaration /* Java17 */                                #intRecDecl
    | constDeclaration                                              #intConsDecl
    | interfaceMethodDeclaration                                    #intMethDecl
    | genericInterfaceMethodDeclaration                             #genIntMethDecl
    | interfaceDeclaration                                          #intIntDecl
    | annotationTypeDeclaration                                     #annotDecl
    | classDeclaration                                              #intClassDecl
    | enumDeclaration                                               #intEnumDecl
    ;

constDeclaration
    : typeType constantDeclarator (',' constantDeclarator)* ';'
    ;

constantDeclarator
    : identifier (LBRACK RBRACK)* '=' variableInitializer
    ;

// Early versions of Java allows brackets after the method name, eg.
// public int[] return2DArray() [] { ... }
// is the same as
// public int[][] return2DArray() { ... }
interfaceMethodDeclaration
    : interfaceMethodModifier* interfaceCommonBodyDeclaration
    ;

// Java8
interfaceMethodModifier
    : annotation                                        #intAnnotMod
    | PUBLIC                                            #intPubMod
    | ABSTRACT                                          #intAbsMod
    | DEFAULT                                           #intDefMod
    | STATIC                                            #intStatMod
    | STRICTFP                                          #intStriMod
    ;

genericInterfaceMethodDeclaration
    : interfaceMethodModifier* typeParameters interfaceCommonBodyDeclaration
    ;

interfaceCommonBodyDeclaration
    : annotation* typeTypeOrVoid identifier formalParameters (LBRACK RBRACK)* (THROWS qualifiedNameList)? methodBody
    ;

variableDeclarators
    : variableDeclarator (',' variableDeclarator)*
    ;

variableDeclarator
    : variableDeclaratorId ('=' variableInitializer)?
    ;

variableDeclaratorId
    : identifier (LBRACK RBRACK)*
    ;

variableInitializer
    : arrayInitializer                              #arrayInit
    | expression                                    #varExprInit
    ;

arrayInitializer
    : '{' (variableInitializer (',' variableInitializer)* COMMA? )? '}'
    ;

classOrInterfaceType
    : (identifier typeArguments? '.')* typeIdentifier typeArguments?
    ;

typeArgument
    : typeType                                                  #tyTyTypeArg
    | annotation* '?' ((EXTENDS | SUPER) typeType)?             #tyTyAnnot
    ;

qualifiedNameList
    : qualifiedName (',' qualifiedName)*
    ;

formalParameters
    : '(' ( receiverParameter?
          | receiverParameter (',' formalParameterList)?
          | formalParameterList?
          ) ')'
    ;

receiverParameter
    : typeType (identifier '.')* THIS
    ;

formalParameterList
    : formalParameter (',' formalParameter)* (',' lastFormalParameter)?
    | lastFormalParameter
    ;

formalParameter
    : variableModifier* typeType variableDeclaratorId
    ;

lastFormalParameter
    : variableModifier* typeType annotation* '...' variableDeclaratorId
    ;

// local variable type inference
lambdaLVTIList
    : lambdaLVTIParameter (',' lambdaLVTIParameter)*
    ;

lambdaLVTIParameter
    : variableModifier* VAR identifier
    ;

qualifiedName
    : identifier ('.' identifier)*
    ;

literal
    : integerLiteral                                        #intLit
    | floatLiteral                                          #floatLit
    | CHAR_LITERAL                                          #charLit
    | STRING_LITERAL                                        #stringLit
    | BOOL_LITERAL                                          #boolLit
    | NULL_LITERAL                                          #nullLit
    | TEXT_BLOCK /* Java17 */                               #txtBlocLit
    ;

integerLiteral
    : DECIMAL_LITERAL                                       #decLit
    | HEX_LITERAL                                           #hexLit
    | OCT_LITERAL                                           #octLit
    | BINARY_LITERAL                                        #binLit
    ;

floatLiteral
    : FLOAT_LITERAL                                         #actFloatLit
    | HEX_FLOAT_LITERAL                                     #hexFloatLit
    ;

// ANNOTATIONS
altAnnotationQualifiedName
    : (identifier DOT)* '@' identifier
    ;

annotation
    : ('@' qualifiedName | altAnnotationQualifiedName) (LPAREN ( elementValuePairs | elementValue )? RPAREN)?
    ;

elementValuePairs
    : elementValuePair (',' elementValuePair)*
    ;

elementValuePair
    : identifier '=' elementValue
    ;

elementValue
    : expression                                                #exprElVal
    | annotation                                                #annotElVal
    | elementValueArrayInitializer                              #arrayElVal
    ;

elementValueArrayInitializer
    : '{' (elementValue (',' elementValue)*)? COMMA? '}'
    ;

annotationTypeDeclaration
    : '@' INTERFACE identifier annotationTypeBody
    ;

annotationTypeBody
    : '{' annotationTypeElementDeclaration* '}'
    ;

annotationTypeElementDeclaration
    : modifier* annotationTypeElementRest
    | ';' // this is not allowed by the grammar, but apparently allowed by the actual compiler
    ;

annotationTypeElementRest
    : typeType annotationMethodOrConstantRest ';'                   #annotTyTy
    | classDeclaration SEMI?                                        #annotClass
    | interfaceDeclaration SEMI?                                    #annotInt
    | enumDeclaration SEMI?                                         #annotEnum
    | annotationTypeDeclaration SEMI?                               #annotType
    | recordDeclaration SEMI? /* Java17 */                          #annotRec
    ;

annotationMethodOrConstantRest
    : annotationMethodRest                                          #annotMeth
    | annotationConstantRest                                        #annotConst
    ;

annotationMethodRest
    : identifier '(' ')' defaultValue?
    ;

annotationConstantRest
    : variableDeclarators
    ;

defaultValue
    : DEFAULT elementValue
    ;

// MODULES - Java9

moduleDeclaration
    : OPEN? MODULE qualifiedName moduleBody
    ;

moduleBody
    : '{' moduleDirective* '}'
    ;

moduleDirective
	: REQUIRES requiresModifier* qualifiedName ';'                              #modReq
	| EXPORTS qualifiedName (TO qualifiedName)? ';'                             #modExp
	| OPENS qualifiedName (TO qualifiedName)? ';'                               #modOpen
	| USES qualifiedName ';'                                                    #modUses
	| PROVIDES qualifiedName WITH qualifiedName ';'                             #modProv
	;

requiresModifier
	: TRANSITIVE                                                                #reqTrans
	| STATIC                                                                    #reqStat
	;

// RECORDS - Java 17

recordDeclaration
    : RECORD identifier typeParameters? recordHeader
      (IMPLEMENTS typeList)?
      recordBody
    ;

recordHeader
    : '(' recordComponentList? ')'
    ;

recordComponentList
    : recordComponent (',' recordComponent)*
    ;

recordComponent
    : typeType identifier
    ;

recordBody
    : '{' (classBodyDeclaration | compactConstructorDeclaration)*  '}'
    ;

// STATEMENTS / BLOCKS

block
    : '{' blockStatement* '}'
    ;

blockStatement
    : localVariableDeclaration ';'
    | localTypeDeclaration
    | statement
    ;

localVariableDeclaration
    : variableModifier* (VAR identifier '=' expression | typeType variableDeclarators)
    ;

identifier
    : IDENTIFIER
    | MODULE
    | OPEN
    | REQUIRES
    | EXPORTS
    | OPENS
    | TO
    | USES
    | PROVIDES
    | WITH
    | TRANSITIVE
    | YIELD
    | SEALED
    | PERMITS
    | RECORD
    | VAR
    ;

typeIdentifier  // Identifiers that are not restricted for type declarations
    : IDENTIFIER
    | MODULE
    | OPEN
    | REQUIRES
    | EXPORTS
    | OPENS
    | TO
    | USES
    | PROVIDES
    | WITH
    | TRANSITIVE
    | SEALED
    | PERMITS
    | RECORD
    ;

localTypeDeclaration
    : classOrInterfaceModifier*
      (classDeclaration | interfaceDeclaration | recordDeclaration)
    ;

statement
    : blockLabel=block                                                              #blockStmt
    | ASSERT expression (':' expression)? ';'                                       #assertStmt
    | IF parExpression statement (ELSE statement)?                                  #ifStmt
    | FOR '(' forControl ')' statement                                              #forStmt
    | WHILE parExpression statement                                                 #whileStmt
    | DO statement WHILE parExpression ';'                                          #doStmt
    | TRY block (catchClause+ finallyBlock? | finallyBlock)                         #tryBlockStmt
    | TRY resourceSpecification block catchClause* finallyBlock?                    #tryStmt
    | SWITCH parExpression '{' switchBlockStatementGroup* switchLabel* '}'          #switchStmt
    | SYNCHRONIZED parExpression block                                              #syncStmt
    | RETURN expression? ';'                                                        #returnStmt
    | THROW expression ';'                                                          #throwStmt
    | BREAK identifier? ';'                                                         #breakStmt
    | CONTINUE identifier? ';'                                                      #contStmt
    | YIELD expression ';' /* Java17 */                                             #yieldStmt
    | SEMI                                                                          #semiStmt
    | statementExpression=expression ';'                                            #exprStmt
    | switchExpression SEMI? /* Java17 */                                           #swtchExprStmt
    | identifierLabel=identifier ':' statement                                      #identStmt
    ;

catchClause
    : CATCH '(' variableModifier* catchType identifier ')' block
    ;

catchType
    : qualifiedName ('|' qualifiedName)*
    ;

finallyBlock
    : FINALLY block
    ;

resourceSpecification
    : '(' resources SEMI? ')'
    ;

resources
    : resource (';' resource)*
    ;

resource
    : variableModifier* ( classOrInterfaceType variableDeclaratorId | VAR identifier ) '=' expression       #varRes
    | qualifiedName                                                                                         #qualRes
    ;

/** Matches cases then statements, both of which are mandatory.
 *  To handle empty cases at the end, we add switchLabel* to statement.
 */
switchBlockStatementGroup
    : switchLabel+ blockStatement+
    ;

switchLabel
    : CASE (constantExpression=expression | enumConstantName=IDENTIFIER | typeType varName=identifier) ':'  #caseSwLbl
    | DEFAULT ':'                                                                                           #defSwLbl
    ;

forControl
    : enhancedForControl                                                        #enhanForCtrl
    | forInit? ';' expression? ';' forUpdate=expressionList?                    #forInitForCtrl
    ;

forInit
    : localVariableDeclaration                                                  #forInitVar
    | expressionList                                                            #forInitExprL
    ;

enhancedForControl
    : variableModifier* (typeType | VAR) variableDeclaratorId ':' expression
    ;

// EXPRESSIONS

parExpression
    : '(' expression ')'
    ;

expressionList
    : expression (',' expression)*
    ;

methodCall
    : (identifier | THIS | SUPER) arguments
    ;

expression
    // Expression order in accordance with https://introcs.cs.princeton.edu/java/11precedence/
    // Level 16, Primary, array and member access
    : primary                                                                               #primaryExpr
    | expression '[' expression ']'                                                         #brackExpr
    | expression bop='.'
      (
         identifier
       | methodCall
       | THIS
       | NEW nonWildcardTypeArguments? innerCreator
       | SUPER superSuffix
       | explicitGenericInvocation
      )                                                                                     #bopExpr
    // Method calls and method references are part of primary, and hence level 16 precedence
    | methodCall                                                                            #methCallExpr
    | expression '::' typeArguments? identifier                                             #methRef1Expr
    | typeType '::' (typeArguments? identifier | NEW)                                       #methRef2Expr
    | classType '::' typeArguments? NEW                                                     #methRef3Expr

    | switchExpression /* Java17 */                                                         #swtchExpr

    // Level 15 Post-increment/decrement operators
    | expression postfix=('++' | '--')                                                      #incDecOpExpr

    // Level 14, Unary operators
    | prefix=('+'|'-'|'++'|'--'|'~'|'!') expression                                         #unaryOpExpr

    // Level 13 Cast and object creation
    | '(' annotation* typeType ('&' typeType)* ')' expression                               #castExpr
    | NEW creator                                                                           #objCreateExpr

    // Level 12 to 1, Remaining operators
    | expression bop=('*'|'/'|'%') expression  /* Level 12, Multiplicative operators */             #multOpExpr
    | expression bop=('+'|'-') expression  /* Level 11, Additive operators */                       #addOpExpr
    | expression ('<' '<' | '>' '>' '>' | '>' '>') expression  /* Level 10, Shift operators */      #shiftOpExpr
    | expression bop=('<=' | '>=' | '>' | '<') expression  /* Level 9, Relational operators */      #relOpExpr
    | expression bop=INSTANCEOF (typeType | pattern)                                                #instOfOpExpr
    | expression bop=('==' | '!=') expression  /* Level 8, Equality Operators */                    #equaOpExpr
    | expression bop='&' expression  /* Level 7, Bitwise AND */                                     #bitAndOpExpr
    | expression bop='^' expression  /* Level 6, Bitwise XOR */                                     #bitXorOpExpr
    | expression bop='|' expression  /* Level 5, Bitwise OR */                                      #bitOrExpr
    | expression bop='&&' expression  /* Level 4, Logic AND */                                      #logAndExpr
    | expression bop='||' expression  /* Level 3, Logic OR */                                       #logOrExpr
    | <assoc=right> expression bop='?' expression ':' expression  /* Level 2, Ternary */            #ternExpr
    // Level 1, Assignment
    | <assoc=right> expression
      bop=('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%=')
      expression                                                                                    #assignExpr

    // Level 0, Lambda Expression
    | lambdaExpression /* Java8 */                                                                  #lambdaExpr
    ;

// Java17
pattern
    : variableModifier* typeType annotation* identifier
    ;

// Java8
lambdaExpression
    : lambdaParameters '->' lambdaBody
    ;

// Java8
lambdaParameters
    : identifier                                                    #lambIdent
    | '(' formalParameterList? ')'                                  #lambList
    | '(' identifier (',' identifier)* ')'                          #lambIdent2
    | '(' lambdaLVTIList? ')'                                       #lambLvti
    ;

// Java8
lambdaBody
    : expression                                                    #lambBodyExpr
    | block                                                         #lambBodyBloc
    ;

primary
    : '(' expression ')'                                                                #primExpr
    | THIS                                                                              #primThis
    | SUPER                                                                             #primSuper
    | literal                                                                           #primLit
    | identifier                                                                        #primIdent
    | typeTypeOrVoid '.' CLASS                                                          #primClass
    | nonWildcardTypeArguments (explicitGenericInvocationSuffix | THIS arguments)       #primWild
    ;

// Java17
switchExpression
    : SWITCH parExpression '{' switchLabeledRule* '}'
    ;

// Java17
switchLabeledRule
    : CASE (expressionList | NULL_LITERAL | guardedPattern) (ARROW | COLON) switchRuleOutcome       #caseSwLblRule
    | DEFAULT (ARROW | COLON) switchRuleOutcome                                                     #defSwLblRule
    ;

// Java17
guardedPattern
    : '(' guardedPattern ')'                                                                #guardPatt
    | variableModifier* typeType annotation* identifier ('&&' expression)*                  #varModGrdPatt
    | guardedPattern '&&' expression                                                        #exprGrdPatt
    ;

// Java17
switchRuleOutcome
    : block                                                                                 #blckSwRuleOut
    | blockStatement*                                                                       #blckStmtSwRuleOut
    ;

classType
    : (classOrInterfaceType '.')? annotation* identifier typeArguments?
    ;

creator
    : nonWildcardTypeArguments? createdName classCreatorRest                                #nonWildCreate
    | createdName arrayCreatorRest                                                          #arrayCreate
    ;

createdName
    : identifier typeArgumentsOrDiamond? ('.' identifier typeArgumentsOrDiamond?)*          #identCreate
    | primitiveType                                                                         #primCreate
    ;

innerCreator
    : identifier nonWildcardTypeArgumentsOrDiamond? classCreatorRest
    ;

arrayCreatorRest
    : (LBRACK RBRACK)+ arrayInitializer                                                     #arrayInitCreate
    | ('[' expression ']')+ (LBRACK RBRACK)*                                                #arrayExprCreate
    ;

classCreatorRest
    : arguments classBody?
    ;

explicitGenericInvocation
    : nonWildcardTypeArguments explicitGenericInvocationSuffix
    ;

typeArgumentsOrDiamond
    : '<' '>'                                                                               #diamond
    | typeArguments                                                                         #typeArgs
    ;

nonWildcardTypeArgumentsOrDiamond
    : '<' '>'                                                                               #nonWildDiamond
    | nonWildcardTypeArguments                                                              #nonWildTypeArgs
    ;

nonWildcardTypeArguments
    : '<' typeList '>'
    ;

typeList
    : typeType (',' typeType)*
    ;

typeType
    : annotation* (classOrInterfaceType | primitiveType) (annotation* '[' ']')*
    ;

primitiveType
    : BOOLEAN
    | CHAR
    | BYTE
    | SHORT
    | INT
    | LONG
    | FLOAT
    | DOUBLE
    ;

typeArguments
    : '<' typeArgument (',' typeArgument)* '>'
    ;

superSuffix
    : arguments                                                                             #argsSupSuffix
    | '.' typeArguments? identifier arguments?                                              #typeArgsSupSuffix
    ;

explicitGenericInvocationSuffix
    : SUPER superSuffix                                                                     #superInvoSuffix
    | identifier arguments                                                                  #identInvoSuffix
    ;

arguments
    : '(' expressionList? ')'
    ;
