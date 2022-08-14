package nl.rabobank.mongo.repositories;

import org.springframework.stereotype.Service;

import nl.rabobank.account.Account;
import nl.rabobank.mongo.models.AccountDocument;
import nl.rabobank.repositories.AccountRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Facade service to interface between Mongo document class and domain class
 */
@Service
public class AccountRepositoryImpl implements AccountRepository {

    SpringAccountRepository springAccountRepository;

    public AccountRepositoryImpl(SpringAccountRepository springAccountRepository) {
        this.springAccountRepository = springAccountRepository;
    }

    @Override
    public Flux<Account> findAllByAccountHolderName(String accountHolderName) {
        return springAccountRepository.findAllByAccount_AccountHolderName(accountHolderName).map(AccountDocument::getAccount);
    }

    @Override
    public Mono<Account> save(Account account) {
        return springAccountRepository.save(new AccountDocument(account)).map(AccountDocument::getAccount);
    }

    @Override
    public Mono<Account> findByAccountNumber(String accountNumber) {
        return springAccountRepository.findAllByAccountNumber(accountNumber).map(AccountDocument::getAccount).singleOrEmpty();
    }

    @Override
    public Mono<Account> findAllByAccountHolderNameAndAccountNumber(String accountHolderName, String accountNumber) {
        return springAccountRepository.findAllByAccount_AccountHolderNameAndAccountNumber(accountHolderName, accountNumber)
                                      .map(AccountDocument::getAccount).singleOrEmpty();
    }
}
