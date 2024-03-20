package ch.mada;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;

public class Decrypt {
    public static void main(String[] args) throws IOException {
        Path fileToDecrypt = Path.of("src/chiffre.txt");
        BigInteger[] dn = CryptorHelper.readSkFile();
        List<Character> characterList = CryptorHelper.readAndDecrypt(fileToDecrypt, dn[0], dn[1]);
        List<String> lines = CryptorHelper.saveLinesToList(characterList);
        CryptorHelper.saveToFile(lines);
    }
}
