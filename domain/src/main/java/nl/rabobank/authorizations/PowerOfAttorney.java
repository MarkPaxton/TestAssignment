package nl.rabobank.authorizations;

import nl.rabobank.account.Account;

public interface PowerOfAttorney extends Comparable<PowerOfAttorney> {
    String getGranteeName();

    void setGranteeName(String granteeName);

    String getGrantorName();

    void setGrantorName(String grantorName);

    Account getAccount();

    void setAccount(Account account);

    Authorization getAuthorization();

    void setAuthorization(Authorization authorization);

    @Override
    default int compareTo(PowerOfAttorney o) {
        int compare = this.getGranteeName().compareTo(o.getGranteeName());
        if (compare != 0)
            return compare;
        compare = this.getGrantorName().compareTo(o.getGrantorName());
        if (compare != 0)
            return compare;
        compare = this.getAuthorization().compareTo(o.getAuthorization());
        if (compare != 0)
            return compare;
        return this.getAccount().compareTo(o.getAccount());

    }
}
