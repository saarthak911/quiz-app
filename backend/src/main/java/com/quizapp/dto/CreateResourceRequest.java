package com.quizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateResourceRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    // Only https YouTube links are accepted, and quotes/angle brackets/whitespace
    // are rejected, so the stored value is safe to render as a link.
    @NotBlank
    @Size(max = 500)
    @Pattern(regexp = "^https://(www\\.|m\\.)?(youtube\\.com|youtu\\.be)/[^\\s\"'<>]+$",
             message = "must be a valid https YouTube link (youtube.com or youtu.be)")
    private String url;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
