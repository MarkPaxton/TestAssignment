package nl.rabobank.services;

import lombok.AllArgsConstructor;
import nl.rabobank.repositories.AccountRepository;
import nl.rabobank.repositories.PowerOfAttorneyRepository;

@AllArgsConstructor
public class TestPowerOfAttorneyService  extends AbstractPowerOfAttorneyService {
    AccountRepository accountRepository;
    PowerOfAttorneyRepository powerOfAttorneyAuthorisationRepository;

    @Override protected AccountRepository getAccountRepository() {
        return accountRepository;
    }

    @Override protected PowerOfAttorneyRepository getPowerOfAttorneyAuthorisationRepository() {
        return powerOfAttorneyAuthorisationRepository;
    }
}
