package ch.mada;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Random random = new Random();
        // Generate Random Prime Number
        BigInteger p = BigInteger.valueOf(11);
        BigInteger q = BigInteger.valueOf(3);

        // p und q multiplizieren und n erhalten
        BigInteger n = p.multiply(q);

        //PHI von n berechnen
        BigInteger nPHI = phiOfn(p,q);
        System.out.println("PHI von n: " + nPHI);

        //Menge an GGTs von phiOfn
        BigInteger e = calculateE(nPHI);
        System.out.println("E: " + e);

        //d berechnen Mithilfe des euklidischen Algorithmus
        BigInteger d = calculateD(nPHI, e);
        System.out.println("D: " + d);

        //Inhalt einlesen
        Path path = Path.of("src/text.txt");
        try(Scanner scanner = new Scanner(path);
            PrintWriter writer = new PrintWriter("src/chiffre.txt", StandardCharsets.UTF_8)) {
            char[] s;
            while (scanner.hasNextLine()) {
                s = scanner.nextLine().toCharArray();
                System.out.println(s);
                for(char c : s) {
                    BigInteger encryptedValue = encrypt(e, c, n);
                        writer.println(encryptedValue.toString());
                }
            }
        }

        //Inhalt entschlüsseln
        // System.out.println(new String(encrypted.modPow(d, n).toByteArray(), StandardCharsets.UTF_8));







    }

    public static BigInteger phiOfn(BigInteger p, BigInteger q) {
        // p und q müssen Primzahlen sein
        // (p-1)*(q-1) = PHI von n
        p = p.subtract(BigInteger.ONE);
        q = q.subtract(BigInteger.ONE);
        BigInteger n = p.multiply(q);
        return n;
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
    }


    //Erweiterter Euklidischer Algorithmus
    public static BigInteger calculateD(BigInteger nPHI, BigInteger e) {
        BigInteger a = nPHI;
        BigInteger b = e;
        BigInteger x0 = BigInteger.ONE;
        BigInteger x1 = BigInteger.ZERO;
        BigInteger y0 = BigInteger.ZERO;
        BigInteger y1 = BigInteger.ONE;
        BigInteger q;
        BigInteger r;

        while(!b.equals(BigInteger.ZERO)) {
            q = a.divide(b);
            r = a.mod(b);
            a = b;
            b = r;
            BigInteger temporaryX = x0.subtract(q.multiply(x1));
            BigInteger temporaryY = y0.subtract(q.multiply(y1));
            x0 = x1;
            y0 = y1;
            x1 = temporaryX;
            y1 = temporaryY;
        }
        return y0;
    }

    public static BigInteger encrypt(BigInteger e, int charToEncrypt, BigInteger n) {
        //Inhalt zu binär
        String binary = e.toString(2);
        System.out.println(binary);


        //Initialisierung
        int i = binary.length() - 1;
        BigInteger k = BigInteger.valueOf(charToEncrypt);
        BigInteger h = BigInteger.valueOf(1);

        //Iteriertes Quadrieren
        while (i >= 0) {
            if(binary.charAt(i) == '1') {
                h = h.multiply(k).mod(n);
            }
            k = k.pow(2).mod(n);
            i = i - 1;
            System.out.println("I: " + i + ", k: " + k + ", h: " + h);
        }
        return h;
    }

    public static BigInteger generateRandomBigIntBetween(BigInteger min, BigInteger max) {
        Random random = new Random();
        int numBits = max.bitLength();

        BigInteger result;
        do {
            result = new BigInteger(numBits, random);
        } while (result.compareTo(min) < 0 || result.compareTo(max) > 0);

        return result;
    }
}