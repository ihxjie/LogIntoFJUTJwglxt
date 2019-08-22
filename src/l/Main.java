package l;

public class Main {

	public static void main(String[] args) {
		String user = "";
		String password = "";

		Connect connect = new Connect(user,password);
		connect.webvpn_link();
		connect.getCourse();
		//connect.link();

	}

}
