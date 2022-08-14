package nl.rabobank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountListDTO {
    Authorization authorization;
    Account account;
}
