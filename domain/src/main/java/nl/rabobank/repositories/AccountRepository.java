package nl.rabobank.repositories;

import nl.rabobank.account.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository {
    Flux<Account> getAllAccountsByAccountHolder(String accountHolder);

    Mono<Account> createAccount(Account account);
    Mono<Account> getAccountByAccountNumber(String accountNumber);

    Mono<Account> getByAccountHolderAndAccountNumber(String accountHolder, String accountNumber);
}
