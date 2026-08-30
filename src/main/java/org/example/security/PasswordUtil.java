package org.example.security;

public class PasswordUtil {

    public static String hashPassword(String password) {
        return password; // no hashing, store as plain text
    }

    public static boolean verifyPassword(String password, String storedPassword) {
        return password.equals(storedPassword); // direct comparison
    }
}