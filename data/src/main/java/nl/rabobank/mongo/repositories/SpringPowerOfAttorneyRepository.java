package nl.rabobank.mongo.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import nl.rabobank.mongo.models.PowerOfAttorneyDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SpringPowerOfAttorneyRepository extends ReactiveMongoRepository<PowerOfAttorneyDocument, String> {
    Flux<PowerOfAttorneyDocument> findAllByGranteeName(String granteeName);

    Flux<PowerOfAttorneyDocument> findAllByGranteeNameAndAccountDocumentId(String granteeName, String accoutnDocumentId);

    Mono<PowerOfAttorneyDocument> save(PowerOfAttorneyDocument powerOfAttorneyDocument);
}