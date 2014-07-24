package com.cloudmediaplus.followme.framework;

import java.util.Date;

public class TokenManager {

    private PhoneService phoneService;
    private Encryptor encryptor;
    private String privateKey = "Wol3ar!n@";

    public TokenManager(PhoneService phoneService, Encryptor encryptor){

        this.phoneService = phoneService;
        this.encryptor = encryptor;
    }

    public String CreateNewToken(Date expiryDate){
        String myNumber = phoneService.getMyNumber();
        String token = myNumber +  expiryDate.getTime();
        return encryptor.encrypt(privateKey, token);
    }

    public boolean isExiped(String encryptedToken){
        String myNumber = phoneService.getMyNumber();
        String token = encryptor.dencrypt(privateKey, encryptedToken);
        Date expiryDate = new Date(token.replace(myNumber, ""));
        return expiryDate.after(new Date());
    }
}
