package nl.rabobank.repositories;

import nl.rabobank.authorizations.PowerOfAttorney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PowerOfAttorneyRepository {
    Flux<PowerOfAttorney> findAllByGranteeName(String granteeName);

    Mono<PowerOfAttorney> findAllByGranteeNameAndAccountNumber(String granteeName, String accountNumber);

    Mono<PowerOfAttorney> save(PowerOfAttorney powerOfAttorney);
}
