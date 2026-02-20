package com.example.foodguardai.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClaudeResponse {
    @SerializedName("content")
    private List<Content> content;

    public List<Content> getContent() { return content; }

    public static class Content {
        @SerializedName("text")
        private String text;

        public String getText() { return text; }
    }
}