package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.integration.base.BaseAuthIntegrationTest;
import com.valentin_d.focusarc.model.user.AuthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.valentin_d.focusarc.fixtures.factory.auth.AuthFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.auth.RegisterRequestDtoFactory.aRegisterRequestDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.RegisterRequestDtoFactory.aRegisterRequestDtoWithMail;
import static org.junit.jupiter.api.Assertions.*;

public class AuthControllerIntegrationTest extends BaseAuthIntegrationTest {

    @Test
    void shouldCreateUserAndReturnToken_whenDataIsValid() {
        final var dto = aRegisterRequestDto();

        final var response = request(URL + "/register", HttpMethod.POST, dto, Void.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertAuthCookiesPresent(response);

        final var optUser = userLoader.findUserByEmail(dto.email());
        assertTrue(optUser.isPresent());

        final var user = optUser.get();
        assertEquals(user.getEmail(), dto.email());
        assertNotEquals(user.getPassword(), dto.password());
        assertNotNull(user.getId());
        assertEquals(AuthProvider.LOCAL, user.getAuthProvider());
    }

    @Test
    void shouldNotCreateUser_whenEmailAlreadyExists() {
        final var user = domainFixture.user();
        final var dto = aRegisterRequestDtoWithMail(user.getEmail());

        final var response = request(URL + "/register", HttpMethod.POST, dto, Void.class);
        assertionHelper.assertConflict(response);
        assertNull(response.getBody());
    }

    @Test
    void shouldLoginUser_whenDataIsValid() {
        final var registerDto = aRegisterRequestDto();
        request(URL + "/register", HttpMethod.POST, registerDto, Void.class);

        final var loginDto = aLoginDtoWithMailAndPassword(registerDto.email(), registerDto.password());
        final var response = request(URL + "/login", HttpMethod.POST, loginDto, Void.class);
        assertionHelper.assertOk(response);
        assertAuthCookiesPresent(response);
    }

    @Test
    void shouldNotLoginUser_whenCredsAreInvalid() {
        final var loginDto = aLoginDto();
        final var response = request(URL + "/login", HttpMethod.POST, loginDto, Void.class);
        assertionHelper.assertUnauthorized(response);
        assertNull(response.getBody());
    }

    @Test
    void shouldRefreshToken_whenUserExists() {
        final var registerDto = aRegisterRequestDto();
        final var registerResponse = request(URL + "/register", HttpMethod.POST, registerDto, Void.class);
        final var refreshToken = extractCookieValue(registerResponse, "refresh_token");
        assertNotNull(refreshToken);

        final var headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "refresh_token=" + refreshToken);
        final var response = request(URL + "/refresh", HttpMethod.POST, new HttpEntity<>(null, headers), Void.class);
        assertionHelper.assertOk(response);
        assertAuthCookiesPresent(response);
    }

    @Test
    void shouldNotRefreshToken_whenTokenInvalid() {
        final var headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "refresh_token=invalid-token");
        final var response = request(URL + "/refresh", HttpMethod.POST, new HttpEntity<>(null, headers), Void.class);
        assertionHelper.assertUnauthorized(response);
        assertNull(response.getBody());
    }

    @Test
    void shouldReturnBadRequestOnRegister_whenEmailIsInvalid() {
        final var dto = aRegisterRequestDtoWithMail("not-an-email");
        final var response = request(URL + "/register", HttpMethod.POST, dto, Void.class);
        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldNotLoginWithGoogle_whenTokenIsBlank() {
        final var dto = aGoogleAuthRequestDtoWithToken("");
        final var response = request(URL + "/google", HttpMethod.POST, dto, Void.class);
        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldNotLoginWithGoogle_whenTokenIsInvalid() {
        final var dto = aGoogleAuthRequestDtoWithToken("invalid-google-token");
        final var response = request(URL + "/google", HttpMethod.POST, dto, Void.class);
        assertionHelper.assertUnauthorized(response);
        assertNull(response.getBody());
    }
}