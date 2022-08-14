package nl.rabobank.services;

import org.springframework.stereotype.Service;

import lombok.Getter;
import nl.rabobank.repositories.AccountRepository;
import nl.rabobank.repositories.PowerOfAttorneyRepository;

@Service
@Getter
public class PowerOfAttorneyServiceImpl extends AbstractPowerOfAttorneyService {

    private final AccountRepository accountRepository;
    private final PowerOfAttorneyRepository powerOfAttorneyAuthorisationRepository;

    public PowerOfAttorneyServiceImpl(AccountRepository accountRepository, PowerOfAttorneyRepository powerOfAttorneyAuthorisationRepository) {
        this.accountRepository = accountRepository;
        this.powerOfAttorneyAuthorisationRepository = powerOfAttorneyAuthorisationRepository;
    }
}
