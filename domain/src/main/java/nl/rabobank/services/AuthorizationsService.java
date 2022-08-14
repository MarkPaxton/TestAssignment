package nl.rabobank.services;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthorizationsService {
    Flux<Authorization> getAuthorizationFor(String userName, String accountType, String accountNumber);

    Flux<Authorization> setAuthorizationFor(String userName, String accountType, String accountNumber);

    Flux<PowerOfAttorney> getPowerPowerOfAttorney(String userName, String accountType, String accountNumber);

    Flux<Authorization> setPowerOfAttorney(String userName, String accountType, String accountNumber);


    Flux<PowerOfAttorney> getAllPowerOfAttorneyFor(String userName);

    Flux<PowerOfAttorney> getAllPowerOfAttorneyOf(String userName);

}
