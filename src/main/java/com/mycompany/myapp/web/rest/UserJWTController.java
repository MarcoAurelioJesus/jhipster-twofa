package com.mycompany.myapp.web.rest;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.zxing.WriterException;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.TwoFAGenerate;
import com.mycompany.myapp.security.jwt.JWTFilter;
import com.mycompany.myapp.security.jwt.TokenProvider;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.AdminUserDTO;
import com.mycompany.myapp.web.rest.vm.LoginVM;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import java.io.IOException;
import java.util.*;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {

    @Autowired
    private QrGenerator qrGenerator;

    @Autowired
    private QrDataFactory qrDataFactory;

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserRepository userRepository;

    public UserJWTController(
        TokenProvider tokenProvider,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        UserService userService,
        UserRepository userRepository
    ) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userRepository = userRepository;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );
        Optional<User> existingUser = userRepository.findOneByLogin(loginVM.getUsername());
        String keyQrCode = (existingUser.get().getEmail() + existingUser.get().getPassword()).replaceAll("[^a-zZ]", "").toUpperCase();
        HttpHeaders httpHeaders = new HttpHeaders();
        if (!TwoFAGenerate.validationCodeTwoFA(loginVM.getTwofacode(), keyQrCode)) {
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Invalid Code!");
            return new ResponseEntity<>(httpHeaders, HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, loginVM.isRememberMe());

        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

    @PostMapping("/twofa")
    public ResponseEntity<User> twoFA(@RequestBody LoginVM loginVM) throws WriterException, IOException, QrGenerationException {
        Optional<User> existingUser = userRepository.findOneByLogin(loginVM.getUsername());
        existingUser = userRepository.findOneByLogin(loginVM.getUsername().toLowerCase());
        String keyQrCode = (existingUser.get().getEmail() + existingUser.get().getPassword()).replaceAll("[^a-zZ]", "").toUpperCase();
        QrData data = qrDataFactory.newBuilder().label(existingUser.get().getEmail()).secret(keyQrCode).issuer("Company").build();
        existingUser.get().setImageUrl(getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType()));

        return ResponseUtil.wrapOrNotFound(
            existingUser,
            HeaderUtil.createAlert(applicationName, "userManagement.twofa", existingUser.get().getImageUrl())
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<AdminUserDTO> verify(@Valid @RequestBody AdminUserDTO userDTO) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        String keyQrCode = (existingUser.get().getEmail() + existingUser.get().getPassword()).replaceAll("[^a-zZ]", "").toUpperCase();
        if (!TwoFAGenerate.validationCodeTwoFA(userDTO.getTwoFACode(), keyQrCode)) {
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Invalid Code!");
            return new ResponseEntity<>(httpHeaders, HttpStatus.BAD_REQUEST);
        }
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "TwoFaCode");
        return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
    }
}
