package com.shopme.shopmebackend.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
   @Test
   public void testEncodePassword(){
       BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
       String rawPass = "goshaPass";
       String endcoded = passwordEncoder.encode(rawPass);
       System.out.println(endcoded);
   }
}
