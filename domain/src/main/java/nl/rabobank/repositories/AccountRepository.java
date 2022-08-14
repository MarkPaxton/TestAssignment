package nl.rabobank.repositories;

import nl.rabobank.account.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository {
    Flux<Account> findAllByAccountHolderName(String accountHolder);

    Mono<Account> save(Account account);
    Mono<Account> findByAccountNumber(String accountNumber);

    Mono<Account> findAllByAccountHolderNameAndAccountNumber(String accountHolder, String accountNumber);
}
