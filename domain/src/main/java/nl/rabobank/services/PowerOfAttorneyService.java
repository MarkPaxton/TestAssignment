package nl.rabobank.services;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface PowerOfAttorneyService {
     Flux<Tuple2<Account, Authorization>> getAllAccounts(String userName);

     Mono<Void> setAuthorization(String currentUser, String accountNumber, String targetUser, Authorization authorization);
}
