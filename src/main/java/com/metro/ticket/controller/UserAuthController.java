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

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String mobile) {

        User user = userRepository.findByMobile(mobile)
                .orElseGet(() -> {
                    User u = new User();
                    u.setMobile(mobile);
                    u.setVerified(false);
                    return u;
                });

        String otp = String.valueOf(
                100000 + new Random().nextInt(900000));

        user.setOtp(otp);
        user.setOtpCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        System.out.println("OTP for " + mobile + " = " + otp);

        return "OTP sent";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String mobile,
            @RequestParam String otp,
            HttpSession session) {

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "User not found"));

        if (user.getOtp() == null ||
                user.getOtpCreatedAt() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "OTP not generated");
        }

        if (user.getOtpCreatedAt()
                .isBefore(LocalDateTime.now()
                        .minusMinutes(5))) {

            user.setOtp(null);

            userRepository.save(user);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "OTP expired");
        }

        if (!otp.equals(user.getOtp())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid OTP");
        }

        user.setVerified(true);
        user.setOtp(null);
        user.setOtpCreatedAt(null);

        userRepository.save(user);

        session.setAttribute(
                "USER",
                user.getId());

        session.setAttribute(
                "MOBILE",
                mobile);

        return "OTP verified";
    }

    @PostMapping("/firebase-login")
    public String firebaseLogin(
            @RequestParam String mobile,
            HttpSession session) {

        User user = userRepository.findByMobile(mobile)
                .orElseGet(() -> {
                    User u = new User();
                    u.setMobile(mobile);
                    u.setVerified(true);
                    return userRepository.save(u);
                });

        session.setAttribute(
                "USER",
                user.getId());

        session.setAttribute(
                "MOBILE",
                mobile);

        return "SUCCESS";
    }
}