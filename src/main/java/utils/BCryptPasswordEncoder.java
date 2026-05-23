package utils;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordEncoder implements PasswordEncoder {
	@Override
	public String encode(String rawPassword) {
		if (rawPassword == null) {
			return null;
		}
		return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
	}

	@Override
	public boolean matches(String rawPassword, String encodedPassword) {
		if (rawPassword == null || encodedPassword == null) {
			return false;
		}
		try {
			return BCrypt.checkpw(rawPassword, encodedPassword);
		} catch (Exception e) {
			return false;
		}
	}
}
