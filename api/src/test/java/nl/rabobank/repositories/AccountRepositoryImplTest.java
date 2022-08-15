package nl.rabobank.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import nl.rabobank.mongo.repositories.AccountRepositoryImpl;
import reactor.test.StepVerifier;

@DataMongoTest(includeFilters = @ComponentScan.Filter(Service.class))
class AccountRepositoryImplTest {

    @Autowired
    AccountRepositoryImpl accountRepository;

    @Test
    void AccountRepository_Save_And_Find_Ok() {
        var original = TestAccount.builder().build();
        var testSaved = accountRepository.save(original);

        StepVerifier.create(testSaved)
                    .expectNextMatches(saved -> original.compareTo(saved) == 0)
                    .expectComplete().verify();

        var testFound = accountRepository.findAllByAccountHolderName(original.getAccountHolderName());

        StepVerifier.create(testFound)
                    .expectNextMatches(found -> found.compareTo(original) == 0)
                    .expectComplete().verify();
    }

    @Test
    void AccountRepository_Save_And_Find_None() {
        var original = TestAccount.builder().build();
        var testSaved = accountRepository.save(original);

        StepVerifier.create(testSaved)
                    .expectNextMatches(saved -> original.compareTo(saved) == 0)
                    .expectComplete().verify();

        var testFound = accountRepository.findAllByAccountHolderName(original.getAccountHolderName() + "XXX");

        StepVerifier.create(testFound)
                    .expectNextCount(0)
                    .expectComplete().verify();
    }

    @Test
    void AccountRepository_Save_And_FindByAccountNumber_Ok() {
        var original = TestAccount.builder().build();
        var testSaved = accountRepository.save(original);

        StepVerifier.create(testSaved)
                    .expectNextMatches(saved -> original.compareTo(saved) == 0)
                    .expectComplete().verify();

        var testFound = accountRepository.findByAccountNumber(original.getAccountNumber());

        StepVerifier.create(testFound)
                    .expectNextMatches(found -> found.compareTo(original) == 0)
                    .expectComplete().verify();
    }

    @Test
    void AccountRepository_Save_And_FindByAccountNumber_None() {
        var original = TestAccount.builder().build();
        var testSaved = accountRepository.save(original);

        StepVerifier.create(testSaved)
                    .expectNextMatches(saved -> original.compareTo(saved) == 0)
                    .expectComplete().verify();

        var testFound = accountRepository.findByAccountNumber(original.getAccountNumber() + "XXXX");

        StepVerifier.create(testFound)
                    .expectNextCount(0)
                    .expectComplete().verify();
    }
}
