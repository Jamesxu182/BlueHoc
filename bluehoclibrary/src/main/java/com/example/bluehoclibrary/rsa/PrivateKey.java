package com.example.bluehoclibrary.rsa;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by james on 3/7/17.
 */

public class PrivateKey {
    private BigInteger d;
    private BigInteger N;

    public PrivateKey(BigInteger d, BigInteger N) {
        this.d = d;
        this.N = N;
    }

    public PrivateKey(JSONObject object) {
        try {
            this.d = new BigInteger(object.getString("d"));
            this.N = new BigInteger(object.getString("N"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger getN() {
        return N;
    }
}
