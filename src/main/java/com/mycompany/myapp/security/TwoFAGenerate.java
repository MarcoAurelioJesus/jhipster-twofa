package com.mycompany.myapp.security;

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base32;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Hex;
import de.taimos.totp.TOTP;
import java.security.SecureRandom;

public class TwoFAGenerate {

    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public static boolean validationCodeTwoFA(String code, String secretKey) {
        if (code.equals(getTOTPCode(secretKey))) {
            System.out.println("Logged in successfully");
            return true;
        }
        System.out.println("Invalid 2FA Code");
        return false;
    }
}
