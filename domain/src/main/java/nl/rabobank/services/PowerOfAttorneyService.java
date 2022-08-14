package nl.rabobank.services;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface PowerOfAttorneyService {
     Flux<Tuple2<Account, Authorization>> getAllAccounts(String userName);

     /**
      * Creates a PoA authorization for grantee on grantor with account number
      * Returns empty is no changes were needed (PoA grant already given)
      * Returns the created PoA if it was granted as a new grant
      * @param grantor
      * @param accountNumber
      * @param grantee
      * @param authorization
      * @return
      */
     Mono<PowerOfAttorney> setAuthorization(String grantor, String accountNumber, String grantee, Authorization authorization);
}
