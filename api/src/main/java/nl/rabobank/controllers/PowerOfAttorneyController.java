package nl.rabobank.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.dto.AccountListDTO;
import nl.rabobank.dto.PowerOfAttorneyAuthorizationDTO;
import nl.rabobank.exceptions.NoAccountException;
import nl.rabobank.services.PowerOfAttorneyService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Main controller to provide access to Power of Attorney API
 */
@RestController
@RequestMapping("/powerofattorney")
@Slf4j
public class PowerOfAttorneyController {
    private final PowerOfAttorneyService powerOfAttorneyService;

    public PowerOfAttorneyController(PowerOfAttorneyService powerOfAttorneyService) {
        this.powerOfAttorneyService = powerOfAttorneyService;
    }

    @ApiOperation(value = "Get all accounts user has Power of Attorney over",
                    notes = "User is identified by 'sub' claim within the JWT in the Authorization header")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
                    required = true, paramType = "header",
                    dataTypeClass = String.class,
                    example = "Bearer {access_token}")
    public Flux<AccountListDTO> getAllAccounts(@ApiIgnore @AuthenticationPrincipal Jwt jwt) {

        // Not logging userName directly
        log.debug("getAllAccounts for user# {}", jwt.getSubject().hashCode());

        return powerOfAttorneyService.getAllAccountsForName(jwt.getSubject()).map(a -> AccountListDTO.builder()
                                                                                                     .accountType(a.getT1().getClass()
                                                                                                                   .getSimpleName())
                                                                                                     .account(a.getT1()).authorization(a.getT2())
                                                                                                     .build());
    }

    @ApiOperation(value = "Create Power of Attorney over a given account",
                    notes = "User is identified by 'sub' claim within the JWT in the Authorization header. User must match the given accountHolderName of the referenced account")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
                    required = true, paramType = "header",
                    dataTypeClass = String.class,
                    example = "Bearer {access_token}")
    public Mono<ResponseEntity<Void>> setPowerOfAttorney(
                    @ApiIgnore @AuthenticationPrincipal Jwt jwt,
                    @ApiParam(value = "PowerOfAttorneyAuthorization: json object describing the Power of Attorney to create")
                    @RequestBody Mono<PowerOfAttorneyAuthorizationDTO> authorizationDto) {

        return authorizationDto.flatMap(auth -> {

            log.info("setPowerOfAttorney user# {} to user# {} permission {}", jwt.getSubject().hashCode(), auth.getGrantTo().hashCode(),
                            auth.getAuthorization());

            return powerOfAttorneyService.setAuthorization(jwt.getSubject(), auth.getAccountNumber(), auth.getGrantTo(), auth.getAuthorization())
                                         .onErrorMap(NoAccountException.class::isInstance,
                                                         e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account not found", e))
                                         .onErrorMap(e -> !(e instanceof ResponseStatusException),
                                                         e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown exception", e))
                                         .map(r -> ResponseEntity.status(HttpStatus.CREATED).build());
        });
    }
}
