package com.em.testtask.util;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;


public class MockJwtAuthTools {
    public static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor testJwtAuth(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt((b) -> b.claim("sub", sub));
    }
}
