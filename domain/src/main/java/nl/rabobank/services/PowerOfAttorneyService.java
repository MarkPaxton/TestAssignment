package nl.rabobank.services;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * Interface definition for PowerOfAttorney Service
 */
public interface PowerOfAttorneyService {
     /**
      * Gets a list of all the Accounts with active Power of Attorney for given userNAme (accountHolderName)
      * @param userName the userName/accountHolderName to return the accounts for
      * @return Tuple containing power type (READ/WRITE) and the Account
      */
     Flux<Tuple2<Account, Authorization>> getAllAccountsForName(String userName);

     /**
      * Creates a Power of Attorney for grantee on grantor with account number
      * Returns empty is no changes were needed (PoA grant already given)
      * Returns the created PoA if it was granted as a new grant
      * @param grantor The userName/accountHolderName who is giving power of attorney
      * @param accountNumber the account number to grant the power on
      * @param grantee Username of the person who will receive power of attorney
      * @param authorization type of power READ | WRITE | NONE if authorization NONE is used, the Power is cancelled but a record kept
      * @return The created power of attorney
      */
     Mono<PowerOfAttorney> setAuthorization(String grantor, String accountNumber, String grantee, Authorization authorization);
}
