package com.plugin.frege.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
// import com.plugin.frege.psi.FregeTypes; it will be needed after we write parser

%%

%class FregeLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

%xstate LINE_COMMENT, BLOCK_COMMENT

whitespace           = \s

/* number literals */
digit                = \d
digits               = {digit}+
digitNonZero         = [1-9]
signs                = [+-]
decimal              = {signs}?(0 | {digitNonZero}{digits}({underscore}{digit}{digit}{digit})*)
hexChar              = [0-9A-Fa-f]
hex                  = 0[xX]{hexChar}({underscore}{hexChar}{hexChar}{hexChar})*
octalChar            = [0-7]
octal                = 0{octalChar}({underscore}{octalChar}{octalChar}{octalChar})*
integer              = {decimal} | {hex} | {octal}
integerLong          = {integer}[lL]
integerBig           = {digit}+({underscore}{digit}{digit}{digit})*[nN]

exponentIndicator    = [eE]
exponentPart         = {exponentIndicator}{signs}{digits}
floatSuffix          = [fFdD]
float                = {digits}{dot}{digits}?{exponentPart}?{floatSuffix}?
                       | {digits}{exponentPart}{floatSuffix}?
                       | {digits}{exponentPart}?{floatSuffix}

/* char literal */
octalEscape          = {backSlash}{octalChar} | {backSlash}{octalChar}{octalChar}
                       | {backSlash}[0-3]{octalChar}{octalChar}
escapeSequence       = {backSlash}b | {backSlash}t | {backSlash}n | {backSlash}f {backSlash}r
                       | {backSlash}{doubleQuote} | {backSlash}{quote} | {backSlash}{backSlash}
                       | {octalEscape}
char                 = {quote}([^\'\\\n] | {escapeSequence}){quote}

/* string literal */
string               = {doubleQuote}([^\"]{backSlash} | {escapeSequence}){doubleQuote}

/* regex literal */
regex                = `(\\`|[^\`])*`

/* comments */
lineCommentStart     = {dash}{dash}{dash}{questionMark} // ---?
blockCommentStart    = {leftBrace}{dash}{dash}          // {--
blockCommentEnd      = {dash}{rightBrace}               // -}

/* identifiers */
conid                = \p{Lu}(\d | {underscore} | \p{L})*
varid                = \p{Ll}(\d | {underscore} | \p{L})*{quote}*
qualifier            = {conid}{dot}

/* operators */
precedence           = [123456789] | 1[0123456]
symop                = \W+
wordop               = {backQuote}\w+{backQuote}

colon                = :
doubleColon          = ::
rightArrow           = ->
leftArrow            = <-
doubleRightArrow     = =>
vertBar              = \|
equal                = =
dash                 = -
exlamationMark       = \!
questionMark         = \?
comma                = \,
semicolon            = ;
dot                  = \.
backSlash            = \\
underscore           = _

/* parentheses  */
leftParen            = \(
rightParen           = \)
leftBracket          = \[
rightBracket         = \]
leftBrace            = \{
rightBrace           = \}

/* quotes */
quote                = \'
doubleQuote          = \"
hash                 = #
backQuote            = ‘

%%

{lineCommentStart}            { yybegin(LINE_COMMENT); }

<LINE_COMMENT> {
      \n                      { yybegin(YYINITIAL); }
      .                       {}
}

{blockCommentStart}           { yybegin(BLOCK_COMMENT); }

<BLOCK_COMMENT> {
      {blockCommentEnd}       { yybegin(YYINITIAL); }
      .                       {}
}

   /* keywords */
      "abstract"              { return FregeTypes.ABSTRACT; }
      "case"                  { return FregeTypes.CASE; }
      "class"                 { return FregeTypes.CLASS; }
      "interface"             { return FregeTypes.INTERFACE; }
      "data"                  { return FregeTypes.DATA; }
      "derive"                { return FregeTypes.DERIVE; }
      "do"                    { return FregeTypes.DO; }
      "else"                  { return FregeTypes.ELSE; }
      "false"                 { return FregeTypes.FALSE; }
      "forall"                { return FregeTypes.FORALL; }
      "if"                    { return FregeTypes.IF; }
      "import"                { return FregeTypes.IMPORT; }
      "in"                    { return FregeTypes.IN; }
      "infix"                 { return FregeTypes.INFIX; }
      "infixl"                { return FregeTypes.INFIXL; }
      "infixr"                { return FregeTypes.INFIXR; }
      "instance"              { return FregeTypes.INSTANCE; }
      "let"                   { return FregeTypes.LET; }
      "mutable"               { return FregeTypes.MUTABLE; }
      "native"                { return FregeTypes.NATIVE; }
      "of"                    { return FregeTypes.OF; }
      "package"               { return FregeTypes.PACKAGE; }
      "module"                { return FregeTypes.MODULE; }
      "private"               { return FregeTypes.PRIVATE; }
      "protected"             { return FregeTypes.PROTECTED; }
      "pure"                  { return FregeTypes.PURE; }
      "public"                { return FregeTypes.PUBLIC; }
      "then"                  { return FregeTypes.THEN; }
      "throws"                { return FregeTypes.THROWS; }
      "true"                  { return FregeTypes.TRUE; }
      "type"                  { return FregeTypes.TYPE; }
      "where"                 { return FregeTypes.WHERE; }

   /* literals */
      {integer}               { return FregeTypes.INTEGER; }
      {float}                 { return FregeTypes.FLOAT; }
      {char}                  { return Frege.CHAR; }
      {string}                { return Frege.STRING; }
      {regex}                 { return Frege.REGEX; }

   /* parentheses */
      {leftParen}             { return FregeTypes.LEFT_PAREN; }
      {rightParen}            { return FregeTypes.RIGHT_PAREN; }
      {leftBracket}           { return FregeTypes.LEFT_BRACKET; }
      {rightBracket}          { return FregeTypes.RIGHT_BRACKET; }
      {leftBrace}             { return FregeTypes.LEFT_BRACE; }
      {rightBrace}            { return FregeTypes.RIGHT_BRACE; }

   /* symbols */
      {doubleColon}           { return FregeTypes.DOUBLE_COLON; }
      {colon}                 { return FregeTypes.COLON; }
      {rightArrow}            { return FregeTypes.RIGHT_ARROW; }
      {leftArrow}             { return FregeTypes.LEFT_ARROW; }
      {doubleRightArrow}      { return FregeTypes.DOUBLE_RIHGT_ARROW; }
      {vertBar}               { return FregeTypes.VERT_BAR; }
      {equal}                 { return FregeTypes.EQUAL; }
      {dash}                  { return FregeTypes.DASH; }
      {exlamationMark}        { return FregeTypes.EXLAMATION_MARK; }
      {questionMark}          { return FregeTypes.QUESTION_MARK; }
      {comma}                 { return FregeTypes.COMMA; }
      {semicolon}             { return FregeTypes.SEMICOLON; }
      {backSlash}             { return FregeTypes.BACK_SLASH; }
      {underscore}            { return FregeTypes.UNDERSCORE; }

   /* operators */
      {symop}                 { return FregeTypes.SYMBOL_OPERATOR; }
      {wordop}                { return FregeTypes.WORD_OPERATOR; }

   /* identifiers */
      {qualifier}             { return FregeTypes.QUALIFIER; }
      {conid}                 { return FregeTypes.CONID; }
      {varid}                 { return FregeTypes.VARID; }
