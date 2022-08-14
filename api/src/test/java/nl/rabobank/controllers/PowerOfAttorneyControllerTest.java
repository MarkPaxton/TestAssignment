package nl.rabobank.controllers;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.dto.PowerOfAttorneyAuthorizationDto;
import nl.rabobank.exceptions.AccountHolderException;
import nl.rabobank.exceptions.NoAccountException;
import nl.rabobank.services.PowerOfAttorneyService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
class PowerOfAttorneyControllerTest {

    @MockBean
    ReactiveJwtDecoder reactiveJwtDecoder;
    @MockBean
    PowerOfAttorneyService powerOfAttorneyService;
    List<Tuple2<Account, Authorization>> testAccountsList;
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        testAccountsList = List.of(Tuples.of(new TestAccount(), Authorization.READ),
                        Tuples.of(new TestAccount(), Authorization.WRITE),
                        Tuples.of(new TestAccount(), Authorization.READ)
        );
    }

    private WebTestClient webTestClientWithSubject(String sub) {
        return webTestClient.mutateWith(mockJwt().jwt(token -> token.subject(sub)));
    }

    @Test
    void getAllAccounts_Empty() {
        when(powerOfAttorneyService.getAllAccounts("sub")).thenReturn(Flux.fromIterable(List.of()));
        webTestClientWithSubject("sub")
                        .get().uri("/powerofattorney").exchange()
                        .expectStatus().isOk()
                        .expectBody().jsonPath("$").isArray()
                        .jsonPath("$.length()").isEqualTo(0);
        verify(powerOfAttorneyService, times(1)).getAllAccounts("sub");
    }

    @Test
    void getAllAccounts_List() {
        when(powerOfAttorneyService.getAllAccounts("test")).thenReturn(Flux.fromIterable(testAccountsList));
        webTestClientWithSubject("test")
                        .get().uri("/powerofattorney").exchange()
                        .expectStatus().isOk()
                        .expectBody().jsonPath("$").isArray()
                        .jsonPath("$.length()").isEqualTo(3);
        verify(powerOfAttorneyService, times(1)).getAllAccounts("test");

    }

    @Test
    void getAllAccounts_Exception() {
        when(powerOfAttorneyService.getAllAccounts("user")).thenReturn(Flux.error(new RuntimeException()));
        webTestClientWithSubject("user")
                        .get().uri("/powerofattorney").exchange()
                        .expectStatus().is5xxServerError();
        verify(powerOfAttorneyService, times(1)).getAllAccounts("user");
    }

    @Test
    void setAuthorization_Success() {
        when(powerOfAttorneyService.setAuthorization(eq("sub"), anyString(), anyString(), any(Authorization.class))).thenReturn(Mono.empty());
        webTestClientWithSubject("sub")
                        .post().uri("/powerofattorney")
                        //.contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new PowerOfAttorneyAuthorizationDto("testAccNumber", "testOtherUser", Authorization.READ)))
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody().isEmpty();
        verify(powerOfAttorneyService, times(1)).setAuthorization("sub", "testAccNumber", "testOtherUser", Authorization.READ);
    }

    @Test
    void setAuthorization_AccountHolderException() {
        when(powerOfAttorneyService.setAuthorization(eq("sub"), anyString(), anyString(), any(Authorization.class))).thenReturn(Mono.error(new AccountHolderException()));
        webTestClientWithSubject("sub")
                        .post().uri("/powerofattorney")
                     //   .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new PowerOfAttorneyAuthorizationDto("testAccNumber", "testOtherUser", Authorization.READ)))
                        .exchange()
                        .expectStatus().isBadRequest();
        verify(powerOfAttorneyService, times(1)).setAuthorization("sub", "testAccNumber", "testOtherUser", Authorization.READ);
    }

    @Test
    void setAuthorization_NoAccountException() {
        when(powerOfAttorneyService.setAuthorization(eq("sub"), anyString(), anyString(), any(Authorization.class))).thenReturn(Mono.error(new NoAccountException()));
        webTestClientWithSubject("sub")
                        .post().uri("/powerofattorney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new PowerOfAttorneyAuthorizationDto("testAccNumber", "testOtherUser", Authorization.READ)))
                        .exchange()
                        .expectStatus().isNotFound();
        verify(powerOfAttorneyService, times(1)).setAuthorization("sub", "testAccNumber", "testOtherUser", Authorization.READ);
    }

    @Test
    void setAuthorization_OtherException() {
        when(powerOfAttorneyService.setAuthorization(eq("sub"), anyString(), anyString(), any(Authorization.class))).thenReturn(Mono.error(new RuntimeException()));
        webTestClientWithSubject("sub")
                        .post().uri("/powerofattorney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new PowerOfAttorneyAuthorizationDto("testAccNumber", "testOtherUser", Authorization.READ)))
                        .exchange()
                        .expectStatus().is5xxServerError();
        verify(powerOfAttorneyService, times(1)).setAuthorization("sub", "testAccNumber", "testOtherUser", Authorization.READ);
    }
}
