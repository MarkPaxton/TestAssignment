package nl.rabobank.services;

import lombok.AllArgsConstructor;
import nl.rabobank.repositories.AccountRepository;
import nl.rabobank.repositories.PowerOfAttorneyAuthorisationRepository;

@AllArgsConstructor
public class TestPowerOfAttorneyService  extends AbstractPowerOfAttorneyService {
    AccountRepository accountRepository;
    PowerOfAttorneyAuthorisationRepository powerOfAttorneyAuthorisationRepository;

    @Override protected AccountRepository getAccountRepository() {
        return accountRepository;
    }

    @Override protected PowerOfAttorneyAuthorisationRepository getPowerOfAttorneyAuthorisationRepository() {
        return powerOfAttorneyAuthorisationRepository;
    }
}
