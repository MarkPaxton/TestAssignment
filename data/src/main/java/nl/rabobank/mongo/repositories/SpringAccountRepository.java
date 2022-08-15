package nl.rabobank.mongo.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import nl.rabobank.mongo.models.AccountDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SpringAccountRepository extends ReactiveMongoRepository<AccountDocument, String> {
    Flux<AccountDocument> findAllByAccount_AccountHolderName(String accountHolderName);

    Flux<AccountDocument> findAllByAccount_AccountHolderNameAndAccount_AccountNumber(String granteeName, String accountNumber);

    Mono<AccountDocument> findById(String id);

    Mono<AccountDocument> save(AccountDocument powerOfAttorneyDocument);

    Flux<AccountDocument> findAllByAccount_AccountNumber(String accountNumber);
}