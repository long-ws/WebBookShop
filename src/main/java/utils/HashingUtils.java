package utils;

import org.mindrot.jbcrypt.BCrypt;

public class HashingUtils {
	public static String hash(String password) {
		if (password == null)
			return null;
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	public static boolean verify(String password, String hashed) {
		if (password == null || hashed == null)
			return false;
		try {
			return BCrypt.checkpw(password, hashed);
		} catch (Exception e) {
			return false;
		}

	}
}