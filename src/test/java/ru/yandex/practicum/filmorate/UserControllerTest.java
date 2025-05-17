package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createUser() {
        User user = new User();
        user.setLogin("Login");
        user.setName("Name");
        user.setEmail("email@email.ru");
        user.setBirthday(LocalDate.of(2001, 1, 14));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(user.getName(), response.getBody().getName());
        assertEquals(user.getLogin(), response.getBody().getLogin());
        assertEquals(user.getEmail(), response.getBody().getEmail());
        assertEquals(user.getBirthday(), response.getBody().getBirthday());
    }

    @Test
    void createUserWithLoginWithSpace() {
        User user = new User();
        user.setLogin("Login Login");
        user.setName("Name");
        user.setEmail("email@email.ru");
        user.setBirthday(LocalDate.of(2001, 1, 14));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createUserWithIncorrectEmail() {
        User user = new User();
        user.setLogin("Login");
        user.setName("Name");
        user.setEmail("email.ru");
        user.setBirthday(LocalDate.of(2001, 1, 14));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createUserWithBirthdayInFuture() {
        User user = new User();
        user.setLogin("Login");
        user.setName("Name");
        user.setEmail("email@email.ru");
        user.setBirthday(LocalDate.of(2026, 1, 14));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createUserWithEmptyName() {
        User user = new User();
        user.setLogin("Login");
        user.setName("");
        user.setEmail("email@email.ru");
        user.setBirthday(LocalDate.of(2001, 1, 14));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(user.getLogin(), response.getBody().getName());
        assertEquals(user.getLogin(), response.getBody().getLogin());
        assertEquals(user.getEmail(), response.getBody().getEmail());
        assertEquals(user.getBirthday(), response.getBody().getBirthday());
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setLogin("Login");
        user.setName("Name");
        user.setEmail("email@email.ru");
        user.setBirthday(LocalDate.of(2001, 1, 14));

        ResponseEntity<User> createResponse = restTemplate.postForEntity("/users", user, User.class);

        assertNotNull(createResponse.getBody());

        user.setId(createResponse.getBody().getId());
        user.setLogin("NewLogin");
        user.setName("NewName");
        user.setEmail("NewEmail@email.ru");
        user.setBirthday(LocalDate.of(2001, 12, 14));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);

        ResponseEntity<User> updateResponse = restTemplate.exchange(
                "/users",
                HttpMethod.PUT,
                requestEntity,
                User.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals(user.getId(), updateResponse.getBody().getId());
        assertEquals(user.getName(), updateResponse.getBody().getName());
        assertEquals(user.getLogin(), updateResponse.getBody().getLogin());
        assertEquals(user.getEmail(), updateResponse.getBody().getEmail());
        assertEquals(user.getBirthday(), updateResponse.getBody().getBirthday());
    }

    @Test
    void updateUserWithUnknowId() {
        User user = new User();
        user.setId(-1L);
        user.setLogin("Login");
        user.setName("Name");
        user.setEmail("email@email.ru");
        user.setBirthday(LocalDate.of(2001, 1, 14));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);

        ResponseEntity<User> response = restTemplate.exchange("/users", HttpMethod.PUT, requestEntity, User.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAllUsers() {
        User user = new User();
        user.setLogin("Login");
        user.setName("Name");
        user.setEmail("email@email.ru");
        user.setBirthday(LocalDate.of(2001, 1, 14));

        restTemplate.postForEntity("/users", user, User.class);

        ResponseEntity<User[]> response = restTemplate.getForEntity("/users", User[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody()[0].getId());
    }
}