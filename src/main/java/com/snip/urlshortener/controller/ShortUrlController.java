package com.snip.urlshortener.controller;

import com.snip.urlshortener.dto.ErrorResponse;
import com.snip.urlshortener.dto.ShortenRequest;
import com.snip.urlshortener.dto.ShortenResponse;
import com.snip.urlshortener.dto.UrlInfoResponse;
import com.snip.urlshortener.model.ShortUrl;
import com.snip.urlshortener.service.ShortUrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class ShortUrlController {

    private final ShortUrlService service;

    public ShortUrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<?> shorten(@RequestBody ShortenRequest request) {
        try {
            ShortUrl created = service.createShortUrl(request.getUrl(), request.getCustomCode());
            ShortenResponse response = new ShortenResponse(
                    service.buildShortUrl(created.getCode()),
                    created.getCode(),
                    created.getOriginalUrl()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ShortUrlService.ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (ShortUrlService.ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/api/stats/{code}")
    public ResponseEntity<?> stats(@PathVariable String code) {
        Optional<ShortUrl> found = service.findByCode(code);
        if (found.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Short URL not found."));
        }
        return ResponseEntity.ok(new UrlInfoResponse(found.get()));
    }

    @GetMapping("/api/urls")
    public List<UrlInfoResponse> listRecent() {
        return service.listRecent().stream().map(UrlInfoResponse::new).toList();
    }

    @GetMapping("/{code}")
    public void redirect(@PathVariable String code, HttpServletResponse response) throws IOException {
        var found = service.findByCode(code);
        if (found.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.sendRedirect("/404.html");
            return;
        }
        ShortUrl shortUrl = found.get();
        service.registerClick(shortUrl);
        response.sendRedirect(shortUrl.getOriginalUrl());
    }
}
