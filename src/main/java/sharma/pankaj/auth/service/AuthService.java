package sharma.pankaj.auth.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharma.pankaj.auth.config.jwt.JWTTokenProviders;
import sharma.pankaj.auth.config.service.AuthUserDetails;
import sharma.pankaj.auth.dto.LoginRequest;
import sharma.pankaj.auth.dto.LoginResponse;
import sharma.pankaj.auth.dto.RegisterRequest;
import sharma.pankaj.auth.dto.RegisterResponse;
import sharma.pankaj.auth.model.ConfirmationToken;
import sharma.pankaj.auth.model.NotificationEmail;
import sharma.pankaj.auth.model.User;
import sharma.pankaj.auth.repository.RoleRepository;
import sharma.pankaj.auth.repository.UserRepository;
import sharma.pankaj.auth.repository.VerificationTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import static sharma.pankaj.auth.comman.Util.generateUserName;

@Service
@AllArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProviders jwtUtils;
    public RegisterResponse register(RegisterRequest request){
        boolean error;
        String message;
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            error = true;
            message = "Email is already register please register with different email address";
        }else {
            User user = new User();
            user.setActive(false);
            user.setUserName(generateUserName(6));
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(Instant.now());
            user.setLastUpdated(Instant.now());
            user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
            userRepository.save(user);
            String token  = generateVerificationToken(user);
            mailService.sendMail(new NotificationEmail(
                    "Please Activate your account",
                    user.getEmail(),
                    "please click on this below url to activate your account:\n\n"
                            +"http://localhost:8081/api/auth/accountVerification?key="+token
            ));
            error = false;
            message = "Your account is register successfully. Verification link sent to your email address";
        }
        return new RegisterResponse(error, message);
    }

    private String generateVerificationToken(User user)  {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(token);
        confirmationToken.setCreateAt(Instant.now());
        confirmationToken.setExpireAt(Instant.now().plus(30, ChronoUnit.MINUTES));
        confirmationToken.setUser(user);
        verificationTokenRepository.save(confirmationToken);
        return token;
    }


    @Transactional
    public RegisterResponse verify(String key) {
        String data  = key;
        RegisterResponse response = new RegisterResponse();
        if (verificationTokenRepository.findByToken(key).isPresent()){
            Optional<ConfirmationToken> confirmationToken = verificationTokenRepository.findByToken(key);
            if (confirmationToken.isPresent() && confirmationToken.get().getExpireAt().isAfter(Instant.now())){
                User user = confirmationToken.get().getUser();
                userRepository.updateData(true, true, user.getId());
                response.setError(false);
                response.setMessage("Your Account has been verified successfully");
            }else {
                response.setError(true);
                response.setMessage("verification is expired!");
            }
        }else {
            response.setError(true);
            response.setMessage("Invalid verification code");
        }
        return response;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateJwtToken(userDetails);

        LoginResponse loginResponse = new LoginResponse();
        if (authentication.isAuthenticated()){
            loginResponse.setError(false);
            loginResponse.setMessage("Login successfully");
            LoginResponse.Data data = new LoginResponse.Data();
            data.setEmail(userDetails.getUser().getEmail());
            data.setFirstName(userDetails.getUser().getFirstName());
            data.setLastName(userDetails.getUser().getLastName());
            data.setToken(token);
            loginResponse.setData(data);
        }else {
            loginResponse.setError(true);
            loginResponse.setMessage("User is not allow to login");
            loginResponse.setData(null);
        }
        return loginResponse;
    }
}
