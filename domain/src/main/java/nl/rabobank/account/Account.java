package nl.rabobank.account;

public interface Account extends Comparable<Account>
{
    String getAccountNumber();
    String getAccountHolderName();
    Double getBalance();

    default int compareTo(Account o) {
        int compare = this.getAccountNumber().compareTo(o.getAccountNumber());
        if (compare != 0)
            return compare;
        compare = this.getBalance().compareTo(o.getBalance());
        if (compare != 0)
            return compare;
        return this.getAccountHolderName().compareTo(o.getAccountHolderName());
    }
}
