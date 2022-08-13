package nl.rabobank.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.Account;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.services.AccountAccessService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import org.springframework.security.oauth2.jwt.Jwt;

@RestController @RequestMapping("/access") @Slf4j public class ApiController {
    private final Map<String, AccountAccessService> accountAccessServices;

    public ApiController(Map<String, AccountAccessService> accountAccessServices) {
        this.accountAccessServices = accountAccessServices;
    }

    private Mono<AccountAccessService> getAccountAccessServiceByType(@NonNull String accountType) {
        return Mono.justOrEmpty(accountAccessServices.get(accountType)).switchIfEmpty(Mono.defer(() -> {
            log.error("Unsupported account type {}", accountType);
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Unsupported account type '%s'", accountType)));
        }));
    }

    @GetMapping("/accounts/{accountType}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer {access_token}")
    public Flux<Account> getAccountsByType(@ApiIgnore @AuthenticationPrincipal Jwt jwt,
                    @ApiParam(name="accountType", value = "Account Type", example = "[savings | current]") @PathVariable String accountType) {
        return getAccountAccessServiceByType(accountType).flatMapMany(svc -> svc.getAllAccountsByUserName(jwt.getSubject()));
    }

    @GetMapping("/accounts")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer {access_token}")
    public Mono<Map<String, List<Account>>> getAllAccounts(@ApiIgnore @AuthenticationPrincipal Jwt jwt) {
       /* return accountAccessServices.entrySet().stream()
                                    .collect(Collectors.toMap(Map.Entry::getKey,
                                                    e -> e.getValue().getAllAccountsByUserName(getAuth().getName())
                                                          .collectList().blockOptional()
                                                          .orElse(List.of())));*/
        var l = new ArrayList<Account>();
        l.add(new SavingsAccount("123", jwt.getSubject(), 0.0));
        var m = new HashMap<String, List<Account>>();
        //m.put("test", l);
        return Mono.just(m);
    }
/*
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public Mono<User> create(@RequestBody User user){
            return accountAccessService.createUser(user);
        }



        @GetMapping("/{accountType}/{accountNumber}")
        public Mono<ResponseEntity<Account>> getAccountByAccountNumber(@PathVariable String accountType, @PathVariable String accountNumber){
            Mono<User> user = accountAccessService.findById(userId);
            return user.map( u -> ResponseEntity.ok(u))
                       .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @PutMapping("/{userId}")
        public Mono<ResponseEntity<User>> updateUserById(@PathVariable Integer userId, @RequestBody User user){
            return accountAccessService.updateUser(userId,user)
                                       .map(updatedUser -> ResponseEntity.ok(updatedUser))
                                       .defaultIfEmpty(ResponseEntity.badRequest().build());
        }

        @DeleteMapping("/{userId}")
        public Mono<ResponseEntity<Void>> deleteUserById(@PathVariable Integer userId){
            return accountAccessService.deleteUser(userId)
                                       .map( r -> ResponseEntity.ok().<Void>build())
                                       .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @GetMapping("/age/{age}")
        public Flux<User> getUsersByAge(@PathVariable int age) {
            return accountAccessService.findUsersByAge(age);
        }

        @PostMapping("/search/id")
        public Flux<User> fetchUsersByIds(@RequestBody List<Integer> ids) {
            return accountAccessService.fetchUsers(ids);
        }

        @GetMapping("/{userId}/department")
        public Mono<UserDepartmentDTO> fetchUserAndDepartment(@PathVariable Integer userId){
            return accountAccessService.fetchUserAndDepartment(userId);
        }
*/
}
