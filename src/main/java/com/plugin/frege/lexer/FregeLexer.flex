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

newline              = \r|\n|\r\n
whitespace           = \s

/* Number Literals */
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
superOrSubscript     = \u00B2 | \u00B3 | \u00B9 | \u2070 | \u2071 | [\u2074-\u209C]

exponentIndicator    = [eE]
exponentPart         = {exponentIndicator}{signs}{digits}
floatSuffix          = [fFdD]
float                = {digits}{dot}{whitespace}
                     | {digits}{dot}{floatSuffix}
                     | {digits}{dot}{exponentPart}{floatSuffix}?
                     | {digits}{dot}{digits}{exponentPart}?{floatSuffix}?
                     | {digits}{exponentPart}{floatSuffix}?
                     | {digits}{exponentPart}?{floatSuffix}

/* Char Literals */
octalEscape          = {backSlash}{octalChar} | {backSlash}{octalChar}{octalChar}
                     | {backSlash}[0-3]{octalChar}{octalChar}
escapeSequence       = {backSlash}\S
char                 = {quote}([^\'\\\n] | {escapeSequence}){quote}

/* String Literal */
string               = {doubleQuote}([^\"\\] | {escapeSequence})*{doubleQuote}

/* Regex Literal */
regex                = \u00B4([^\u00B4\\] | {escapeSequence})*\u00B4

/* Comments */
lineCommentStart     = {dash}{dash}{dash}?
lineComment          = {lineCommentStart}[^\n]*
blockCommentStart    = {leftBrace}{dash}
blockCommentEnd      = {dash}{rightBrace}
blockComment         = {blockCommentStart}~{blockCommentEnd}

/* Identifiers */
conid                = \p{Lu}(\d | {underscore} | \p{L})*
varid                = \p{Ll}(\d | {underscore} | \p{L})*{quote}*

/* Operators */
wordop               = {backQuote}\w+{backQuote}

colon                = :
doubleColon          = :: | \u2237
rightArrow           = -> | \u2192
leftArrow            = <- | \u2190
doubleRightArrow     = => | \u21D2
verticalBar          = \|
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
carret               = \^
percent              = %
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

%%

   /* keywords */
      "abstract"              { return FregeTypes.ABSTRACT; }
      "as"                    { return FregeTypes.AS; }
      "case"                  { return FregeTypes.CASE; }
      "class"                 { return FregeTypes.CLASS; }
      "interface"             { return FregeTypes.INTERFACE; }
      "data"                  { return FregeTypes.DATA; }
      "default"               { return FregeTypes.DEFAULT; }
      "derive"                { return FregeTypes.DERIVE; }
      "deriving"              { return FregeTypes.DERIVING; }
      "do"                    { return FregeTypes.DO; }
      "else"                  { return FregeTypes.ELSE; }
      "false"                 { return FregeTypes.FALSE; }
      "foreign"               { return FregeTypes.FOREIGN; }
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
      "newtype"               { return FregeTypes.NEWTYPE; }
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
      {verticalBar}           { return FregeTypes.VERTICAL_BAR; }
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
      {carret}                { return FregeTypes.CARRET; }
      {percent}               { return FregeTypes.PERCENT; }
      {forall}                { return FregeTypes.FORALL; }
      {degreeSign}            { return FregeTypes.DEGREE_SIGN; }
      {superOrSubscript}      { return FregeTypes.SUPER_OR_SUBSCRIPT; }

   /* operators */
      {wordop}                { return FregeTypes.WORD_OPERATOR; }

   /* identifiers */
      {conid}                 { return FregeTypes.CONID; }
      {varid}                 { return FregeTypes.VARID; }

      [^]                     { return FregeTypes.UNKNOWN_SYMBOL; }
