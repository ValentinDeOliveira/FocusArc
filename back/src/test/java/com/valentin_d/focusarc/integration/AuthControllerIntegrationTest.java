package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.integration.base.BaseAuthIntegrationTest;
import com.valentin_d.focusarc.model.auth.AuthResponseDto;
import com.valentin_d.focusarc.model.user.AuthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static com.valentin_d.focusarc.fixtures.factory.auth.AuthFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.auth.RegisterRequestDtoFactory.aRegisterRequestDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.RegisterRequestDtoFactory.aRegisterRequestDtoWithMail;
import static org.junit.jupiter.api.Assertions.*;

public class AuthControllerIntegrationTest extends BaseAuthIntegrationTest {
    @Test
    void shouldCreateUserAndReturnToken_whenDataIsValid() {
        final var dto = aRegisterRequestDto();

        final var response = request(URL + "/register", HttpMethod.POST, dto, AuthResponseDto.class);
        assertionHelper.assertCreated(response);
        assertNotNull(response.getBody());

        final var optUser = userLoader.getUserByEmail(dto.email());
        assertTrue(optUser.isPresent());

        final var user = optUser.get();
        assertEquals(user.getEmail(), dto.email());
        assertNotEquals(user.getPassword(), dto.password());
        assertNotNull(user.getId());
        assertEquals(AuthProvider.LOCAL, user.getAuthProvider());

        final var authResponse = response.getBody();
        assertNotNull(authResponse.accessToken());
        assertNotNull(authResponse.refreshToken());
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
        // save user with encrypted password
        request(URL + "/register", HttpMethod.POST, registerDto, AuthResponseDto.class);

        final var loginDto = aLoginDtoWithMailAndPassword(registerDto.email(), registerDto.password());
        final var response = request(URL + "/login", HttpMethod.POST, loginDto, AuthResponseDto.class);
        assertionHelper.assertOk(response);
        assertNotNull(response.getBody());

        final var authResponse = response.getBody();
        assertNotNull(authResponse.accessToken());
        assertNotNull(authResponse.refreshToken());
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
        // save user with encrypted password
        final var registerResponse = request(URL + "/register", HttpMethod.POST, registerDto, AuthResponseDto.class);
        assertNotNull(registerResponse.getBody());

        final var refreshDto = aRefreshRequestDtoWithToken(registerResponse.getBody().refreshToken());
        final var response = request(URL + "/refresh", HttpMethod.POST, refreshDto, AuthResponseDto.class);
        assertionHelper.assertOk(response);
        assertNotNull(response.getBody());

        final var authResponse = response.getBody();
        assertNotNull(authResponse.accessToken());
        assertNotNull(authResponse.refreshToken());
    }

    @Test
    void shouldNotRefreshToken_whenTokenInvalid() {
        final var refreshDto = aRefreshRequestDto();
        final var response = request(URL + "/refresh", HttpMethod.POST, refreshDto, Void.class);
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
    }
}