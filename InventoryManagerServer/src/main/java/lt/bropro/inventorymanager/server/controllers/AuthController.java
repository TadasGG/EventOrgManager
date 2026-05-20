package lt.bropro.inventorymanager.server.controllers;

import lombok.RequiredArgsConstructor;
import lt.bropro.inventorymanager.server.middleware.AuthRequest;
import lt.bropro.inventorymanager.server.middleware.AuthResponse;
import lt.bropro.inventorymanager.server.middleware.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            AuthResponse response = authService.authenticate(authRequest);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println(now.format(fmt) + " User " + authRequest.getEmail() + " has logged in.");

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            // Handles invalid credentials
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "Invalid email or password"));
        } catch (Exception e) {
            // Catches any other unexpected exceptions
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}
