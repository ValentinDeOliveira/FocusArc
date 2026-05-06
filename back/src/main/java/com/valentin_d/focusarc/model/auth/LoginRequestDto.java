package com.valentin_d.focusarc.model.auth;

import com.valentin_d.focusarc.util.validation.ValidEmail;
import com.valentin_d.focusarc.util.validation.ValidPassword;

public record LoginRequestDto(@ValidEmail String email,
                              @ValidPassword String password) {}