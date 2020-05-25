package travel;
import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.mysql.jdbc.Driver;//exceptions.MysqlDataTruncation;
import com.mysql.jdbc.MysqlDataTruncation;
/**
 * @author oliver
 * Class for connecting to MySQL database
 */
public class DBConnect {
    private Connection con = null;
    private Statement st;
    private ResultSet rs;
     
    public DBConnect() {
        try {
            //Class 的靜態 forName() 方法實現動態加載類別
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connection successful");
            //3306|MySQL開放此端口
            con= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/trip_data","root","root");
            st= con.createStatement();
             
        }catch(Exception ex){
            System.out.println("Error: "+ex);
        }
    }
    
    public void getTravelCode() {
        try {
            String query = "select * from travel_code";
            rs = st.executeQuery(query);
            System.out.println("Records for Database");
            while(rs.next()) {
                String name = rs.getString("travel_code_name");
                int code = rs.getInt("travel_code");
                System.out.println("travel_code_name =  " + name + " travel_code = " + code);
            }
        }catch(Exception ex) {
            System.out.println(ex);
        }
    }
    
    public void getTripData() {
        try {
            String query = "select * from all_trip";
            rs = st.executeQuery(query);
            System.out.println("Records for Database");
            while(rs.next()) {
                String title = rs.getString("title");
                int code = rs.getInt("travel_code");
                String key = rs.getString("product_key");
                Date start = rs.getDate("start_date");
                Date end = rs.getDate("end_date");
                int lb = rs.getInt("lower_bound");
                int ub = rs.getInt("upper_bound");
                System.out.println(title + " " + code + " " + key + " " + start + " " + end + " " + lb + " " + ub);
            }
        }catch(Exception ex) {
            System.out.println(ex);
        }
    }



    public static Map<String, Integer> jsonToMap(String jsonPath)
    {	Map<String, Integer> my_map = new HashMap<>();
        if (jsonPath != null)
        {
            try (InputStream fileStream = new FileInputStream(jsonPath))
            {
            	JSONArray json_array = new JSONArray(new JSONTokener(fileStream));
                for (int i = 0; i < json_array.length(); i++) {            
                    Integer travel_code = json_array.getJSONObject(i).getInt("travel_code");
                    String travel_code_name = json_array.getJSONObject(i).getString("travel_code_name");
                    my_map.put(travel_code_name, travel_code);
                }
            }
            catch (Exception ex)
            {
            	System.out.println(ex);
                // ignore
            }
        }
        return my_map;
    }
    
	public boolean searchTripByCode(int search_code, String search_start) {
		boolean empty=true;
		try {
            String query = String.format("SELECT * FROM all_trip WHERE travel_code=%d and start_date=\"%s\" ORDER BY price ASC,start_date ASC",search_code,search_start);
            rs = st.executeQuery(query);
            System.out.println("Records for Database");
            
            while(rs.next()) {
            	empty=false;
                String title = rs.getString("title");
                int code = rs.getInt("travel_code");
                String key = rs.getString("product_key");
                int price=rs.getInt("price");
                Date start = rs.getDate("start_date");
                Date end = rs.getDate("end_date");
                int lb = rs.getInt("lower_bound");
                int ub = rs.getInt("upper_bound");
                System.out.println(title);//title + " " + code + " " + key + " "+ price + " " + start + " " + end + " " + lb + " " + ub
                System.out.println("價格: "+price);
                System.out.println("最少出團人數: "+lb);
                System.out.println("最多出團人數: "+ub);
                System.out.println("出發日期: "+start);
                System.out.println("回台抵達日期: "+end);
                
            }
            if(empty==true)
            	System.out.println("選擇的日期無開團資訊");
        }catch(Exception ex) {
            System.out.println(ex);
        }
		return false;
	}
    
    /**
     * Query user in database
     * @param username
     * @param password
     * @return boolean 
     */
    public boolean queryUser(String username, String password) {
    	try {
    		String query = String.format("SELECT * FROM user_info WHERE username = \"%s\" and password = \"%s\"", username, password);
    		
    		rs = st.executeQuery(query);
    		if (rs.next()) {
    			return true;
    		}
    		else {
    			query = String.format("INSERT INTO user_info " + "VALUES (\"%s\", \"%s\")", username, password);
        		st.executeUpdate(query);
        		return true;
    		}
    	}
    	catch(SQLIntegrityConstraintViolationException ex) {
    		System.out.println("Duplicate username or incorrect password, try again.");
    		return false;
    	}
    	catch (MysqlDataTruncation ex) {
    		System.out.println("Username and password cannot exceed 10 characters!");
    		return false;
    	}
    	catch (Exception ex) {
    		System.out.println(ex);
    		return false;
    	}
    }
    
    public boolean deleteEntry(String order) {
    	try {
    		String query = String.format("SELECT * FROM `order` WHERE `order_id` = \"%s\"", order);
    		rs = st.executeQuery(query);
    		if (rs.next()) {
    			query = String.format("DELETE FROM `order` WHERE `order_id` = %s", order);
    			st.executeUpdate(query);
    			return true;
    		}
    		return false;
    	}
    	catch (Exception ex) {
    		System.out.println(ex);
    		return false;
    	}
    }
    
    public boolean changeGeustNumber(String order, int adults, int children) {
    	try {
    		String query = String.format("SELECT * FROM `order` WHERE `order_id` = \"%s\"", order);
    		rs = st.executeQuery(query);
    		if (rs.next()) {
    			
    			LocalDate departure = rs.getDate("departure_date").toLocalDate();
    			LocalDate today = LocalDate.now();
    			if (ChronoUnit.DAYS.between(today, departure) < 10 ) {
    				System.out.println("Can only modify order 10 days before departure");
    				return false;
    			}
    			query = String.format("UPDATE `order` SET `adults` = \'%d\', `children` = \'%d\' WHERE (`order_id` = \'%s\')", adults, children, order);
    			st.executeUpdate(query);
    			return true;
    		}
    		System.out.println("Order ID not found");
    		return false;
    	}
    	catch (Exception ex) {
    		System.out.println(ex);
    		return false;
    	}
    }
    
    public boolean queryOrder(String order) {
    	try {
    		String query = String.format("SELECT * FROM `order` WHERE `order_id` = \"%s\"", order);
    		rs = st.executeQuery(query);
    		if (rs.next()) {
    			String orderID = rs.getString("order_id");
    			Date departure = rs.getDate("departure_date");
    			Date arrival = rs.getDate("arrival_date");
    			int adults = rs.getInt("adults");
    			int children = rs.getInt("children");
    			int price = rs.getInt("price");
    			System.out.print(String.format("Order ID: %s\n%s ~ %s\nGuests: %d adults %d children\nPrice: $%d", orderID, departure, arrival, adults, children, price));
    			return true;
    		}
    		System.out.println("Order ID not found");
    		return false;
    	}
    	catch (Exception ex) {
    		System.out.println(ex);
    		return false;
    	}
    }
}
