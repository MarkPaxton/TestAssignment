package nl.rabobank.services;

import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.repositories.AccountRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DebugAccountService {
    AccountRepository accountRepository;
    Random random = new Random();

    public DebugAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Flux<Account> findAll() {
        return accountRepository.findAll();
    }

    public Mono<Account> createAccount(String accountHolderName, String accountNumber, String type) {
        if ("payment".equals(type)) {
            return accountRepository.save(
                            new PaymentAccount(accountNumber, accountHolderName, random.nextDouble() * 5000, random.nextDouble() * 1000));
        }
        return accountRepository.save(new SavingsAccount(accountNumber, accountHolderName, random.nextDouble() * 50000, random.nextDouble() * 3000));
    }
}
