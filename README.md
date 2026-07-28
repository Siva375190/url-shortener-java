# Snip — a URL Shortener (Java / Spring Boot)

A self-contained URL shortener built with **Spring Boot 3**, **Spring Data JPA**, and an embedded **H2** database. No external database server needed — clone it, build it, run it.

![Java](https://img.shields.io/badge/java-17-orange)
![Spring Boot](https://img.shields.io/badge/spring--boot-3.3.4-green)
![License](https://img.shields.io/badge/license-MIT-blue)

## Features

- 🔗 Shorten any valid `http(s)` URL into a compact 7-character code
- ✍️ Optional custom short codes
- 📊 Click tracking per link
- 📋 Recent-links dashboard on the homepage
- 💾 Embedded H2 file-based database — no setup required
- 🎨 Custom-designed frontend (vanilla HTML/CSS/JS, no framework needed)

## Tech Stack

| Layer      | Choice                              |
|------------|---------------------------------------|
| Runtime    | Java 17+, Spring Boot 3.3             |
| Persistence| Spring Data JPA + H2 (file-based)     |
| Build tool | Maven                                 |
| Frontend   | Vanilla HTML/CSS/JS (served as static resources) |

## Getting Started

### Prerequisites
- **JDK 17 or newer** — check with `java -version`
- **Maven** — check with `mvn -version` (or use the included wrapper if you add one)

### Run it

```bash
git clone <your-repo-url>
cd url-shortener-java
mvn spring-boot:run
```

The app runs at `http://localhost:8080` by default.

To build a standalone jar instead:

```bash
mvn clean package
java -jar target/url-shortener.jar
```

### Environment variables (optional)

| Variable   | Default                 | Description                            |
|------------|---------------------------|-----------------------------------------|
| `PORT`     | `8080`                     | Port the server listens on              |
| `BASE_URL` | `http://localhost:8080`    | Used to build the short URL returned by the API |

The H2 database file is created at `./data/urlshortener.mv.db` on first run.

## API

### `POST /api/shorten`
Create a short URL.

```json
// Request
{ "url": "https://example.com/some/long/path", "customCode": "my-link" }

// Response (201)
{
  "shortUrl": "http://localhost:8080/my-link",
  "code": "my-link",
  "originalUrl": "https://example.com/some/long/path"
}
```

### `GET /:code`
Redirects to the original URL and increments the click counter.

### `GET /api/stats/{code}`
Returns metadata (clicks, created date, destination) for a given code.

### `GET /api/urls`
Returns the 50 most recently created short URLs.

### `GET /h2-console`
Optional built-in H2 web console for inspecting the database during development (JDBC URL: `jdbc:h2:file:./data/urlshortener`, user `sa`, blank password).

## Project Structure

```
url-shortener-java/
├── pom.xml
├── src/main/java/com/snip/urlshortener/
│   ├── UrlShortenerApplication.java   # entry point
│   ├── controller/ShortUrlController.java
│   ├── service/ShortUrlService.java   # validation, code generation
│   ├── repository/ShortUrlRepository.java
│   ├── model/ShortUrl.java             # JPA entity
│   └── dto/                            # request/response payloads
├── src/main/resources/
│   ├── application.properties
│   └── static/
│       ├── index.html
│       └── 404.html
└── README.md
```

## Roadmap / Ideas

- [ ] Link expiration dates
- [ ] QR code generation for each short link
- [ ] Basic auth for private dashboards
- [ ] Rate limiting on `/api/shorten`
- [ ] Swap H2 for Postgres/MySQL for production use

## License

MIT
