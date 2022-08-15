package nl.rabobank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.authorizations.Authorization;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PowerOfAttorneyAuthorizationDTO {
    String accountNumber;
    String grantTo;
    Authorization authorization;
}
