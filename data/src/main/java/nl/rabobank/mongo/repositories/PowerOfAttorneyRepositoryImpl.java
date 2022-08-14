package nl.rabobank.mongo.repositories;

import org.springframework.stereotype.Service;

import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.models.PowerOfAttorneyDocument;
import nl.rabobank.repositories.PowerOfAttorneyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Facade service to interface between Mongo document class and domain class
 */
@Service
public class PowerOfAttorneyRepositoryImpl implements PowerOfAttorneyRepository {
    SpringPowerOfAttorneyRepository springPowerOfAttorneyRepository;

    public PowerOfAttorneyRepositoryImpl(SpringPowerOfAttorneyRepository springPowerOfAttorneyRepository) {
        this.springPowerOfAttorneyRepository = springPowerOfAttorneyRepository;
    }

    @Override
    public Flux<PowerOfAttorney> findAllByGranteeName(String granteeName) {
        return springPowerOfAttorneyRepository.findAllByGranteeName(granteeName)
                                              .groupBy(PowerOfAttorneyDocument::getAccount)
                                              // For each account, keep only the newest grant
                                              .flatMap(group -> group.reduce((o1, o2) -> o1.getCreated().compareTo(o2.getCreated()) > 0 ? o1 : o2))
                                              // Exclude the NONE grant as is a placeholder to cancel any previous READ/WRITE grants only to maintain audit trail
                                              .filter(d -> d.getAuthorization() != Authorization.NONE)
                                              .map(PowerOfAttorney.class::cast);
    }

    @Override
    public Mono<PowerOfAttorney> findAllByGranteeNameAndAccountNumber(String granteeName, String accountNumber) {
        return springPowerOfAttorneyRepository.findAllByGranteeNameAndAccountAccountNumber(granteeName, accountNumber)
                                              .groupBy(PowerOfAttorneyDocument::getAccount)
                                              .flatMap(group -> group.reduce((o1, o2) -> o1.getCreated().compareTo(o2.getCreated()) > 0 ? o1 : o2))
                                              .filter(d -> d.getAuthorization() != Authorization.NONE)
                                              .map(PowerOfAttorney.class::cast).singleOrEmpty();

    }

    @Override
    public Mono<PowerOfAttorney> save(PowerOfAttorney powerOfAttorney) {
        return springPowerOfAttorneyRepository.save(PowerOfAttorneyDocument.from(powerOfAttorney)).map(PowerOfAttorney.class::cast);
    }
}
