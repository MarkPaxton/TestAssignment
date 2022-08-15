package nl.rabobank.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.Account;
import nl.rabobank.services.DebugAccountService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

/**
 * This controller is provided entirely for convenience to demonstrate the application.
 * There are some options for this for production:
 *  - delete it or remove the controller tag
 *  - use an application profiles to enable it only in 'test' or 'production'
 *  - change the security settings to allow only tokens with a given role/claim
 *  - use upstream ngnix to block the account url
 *  - use an environment var / feature flag
 */
@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {
    private final DebugAccountService debugAccountService;

    public AccountController(DebugAccountService debugAccountService) {
        this.debugAccountService = debugAccountService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer {access_token}")
    @ApiOperation("Create an account")
    public Mono<ResponseEntity<Account>> createAccount(@ApiIgnore @AuthenticationPrincipal Jwt jwt, @ApiParam(value = "Account number", example = "123456", defaultValue = "123456") @RequestParam String accountNumber,
                 @ApiParam(value = "Account type: payment | savings", defaultValue = "payment")   @RequestParam String accountType) {
        log.info("createAccount user# {} / {} / {}", jwt.getSubject().hashCode(), accountNumber, accountType);
        return debugAccountService.createAccount(jwt.getSubject(), accountNumber, accountType)
                                  .switchIfEmpty(Mono.error((new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR))))
                                  .map(a -> ResponseEntity.status(HttpStatus.CREATED).body(a));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List accounts", notes = "AccountHolderName is populated by the Authorization header JWT sub (subject) claim")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer {access_token}")
    public Flux<Account> listAccounts(@ApiIgnore @AuthenticationPrincipal Jwt jwt) {
        return debugAccountService.findAll();
    }
}
