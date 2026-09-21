package edu.final_year.backendwebm.obr;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.final_year.backendwebm.exception.InvalidNifException;
import edu.final_year.backendwebm.exception.ObrUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import tools.jackson.databind.json.JsonMapper;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Component
public class ObrClient {

    private static final Logger log = LoggerFactory.getLogger(ObrClient.class);

    private final JsonMapper jsonMapper = JsonMapper.builder().build();
    private final RestClient restClient;
    private final String username;
    private final String password;

    public ObrClient(@Value("${app.obr.base-url}") String baseUrl,
                     @Value("${app.obr.username}") String username,
                     @Value("${app.obr.password}") String password,
                     @Value("${app.obr.timeout-seconds}") long timeoutSeconds) {
        // JDK HttpClient sends Content-Length; the default HttpURLConnection factory sends chunked bodies, which OBR rejects.
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(timeoutSeconds));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the official fiscal name registered with OBR for the NIF.
     *
     * @throws InvalidNifException     if OBR does not know the NIF
     * @throws ObrUnavailableException if OBR cannot be reached or answers unexpectedly
     */
    public String checkTin(String nif) {
        try {
            String token = login();

            CheckTinResponse response = postJson("/checkTIN/", token, new CheckTinRequest(nif))
                    .body(CheckTinResponse.class);

            String fiscalName = extractFiscalName(response);
            if (fiscalName == null) {
                throw new InvalidNifException();
            }
            return fiscalName;
        } catch (HttpClientErrorException e) {
            // Unknown NIF is reported as "NIF du contribuable inconnu." with 400 (spec) or 401 (test server), so match on the message.
            if (e.getResponseBodyAsString().contains("inconnu")) {
                log.info("OBR does not know NIF: {}", e.getMessage());
                throw new InvalidNifException();
            }
            log.error("OBR call rejected", e);
            throw new ObrUnavailableException(e);
        } catch (RestClientException e) {
            log.error("OBR checkTIN call failed", e);
            throw new ObrUnavailableException(e);
        }
    }

    private String login() {
        LoginResponse response = postJson("/login/", null, new LoginRequest(username, password))
                .body(LoginResponse.class);

        if (response == null || response.result() == null || response.result().token() == null) {
            throw new ObrUnavailableException(new IllegalStateException("OBR login returned no token"));
        }
        return response.result().token();
    }

    // OBR rejects chunked request bodies, so the body is serialized up front to send an explicit Content-Length.
    private RestClient.ResponseSpec postJson(String uri, String bearerToken, Object body) {
        byte[] bytes = jsonMapper.writeValueAsBytes(body);
        RestClient.RequestBodySpec request = restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(bytes.length);
        if (bearerToken != null) {
            request.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        }
        return request.body(bytes).retrieve();
    }

    private String extractFiscalName(CheckTinResponse response) {
        if (response == null || !response.success() || response.result() == null
                || response.result().taxpayer() == null || response.result().taxpayer().isEmpty()) {
            return null;
        }
        String name = response.result().taxpayer().get(0).name();
        return name == null || name.isBlank() ? null : name.trim();
    }

    private record LoginRequest(String username, String password) {
    }

    private record LoginResponse(boolean success, LoginResult result) {
    }

    private record LoginResult(String token) {
    }

    private record CheckTinRequest(@JsonProperty("tp_TIN") String tpTin) {
    }

    private record CheckTinResponse(boolean success, CheckTinResult result) {
    }

    private record CheckTinResult(List<Taxpayer> taxpayer) {
    }

    private record Taxpayer(@JsonProperty("tp_name") String name) {
    }
}
