package com.plugin.frege.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
// import com.plugin.frege.psi.FregeTypes; it will be needed after we write parser

%%

%class FregeLexer
%implements FlexLexer
%unicode
%type IElementType
%eof{ return;
%eof}

// there will be tokens

%%

// there will be rules

