package ch.mada;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        //File Paths
        Path fileToEncrypt = Path.of("src/text.txt");
        Path fileToDecrypt = Path.of("src/chiffre.txt");

        Random random = new Random();

        // Generate Random Prime Number
        BigInteger p = BigInteger.probablePrime(2048, random);
        BigInteger q = BigInteger.probablePrime(2048, random);

        // p und q multiplizieren und n erhalten
        BigInteger n = p.multiply(q);

        //PHI von n berechnen
        BigInteger nPHI = phiOfn(p,q);
        System.out.println("PHI von n: " + nPHI);

        //Ein GGT von phiOfn
        BigInteger e = calculateE(nPHI);
        System.out.println("E: " + e);

        //d berechnen Mithilfe des euklidischen Algorithmus
        BigInteger d = calculateD(nPHI, e);
        System.out.println("D: " + d);

        // n,e in pk File schreiben
        try (PrintWriter writer = new PrintWriter("src/pk.txt", StandardCharsets.UTF_8)) {
            writer.print(n + "," + e);
        }

        // d,n in sk File schreiben
        try (PrintWriter writer = new PrintWriter("src/sk.txt", StandardCharsets.UTF_8)) {
            writer.print(n + "," + d);
        }

        //Inhalt einlesen und verschlüsseln
        try(Scanner scanner = new Scanner(fileToEncrypt);
            PrintWriter writer = new PrintWriter("src/chiffre.txt", StandardCharsets.UTF_8)) {
            List<Character> characterList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                char[] chars = scanner.nextLine().toCharArray();
                for (char c : chars) {
                    characterList.add(c);
                }
                characterList.add('\r');
            }

            BigInteger[] en = DecryptHelper.readKeyFile("src/pk.txt");
            for (Character c : characterList) {
                BigInteger charAsBigInt = BigInteger.valueOf(c);
                BigInteger encryptedChar = encrypt(en[1], charAsBigInt, en[0]);
                writer.print(encryptedChar + ",");
            }
        }

        // Verschlüsselten Inhalt auslesen und entschlüsseln
        BigInteger[] dn = DecryptHelper.readKeyFile("src/sk.txt");
        List<Character> characterList = DecryptHelper.readAndDecrypt(fileToDecrypt, dn[1], dn[0]);

        // Entschlüsselten Inhalt in Datei schreiben
        List<String> lines = DecryptHelper.saveLinesToList(characterList);
        DecryptHelper.saveToFile(lines);
    }

    public static BigInteger phiOfn(BigInteger p, BigInteger q) {
        // p und q müssen Primzahlen sein
        // (p-1)*(q-1) = PHI von n
        p = p.subtract(BigInteger.ONE);
        q = q.subtract(BigInteger.ONE);
        return p.multiply(q);
    }

    public static BigInteger calculateE (BigInteger nPHI) {
        BigInteger random = generateRandomBigIntBetween(BigInteger.valueOf(0), nPHI.subtract(BigInteger.ONE));

        while (!nPHI.equals(random)) {
            if (nPHI.gcd(random).equals(BigInteger.valueOf(1))) {
                return random;
            }
            random = random.add(BigInteger.ONE);
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
        if(y0.compareTo(BigInteger.ZERO) <= 0) {
            y0 = y0.add(nPHI);
        }
        System.out.println("e*d mod phi(n): " + (e.multiply(y0)).mod(nPHI));
        return y0;
    }

    public static BigInteger encrypt(BigInteger e, BigInteger charToEncrypt, BigInteger n) {
        System.out.println("Char to encrypt: "+ charToEncrypt);
        //Inhalt zu binär
        String binary = e.toString(2);

        //Initialisierung
        int i = binary.length() - 1;
        BigInteger k = charToEncrypt;
        BigInteger h = BigInteger.ONE;

        //Iteriertes Quadrieren
        while (i >= 0) {
            if(binary.charAt(i) == '1') {
                h = h.multiply(k).mod(n);
            }
            k = k.pow(2).mod(n);
            i = i - 1;
        }
        System.out.println("Encrypted char: " + h);
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