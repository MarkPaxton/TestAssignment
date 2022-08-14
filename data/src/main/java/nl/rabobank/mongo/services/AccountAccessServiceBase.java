package nl.rabobank.mongo.services;

import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import nl.rabobank.account.*;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.services.AccountAccessService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AccountAccessServiceBase implements AccountAccessService {

    protected abstract Class getAccountType();

    ReactiveMongoTemplate template;



    public Flux<Account> findAll() {
        return template.findAll(Account.class);
    }
    public Mono<Account> save(Mono<Account> account) {
        return template.save(account);
    }

    @Override public Flux<Account> getAllAccounts() {
        return null;
    }

    @Override public Flux<Account> getAllAccountsByUserName(String userName) {
        return null;
    }

    @Override public Mono<Account> getAccountByAccountNumber(String accountNumber) {
        return null;
    }

    @Override public Flux<Authorization> getAllAuthorizations() {
        return null;
    }

    @Override public Mono<Authorization> getAuthorizationsByAccountNumber(String accountNumber) {
        return null;
    }
}
