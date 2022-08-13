package nl.rabobank.controllers;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.Account;
import nl.rabobank.services.AccountAccessService;
import reactor.core.publisher.Flux;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
class ApiControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean(name="testAccountTypeEmpty")
    private AccountAccessService mockAccountAccessService;

    @MockBean(name="testAccountTypeData")
    private AccountAccessService mockAccountAccessServiceWithValues;

    @BeforeEach
    public void setup() {
       when(mockAccountAccessService.getAllAccounts()).thenReturn(Flux.empty());
       when(mockAccountAccessServiceWithValues.getAllAccounts()).thenReturn(Flux.just(new TestAccount(), new TestAccount(), new TestAccount()));
    }

    /**
     * When an account type that does not have an available service is given
     * A BAD_REQUEST response is expected
     */
    @Test
    void getAllAccounts_InvalidAccountType() {
        webTestClient.get().uri("/access/invalidAccountType").exchange()
                     .expectStatus().isBadRequest();
    }

    /**
     * When an account type that does have an available service but no data stored is given
     * An OK but empty jsonarray response is expected
     */
    @Test
    void getAllAccounts_TestAccountType_Empty() {
        webTestClient.get().uri("/access/testAccountTypeEmpty").exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$").isArray()
                        .jsonPath("$.length()").isEqualTo(0);
    }


    /**
     * When an account type that does have an available service with data is given
     * An OK but empty jsonarray response is expected
     */
    @Test
    void getAllAccounts_TestAccountType_Data() {
        webTestClient.get().uri("/access/testAccountTypeData").exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$").isArray()
                     .jsonPath("$.length()").isEqualTo(3);
    }
}
