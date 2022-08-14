package nl.rabobank.mongo.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import nl.rabobank.mongo.models.AccountDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SpringAccountRepository extends ReactiveMongoRepository<AccountDocument, String> {
    Flux<AccountDocument> findAllByAccount_AccountHolderName(String accountHolderName);

    Flux<AccountDocument> findAllByAccount_AccountHolderNameAndAccountNumber(String granteeName, String accountNumber);

    Mono<AccountDocument> save(AccountDocument powerOfAttorneyDocument);

    Flux<AccountDocument> findAllByAccountNumber(String accountNumber);
}