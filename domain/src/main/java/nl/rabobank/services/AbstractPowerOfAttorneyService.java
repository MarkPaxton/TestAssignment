package nl.rabobank.services;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.authorizations.PowerOfAttorneyImpl;
import nl.rabobank.exceptions.NoAccountException;
import nl.rabobank.repositories.AccountRepository;
import nl.rabobank.repositories.PowerOfAttorneyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import static reactor.core.publisher.Mono.defer;

public abstract class AbstractPowerOfAttorneyService implements PowerOfAttorneyService {

    protected abstract AccountRepository getAccountRepository();

    protected abstract PowerOfAttorneyRepository getPowerOfAttorneyAuthorisationRepository();

    @Override
    public Flux<Tuple2<Account, Authorization>> getAllAccounts(String userName) {
        return getPowerOfAttorneyAuthorisationRepository().findAllByGranteeName(userName)
                                                          .map(auth -> Tuples.of(auth.getAccount(), auth.getAuthorization()));
    }

    @Override
    public Mono<PowerOfAttorney> setAuthorization(String grantor, String accountNumber, String grantee, Authorization authorization) {
        return getPowerOfAttorneyAuthorisationRepository().findAllByGranteeNameAndAccountNumber(grantee, accountNumber)
                                                          .filter(existingPower -> existingPower.getAuthorization() == authorization)
                                                          .switchIfEmpty(defer(() ->
                                                                          getAccountRepository()
                                                                                          .findAllByAccountHolderNameAndAccountNumber(grantor, accountNumber)
                                                                                          .switchIfEmpty(Mono.error(new NoAccountException()))
                                                                                          .flatMap(account -> getPowerOfAttorneyAuthorisationRepository().save(
                                                                                                          PowerOfAttorneyImpl.builder()
                                                                                                                             .authorization(authorization)
                                                                                                                             .granteeName(grantee)
                                                                                                                             .grantorName(grantor)
                                                                                                                             .account(account).build()))));
    }
}
