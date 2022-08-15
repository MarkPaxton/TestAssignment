package nl.rabobank.controllers;

import java.util.Random;

import lombok.Builder;
import lombok.Data;
import nl.rabobank.account.Account;

/**
 * Test implementation for Accounts
 */
@Data
@Builder
public class TestAccount implements Account {
    private static Random r = new Random();
    @Builder.Default
    String accountNumber = "1234567889" + r.nextInt();
    @Builder.Default
    String accountHolderName = "Test Name" + r.nextInt();
    @Builder.Default
    Double balance = r.nextDouble();
}
