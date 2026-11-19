package com.secureauth;
import java.time.Instant;
import java.util.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
@RestController
@RequestMapping("/api")
public class AuthController {
 private final AccountRepository accounts;private final OtpService otps;private final JwtEncoder encoder;
 public AuthController(AccountRepository accounts,OtpService otps,JwtEncoder encoder){this.accounts=accounts;this.otps=otps;this.encoder=encoder;}
 public record Request(@NotBlank @Email @Size(max=254) String email,@Size(max=100) String name){}
 public record Verification(@NotBlank @Email @Size(max=254) String email,@NotBlank @Pattern(regexp="[0-9]{6}") String code){}
 private String normalize(String email){return email.trim().toLowerCase(Locale.ROOT);}
 @PostMapping("/auth/request") public Map<String,String> request(@Valid @RequestBody Request body){String address=normalize(body.email());String name=body.name()==null?"":body.name().trim();otps.request(address,name);return Map.of("message","Code sent");}
 @PostMapping("/auth/verify") public Map<String,String> verify(@Valid @RequestBody Verification body){String address=normalize(body.email());String name=otps.verify(address,body.code());Account account=accounts.findById(address).orElseGet(()->new Account(address,name.isBlank()?"Member":name));account.verified=true;accounts.save(account);Instant now=Instant.now();var claims=JwtClaimsSet.builder().subject(address).issuedAt(now).expiresAt(now.plusSeconds(900)).build();String token=encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();return Map.of("token",token);}
 @GetMapping("/me") public Map<String,Object> profile(@AuthenticationPrincipal Jwt jwt){var a=accounts.findById(jwt.getSubject()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));return Map.of("name",a.name,"email",a.email,"verified",a.verified);}
}
