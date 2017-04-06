package com.example.bluehoclibrary.rsa;

/**
 * Created by james on 3/7/17.
 */

import java.io.DataInputStream;

import java.io.IOException;

import java.math.BigInteger;

import java.util.Random;



public class RSA
{
    private BigInteger p;

    private BigInteger q;

    private BigInteger N;

    private BigInteger phi;

    private BigInteger e;

    private BigInteger d;

    private int bitlength = 128;

    private Random r;

    public RSA()
    {
        r = new Random();

        p = BigInteger.probablePrime(bitlength, r);

        q = BigInteger.probablePrime(bitlength, r);

        N = p.multiply(q);

        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        e = BigInteger.probablePrime(bitlength / 2, r);

        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0)
        {
            e.add(BigInteger.ONE);
        }

        d = e.modInverse(phi);
    }

    public RSA(BigInteger e, BigInteger d, BigInteger N)
    {
        this.e = e;
        this.d = d;
        this.N = N;
    }

    public RSA(PublicKey publicKey) {
        this.e = publicKey.getE();
        this.N = publicKey.getN();
    }

    public RSA(PrivateKey privateKey) {
        this.d = privateKey.getD();
        this.N = privateKey.getN();
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();

        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    // Encrypt message
    public byte[] encrypt(byte[] message)
    {
        return (new BigInteger(message)).modPow(e, N).toByteArray();
    }

    // Decrypt message
    public byte[] decrypt(byte[] message)
    {
        return (new BigInteger(message)).modPow(d, N).toByteArray();
    }

    public PublicKey getPublicKey() {
        return new PublicKey(e, N);
    }

    public PrivateKey getPrivateKey() {
        return new PrivateKey(d, N);
    }

}