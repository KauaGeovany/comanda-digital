package br.com.bomefit.comandadigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class ComandaDigitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComandaDigitalApplication.class, args);
    }
}
