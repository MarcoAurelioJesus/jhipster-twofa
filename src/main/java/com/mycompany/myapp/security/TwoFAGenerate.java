package com.mycompany.myapp.security;

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base32;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Hex;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.mycompany.myapp.service.dto.AdminUserDTO;
import de.taimos.totp.TOTP;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    public static void generateQRCodeTwoFA(AdminUserDTO userDTO) throws WriterException, IOException {
        System.out.println("generateQRCodeTwoFA" + userDTO.getEmail() + "-" + userDTO.getLogin());
        String barCodeUrl = getGoogleAuthenticatorBarCode(userDTO);
        System.out.println(barCodeUrl);
        createQRCode(barCodeUrl, "./src/main/webapp/images/" + userDTO.getLogin() + "_QRCode.png", 400, 400);
    }

    public void validationCodeTwoFA(String code, String secretKey) {
        if (code.equals(Utils.getTOTPCode(secretKey))) {
            System.out.println("Logged in successfully");
        } else {
            System.out.println("Invalid 2FA Code");
        }
    }

    public static String getGoogleAuthenticatorBarCode(AdminUserDTO userDTO) {
        try {
            return (
                "otpauth://totp/" +
                URLEncoder.encode(userDTO.getEmail() + ":" + userDTO.getLogin(), "UTF-8").replace("+", "%20") +
                "?fistname=" +
                URLEncoder.encode(userDTO.getFirstName(), "UTF-8").replace("+", "%20") +
                "&lastname=" +
                URLEncoder.encode(userDTO.getLastName(), "UTF-8").replace("+", "%20") +
                "&authorities=" +
                URLEncoder.encode(userDTO.getAuthorities().toString(), "UTF-8").replace("+", "%20")
            );
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void createQRCode(String barCodeData, String filePath, int height, int width) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        try {
            FileOutputStream out = new FileOutputStream(filePath);
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void infinityGeneratingCodes(String secretKey) {
        String lastCode = null;
        while (true) {
            String code = getTOTPCode(secretKey);
            if (!code.equals(lastCode)) {
                System.out.println(code);
            }
            lastCode = code;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
    }
}
