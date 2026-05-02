package com.estacionamiento.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    
    public static String hash(String passwordPlana) {
        return BCrypt.hashpw(passwordPlana, BCrypt.gensalt());
    }

    
    public static boolean verificar(String passwordPlana, String hashBD) {
        return BCrypt.checkpw(passwordPlana, hashBD);
    }
}