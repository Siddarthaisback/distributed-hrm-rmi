package org.example.security;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        String hashed = PasswordUtil.hashPassword("hr123");
        System.out.println(hashed);
    }
}