package nl.rabobank.services;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountAccessService {
    Flux<Account> getAllAccounts();

    Flux<Account> getAllAccountsByUserName(String userName);

    Mono<Account> getAccountByAccountNumber(String accountNumber);

    Flux<Authorization> getAllAuthorizations();

    Mono<Authorization> getAuthorizationsByAccountNumber(String accountNumber);


}
