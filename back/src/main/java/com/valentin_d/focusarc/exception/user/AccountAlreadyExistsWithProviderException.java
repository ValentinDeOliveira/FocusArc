package com.valentin_d.focusarc.exception.user;

import com.valentin_d.focusarc.exception.base.ApiException;
import com.valentin_d.focusarc.model.user.AuthProvider;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class AccountAlreadyExistsWithProviderException extends ApiException {
    public AccountAlreadyExistsWithProviderException(final String email, final AuthProvider authProvider) {
        super("Email already exists: " + email + " with provider: " + authProvider.name(),
                HttpStatus.CONFLICT, Map.of("provider", authProvider.name()));
    }
}