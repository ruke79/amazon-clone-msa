package com.project.chatserver.testUser;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomAccountSecurityContextFactory.class)
public @interface WithMockCustomAccount {

    String username() default "";

    String name() default "";

    String email() default "";
}
