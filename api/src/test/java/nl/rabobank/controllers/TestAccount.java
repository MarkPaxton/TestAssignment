package nl.rabobank.controllers;

import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.account.Account;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestAccount implements Account {
    private static Random r = new Random();
    String accountNumber = "1234567889" + r.nextInt();
    String accountHolderName = "Test Name" + r.nextInt();
    Double balance = r.nextDouble();
}
