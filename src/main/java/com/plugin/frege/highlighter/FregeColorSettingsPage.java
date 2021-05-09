package com.plugin.frege.highlighter;

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
            new AttributesDescriptor("Constructor", FregeSyntaxHighlighter.CONSTRUCTOR),
            new AttributesDescriptor("Type", FregeSyntaxHighlighter.TYPE), //TODO
            new AttributesDescriptor("Brackets", FregeSyntaxHighlighter.BRACKETS),
            new AttributesDescriptor("Operator", FregeSyntaxHighlighter.OPERATOR),
            new AttributesDescriptor("Function name", FregeSyntaxHighlighter.FUNCTION_NAME),
            new AttributesDescriptor("Line comment", FregeSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor("Block comment", FregeSyntaxHighlighter.BLOCK_COMMENT),
            new AttributesDescriptor("Undefined", FregeSyntaxHighlighter.UNDEFINED),
            new AttributesDescriptor("Bad value", FregeSyntaxHighlighter.BAD_CHARACTER)
    };

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
                "module examples.Grep where\n" +
                "\n" +
                "--- exception thrown when an invalid regular expression is compiled\n" +
                "data PatternSyntax = native java.util.regex.PatternSyntaxException\n" +
                "derive Exceptional PatternSyntax\n" +
                "\n" +
                "main [] = stderr.println \"Usage: java examples.Grep regex [files ...]\"\n" +
                "main (pat:xs) = do\n" +
                "        rgx <- return (regforce pat)\n" +
                "        case xs of\n" +
                "            [] -> grepit rgx stdin\n" +
                "            fs -> mapM_ (run rgx) fs\n" +
                "     `catch` badpat where\n" +
                "        badpat :: PatternSyntax -> IO ()\n" +
                "        badpat pse = do\n" +
                "            stderr.println \"The regex is not valid.\"\n" +
                "            stderr.println pse.getMessage        \n" +
                "\n" +
                "notImplemented = undefined" +
                "\n" +
                "run regex file = do\n" +
                "        rdr <- openReader file\n" +
                "        grepit regex rdr\n" +
                "    `catch` fnf where\n" +
                "        fnf :: FileNotFoundException -> IO ()\n" +
                "        fnf _ = stderr.println (\"Could not read \" ++ file)\n" +
                "\n" +
                "\n" +
                "grepit :: Regex -> MutableIO BufferedReader -> IO ()                \n" +
                "grepit pat rdr = forever line `catch` eof `finally` rdr.close \n" +
                "    where\n" +
                "        eof :: EOFException -> IO ()\n" +
                "        eof _ = return ()\n" +
                "        line = do\n" +
                "            line <- rdr.getLine \n" +
                "            when (line ~ pat) (println line)";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
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
