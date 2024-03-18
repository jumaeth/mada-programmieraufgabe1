package ch.mada;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random random = new Random();
        // Generate Random Prime Number
        BigInteger p = BigInteger.probablePrime(2048, random);
        BigInteger q = BigInteger.probablePrime(2048, random);

        // p und q multiplizieren und n erhalten
        BigInteger n = p.multiply(q);

        //PHI von n berechnen
        BigInteger nPHI = phiOfn(p,q);
        System.out.println("PHI von n: " + nPHI);

        //Menge an GGTs von phiOfn
        BigInteger e = calculateE(nPHI);
        System.out.println("E: " + e);

        //d berechnen Mithilfe des euklidischen Algorithmus





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
            t = t.add(BigInteger.ONE);*/
    }

    public static BigInteger calculateE (BigInteger nPHI) {
        List<BigInteger> result = new ArrayList<>();
        BigInteger r = generateRandomBigIntBetween(BigInteger.valueOf(0), nPHI.subtract(BigInteger.ONE));


        while (!nPHI.equals(r)) {
            if (nPHI.gcd(r).equals(BigInteger.valueOf(1))) {
                return r;
            }
            r = r.add(BigInteger.ONE);
        }

        return BigInteger.valueOf(-1);

        /*while (!nPHI.equals(t)) {
            if (nPHI.gcd(t).equals(BigInteger.valueOf(1))) {
                result.add(t);
            }
            t = t.add(BigInteger.ONE);
        }
        return result; */
    }

    public static BigInteger generateRandomBigIntBetween(BigInteger min, BigInteger max) {
        Random random = new Random();
        int numBits = max.bitLength();

        BigInteger result;
        do {
            result = new BigInteger(numBits, random);
        } while (result.compareTo(min) < 0 || result.compareTo(max) > 0);

        System.out.println("Random: " + result);
        return result;
    }
}