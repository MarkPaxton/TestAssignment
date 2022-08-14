package nl.rabobank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.authorizations.Authorization;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowerOfAttorneyAuthorizationDto {
    String accountNumber;
    String grantTo;
    Authorization authorization;
}
