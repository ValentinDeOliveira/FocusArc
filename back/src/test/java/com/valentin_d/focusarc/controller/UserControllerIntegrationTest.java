package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.user.UserCreationDto;
import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIntegrationTest{
    @LocalServerPort
    private int port;
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private UserRepository userRepository;

    private static final UserId userId = UserId.random();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // clean db before each test
        userRepository.save(new User(userId, "foobar", "foo@test.com"));
    }

    @Test
    void createUser_returnsCreatedUser() {
        UserCreationDto dto = new UserCreationDto("Alice", "alice@example.com");

        ResponseEntity<User> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/users",
                new HttpEntity<>(dto),
                User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        final User user = response.getBody();

        assertEquals("alice@example.com", user.getEmail());
        assertEquals("Alice", user.getName());
        assertThat(user.getId()).isNotNull();
        assertThat(user.getLastLogin()).isNotNull();
    }

    @Test
    void createUser_withExistingMail_shouldFail() {
        UserCreationDto dto = new UserCreationDto("foo", "foo@test.com");

        ResponseEntity<User> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/users",
                new HttpEntity<>(dto),
                User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getUserByEmail_returnsUser_whenFound() {
        ResponseEntity<User> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/users/email?email={email}",
                User.class,
                "foo@test.com"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        final User user = response.getBody();

        assertEquals("foo@test.com", user.getEmail());
        assertEquals("foobar", user.getName());
        assertEquals(userId, user.getId());
        assertThat(user.getLastLogin()).isNotNull();
    }

    @Test
    void getUserByEmail_returnsNotFound_whenNotFound() {
        ResponseEntity<User> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/users/email?email={email}",
                User.class,
                "bar@test.com"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getUserById_returnsUser_whenFound() {
        ResponseEntity<User> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/users/" + userId.id(),
                User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        final User user = response.getBody();

        assertEquals("foo@test.com", user.getEmail());
        assertEquals("foobar", user.getName());
        assertEquals(userId, user.getId());
        assertThat(user.getLastLogin()).isNotNull();
    }

    @Test
    void getUserById_returnsNotFound_whenNotFound() {
        ResponseEntity<User> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/users/" + UserId.random().id(),
                User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void updateUser_withNewFields_whenFound() {
        final var dto = new UserUpdateDto("bar");

        ResponseEntity<User> response = restTemplate.exchange(
                "http://localhost:" + port + "/users/" + userId.id(),
                HttpMethod.PUT,
                new HttpEntity<>(dto),
                User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        final User user = response.getBody();

        assertEquals("foo@test.com", user.getEmail());
        assertEquals("bar", user.getName());
        assertEquals(userId, user.getId());
        assertThat(user.getLastLogin()).isNotNull();
    }

    @Test
    void updateUser_withNewFields_whenNotFound() {
        final var dto = new UserUpdateDto("bar");

        ResponseEntity<User> response = restTemplate.exchange(
                "http://localhost:" + port + "/users/" + UserId.random().id(),
                HttpMethod.PUT,
                new HttpEntity<>(dto),
                User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteUser_whenFound() {
        ResponseEntity<User> response = restTemplate.exchange(
                "http://localhost:" + port + "/users/" + userId.id(),
                HttpMethod.DELETE,
                null,
                User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void deleteUser_whenNotFound() {
        ResponseEntity<User> response = restTemplate.exchange(
                "http://localhost:" + port + "/users/" + UserId.random().id(),
                HttpMethod.DELETE,
                null,
                User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}