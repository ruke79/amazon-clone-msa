package com.project.user-service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.user-service.dto.UserProfileDTO;
import com.project.user-service.model.Role;
import com.project.user-service.model.User;
import com.project.user-service.model.VerificationToken;
import com.project.user-service.security.request.SignupRequest;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void updateUserRole(Long userId, String roleName);

    List<User> getAllUsers();

    UserProfileDTO getUserById(Long id);

    User findByUsername(String username);

    void updateAccountLockStatus(Long userId, boolean lock);

    List<Role> getAllRoles();

    void updateAccountExpiryStatus(Long userId, boolean expire);

    void updateAccountEnabledStatus(Long userId, boolean enabled);

    void updateCredentialsExpiryStatus(Long userId, boolean expire);

    void updatePassword(Long userId, String currPassword, String password);

    // Admin
    void updatePassword(Long userId, String password);

    void generatePasswordResetToken(String email);

    void resetPassword(String token, String newPassword);

    User findByEmail(String email);

    User getUser(final String verificationToken);

    // OAuth 2.0
    User registerOAuthUser(User user);

    User registerNewUserAccount(SignupRequest request);

    void registerConfirmed(User user) throws JsonProcessingException;

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    String validateVerificationToken(String token);

    GoogleAuthenticatorKey generate2FASecret(Long userId);

    boolean validate2FACode(Long userId, int code);

    void enable2FA(Long userId);

    void disable2FA(Long userId);

    List<String> getUsersFromSessionRegistry();

    UserProfileDTO findUserWithAddresses(User user);

    UserProfileDTO findUserWithdefaultPaymentMethod(User user);

}
