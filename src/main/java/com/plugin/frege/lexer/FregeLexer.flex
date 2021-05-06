package com.plugin.frege.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.plugin.frege.psi.FregeTypes;

%%

%class FregeLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

%xstate LINE_COMMENT, BLOCK_COMMENT

newline              = \r|\n|\r\n
whitespace           = \s

/* number literals */
digit                = \d
digits               = {digit}+
digitNonZero         = [1-9]
signs                = [+-]
decimal              = {signs}?(0 | {digitNonZero}((\d)*)({underscore}{digit}{digit}{digit})*)
hexChar              = [0-9A-Fa-f]
hex                  = 0[xX]{hexChar}({underscore}{hexChar}{hexChar}{hexChar})*
octalChar            = [0-7]
octal                = 0{octalChar}({underscore}{octalChar}{octalChar}{octalChar})*
int                  = {decimal} | {hex} | {octal}
intLong              = {int}[lL]
intBig               = {digit}+({underscore}{digit}{digit}{digit})*[nN]
integer              = {int} | {intLong} | {intBig}

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
string               = {doubleQuote}([^\"] | {escapeSequence})*{doubleQuote}

/* regex literal */
regex                = {regexQuote}(\\{regexQuote}|[^\`])*{regexQuote}

/* comments */
lineCommentStart     = {dash}{dash}{dash}?
lineComment          = {lineCommentStart}[^\n]*
blockCommentStart    = {leftBrace}{dash}
blockCommentEnd      = {dash}{rightBrace}
blockComment         = {blockCommentStart}~{blockCommentEnd}

/* identifiers */
conid                = \p{Lu}(\d | {underscore} | \p{L})*
varid                = \p{Ll}(\d | {underscore} | \p{L})*{quote}*
qualifier            = {conid}{dot}

/* operators */
precedence           = [123456789] | 1[0123456]
wordop               = {backQuote}\w+{backQuote}

colon                = :
doubleColon          = :: | \u2237
rightArrow           = -> | \u2192
leftArrow            = <- | \u2190
doubleRightArrow     = => | \u21D2
vertBar              = \|
ampersand            = \&
equal                = =
dash                 = -
exlamationMark       = \!
questionMark         = \?
comma                = \,
semicolon            = ;
dot                  = \. | \u2022 | \u00B7 | \u2218
doubleDot            = \.\.
slash                = \/
backSlash            = \\
underscore           = _
star                 = \* | \u2605
at                   = @
tilda                = \~
plus                 = \+
less                 = <
greater              = >
dollar               = \$
forall               = \u2200
degreeSign           = \u00B0

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
backQuote            = \`
regexQuote           = ´

%%

   /* keywords */
      "abstract"              { return FregeTypes.ABSTRACT; }
      "as"                    { return FregeTypes.AS; }
      "case"                  { return FregeTypes.CASE; }
      "class"                 { return FregeTypes.CLASS; }
      "interface"             { return FregeTypes.INTERFACE; }
      "data"                  { return FregeTypes.DATA; }
      "derive"                { return FregeTypes.DERIVE; }
      "do"                    { return FregeTypes.DO; }
      "else"                  { return FregeTypes.ELSE; }
      "false"                 { return FregeTypes.FALSE; }
      "forall"                { return FregeTypes.FORALL; }
      "hiding"                { return FregeTypes.HIDING; }
      "if"                    { return FregeTypes.IF; }
      "import"                { return FregeTypes.IMPORT; }
      "in"                    { return FregeTypes.IN; }
      "infix"                 { return FregeTypes.INFIX; }
      "infixl"                { return FregeTypes.INFIXL; }
      "infixr"                { return FregeTypes.INFIXR; }
      "inline"                { return FregeTypes.INLINE; }
      "instance"              { return FregeTypes.INSTANCE; }
      "let"                   { return FregeTypes.LET; }
      "mutable"               { return FregeTypes.MUTABLE; }
      "native"                { return FregeTypes.NATIVE; }
      "of"                    { return FregeTypes.OF; }
      "package"               { return FregeTypes.PACKAGE; }
      "module"                { return FregeTypes.MODULE; }
      "private"               { return FregeTypes.PRIVATE_MODIFIER; }
      "protected"             { return FregeTypes.PROTECTED_MODIFIER; }
      "pure"                  { return FregeTypes.PURE; }
      "public"                { return FregeTypes.PUBLIC_MODIFIER; }
      "then"                  { return FregeTypes.THEN; }
      "throws"                { return FregeTypes.THROWS; }
      "true"                  { return FregeTypes.TRUE; }
      "type"                  { return FregeTypes.TYPE; }
      "where"                 { return FregeTypes.WHERE; }

      {blockComment}          { return FregeTypes.BLOCK_COMMENT; }
      {lineComment}           { return FregeTypes.LINE_COMMENT; }
      {newline}               { return FregeTypes.NEW_LINE; }
      {whitespace}            { return TokenType.WHITE_SPACE; }

      {doubleDot}             { return FregeTypes.DOUBLE_DOT; }

   /* literals */
      {integer}               { return FregeTypes.INTEGER; }
      {float}                 { return FregeTypes.FLOAT; }
      {char}                  { return FregeTypes.CHAR; }
      {string}                { return FregeTypes.STRING; }
      {regex}                 { return FregeTypes.REGEX; }

   /* parentheses */
      {leftParen}             { return FregeTypes.LEFT_PAREN; }
      {rightParen}            { return FregeTypes.RIGHT_PAREN; }
      {leftBracket}           { return FregeTypes.LEFT_BRACKET; }
      {rightBracket}          { return FregeTypes.RIGHT_BRACKET; }
      {leftBrace}             { return FregeTypes.LEFT_BRACE; }
      {rightBrace}            { return FregeTypes.RIGHT_BRACE; }

   /* special symbols */
      {doubleColon}           { return FregeTypes.DOUBLE_COLON; }
      {colon}                 { return FregeTypes.COLON; }
      {rightArrow}            { return FregeTypes.RIGHT_ARROW; }
      {leftArrow}             { return FregeTypes.LEFT_ARROW; }
      {doubleRightArrow}      { return FregeTypes.DOUBLE_RIGHT_ARROW; }
      {vertBar}               { return FregeTypes.VERT_BAR; }
      {ampersand}             { return FregeTypes.AMPERSAND; }
      {equal}                 { return FregeTypes.EQUAL; }
      {dash}                  { return FregeTypes.DASH; }
      {exlamationMark}        { return FregeTypes.EXLAMATION_MARK; }
      {questionMark}          { return FregeTypes.QUESTION_MARK; }
      {comma}                 { return FregeTypes.COMMA; }
      {semicolon}             { return FregeTypes.SEMICOLON; }
      {slash}                 { return FregeTypes.SLASH; }
      {backSlash}             { return FregeTypes.BACK_SLASH; }
      {underscore}            { return FregeTypes.UNDERSCORE; }
      {star}                  { return FregeTypes.STAR; }
      {at}                    { return FregeTypes.AT; }
      {tilda}                 { return FregeTypes.TILDA; }
      {hash}                  { return FregeTypes.HASH; }
      {backQuote}             { return FregeTypes.BACK_QUOTE; }
      {dot}                   { return FregeTypes.DOT; }
      {plus}                  { return FregeTypes.PLUS; }
      {less}                  { return FregeTypes.LESS; }
      {greater}               { return FregeTypes.GREATER; }
      {dollar}                { return FregeTypes.DOLLAR; }
      {forall}                { return FregeTypes.FORALL; }
      {degreeSign}            { return FregeTypes.DEGREE_SIGN; }

   /* operators */
      {wordop}                { return FregeTypes.WORD_OPERATOR; }

   /* identifiers */
      {qualifier}             { return FregeTypes.QUALIFIER; }
      {conid}                 { return FregeTypes.CONID; }
      {varid}                 { return FregeTypes.VARID; }

      [^]                     { return TokenType.BAD_CHARACTER; }
