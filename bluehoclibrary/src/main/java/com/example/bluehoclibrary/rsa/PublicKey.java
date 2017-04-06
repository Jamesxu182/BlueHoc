package com.example.bluehoclibrary.rsa;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by james on 3/7/17.
 */

public class PublicKey {
    private BigInteger e;
    private BigInteger N;

    public PublicKey(BigInteger e, BigInteger N) {
        this.e = e;
        this.N = N;
    }

    public PublicKey(JSONObject object) {
        try {
            this.e = new BigInteger(object.getString("E"));
            this.N = new BigInteger(object.getString("N"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getN() {
        return N;
    }
}
