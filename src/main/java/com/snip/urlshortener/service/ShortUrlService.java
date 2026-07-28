package com.snip.urlshortener.service;

import com.snip.urlshortener.model.ShortUrl;
import com.snip.urlshortener.repository.ShortUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ShortUrlService {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;
    private static final Pattern CUSTOM_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,20}$");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ShortUrlRepository repository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public ShortUrlService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }
    }

    public ShortUrl createShortUrl(String rawUrl, String customCode) {
        if (rawUrl == null || rawUrl.isBlank() || !isValidUrl(rawUrl)) {
            throw new ValidationException("Please provide a valid URL (including http:// or https://).");
        }

        String code;
        if (customCode != null && !customCode.isBlank()) {
            code = customCode.trim();
            if (!CUSTOM_CODE_PATTERN.matcher(code).matches()) {
                throw new ValidationException("Custom code must be 3-20 characters (letters, numbers, - or _).");
            }
            if (repository.existsByCode(code)) {
                throw new ConflictException("That custom code is already taken.");
            }
        } else {
            code = generateUniqueCode();
        }

        ShortUrl shortUrl = new ShortUrl(code, rawUrl);
        return repository.save(shortUrl);
    }

    public Optional<ShortUrl> findByCode(String code) {
        return repository.findByCode(code);
    }

    public void registerClick(ShortUrl shortUrl) {
        shortUrl.setClicks(shortUrl.getClicks() + 1);
        repository.save(shortUrl);
    }

    public List<ShortUrl> listRecent() {
        return repository.findTop50ByOrderByCreatedAtDesc();
    }

    public String buildShortUrl(String code) {
        return baseUrl + "/" + code;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = randomCode();
        } while (repository.existsByCode(code));
        return code;
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private boolean isValidUrl(String value) {
        try {
            URI uri = new URI(value);
            URL url = uri.toURL();
            String scheme = url.getProtocol();
            return ("http".equals(scheme) || "https".equals(scheme)) && url.getHost() != null && !url.getHost().isBlank();
        } catch (MalformedURLException | IllegalArgumentException | java.net.URISyntaxException e) {
            return false;
        }
    }
}
