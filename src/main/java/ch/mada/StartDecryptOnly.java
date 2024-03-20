package ch.mada;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;

public class StartDecryptOnly {
    public static void main(String[] args) throws IOException {
        Path fileToDecrypt = Path.of("src/chiffre.txt");
        BigInteger[] dn = DecryptHelper.readKeyFile("src/sk.txt");
        List<Character> characterList = DecryptHelper.readAndDecrypt(fileToDecrypt, dn[0], dn[1]);
        List<String> lines = DecryptHelper.saveLinesToList(characterList);
        DecryptHelper.saveToFile(lines);
    }
}
