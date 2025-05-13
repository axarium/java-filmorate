package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createFilm() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2001, 1, 14));
        film.setDuration(100);

        var response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(film.getName(), response.getBody().getName());
        assertEquals(film.getDescription(), response.getBody().getDescription());
        assertEquals(film.getReleaseDate(), response.getBody().getReleaseDate());
        assertEquals(film.getDuration(), response.getBody().getDuration());
    }

    @Test
    void createFilmWithEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2001, 1, 14));
        film.setDuration(100);

        var response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createFilmWithTooLongDescription() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description".repeat(20));
        film.setReleaseDate(LocalDate.of(2001, 1, 14));
        film.setDuration(100);

        var response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createFilmWithTooEarlyReleaseDate() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        film.setDuration(100);

        var response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2001, 1, 14));
        film.setDuration(-100);

        var response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void updateFilm() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2001, 1, 14));
        film.setDuration(100);

        var createResponse = restTemplate.postForEntity("/films", film, Film.class);

        assertNotNull(createResponse.getBody());

        film.setId(createResponse.getBody().getId());
        film.setName("New Name");
        film.setDescription("New Description");
        film.setReleaseDate(LocalDate.of(2001, 12, 14));
        film.setDuration(200);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var requestEntity = new HttpEntity<>(film, headers);

        var updateResponse = restTemplate.exchange("/films", HttpMethod.PUT, requestEntity, Film.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals(film.getId(), updateResponse.getBody().getId());
        assertEquals(film.getName(), updateResponse.getBody().getName());
        assertEquals(film.getDescription(), updateResponse.getBody().getDescription());
        assertEquals(film.getReleaseDate(), updateResponse.getBody().getReleaseDate());
        assertEquals(film.getDuration(), updateResponse.getBody().getDuration());
    }

    @Test
    void updateFilmWithUnknowId() {
        Film film = new Film();
        film.setId(-1L);
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2001, 1, 14));
        film.setDuration(100);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var requestEntity = new HttpEntity<>(film, headers);

        var response = restTemplate.exchange("/films", HttpMethod.PUT, requestEntity, Film.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAllFilms() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2001, 1, 14));
        film.setDuration(100);

        restTemplate.postForEntity("/films", film, Film.class);

        var response = restTemplate.getForEntity("/films", Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody()[0].getId());
    }
}