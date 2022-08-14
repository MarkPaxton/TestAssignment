package nl.rabobank.repositories;

import java.util.Optional;

import nl.rabobank.authorizations.PowerOfAttorney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PowerOfAttorneyAuthorisationRepository {
    Flux<PowerOfAttorney> getAllByGrantee(String granteeName);

    Mono<PowerOfAttorney> getByGranteeNameAndAccountNumber(String granteeName, String accountNumber);

    Mono<PowerOfAttorney> save(PowerOfAttorney powerOfAttorney);
}
