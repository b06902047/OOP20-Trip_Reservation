package travel;

public class User {
	
	private String username;
	private String password;
	private DBConnect connection;
	
	/**
	 * @param username
	 * @param password
	 */
	public User(String username, String password) {
		this.username = username;
		this.password = password;
		this.connection = new DBConnect();
	}

	public boolean LogIn() {
		
		if (connection.queryUser(username, password)) {
			System.out.println("Log in successfully!");
			return true;
		}
		return false;
	}
	
	public boolean Cancel(String order) {
		
		if(connection.deleteEntry(order)) {
			System.out.println("Order Canceled");
			return true;
		}
		else {
			System.out.println("Order ID not found");
			return false;
		}
	}
	
	public boolean Modify(String order, int adults, int children) {
		if (connection.changeGeustNumber(order, adults, children)) {
			System.out.println(String.format("Guests changed to %d adults and %d children", adults, children));
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean getOrderInfo(String order) {
		if (connection.queryOrder(order)) {
			return true;
		}
		else {
			return false;
		}
	}
	
}









