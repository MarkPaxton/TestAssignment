package nl.rabobank.mongo.models;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.authorizations.Authorization;

/**#
 * MongoDB persistence wrapper for PowerOfAttorney
 */
@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowerOfAttorneyDocument {
    @Indexed
    private String grantorName;

    //@DBRef not supported
    private String accountDocumentId;

    private Authorization authorization;

    @CreatedDate
    private DateTime created;

    @Indexed
    private String granteeName;

}
