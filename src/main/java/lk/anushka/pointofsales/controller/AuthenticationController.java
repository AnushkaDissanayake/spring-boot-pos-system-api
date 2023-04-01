package lk.anushka.pointofsales.controller;

import lk.anushka.pointofsales.dto.AuthResponseDTO;
import lk.anushka.pointofsales.dto.LoginDTO;
import lk.anushka.pointofsales.dto.RegisterDTO;
import lk.anushka.pointofsales.dto.RejectResponseDTO;
import lk.anushka.pointofsales.entity.Role;
import lk.anushka.pointofsales.entity.UserEntity;
import lk.anushka.pointofsales.repo.RoleRepo;
import lk.anushka.pointofsales.repo.UserRepo;
import lk.anushka.pointofsales.security.JWTokenGenerator;
import lk.anushka.pointofsales.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.*;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("api/auth")
public class AuthenticationController {
    private final String serverURL = "http://localhost:8080/api/auth";
    private AuthenticationManager authenticationManager;
    private UserRepo userRepo;
    private RoleRepo roleRepo;
    private PasswordEncoder passwordEncoder;
    private JWTokenGenerator tokenGenerator;
    private Validator validator;
    private JavaMailSender mailSender;
    @Autowired

    public AuthenticationController(AuthenticationManager authenticationManager, UserRepo userRepo, RoleRepo roleRepo,
                                    PasswordEncoder passwordEncoder, JWTokenGenerator tokenGenerator,
                                    Validator validator, JavaMailSender mailSender) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.validator = validator;
        this.mailSender = mailSender;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO){
        Set<ConstraintViolation<@Valid RegisterDTO>> violations = validator.validate(registerDTO);
        if (violations.size() != 0) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<RegisterDTO> violation : violations) {
                errorMessage.append("\n").append(violation.getMessage());
            }
            errorMessage.replace(0,1,"");
            return new ResponseEntity<>(errorMessage.toString(),HttpStatus.BAD_REQUEST);
        }
        if (userRepo.existsByUsername(registerDTO.getUsername())) {
            return new ResponseEntity<>("Username already exist.", HttpStatus.BAD_REQUEST);
        }
        UserEntity user = new UserEntity();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        Role roles = roleRepo.findByName("ADMIN").get();
        user.setRoles(Collections.singletonList(roles));
        EmailUtil email =new EmailUtil(mailSender);
        try {
            email.sendVerificationEmail(user,serverURL);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        try {
            userRepo.save(user);
        } catch (Exception m){
            return new ResponseEntity<>("Something went wrong.. try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Registration successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDTO loginDTO){
        UserEntity user = null;
        try {
            user = userRepo.findByUsername(loginDTO.getUsername()).orElseThrow();
        } catch (Exception e) {
            return new ResponseEntity<>(new RejectResponseDTO("Invalid username"),HttpStatus.BAD_REQUEST);
        }
        if (!user.isEnabled()) {
            return new ResponseEntity<>(new RejectResponseDTO("verification fail"),HttpStatus.BAD_REQUEST);
        } else {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenGenerator.generateToken(authentication);
            return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
        }
    }
    @GetMapping("/verify")
    public ResponseEntity <String> verify(@RequestParam(name = "code",required = true) String verificationCode,
                                          @RequestParam(name = "username",required = true) String username){
        UserEntity user = userRepo.findByUsername(username.trim()).orElseThrow();
        if (user.getVerificationCode().equals(verificationCode)) {
            user.setEnabled(true);
            userRepo.save(user);
            return new ResponseEntity<>("Verification Successful", HttpStatus.OK);
        }
        return new ResponseEntity<>("Verification Failed",HttpStatus.BAD_REQUEST);

    }

}
