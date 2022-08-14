package nl.rabobank.account;

import lombok.Value;

@Value
public class PaymentAccount implements Account
{
    String accountNumber;
    String accountHolderName;
    Double balance;
    Double overdraftLimit;
}
