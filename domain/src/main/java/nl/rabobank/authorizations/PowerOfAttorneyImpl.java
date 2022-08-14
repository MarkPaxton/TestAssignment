package nl.rabobank.authorizations;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import nl.rabobank.account.Account;

@Data
@Builder(toBuilder = true)
public class PowerOfAttorneyImpl implements PowerOfAttorney {
    String granteeName;
    String grantorName;
    Account account;
    Authorization authorization;
}
