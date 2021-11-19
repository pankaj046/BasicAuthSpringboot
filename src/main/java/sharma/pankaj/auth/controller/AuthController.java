package sharma.pankaj.auth.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sharma.pankaj.auth.dto.LoginRequest;
import sharma.pankaj.auth.dto.LoginResponse;
import sharma.pankaj.auth.dto.RegisterRequest;
import sharma.pankaj.auth.dto.RegisterResponse;
import sharma.pankaj.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(authService.register(request));
    }

    @RequestMapping(value="/accountVerification", method = RequestMethod.GET)
    public ResponseEntity<RegisterResponse> verify(@RequestParam("key") String key){
        return ResponseEntity.status(HttpStatus.OK).body(authService.verify(key));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }
}
