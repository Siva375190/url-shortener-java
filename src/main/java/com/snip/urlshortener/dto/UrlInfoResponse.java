package com.snip.urlshortener.dto;

import com.snip.urlshortener.model.ShortUrl;
import java.time.LocalDateTime;

public class UrlInfoResponse {

    private String code;
    private String originalUrl;
    private int clicks;
    private LocalDateTime createdAt;

    public UrlInfoResponse(ShortUrl entity) {
        this.code = entity.getCode();
        this.originalUrl = entity.getOriginalUrl();
        this.clicks = entity.getClicks();
        this.createdAt = entity.getCreatedAt();
    }

    public String getCode() {
        return code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public int getClicks() {
        return clicks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
