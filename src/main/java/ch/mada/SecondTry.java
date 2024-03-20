package ch.mada;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SecondTry {
    public static void main(String[] args) throws IOException {

        //File Paths
        Path fileToEncrypt = Path.of("src/text.txt");
        Path fileToDecrypt = Path.of("src/chiffre.txt");

        // Generate Random Prime Number
        BigInteger p = BigInteger.valueOf(11);
        BigInteger q = BigInteger.valueOf(3);

        // p und q multiplizieren und n erhalten
        BigInteger n = p.multiply(q);

        //PHI von n berechnen
        BigInteger nPHI = phiOfn(p,q);
        System.out.println("PHI von n: " + nPHI);

        //E berechnen
        BigInteger e = calculateE(nPHI);
        System.out.println("E: " + e);

        //D berechnen mithilfe des euklidischen Algorithmus
        BigInteger d = calculateD(nPHI, e);
        System.out.println("D: " + d);

        //Chars aus File lesen und verschlüsseln
        try(Scanner scanner = new Scanner(fileToEncrypt);
            PrintWriter writer = new PrintWriter("src/chiffre.txt", StandardCharsets.UTF_8)) {
            char[] s;
            while (scanner.hasNextLine()) {
                s = scanner.nextLine().toCharArray();
                System.out.println(s);
                for(char c : s) {
                    System.out.println("Original char:" + (int) c);
                    BigInteger encryptedValue = encrypt(e, c, n);
                    writer.println(encryptedValue);
                }
            }
        }

        //Chars aus File lesen und entschlüsseln
        try(Scanner scanner = new Scanner(fileToDecrypt)) {
            while (scanner.hasNextBigInteger()) {
                BigInteger encryptedBig = scanner.nextBigInteger();
                System.out.println("Encrypted Value: " + encryptedBig); // Print encrypted value for debugging
                char original = decrypt(d, encryptedBig, n);
                System.out.println("Decrypted: " + original);
            }
        }
    }






    public static BigInteger encrypt(BigInteger e, char characterToEncrypt, BigInteger n) {
        // Konvertierung des Characters in eine BigInteger-Repräsentation
        BigInteger charBigInt = BigInteger.valueOf((int) characterToEncrypt);

        // Verschlüsselung des Characters: c = m^e mod n
        BigInteger encryptedValue = charBigInt.modPow(e, n);

        return encryptedValue;
    }


    public static char decrypt(BigInteger d, BigInteger encryptedValue, BigInteger n) {
        // Entschlüsselung des verschlüsselten Werts: m = c^d mod n
        BigInteger decryptedBigInt = encryptedValue.modPow(d, n);

        // Konvertierung der entschlüsselten BigInteger in einen Character
        String decryptedString = decryptedBigInt.toString();
        char decryptedCharacter = decryptedString.charAt(0);

        return decryptedCharacter;
    }

    public static BigInteger phiOfn(BigInteger p, BigInteger q) {
        // p und q müssen Primzahlen sein
        // (p-1)*(q-1) = PHI von n
        p = p.subtract(BigInteger.ONE);
        q = q.subtract(BigInteger.ONE);
        return p.multiply(q);
    }

    public static BigInteger calculateE (BigInteger nPHI) {
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