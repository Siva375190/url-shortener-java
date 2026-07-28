package com.snip.urlshortener.dto;

public class ShortenResponse {

    private String shortUrl;
    private String code;
    private String originalUrl;

    public ShortenResponse(String shortUrl, String code, String originalUrl) {
        this.shortUrl = shortUrl;
        this.code = code;
        this.originalUrl = originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getCode() {
        return code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }
}
