
public class SendEmails {

	public static boolean isValidEmail(String email) {
		return (email.indexOf("@") > 0) && (email.indexOf(".") > 2);
	}
	
}