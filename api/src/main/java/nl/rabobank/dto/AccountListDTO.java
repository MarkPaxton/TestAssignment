package nl.rabobank.dto;

import lombok.Builder;
import lombok.Data;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;

/**
 * DTO Object to allow the AccountList response to be a well formatted JSON Object
 */
@Data
@Builder
public class AccountListDTO {
    Authorization authorization;
    Account account;
    String accountType;
}
