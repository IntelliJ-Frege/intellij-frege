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

%{
    private int blockCommentDepth;
    private int blockDocDepth;
    private int blockCommentStartPos;
    private int blockDocStartPos;
%}

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
                     | \u0027([^\u0027\\] | {escapeSequence})*\u0027


/* Documentation */
lineDocStart         = {dash}{dash}{dash}
lineDoc              = {lineDocStart}[^\n]*
blockDocStart        = {leftBrace}{dash}{dash}
blockDocEnd          = {dash}{rightBrace}

/* Comments */
lineCommentStart     = {dash}{dash}
lineComment          = {lineCommentStart}[^\n]*
blockCommentStart    = {leftBrace}{dash}
blockCommentEnd      = {dash}{rightBrace}

/* Identifiers */
conid                = \p{Lu}(\d | {underscore} | \p{L})*{quote}*
varid                = \p{Ll}(\d | {underscore} | \p{L})*{quote}*

/* Operators */
aloneOps             = [\P{L}~~[\s\p{N}\"\'\`\u00B4\u0027\.,=|{}\[\]()_]]
notAloneOps          = {equal} | {verticalBar} | {doubleColon}
symop_not_first_dot  = {aloneOps} ({aloneOps} | {notAloneOps} | {dot})* | {notAloneOps} ({aloneOps} | {notAloneOps} | {dot})+

colon                = :
doubleColon          = :: | \u2237
rightArrow           = -> | \u2192
leftArrow            = <- | \u2190
doubleRightArrow     = => | \u21D2
verticalBar          = \|
equal                = =
dash                 = -
exlamationMark       = \!
questionMark         = \?
comma                = \,
semicolon            = ;
dot                  = \.
doubleDot            = \.\.
backSlash            = \\
underscore           = _
star                 = \* | \u2605
at                   = @
tilda                = \~
forall               = \u2200
leq                  = \u2264
geq                  = \u2265

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
backQuote            = \`

%state FIRST_DOT_OPERATOR, MAYBE_FIRST_DOT_OPERATOR, BLOCK_COMMENT, BLOCK_DOC

%%
    <YYINITIAL> {
    /* keywords */
        "abstract"                   { return FregeTypes.ABSTRACT; }
        "as"                         { return FregeTypes.AS; }
        "case"                       { return FregeTypes.CASE; }
        "class"                      { return FregeTypes.CLASS; }
        "interface"                  { return FregeTypes.INTERFACE; }
        "data"                       { return FregeTypes.DATA; }
        "derive"                     { return FregeTypes.DERIVE; }
        "do"                         { return FregeTypes.DO; }
        "else"                       { return FregeTypes.ELSE; }
        "false"                      { return FregeTypes.FALSE; }
        "forall"                     { return FregeTypes.FORALL; }
        "hiding"                     { return FregeTypes.HIDING; }
        "if"                         { return FregeTypes.IF; }
        "import"                     { return FregeTypes.IMPORT; }
        "in"                         { return FregeTypes.IN; }
        "infix"                      { return FregeTypes.INFIX; }
        "infixl"                     { return FregeTypes.INFIXL; }
        "infixr"                     { return FregeTypes.INFIXR; }
        "inline"                     { return FregeTypes.INLINE; }
        "instance"                   { return FregeTypes.INSTANCE; }
        "let"                        { return FregeTypes.LET; }
        "mutable"                    { return FregeTypes.MUTABLE; }
        "native"                     { return FregeTypes.NATIVE; }
        "newtype"                    { return FregeTypes.NEWTYPE; }
        "of"                         { return FregeTypes.OF; }
        "package"                    { return FregeTypes.PACKAGE; }
        "module"                     { return FregeTypes.MODULE; }
        "private"                    { return FregeTypes.PRIVATE_MODIFIER; }
        "protected"                  { return FregeTypes.PROTECTED_MODIFIER; }
        "pure"                       { return FregeTypes.PURE; }
        "public"                     { return FregeTypes.PUBLIC_MODIFIER; }
        "then"                       { return FregeTypes.THEN; }
        "throws"                     { return FregeTypes.THROWS; }
        "true"                       { return FregeTypes.TRUE; }
        "type"                       { return FregeTypes.TYPE; }
        "where"                      { return FregeTypes.WHERE; }

        {lineDoc}                    { return FregeTypes.LINE_DOC; }
        {lineComment}                { return FregeTypes.LINE_COMMENT; }
        {blockDocStart}              { blockDocDepth = 0; blockDocStartPos = getTokenStart(); yybegin(BLOCK_DOC); }
        {blockCommentStart}          { blockCommentDepth = 0; blockCommentStartPos = getTokenStart(); yybegin(BLOCK_COMMENT); }
        {newline}                    { return FregeTypes.NEW_LINE; }
        {newline} / {dot}            { yybegin(MAYBE_FIRST_DOT_OPERATOR); return FregeTypes.NEW_LINE; }
        {whitespace}                 { return TokenType.WHITE_SPACE; }
        {whitespace} / {dot}         { yybegin(MAYBE_FIRST_DOT_OPERATOR); return TokenType.WHITE_SPACE; }

    /* literals */
        {integer}                    { return FregeTypes.INTEGER; }
        {float}                      { return FregeTypes.FLOAT; }
        {char}                       { return FregeTypes.CHAR; }
        {string}                     { return FregeTypes.STRING; }
        {regex}                      { return FregeTypes.REGEX; }

    /* parentheses */
        {leftParen}                  { return FregeTypes.LEFT_PAREN; }
        {leftParen} / {dot}          { yybegin(FIRST_DOT_OPERATOR); return FregeTypes.LEFT_PAREN; }
        {rightParen}                 { return FregeTypes.RIGHT_PAREN; }
        {leftBracket}                { return FregeTypes.LEFT_BRACKET; }
        {rightBracket}               { return FregeTypes.RIGHT_BRACKET; }
        {leftBrace}                  { return FregeTypes.LEFT_BRACE; }
        {rightBrace}                 { return FregeTypes.RIGHT_BRACE; }

    /* special symbols */
        {doubleColon}                { return FregeTypes.DOUBLE_COLON; }
        {colon}                      { return FregeTypes.COLON; }
        {rightArrow}                 { return FregeTypes.RIGHT_ARROW; }
        {leftArrow}                  { return FregeTypes.LEFT_ARROW; }
        {doubleRightArrow}           { return FregeTypes.DOUBLE_RIGHT_ARROW; }
        {verticalBar}                { return FregeTypes.VERTICAL_BAR; }
        {equal}                      { return FregeTypes.EQUAL; }
        {exlamationMark}             { return FregeTypes.EXLAMATION_MARK; }
        {questionMark}               { return FregeTypes.QUESTION_MARK; }
        {comma}                      { return FregeTypes.COMMA; }
        {semicolon}                  { return FregeTypes.SEMICOLON; }
        {backSlash}                  { return FregeTypes.BACK_SLASH; }
        {underscore}                 { return FregeTypes.UNDERSCORE; }
        {star}                       { return FregeTypes.STAR; }
        {dash}                       { return FregeTypes.DASH; }
        {at}                         { return FregeTypes.AT; }
        {tilda}                      { return FregeTypes.TILDA; }
        {backQuote}                  { return FregeTypes.BACK_QUOTE; }
        {backQuote} / {dot}          { yybegin(FIRST_DOT_OPERATOR); return FregeTypes.BACK_QUOTE; }
        {dot}                        { return FregeTypes.DOT; }
        {forall}                     { return FregeTypes.FORALL; }
        {superOrSubscript}           { return FregeTypes.SUPER_OR_SUBSCRIPT; }
        {leq}                        { return FregeTypes.LEQ; }
        {geq}                        { return FregeTypes.GEQ; }

    /* operators */
        {dot} / {rightParen}         { return FregeTypes.SYMOP_NO_RESERVED; }
        {doubleDot}                  { return FregeTypes.DOUBLE_DOT; }
        {dot} /
          {dot}{symop_not_first_dot} { yybegin(FIRST_DOT_OPERATOR); return FregeTypes.DOT; }
        {symop_not_first_dot}        { return FregeTypes.SYMOP_NO_RESERVED; }
    /* identifiers */
        {conid}                      { return FregeTypes.CONID; }
        {varid}                      { return FregeTypes.VARID; }
    }

    <FIRST_DOT_OPERATOR> {
        {dot}{dot}                   { yybegin(YYINITIAL); return FregeTypes.DOUBLE_DOT; }
        {dot}{symop_not_first_dot}?  { yybegin(YYINITIAL); return FregeTypes.SYMOP_NO_RESERVED; }
    }

    <MAYBE_FIRST_DOT_OPERATOR> {
        {dot} / {whitespace}         { yybegin(YYINITIAL); return FregeTypes.SYMOP_NO_RESERVED; }
        {dot} / {rightParen}         { yybegin(YYINITIAL); return FregeTypes.SYMOP_NO_RESERVED; }
        {dot}{dot}                   { yybegin(YYINITIAL); return FregeTypes.DOUBLE_DOT; }
        {dot}{symop_not_first_dot}   { yybegin(YYINITIAL); return FregeTypes.SYMOP_NO_RESERVED; }
        {dot}                        { yybegin(YYINITIAL); return FregeTypes.DOT; }
    }

    <BLOCK_DOC> {
        {blockDocStart} {
            blockDocDepth++;
        }
        <<EOF>> {
            yybegin(YYINITIAL);
            zzStartRead = blockDocStartPos;
            return TokenType.BAD_CHARACTER;
        }
        {blockDocEnd} {
            blockDocDepth--;
            if (blockDocDepth == -1) {
                yybegin(YYINITIAL);
                zzStartRead = blockDocStartPos;
                return FregeTypes.BLOCK_DOC;
            }
        }
        [^] {}
    }

    <BLOCK_COMMENT> {
        {blockCommentStart} {
            blockCommentDepth++;
        }
        <<EOF>> {
            yybegin(YYINITIAL);
            zzStartRead = blockCommentStartPos;
            return TokenType.BAD_CHARACTER;
        }
        {blockCommentEnd} {
            blockCommentDepth--;
            if (blockCommentDepth == -1) {
                yybegin(YYINITIAL);
                zzStartRead = blockCommentStartPos;
                return FregeTypes.BLOCK_COMMENT;
            }
        }
        [^] {}
    }

    [^]                              { return TokenType.BAD_CHARACTER; }
