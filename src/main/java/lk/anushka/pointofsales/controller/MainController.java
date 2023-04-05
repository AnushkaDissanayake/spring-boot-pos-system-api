package lk.anushka.pointofsales.controller;

import lk.anushka.pointofsales.dto.ChangePasswordDTO;
import lk.anushka.pointofsales.entity.UserEntity;
import lk.anushka.pointofsales.repo.UserRepo;
import lk.anushka.pointofsales.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/pos")
public class MainController {
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public MainController(PasswordEncoder passwordEncoder, UserRepo userRepo, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo =userRepo;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(changePasswordDTO.getUsername(),changePasswordDTO.getOldPassword()));
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid credentials",HttpStatus.BAD_REQUEST);
        }
        UserEntity user = userRepo.findByUsername(changePasswordDTO.getUsername()).orElseThrow();
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepo.save(user);
        return new ResponseEntity<>("Password change success full",HttpStatus.OK);
    }
}
