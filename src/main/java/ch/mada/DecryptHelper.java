package ch.mada;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DecryptHelper {
    //Verschlüsselter Inhalt auslesen und entschlüsseln
    public static List<Character> readAndDecrypt(Path fileToDecrypt, BigInteger d, BigInteger n) throws IOException {
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

    // b, a aus File auslesen
    public static BigInteger[] readKeyFile(String locationToKeyFile) throws FileNotFoundException {
        BigInteger a;
        BigInteger b;
        try(Scanner scanner = new Scanner(new File(locationToKeyFile))) {
            String[] sk = scanner.nextLine().split(",");
            b = new BigInteger(sk[0]);
            a = new BigInteger(sk[1]);
        }
        return new BigInteger[]{b, a};
    }

    // Entschlüsselten Inhalt in Datei schreiben
    public static void saveToFile(List<String> lines) throws IOException {
        try (PrintWriter writer = new PrintWriter("src/text-d.txt", StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.println(line);
            }
        }
    }

    // Zeilen in Liste speichern
    public static List<String> saveLinesToList(List<Character> characterList) {
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean oneline = true;
        for (Character c : characterList) {
            sb.append(c);
            if (c.equals('\r')) {
                lines.add(sb.toString());
                sb = new StringBuilder();
                oneline = false;
            }
        }
        if (oneline) {
            lines.add(sb.toString());
        }
        return lines;
    }
}
