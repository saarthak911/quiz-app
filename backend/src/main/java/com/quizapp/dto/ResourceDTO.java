package com.quizapp.dto;

public class ResourceDTO {
    private Long id;
    private String title;
    private String url;

    public ResourceDTO(Long id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
}
