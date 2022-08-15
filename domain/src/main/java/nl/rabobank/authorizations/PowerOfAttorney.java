package nl.rabobank.authorizations;

import lombok.Builder;
import lombok.Data;
import nl.rabobank.account.Account;

@Data
@Builder(toBuilder = true)
public class PowerOfAttorney {
    private String granteeName;
    private String grantorName;
    private Account account;
    private Authorization authorization;
}
