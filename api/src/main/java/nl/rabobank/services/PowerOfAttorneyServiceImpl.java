package nl.rabobank.services;

import org.springframework.stereotype.Service;

import lombok.Getter;
import nl.rabobank.repositories.AccountRepository;
import nl.rabobank.repositories.PowerOfAttorneyAuthorisationRepository;

@Service
@Getter
public class PowerOfAttorneyServiceImpl extends AbstractPowerOfAttorneyService {

    private final AccountRepository accountRepository;
    private final PowerOfAttorneyAuthorisationRepository powerOfAttorneyAuthorisationRepository;

    public PowerOfAttorneyServiceImpl(AccountRepository accountRepository, PowerOfAttorneyAuthorisationRepository powerOfAttorneyAuthorisationRepository) {
        this.accountRepository = accountRepository;
        this.powerOfAttorneyAuthorisationRepository = powerOfAttorneyAuthorisationRepository;
    }
}
