package nl.rabobank.mongo.models;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.account.Account;

@Document
@Data
@NoArgsConstructor
@CompoundIndex(name = "acc_name_number", def = "{'accountNumber' : 1, 'account.accountHolderName' : 1}")
public class AccountDocument {
    private Account account;

    public AccountDocument(Account account) {
        this.account = account;
    }

    @Id
    @AccessType(AccessType.Type.PROPERTY)
    public String getAccountNumber() {
        return account.getAccountNumber();
    }

    public void setAccountNumber(String accountNumber) {
        // Don't set it because it's copied from the Account object but setter needed for Mongo
    }
}
