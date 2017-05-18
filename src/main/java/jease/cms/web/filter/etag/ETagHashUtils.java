package jease.cms.web.filter.etag;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ETagHashUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ETagHashUtils.class);
    private static final String ALGORITHM_NAME = "MD5";
    private static final String EMPTY_STRING = "";

    public static String getMd5Digest(byte[] bytes, String resourcePath) {

        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM_NAME);

            StringBuilder hashBuilder = new StringBuilder();
            if (resourcePath != null) {
                hashBuilder.append(digestBytes(md, resourcePath.getBytes()));
            }
            hashBuilder.append(digestBytes(md, bytes));

            return hashBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("No algorithm " + ALGORITHM_NAME + "  exception");
            return EMPTY_STRING;
        }
     }

    private static String digestBytes(MessageDigest md, byte[] byteArray) {
        byte[] messageDigest = md.digest(byteArray);
        BigInteger number = new BigInteger(1, messageDigest);
        return number.toString(16);
    }
}
