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

        //Menge an GGTs von phiOfn
        BigInteger e = calculateE(nPHI);
        System.out.println("E: " + e);

        //d berechnen Mithilfe des euklidischen Algorithmus
        BigInteger d = calculateD(nPHI, e);
        System.out.println("D: " + d);

        // d,n in sk File schreiben
        try (PrintWriter writer = new PrintWriter("src/sk.txt", StandardCharsets.UTF_8)) {
            writer.print(d + "," + n);
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
            for (Character c : characterList) {
                BigInteger charAsBigInt = BigInteger.valueOf(c);
                BigInteger encryptedChar = encrypt(e, charAsBigInt, n);
                writer.print(encryptedChar + ",");
            }
        }

        BigInteger[] dn = readSkFile();
        List<Character> characterList = readAndDecrypt(fileToDecrypt, dn[0], dn[1]);

        // Zeilen in Liste speichern
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (Character c : characterList) {
            sb.append(c);
            if (c.equals('\r')) {
                lines.add(sb.toString());
                sb = new StringBuilder();
            }
        }

        // Entschlüsselten Inhalt in Datei schreiben
        try (PrintWriter writer = new PrintWriter("src/text-d.txt", StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.println(line);
            }
        }
    }

    //Verschlüsselter Inhalt auslesen und entschlüsseln
    private static List<Character> readAndDecrypt(Path fileToDecrypt, BigInteger d, BigInteger n) throws IOException {
        List<Character> characterList = new ArrayList<>();
        try(Scanner scanner = new Scanner(fileToDecrypt)) {
            BigInteger lineAsValue;
            while (scanner.hasNextLine()) {
                for (String input : scanner.nextLine().split(",")) {
                    lineAsValue = new BigInteger(input);
                    System.out.println("Line as value: " + lineAsValue);
                    BigInteger result = lineAsValue.modPow(d, n);
                    System.out.println("Result char Number: " + result);
                    characterList.add((char) Integer.parseInt(result.toString()));
                }
            }
        }
        return characterList;
    }

    // n, d aus sk File lesen
    private static BigInteger[] readSkFile() throws FileNotFoundException {
        BigInteger n;
        BigInteger d;
        try(Scanner scanner = new Scanner(new File("src/sk.txt"))) {
            String[] sk = scanner.nextLine().split(",");
            d = new BigInteger(sk[0]);
            n = new BigInteger(sk[1]);
        }
        return new BigInteger[]{d, n};
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
        //char darf nicht grösser als 32 sein...Fehler
        BigInteger h = charToEncrypt.modPow(e,n);
        System.out.println("Char to encrypt: "+ charToEncrypt);
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