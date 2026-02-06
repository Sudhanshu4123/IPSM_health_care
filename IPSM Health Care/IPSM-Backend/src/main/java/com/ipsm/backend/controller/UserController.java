package com.ipsm.backend.controller;

import com.ipsm.backend.entity.User;
import com.ipsm.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAllUsers() {
        // In a real app, you might want to hide passwords even if hashed
        return userRepository.findAll();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Ensure permissions are not null (default to false or true based on logic if
        // not sent, but entity handles defaults)
        // Entity defaults should work if JSON doesn't include them, but usually JSON
        // includes false for missing booleans.

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody User userDetails) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = optionalUser.get();

        // Only update password if it's new (simple check: if it differs from current
        // hash, but we receive plain text)
        // Logic: client sends password field. If it's valid plain text, we hash it.
        // NOTE: This assumes client sends the *new* password text.
        // If client sends the *hashed* password back (from get), re-hashing it would
        // break it.
        // Strategy: Client asks to update. If password field is present and not empty,
        // hash it.

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            // Check if it's already a hash (starts with $2a$). Not perfect but safer.
            // Better: Client should send specific flag or we always hash if it looks like a
            // crude password.
            // Given the context: The UI fields usually have the password.
            // If the UI sends the *existing* password (which acts as a placeholder), we
            // need to know.
            // However, `UserManagementFrame` currently clears the password field or shows
            // it.
            // Actually `loadUserToForm` pulls the password from DB.
            // If we fetch via API, we get the HASH.
            // If we put the HASH in the password field, and then "Update", we shouldn't
            // re-hash the hash.
            // FIX: The `UserManagementFrame` UI usually shouldn't show the password. It
            // should validly be empty for "no change".

            // For now, to keep it simple and consistent with "deployment ready" fix:
            // We will assume if the password passed doesn't match the existing hash, it's a
            // new plain password.
            if (!passwordEncoder.matches(userDetails.getPassword(), existingUser.getPassword())) {
                // It's not the correct password, so it must be a new one (or the hash itself if
                // logic is weird).
                // Actually `matches` checks plain vs hash.
                // If `userDetails.getPassword()` is the HASH, matches returns false (usually).
                // If `userDetails.getPassword()` is a raw password, matches returns true if
                // it's the same.

                // If user didn't change password, he might send the old plain password?
                // No, we can't get old plain password from DB.

                // Safe bet: If `userDetails.getPassword()` looks like a BCrypt hash, assume no
                // change.
                if (!userDetails.getPassword().startsWith("$2a$")) {
                    existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                }
            }
        }

        existingUser.setRole(userDetails.getRole());
        existingUser.setDepartment(userDetails.getDepartment());
        existingUser.setStaffId(userDetails.getStaffId());
        // Update permissions
        existingUser.setRegNew(userDetails.getRegNew());
        existingUser.setRegEdit(userDetails.getRegEdit());
        existingUser.setRegManage(userDetails.getRegManage());
        existingUser.setInvStatus(userDetails.getInvStatus());
        existingUser.setInvReprint(userDetails.getInvReprint());
        existingUser.setRepOutstanding(userDetails.getRepOutstanding());
        existingUser.setRepSummary(userDetails.getRepSummary());
        existingUser.setRepLedger(userDetails.getRepLedger());
        existingUser.setRepBusiness(userDetails.getRepBusiness());
        existingUser.setRepSales(userDetails.getRepSales());
        existingUser.setTestStatus(userDetails.getTestStatus());

        userRepository.save(existingUser);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        if ("admin".equalsIgnoreCase(username)) {
            return ResponseEntity.badRequest().body("Cannot delete default admin");
        }
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return ResponseEntity.ok("User deleted");
        }
        return ResponseEntity.notFound().build();
    }
}
