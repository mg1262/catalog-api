package com.spscommerce.interview.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private Collection<String> permitted;

    private OAuth2TokenValidatorResult failure;

    public AudienceValidator(String... permitted) {
        this(Arrays.asList(permitted));
    }

    public AudienceValidator(Collection<String> permitted) {
        Assert.notEmpty(permitted, "permitted must not be empty");
        this.permitted = Collections.unmodifiableCollection(permitted);
        this.failure = OAuth2TokenValidatorResult.failure(new OAuth2Error(String.format("Attribute [aud] must be in %s", this.permitted)));
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if ( !containsAny(token.getAudience()) ) {
            return this.failure;
        }

        return OAuth2TokenValidatorResult.success();
    }

    private boolean containsAny(Collection<String> audiences) {
        return audiences.stream().anyMatch(this.permitted::contains);
    }
}
