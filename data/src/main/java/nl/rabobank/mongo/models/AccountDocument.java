package nl.rabobank.mongo.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.account.Account;

/**
 * MongoDB Persistence wrapper for Accounts
 */
@Document
@Data
@NoArgsConstructor
@CompoundIndex(name = "acc_name_number", def = "{'account.accountNumber' : 1, 'account.accountHolderName' : 1}")
public class AccountDocument implements Account {

    @Id
    private String id;
    private Account account;

    public AccountDocument(Account account) {
        this.account = account;
    }

    public String getAccountNumber() {
        return account.getAccountNumber();
    }

    @Override
    public String getAccountHolderName() {
        return account.getAccountHolderName();
    }

    @Override
    public Double getBalance() {
        return account.getBalance();
    }
}
