package nl.rabobank.mongo.models;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(name = "grantor_accountNumber", def = "{'grantorName' : 1, 'account.account.accountNumber' : 1}")
public class PowerOfAttorneyDocument implements PowerOfAttorney {
    @Indexed
    private String grantorName;

    @DocumentReference
    private AccountDocument account;

    private Authorization authorization;

    @CreatedDate
    @Indexed
    private DateTime created;

    @Indexed
    private String granteeName;

    public static PowerOfAttorneyDocument from(PowerOfAttorney o) {
        var d = new PowerOfAttorneyDocument();
        d.setAccount(o.getAccount());
        d.setAuthorization(o.getAuthorization());
        d.setGranteeName(o.getGranteeName());
        d.setGrantorName(o.getGrantorName());
        return d;
    }

    public Account getAccount() {
        return this.account.getAccount();
    }

    public void setAccount(Account account) {
        this.account = new AccountDocument(account);
    }
}
