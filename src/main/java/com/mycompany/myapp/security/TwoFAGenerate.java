package com.mycompany.myapp.security;

import com.google.zxing.WriterException;
import java.io.IOException;

public class TwoFAGenerate {

    public void generateQRCodeTwoFA(String secretKey, String userName, String email, String companyName)
            throws WriterException, IOException {
        System.out.println("generateQRCodeTwoFA" + secretKey + "-" + userName);
        String barCodeUrl = Utils.getGoogleAuthenticatorBarCode(secretKey, email, companyName, userName);
        System.out.println(barCodeUrl);
        Utils.createQRCode(barCodeUrl, "QRCode.png", 400, 400);
    }

    public void validationCodeTwoFA(String code, String secretKey) {
        if (code.equals(Utils.getTOTPCode(secretKey))) {
            System.out.println("Logged in successfully");
        } else {
            System.out.println("Invalid 2FA Code");
        }
    }
}
