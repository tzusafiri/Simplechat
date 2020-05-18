package com.example.simplechat;

import  java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class RSA {
    private static final String PUBLIC_KEY_FILE = "public.key";
    private static final String PRIVATE_KEY_FILE = "private.key";

    public static void main(String[] args) throws IOException{
           try {
               System.out.println("----------------------generate public and private key -------------");
               KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
               keyPairGenerator.initialize(2048);
               KeyPair keyPair = keyPairGenerator.generateKeyPair();
               PublicKey publicKey = keyPair.getPublic();
               PrivateKey privateKey = keyPair.getPrivate();
               
               System.out.println("----------------------Pulling out parameter that makes key pair----------------");
               KeyFactory keyFactory = KeyFactory.getInstance("RSA");
               RSAPublicKeySpec rsaPublicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
               RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
               
               System.out.println("----------------------Saving private and public key to the file---------------");
               RSA rsa = new RSA();
               rsa.saveKeys(PUBLIC_KEY_FILE, rsaPublicKeySpec.getModulus(), rsaPublicKeySpec.getPublicExponent());
               rsa.saveKeys(PRIVATE_KEY_FILE, rsaPrivateKeySpec.getModulus(), rsaPrivateKeySpec.getPrivateExponent());
               
               //Encrypt data using public key
               byte[] encryptData = rsa.encryptData{"Data to encrypt"};
               
               //Decrypt data using private key
               rsa.decryptData(encryptData);
               
           }catch (Exception e){
               
           }
    }

    private void saveKeys(String publicKeyFile, BigInteger modulus, BigInteger publicExponent) throws IOException {
          FileOutputStream fileOutputStream = null;
          ObjectOutputStream objectOutputStream = null;
          
          try {
              
              System.out.println("--------Generate file name-----------------");
              fileOutputStream = new FileOutputStream(publicKeyFile);
              objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(fileOutputStream));
              objectOutputStream.writeObject(modulus);
              objectOutputStream.writeObject(publicExponent);
              System.out.println("------------------Generated successful");
          }catch (Exception e){
              e.printStackTrace();
          }finally {
              if (objectOutputStream == null){
                  
                  objectOutputStream.close();
                  
                  if (fileOutputStream == null){
                      fileOutputStream.close();
                  }
              }
          }
    }
    private byte[] encryptData(String data) throws IOException{
           byte[] dataToEncrypt = data.getBytes();
           byte[] encryptedData = null;
           
           try {
               PublicKey publicKey = readPublicKeyFromFile(this.PUBLIC_KEY_FILE);
               Cipher cipher = Cipher.getInstance("RSA");
               encryptedData = cipher.doFinal(dataToEncrypt);
           } catch (Exception e) {
               e.printStackTrace();
           } 
           return encryptedData;
        }

    private PublicKey readPublicKeyFromFile(String publicKeyFile) {
    }

    private void decryptData(String data) throws IOException{

    }
}
