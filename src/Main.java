import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Main {


    public static boolean isPrime(BigInteger number) {
        //check via BigInteger.isProbablePrime(certainty)
        if (!number.isProbablePrime(5))
            return false;

        //check if even
        BigInteger two = new BigInteger("2");
        if (!two.equals(number) && BigInteger.ZERO.equals(number.mod(two)))
            return false;

        //find divisor if any from 3 to 'number'
        for (BigInteger i = new BigInteger("3"); i.multiply(i).compareTo(number) < 1; i = i.add(two)) { //start from 3, 5, etc. the odd number, and look for a divisor if any
            if (BigInteger.ZERO.equals(number.mod(i))) //check if 'i' is divisor of 'number'
                return false;
        }
        return true;
    }


    static public List<BigInteger> primeFactors(BigInteger number) {
        BigInteger n = number;
        BigInteger i = BigInteger.valueOf(2);
        BigInteger limit = BigInteger.valueOf(10000);
        List<BigInteger> factors = new ArrayList<BigInteger>();
        while (!n.equals(BigInteger.ONE)) {
            while (n.mod(i).equals(BigInteger.ZERO)) {
                factors.add(i);
                n = n.divide(i);
                if (isPrime(n)) {
                    factors.add(n);
                    return factors;
                }
            }
            i = i.add(BigInteger.ONE);
            if (i.equals(limit)) return factors;
        }
        System.out.println(factors);
        return factors;
    }

    static boolean isPrimeRoot(BigInteger g, BigInteger p) {
        BigInteger totient = p.subtract(BigInteger.ONE); //p-1 for primes;// factor.phi(p);
        List<BigInteger> factors = primeFactors(totient);
        int i = 0;
        int j = factors.size();
        for (; i < j; i++) {
            BigInteger factor = factors.get(i);
            BigInteger t = totient.divide(factor);
            if (g.modPow(t, p).equals(BigInteger.ONE)) return false;
        }
        return true;
    }


    static BigInteger findPrimeRoot(BigInteger p) {
        int start = 2;


        for (int i = start; i < 100000000; i++)
            if (isPrimeRoot(BigInteger.valueOf(i), p))
                return BigInteger.valueOf(i);
        return BigInteger.valueOf(0);
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        int bitLength = 25;
        SecureRandom sr = new SecureRandom();


        System.out.println("How many people will be in this conversation?");
        Scanner s = new Scanner(System.in);
        String temp = s.nextLine();
        int liczba = Integer.parseInt(temp);

        BigInteger [] secretKeys = new BigInteger[liczba];


        BigInteger prime = BigInteger.probablePrime(bitLength - 2, sr);
        BigInteger primitiveRoot = findPrimeRoot(prime);

        System.out.println(prime);
        System.out.println(primitiveRoot);

        for(int i=0;i<secretKeys.length;i++)
        {
            secretKeys[i] = new BigInteger(bitLength-2,sr);
        }

        BigInteger [] publicKeys = new BigInteger[liczba];

        for(int i=0;i<publicKeys.length;i++)
        {
            publicKeys[i] =  primitiveRoot.modPow(secretKeys[i],prime);
        }

        for(int i=0;i<secretKeys.length;i++)
        {
            System.out.println("The public key of the person "+ i + " is " + publicKeys[i]);
        }


        BigInteger [] sharedKeys = Arrays.copyOf(publicKeys,publicKeys.length);

        for(int i=0;i<sharedKeys.length;i++) {
            BigInteger[] tempo = Arrays.copyOf(sharedKeys, sharedKeys.length);
            // BigInteger [] temp1 = Arrays.copyOf(sharedKeys,sharedKeys.length);
            int pom2 = (i + 2) % liczba;
            int counter=i;
            for (int j = 1; j < liczba; j++){
                int pom = (counter + 1) % liczba;
                if(j==1){tempo[pom] = publicKeys[counter].modPow(secretKeys[pom], prime);}
                else {
                    tempo[pom]= tempo[counter].modPow(secretKeys[pom],prime);
                }
                counter++;
                if(counter==liczba){counter=0;}
            }
            sharedKeys[counter] = tempo[counter];
        }

        System.out.println();
        for(int i=0;i<secretKeys.length;i++)
        {
            System.out.println("The shared key of the person "+ i + " is " + sharedKeys[i]);
        }





        String aValue = sharedKeys[0].toString();
        String bValue = sharedKeys[1].toString();



        System.out.println();
        byte data [] = "Tojesttestowawiadomosc".getBytes("UTF-8");
        String dataS=new String(data,"UTF-8");
        System.out.println("Plain massage "+ dataS);
        byte key [] = aValue.getBytes("UTF-8");
        key = Arrays.copyOf(key,16);
        SecretKeySpec spec = new SecretKeySpec(key,"AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,spec);

        byte [] encrypted = cipher.doFinal(data);

        System.out.println();
        String encryptedS=new String(encrypted,"UTF-8");
        System.out.println("Ciphered message " + encryptedS);

        key = bValue.getBytes();

        key = Arrays.copyOf(key,16);
        SecretKeySpec spec1 = new SecretKeySpec(key,"AES");

        cipher.init(Cipher.DECRYPT_MODE,spec1);
        byte [] decrypted = cipher.doFinal(encrypted);

        System.out.println();
        String decryptedS=new String(decrypted,"UTF-8");
        System.out.println("Deciphred message with key B "+decryptedS);
    }


}
