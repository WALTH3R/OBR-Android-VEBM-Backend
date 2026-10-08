package edu.final_year.backendwebm.vault;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

import java.net.URI;

@Configuration
public class VaultConfig {

    @Bean
    public VaultTemplate vaultTemplate(@Value("${app.vault.uri}") String uri,
                                        @Value("${app.vault.token}") String token) {
        VaultEndpoint endpoint = VaultEndpoint.from(URI.create(uri));
        return new VaultTemplate(endpoint, new TokenAuthentication(token));
    }
}
