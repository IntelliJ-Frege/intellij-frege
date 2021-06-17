package com.plugin.frege.highlighter;

import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.plugin.frege.FregeIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class FregeColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
            new AttributesDescriptor("Keyword", FregeSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Number", FregeSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Char", FregeSyntaxHighlighter.CHAR),
            new AttributesDescriptor("String", FregeSyntaxHighlighter.STRING),
            new AttributesDescriptor("Type", FregeSyntaxHighlighter.TYPE),
            new AttributesDescriptor("Type parameter", FregeSyntaxHighlighter.TYPE_PARAMETER),
            new AttributesDescriptor("Brackets", FregeSyntaxHighlighter.BRACKETS),
            new AttributesDescriptor("Operator", FregeSyntaxHighlighter.OPERATOR),
            new AttributesDescriptor("Function name", FregeSyntaxHighlighter.FUNCTION_NAME),
            new AttributesDescriptor("Line comment", FregeSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor("Block comment", FregeSyntaxHighlighter.BLOCK_COMMENT),
            new AttributesDescriptor("Line documentation", FregeSyntaxHighlighter.LINE_DOC),
            new AttributesDescriptor("Block documentation", FregeSyntaxHighlighter.BLOCK_DOC),
            new AttributesDescriptor("Undefined", FregeSyntaxHighlighter.UNDEFINED),
            new AttributesDescriptor("Bad value", FregeSyntaxHighlighter.BAD_CHARACTER)
    };

    private static final Map<String, TextAttributesKey> additionalHighlightingTagToDescriptorMap = ImmutableMap.of(
            "funcName", FregeSyntaxHighlighter.FUNCTION_NAME,
            "import", FregeSyntaxHighlighter.TYPE,
            "undefined", FregeSyntaxHighlighter.UNDEFINED
    );

    @Nullable
    @Override
    public Icon getIcon() {
        return FregeIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new FregeSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "-- https://github.com/Frege/frege/blob/master/examples/Grep.fr" +
                "--- A simple grep\n" +
                "module <import>examples.Grep</import> where\n" +
                "\n" +
                "import <import>another.mine.Hello</import> -- importing a module" +
                "\n" +
                "\n" +
                "--- exception thrown when an invalid regular expression is compiled\n" +
                "data PatternSyntax = native java.util.regex.PatternSyntaxException\n" +
                "derive Exceptional PatternSyntax\n" +
                "\n" +
                "<funcName>main</funcName> [] = stderr.println \"Usage: java examples.Grep regex [files ...]\"\n" +
                "<funcName>main</funcName> (pat:xs) = do\n" +
                "        rgx <- return (regforce pat)\n" +
                "        case xs of\n" +
                "            [] -> grepit rgx stdin\n" +
                "            fs -> mapM_ (run rgx) fs\n" +
                "     `catch` badpat where\n" +
                "        <funcName>badpat</funcName> :: PatternSyntax -> IO ()\n" +
                "        <funcName>badpat</funcName> pse = do\n" +
                "            stderr.println \"The regex is not valid.\"\n" +
                "            stderr.println pse.getMessage        \n" +
                "\n" +
                "<funcName>notImplemented</funcName> = <undefined>undefined</undefined>" +
                "\n" +
                "<funcName>run</funcName> regex file = do\n" +
                "        rdr <- openReader file\n" +
                "        grepit regex rdr\n" +
                "    `catch` fnf where\n" +
                "        <funcName>fnf</funcName> :: FileNotFoundException -> IO ()\n" +
                "        <funcName>fnf</funcName> _ = stderr.println (\"Could not read \" ++ file)\n" +
                "\n" +
                "\n" +
                "<funcName>grepit</funcName> :: Regex -> MutableIO BufferedReader -> IO ()                \n" +
                "<funcName>grepit</funcName> pat rdr = forever line `catch` eof `finally` rdr.close \n" +
                "    where\n" +
                "        <funcName>eof</funcName> :: EOFException -> IO ()\n" +
                "        <funcName>eof</funcName> _ = return ()\n" +
                "        <funcName>line</funcName> = do\n" +
                "            line <- rdr.getLine \n" +
                "            when (line ~ pat) (println line)";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return additionalHighlightingTagToDescriptorMap;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Frege";
    }
}
