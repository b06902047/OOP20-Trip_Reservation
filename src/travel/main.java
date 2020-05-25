package travel;

import java.util.HashMap;
import java.util.Map;

public class main {

	public static void main(String[] args) {
       
		DBConnect connection = new DBConnect();
        //connection.getTravelCode();
        //connection.getTripData();
		Map<String, Integer> json_code_mapping = connection.jsonToMap("/Users/iris8822/desktop/oop/project_data/travel_code.json");
		String target_place="捷克．波蘭．匈牙利";
		String target_start="2021-10-03";
        connection.searchTripByCode(json_code_mapping.get(target_place),target_start);
		String username = "TestUser";
		String password = "123";
		
		User usr = new User(username, password);
		
        
        
	}

}