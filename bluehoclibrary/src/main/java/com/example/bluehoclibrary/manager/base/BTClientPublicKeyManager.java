package com.example.bluehoclibrary.manager.base;

import com.example.bluehoclibrary.rsa.PublicKey;

import java.util.Hashtable;

/**
 * Created by james on 3/8/17.
 */

public class BTClientPublicKeyManager {
    Hashtable<String, PublicKey> memberPublicKey;

    public BTClientPublicKeyManager() {
        memberPublicKey = new Hashtable<>();
    }

    public void addNewPublicKey(String address, PublicKey publicKey) {
        memberPublicKey.put(address, publicKey);
    }

    public PublicKey getPublicKeyWithAddress(String address) {
        return memberPublicKey.get(address);
    }

    public void clear() {
        memberPublicKey.clear();
    }
}
