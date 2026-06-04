package com.metro.ticket.controller;

import com.metro.ticket.model.User;
import com.metro.ticket.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserRepository userRepository;

    public UserAuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * =====================
     * SEND OTP (CONSOLE)
     * ======================
     */
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String mobile) {

        User user = userRepository.findByMobile(mobile)
                .orElseGet(() -> {
                    User u = new User();
                    u.setMobile(mobile);
                    u.setVerified(false);
                    return u;
                });

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        user.setOtp(otp);
        user.setOtpCreatedAt(LocalDateTime.now()); // ✅ timestamp
        userRepository.save(user);

        // ✅ OPTION A — CONSOLE OTP
        System.out.println("OTP for " + mobile + " = " + otp);

        return "OTP sent (check server console)";
    }

    /*
     * =====================
     * VERIFY OTP
     * ======================
     */
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String mobile,
            @RequestParam String otp,
            HttpSession session) {

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        // OTP missing
        if (user.getOtp() == null || user.getOtpCreatedAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not generated");
        }

        // OTP expired (5 minutes)
        if (user.getOtpCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            user.setOtp(null);
            userRepository.save(user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        // OTP mismatch
        if (!otp.equals(user.getOtp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        // ✅ SUCCESS
        user.setVerified(true);
        user.setOtp(null);
        user.setOtpCreatedAt(null);
        userRepository.save(user);

        session.setAttribute("USER", user.getId());
        session.setAttribute("MOBILE", mobile);
        return "OTP verified";
    }
}