package ch.mada;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Random random = new Random();
        // Generate Random Prime Number
        BigInteger p = BigInteger.probablePrime(2048, random);
        BigInteger q = BigInteger.probablePrime(2048, random);

        // Multiply p and q to receive n
        BigInteger n = p.multiply(q);

        //PHI von n berechnen
        BigInteger nPHI = phiOfn(p,q);


    }

    public static BigInteger phiOfn(BigInteger p, BigInteger q) {
        // p und q müssen Primzahlen sein
        // (p-1)*(q-1) = PHI von n
        p = p.subtract(BigInteger.ONE);
        q = q.subtract(BigInteger.ONE);
        BigInteger n = p.multiply(q);
        return n;


        /*List<BigInteger> result = new ArrayList<>();
        BigInteger t = BigInteger.valueOf(0);
        while(!n.equals(t)) {
            if(n.gcd(t).equals(BigInteger.valueOf(1))) {
                result.add(t);
            }
            t.add(BigInteger.ONE);*/
    }

    public static List<BigInteger> calculateE (BigInteger nPHI) {
        return null;
    }
}