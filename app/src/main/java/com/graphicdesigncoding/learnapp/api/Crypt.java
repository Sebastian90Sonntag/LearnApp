package com.graphicdesigncoding.learnapp.api;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//COPYRIGHT BY GraphicDesignCoding
public class Crypt {

    public String md5(final String s) {

        final String MD5 = "MD5";
        try {

            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String aes(String message, String username, int mode) throws Exception
    {

        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        kgen.init(256); // 192 and 256 bits may not be available

        // Generate the secret key specs.
        SecretKey skey = kgen.generateKey();
        String keyfilepath = new String(username+".key");
        File keyfile = new File(keyfilepath);
        byte[] raw = "dfd".getBytes(StandardCharsets.UTF_8); // keyfile
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        System.out.println("Key file found\n\n");

        // Instantiate the cipher
        byte[] encdecres;
        String encdecresstr = new String();
        Cipher cipher = Cipher.getInstance("AES");

        if(mode==0)
        {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encdecres= cipher.doFinal(message.getBytes());
            encdecresstr= new String(encdecres);
        }
        else if(mode==1)
        {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            encdecres = cipher.doFinal(message.getBytes());
            encdecresstr= new String(encdecres);
        }

        return encdecresstr;

    }
}
