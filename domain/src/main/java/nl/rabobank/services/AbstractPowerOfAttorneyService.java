package nl.rabobank.services;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exceptions.NoAccountException;
import nl.rabobank.repositories.AccountRepository;
import nl.rabobank.repositories.PowerOfAttorneyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import static reactor.core.publisher.Mono.defer;

/**
 * Business logic implementation fo PowerOfAttorney service, not referencing a particular implementation of the repositories
 */
public abstract class AbstractPowerOfAttorneyService implements PowerOfAttorneyService {

    protected abstract AccountRepository getAccountRepository();

    protected abstract PowerOfAttorneyRepository getPowerOfAttorneyAuthorisationRepository();

    /**
     * Returns all the accounts and authorizations that the given userName has Power of Attorney Over
     *
     * @param userName the accountHolderName to return the accounts for
     * @return Tuple of (Account, Authorization)
     */
    @Override
    public Flux<Tuple2<Account, Authorization>> getAllAccountsForName(String userName) {
        return getPowerOfAttorneyAuthorisationRepository().findAllByGranteeName(userName)
                                                          .map(auth -> Tuples.of(auth.getAccount(), auth.getAuthorization()));
    }

    /**
     * Set a power of attorney over grantee, from grantor on accountNumber n.b. grantor must match accountHolderName
     * if the Power already exists, then nothing is done and the existing power is returned
     *
     * @param grantor       The userName/accountHolderName who is giving power of attorney
     * @param accountNumber the account number to grant the power on
     * @param grantee       Username of the person who will receive power of attorney
     * @param authorization type of power READ | WRITE | NONE if authorization NONE is used, the Power is cancelled but a record kept
     * @return PowerOfAttorney object representing the created / existing power
     */
    @Override
    public Mono<PowerOfAttorney> setAuthorization(String grantor, String accountNumber, String grantee, Authorization authorization) {
        return getPowerOfAttorneyAuthorisationRepository().findAllByGranteeNameAndAccountNumber(grantee, accountNumber)
                                                          .filter(existingPower -> existingPower.getAuthorization() == authorization)
                                                          .switchIfEmpty(defer(() ->
                                                                          getAccountRepository()
                                                                                          .findAllByAccountHolderNameAndAccountNumber(grantor,
                                                                                                          accountNumber)
                                                                                          .switchIfEmpty(Mono.error(new NoAccountException()))
                                                                                          .flatMap(account -> getPowerOfAttorneyAuthorisationRepository().save(
                                                                                                          PowerOfAttorney.builder()
                                                                                                                         .authorization(authorization)
                                                                                                                         .granteeName(grantee)
                                                                                                                         .grantorName(grantor)
                                                                                                                         .account(account)
                                                                                                                         .build()))));
    }
}
