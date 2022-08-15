package nl.rabobank.services;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exceptions.NoAccountException;
import nl.rabobank.models.TestAccount;
import nl.rabobank.repositories.AccountRepository;
import nl.rabobank.repositories.PowerOfAttorneyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PowerOfAttorneyServiceTest {

    AccountRepository accountRepository;

    PowerOfAttorneyRepository powerOfAttorneyAuthorisationRepository;

    Map<String, TestAccount> testAccounts = Map.of("1", TestAccount.builder().accountNumber("1").build(),
                    "2", TestAccount.builder().accountNumber("2").build(),
                    "3", TestAccount.builder().accountNumber("3").build(),
                    "4", TestAccount.builder().accountNumber("4").build(),
                    "5", TestAccount.builder().accountNumber("5").build());

    List<PowerOfAttorney> testPowers = List.of(
                    PowerOfAttorney.builder().granteeName("grant").grantorName(testAccounts.get("1").getAccountHolderName())
                                   .account(testAccounts.get("1"))
                                   .authorization(Authorization.READ).build(),
                    PowerOfAttorney.builder().granteeName("grant").grantorName(testAccounts.get("2").getAccountHolderName())
                                   .account(testAccounts.get("2"))
                                   .authorization(Authorization.WRITE).build(),
                    PowerOfAttorney.builder().granteeName("grant").grantorName(testAccounts.get("3").getAccountHolderName())
                                   .account(testAccounts.get("3"))
                                   .authorization(Authorization.READ).build(),
                    PowerOfAttorney.builder().granteeName("grant").grantorName(testAccounts.get("4").getAccountHolderName())
                                   .account(testAccounts.get("4"))
                                   .authorization(Authorization.WRITE).build(),
                    PowerOfAttorney.builder().granteeName("grant").grantorName(testAccounts.get("5").getAccountHolderName())
                                   .account(testAccounts.get("5"))
                                   .authorization(Authorization.WRITE).build()
    );

    @BeforeEach
    void beforeEach() {
        accountRepository = mock(AccountRepository.class);
        powerOfAttorneyAuthorisationRepository = mock(PowerOfAttorneyRepository.class);
    }

    /**
     * Test an empty set is returned for any user that don't have a record
     */
    @Test
    void getAllAccountsForAccountHolder_Empty() {
        doReturn(Flux.empty()).when(powerOfAttorneyAuthorisationRepository).findAllByGranteeName("testUser");
        var powerOfAttorneyService = new TestPowerOfAttorneyService(accountRepository, powerOfAttorneyAuthorisationRepository);

        var results = powerOfAttorneyService.getAllAccountsForName("testUser");

        StepVerifier.create(results)
                    .expectSubscription().expectNextCount(0)
                    .expectComplete().verify();

        verify(powerOfAttorneyAuthorisationRepository, times(1)).findAllByGranteeName("testUser");

    }

    /**
     * Test that data is returned for a user who is found in the granted PoAs
     */
    @Test
    void getAllAccountsForAccountHolder_WithData() {
        doReturn(Flux.fromIterable(testPowers)).when(powerOfAttorneyAuthorisationRepository).findAllByGranteeName("testUser");
        var powerOfAttorneyService = new TestPowerOfAttorneyService(accountRepository, powerOfAttorneyAuthorisationRepository);

        var results = powerOfAttorneyService.getAllAccountsForName("testUser");
        StepVerifier.create(results)
                    .expectNextCount(5)
                    .expectComplete()
                    .verify();
        verify(powerOfAttorneyAuthorisationRepository, times(1)).findAllByGranteeName("testUser");

    }

    /**
     * If the grantee account name/number don't match up, then return an error
     */
    @Test
    void setAuthorization_With_No_Matching_Account() {
        doReturn(Mono.empty()).when(powerOfAttorneyAuthorisationRepository).findAllByGranteeNameAndAccountNumber("ruby", "1");
        doReturn(Mono.empty()).when(accountRepository).findAllByAccountHolderNameAndAccountNumber("jane", "1");

        var powerOfAttorneyService = new TestPowerOfAttorneyService(accountRepository, powerOfAttorneyAuthorisationRepository);

        var results = powerOfAttorneyService.setAuthorization("jane", "1", "ruby", Authorization.WRITE);
        StepVerifier.create(results)
                    .expectError(NoAccountException.class)
                    .verify();
        verify(powerOfAttorneyAuthorisationRepository, times(1)).findAllByGranteeNameAndAccountNumber("ruby", "1");
        verify(accountRepository, times(1)).findAllByAccountHolderNameAndAccountNumber("jane", "1");
    }

    /**
     * Test that if an identical PoA Authorization already exists, don't create a new one
     */
    @Test
    void setAuthorization_With_Correct_Account_Existing_Grant() {
        var testExistingPower = testPowers.get(0);
        var testAccount = testExistingPower.getAccount();
        doReturn(Mono.just(testExistingPower)).when(powerOfAttorneyAuthorisationRepository)
                                              .findAllByGranteeNameAndAccountNumber(testExistingPower.getGranteeName(), testAccount.getAccountNumber());

        var powerOfAttorneyService = new TestPowerOfAttorneyService(accountRepository, powerOfAttorneyAuthorisationRepository);

        var results = powerOfAttorneyService.setAuthorization(testAccount.getAccountHolderName(),
                        testAccount.getAccountNumber(), testExistingPower.getGranteeName(), testExistingPower.getAuthorization());

        StepVerifier.create(results)
                    .expectNextMatches(granted -> granted.equals(testExistingPower))
                    .expectComplete().verify();
        verify(powerOfAttorneyAuthorisationRepository, times(1)).findAllByGranteeNameAndAccountNumber(testExistingPower.getGranteeName(),
                        testAccount.getAccountNumber());
        verify(accountRepository, never()).findAllByAccountHolderNameAndAccountNumber(anyString(), anyString());
        verify(powerOfAttorneyAuthorisationRepository, never()).save(any(PowerOfAttorney.class));
    }

    /**
     * Test that if no PoA authorisation exits for the user, then create a new one and save it
     */
    @Test
    void setAuthorization_With_Correct_Account_New_Grant() {
        var testAccount = testAccounts.get("1");
        doReturn(Mono.empty()).when(powerOfAttorneyAuthorisationRepository)
                              .findAllByGranteeNameAndAccountNumber("jane", testAccount.getAccountNumber());
        doReturn(Mono.just(testAccount)).when(accountRepository)
                                        .findAllByAccountHolderNameAndAccountNumber(testAccount.getAccountHolderName(), testAccount.getAccountNumber());
        doAnswer(a -> Mono.just(a.getArgument(0))).when(powerOfAttorneyAuthorisationRepository).save(any(PowerOfAttorney.class));

        var powerOfAttorneyService = new TestPowerOfAttorneyService(accountRepository, powerOfAttorneyAuthorisationRepository);
        var results = powerOfAttorneyService.setAuthorization(testAccount.getAccountHolderName(), testAccount.getAccountNumber(), "jane",
                        Authorization.WRITE);

        StepVerifier.create(results)
                    .expectNextMatches(granted ->
                                    granted.getAuthorization().equals(Authorization.WRITE) &&
                                                    granted.getAccount().getAccountNumber().equals(testAccount.getAccountNumber()) &&
                                                    granted.getAccount().getAccountHolderName().equals(testAccount.getAccountHolderName()) &&
                                                    granted.getGranteeName().equals("jane") &&
                                                    granted.getGrantorName().equals(testAccount.getAccountHolderName()))
                    .expectComplete().verify();
        verify(powerOfAttorneyAuthorisationRepository, times(1)).findAllByGranteeNameAndAccountNumber("jane", testAccount.getAccountNumber());
        verify(accountRepository, times(1)).findAllByAccountHolderNameAndAccountNumber(testAccount.getAccountHolderName(), testAccount.getAccountNumber());
        verify(powerOfAttorneyAuthorisationRepository, times(1)).save(argThat(power ->
                        power.getAuthorization() == Authorization.WRITE &&
                                        power.getGrantorName().equals(testAccount.getAccountHolderName()) &&
                                        power.getGranteeName().equals("jane") &&
                                        power.getAccount().getAccountNumber().equals(testAccount.getAccountNumber())));
    }

    /**
     * Test that if a different PoA Authorization already exists, create a new one to replace it
     */
    @Test
    void setAuthorization_With_Correct_Account_Replace_Existing_Grant() {
        var testExistingPower = testPowers.get(0);
        var testAccount = testExistingPower.getAccount();
        doReturn(Mono.just(testExistingPower)).when(powerOfAttorneyAuthorisationRepository)
                                              .findAllByGranteeNameAndAccountNumber(testExistingPower.getGranteeName(), testAccount.getAccountNumber());
        doReturn(Mono.just(testAccount)).when(accountRepository)
                                        .findAllByAccountHolderNameAndAccountNumber(eq(testAccount.getAccountHolderName()),
                                                        eq(testAccount.getAccountNumber()));
        doAnswer(a -> Mono.just(a.getArgument(0))).when(powerOfAttorneyAuthorisationRepository).save(any(PowerOfAttorney.class));

        var powerOfAttorneyService = new TestPowerOfAttorneyService(accountRepository, powerOfAttorneyAuthorisationRepository);

        var results = powerOfAttorneyService.setAuthorization(testAccount.getAccountHolderName(),
                        testAccount.getAccountNumber(), testExistingPower.getGranteeName(), Authorization.NONE);

        StepVerifier.create(results)
                    .expectNextMatches(granted -> testExistingPower.getGrantorName().equals(granted.getGrantorName()) &&
                                    testExistingPower.getGranteeName().equals(granted.getGranteeName()) &&
                                    testExistingPower.getAccount().getAccountNumber().equals(granted.getAccount().getAccountNumber()) &&
                                    granted.getAuthorization() == Authorization.NONE)
                    .expectComplete().verify();
        verify(powerOfAttorneyAuthorisationRepository, times(1)).findAllByGranteeNameAndAccountNumber(testExistingPower.getGranteeName(),
                        testAccount.getAccountNumber());
        verify(accountRepository, times(1)).findAllByAccountHolderNameAndAccountNumber(anyString(), anyString());
        verify(powerOfAttorneyAuthorisationRepository, times(1)).save(argThat(power ->
                        power.getAuthorization() == Authorization.NONE &&
                                        power.getGrantorName().equals(testExistingPower.getGrantorName()) &&
                                        power.getGranteeName().equals(testExistingPower.getGranteeName()) &&
                                        power.getAccount().getAccountNumber().equals(testAccount.getAccountNumber())));
    }

}