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
import com.mycompany.myapp.web.rest.errors.EmailAlreadyUsedException;
import com.mycompany.myapp.web.rest.errors.LoginAlreadyUsedException;
import com.mycompany.myapp.web.rest.vm.LoginVM;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import java.io.IOException;
import java.util.*;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserService userService;

    private final UserRepository userRepository;

    public UserJWTController(
        TokenProvider tokenProvider,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        UserService userService,
        UserRepository userRepository
    ) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, loginVM.isRememberMe());
        HttpHeaders httpHeaders = new HttpHeaders();
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
    public ResponseEntity<AdminUserDTO> twoFA(@Valid @RequestBody AdminUserDTO userDTO)
        throws WriterException, IOException, QrGenerationException {
        String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";
        log.debug("REST twoFA to User : {}", userDTO);
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }
        Optional<AdminUserDTO> updatedUser = userService.updateUser(userDTO);
        QrData data = qrDataFactory.newBuilder().label(userDTO.getEmail()).secret(secretKey).issuer("Company").build();
        userDTO.setImageUrl(getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType()));
        updatedUser.get().setImageUrl(getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType()));
        updatedUser.get().setIsImageQRCode(true);
        return ResponseUtil.wrapOrNotFound(
            updatedUser,
            HeaderUtil.createAlert(applicationName, "userManagement.twofa", userDTO.getImageUrl())
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody String code) {
        log.debug("REST twoFA verify code =: {}", code);
        HttpHeaders httpHeaders = new HttpHeaders();
        if (!TwoFAGenerate.validationCodeTwoFA(code, "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK")) {
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Invalid Code!");
            return new ResponseEntity<>(httpHeaders, HttpStatus.BAD_REQUEST);
        }
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "TwoFaCode");
        return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
    }
}
