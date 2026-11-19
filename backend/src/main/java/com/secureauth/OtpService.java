package com.secureauth;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Service
public class OtpService {
 private record Challenge(String hash,String name,Instant issued,int attempts){}
 private final HashMap<String,Challenge> challenges=new HashMap<>();
 private final SecureRandom random=new SecureRandom();private final BCryptPasswordEncoder bcrypt;private final EmailService email;
 public OtpService(BCryptPasswordEncoder bcrypt,EmailService email){this.bcrypt=bcrypt;this.email=email;}
 public synchronized void request(String address,String name){var now=Instant.now();challenges.entrySet().removeIf(e->e.getValue().issued().plusSeconds(300).isBefore(now));var old=challenges.get(address);if(old!=null&&old.issued().plusSeconds(60).isAfter(now))throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Wait 60 seconds before requesting another code");if(challenges.size()>=1000&&old==null)throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Try again later");String code=String.format("%06d",random.nextInt(1000000));try{email.send(address,code);}catch(Exception e){throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Email delivery failed. Try again later");}challenges.put(address,new Challenge(bcrypt.encode(code),name,now,0));}
 public synchronized String verify(String address,String code){var c=challenges.get(address);if(c==null||!c.issued().plusSeconds(300).isAfter(Instant.now())||c.attempts()>=5)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Code expired or attempts exhausted. Request a new code.");if(!bcrypt.matches(code,c.hash())){challenges.put(address,new Challenge(c.hash(),c.name(),c.issued(),c.attempts()+1));throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid code");}challenges.remove(address);return c.name();}
}
