package nl.rabobank.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.controllers.TestAccount;
import nl.rabobank.mongo.repositories.PowerOfAttorneyRepositoryImpl;
import reactor.test.StepVerifier;

@DataMongoTest(includeFilters = @ComponentScan.Filter(Service.class))
class PowerOfAttorneyRepositoryImplTest {

    @Autowired
    PowerOfAttorneyRepositoryImpl powerOfAttorneyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Test
    void PowerOfAttorneyRepository_Save_And_Find_Ok() {
        var account = TestAccount.builder().build();

        var testSaved = accountRepository.save(account).flatMap(saveAccount ->
                        powerOfAttorneyRepository.save(PowerOfAttorney.builder()
                                                                      .granteeName("TestGrantee")
                                                                      .grantorName(saveAccount.getAccountHolderName())
                                                                      .authorization(Authorization.WRITE)
                                                                      .account(saveAccount).build())
        );
        StepVerifier.create(testSaved)
                    .expectNextMatches(saved -> saved.getAccount().compareTo(account) == 0 &&
                                    saved.getGranteeName().equals("TestGrantee") &&
                                    saved.getAuthorization() == Authorization.WRITE &&
                                    saved.getGrantorName().equals(account.getAccountHolderName()))
                    .expectComplete().verify();

        var testFound = powerOfAttorneyRepository.findAllByGranteeName("TestGrantee");

        StepVerifier.create(testFound)
                    .expectNextMatches(found -> found.getAccount().compareTo(account) == 0 &&
                                    found.getGranteeName().equals("TestGrantee") &&
                                    found.getAuthorization() == Authorization.WRITE &&
                                    found.getGrantorName().equals(account.getAccountHolderName()))
                    .expectComplete().verify();
    }
}