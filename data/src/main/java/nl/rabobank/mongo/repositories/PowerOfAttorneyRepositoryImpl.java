package nl.rabobank.mongo.repositories;

import org.springframework.stereotype.Service;

import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exceptions.NoAccountException;
import nl.rabobank.mongo.models.PowerOfAttorneyDocument;
import nl.rabobank.repositories.PowerOfAttorneyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Facade service to interface between Mongo document class and domain class
 */
@Service
public class PowerOfAttorneyRepositoryImpl implements PowerOfAttorneyRepository {
    private final SpringPowerOfAttorneyRepository springPowerOfAttorneyRepository;
    private final SpringAccountRepository springAccountRepository;

    public PowerOfAttorneyRepositoryImpl(SpringPowerOfAttorneyRepository springPowerOfAttorneyRepository,
                    SpringAccountRepository springAccountRepository) {
        this.springPowerOfAttorneyRepository = springPowerOfAttorneyRepository;
        this.springAccountRepository = springAccountRepository;
    }

    @Override
    public Flux<PowerOfAttorney> findAllByGranteeName(String granteeName) {
        return springPowerOfAttorneyRepository.findAllByGranteeName(granteeName)
                                               .groupBy(PowerOfAttorneyDocument::getAccountDocumentId)
                                              // For each account, keep only the newest grant
                                              .flatMap(group -> group.reduce((o1, o2) -> o1.getCreated().compareTo(o2.getCreated()) > 0 ? o1 : o2))
                                              // Exclude the NONE grant as is a placeholder to cancel any previous READ/WRITE grants only to maintain audit trail
                                              .filter(d -> d.getAuthorization() != Authorization.NONE)
                                              .flatMap(d -> springAccountRepository.findById(d.getAccountDocumentId())
                                                                                   .map(account -> PowerOfAttorney.builder()
                                                                                                                  .granteeName(d.getGranteeName())
                                                                                                                  .grantorName(d.getGrantorName())
                                                                                                                  .authorization(d.getAuthorization())
                                                                                                                  .account(account.getAccount())
                                                                                                                  .build()));

    }

    @Override
    public Mono<PowerOfAttorney> findAllByGranteeNameAndAccountNumber(String granteeName, String accountNumber) {
        return springAccountRepository.findAllByAccount_AccountNumber(accountNumber)
                                      .singleOrEmpty()
                                      .flatMap(account ->
                                                      springPowerOfAttorneyRepository
                                                                      .findAllByGranteeNameAndAccountDocumentId(granteeName, account.getId())
                                                                      .reduce((o1, o2) -> o1.getCreated().compareTo(o2.getCreated()) > 0 ? o1 : o2)
                                                                      .filter(d -> d.getAuthorization() != Authorization.NONE)
                                                                      .map(d -> PowerOfAttorney.builder()
                                                                                               .granteeName(d.getGranteeName())
                                                                                               .grantorName(d.getGrantorName())
                                                                                               .authorization(d.getAuthorization())
                                                                                               .account(account)
                                                                                               .build()));

    }

    @Override
    public Mono<PowerOfAttorney> save(PowerOfAttorney powerOfAttorney) {
        return springAccountRepository.findAllByAccount_AccountHolderNameAndAccount_AccountNumber(
                                                      powerOfAttorney.getAccount().getAccountHolderName(), powerOfAttorney.getAccount().getAccountNumber())
                                      .switchIfEmpty(Flux.error(new NoAccountException("Account not found")))
                                      .singleOrEmpty().flatMap(accountDocument -> {
                            var doc = new PowerOfAttorneyDocument();
                            doc.setGranteeName(powerOfAttorney.getGranteeName());
                            doc.setGrantorName(powerOfAttorney.getGrantorName());
                            doc.setAccountDocumentId(accountDocument.getId());
                            doc.setAuthorization(powerOfAttorney.getAuthorization());
                            return springPowerOfAttorneyRepository.save(doc).map(saved ->
                                            PowerOfAttorney.builder()
                                                           .authorization(saved.getAuthorization())
                                                           .grantorName(saved.getGrantorName())
                                                           .granteeName(saved.getGranteeName())
                                                           .account(accountDocument.getAccount())
                                                           .build());
                        });
    }

}
