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
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.dto.AccountListDTO;
import nl.rabobank.dto.PowerOfAttorneyAuthorizationDto;
import nl.rabobank.exceptions.NoAccountException;
import nl.rabobank.services.PowerOfAttorneyService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/powerofattorney")
@Slf4j
public class PowerOfAttorneyController {
    private final PowerOfAttorneyService powerOfAttorneyService;

    public PowerOfAttorneyController(PowerOfAttorneyService powerOfAttorneyService) {
        this.powerOfAttorneyService = powerOfAttorneyService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer {access_token}")
    public Flux<AccountListDTO> getAllAccounts(@ApiIgnore @AuthenticationPrincipal Jwt jwt) {
        // Not logging userName directly
        log.debug("getAllAccounts for user# {}", jwt.getSubject().hashCode());
        return powerOfAttorneyService.getAllAccounts(jwt.getSubject()).map(a -> new AccountListDTO(a.getT2(), a.getT1()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer {access_token}")
    public Mono<ResponseEntity<Void>> setPowerOfAttorney(@ApiIgnore @AuthenticationPrincipal Jwt jwt,
                    @RequestBody Mono<PowerOfAttorneyAuthorizationDto> authorizationDto) {
        return authorizationDto.flatMap(auth -> {
            log.debug("setPowerOfAttorney user# {} to user# {} permission {}", jwt.getSubject().hashCode(), auth.getGrantTo().hashCode(),
                            auth.getAuthorization());
            return powerOfAttorneyService.setAuthorization(jwt.getSubject(), auth.getAccountNumber(), auth.getGrantTo(), auth.getAuthorization())
                                         .onErrorMap(NoAccountException.class::isInstance,
                                                         e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account not found", e))
                                         .onErrorMap(e -> !(e instanceof ResponseStatusException),
                                                         e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown exception", e))
                                         .log()
                                         .map(r -> ResponseEntity.status(HttpStatus.CREATED).build());
        });
    }
}
