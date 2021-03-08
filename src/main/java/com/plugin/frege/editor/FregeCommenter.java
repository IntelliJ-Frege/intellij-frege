package com.plugin.frege.editor;

import com.intellij.lang.Commenter;

public class FregeCommenter implements Commenter {

    @Override
    public String getLineCommentPrefix() {
        return "--";
    }

    @Override
    public String getBlockCommentPrefix() {
        return "{--";
    }

    @Override
    public String getBlockCommentSuffix() {
        return "-}";
    }

    @Override
    public String getCommentedBlockCommentPrefix() {
        return "{--";
    }

    @Override
    public String getCommentedBlockCommentSuffix() {
        return "-}";
    }
}
