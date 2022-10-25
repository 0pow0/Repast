/**
 * 
 */
package jzombies;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author ziyizhao
 *
 */

//////////////////////////////////////////////////////////////////
//airport_node : contain all attributions for airport
class terrain_node {
	private String name;
	private double top_left_x_coordinate;
	private double top_left_y_coordinate;
	private double bottom_right_x_coordinate;
	private double bottom_right_y_coordinate;
	private double probability;
	
	public String return_terrain_area_name() {
		return name;
	}
	
	public void set_terrain_area_name(String input_name) {
		name = input_name;
	}
	
	public void set_probability(double input) {
		probability = input;
	}
	
	public double return_probability () {
		return probability;
	}
	
	public void set_top_left_x_coordinate (double input) {
		top_left_x_coordinate = input;
	}
	
	public double return_top_left_x_coordinate () {
		return top_left_x_coordinate;
	}
	
    public void set_top_left_y_coordinate (double input) {
		top_left_y_coordinate = input;
	}
	
    public double return_top_left_y_coordinate () {
		return top_left_y_coordinate;
	}
    
    public void set_bottom_right_x_coordinate (double input) {
    	bottom_right_x_coordinate = input;
	}
    
	public double return_bottom_right_x_coordinate () {
		return bottom_right_x_coordinate;
	}
	
    public void set_bottom_right_y_coordinate (double input) {
    	bottom_right_y_coordinate = input;
	}
	
    public double return_bottom_right_y_coordinate () {
		return bottom_right_y_coordinate;
	}
}
//////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////
//airport_node : contain all attributions for airport
class airport_node {
	private String airport_name;
	private double x_coordinate;
	private double y_coordinate;
	
	public airport_node () {
		this.airport_name = "";
		this.x_coordinate = 0;
		this.y_coordinate = 0;
	}
	
	public airport_node (String airport_name, double x_coordinate, double y_coordinate) {
		this.airport_name = airport_name;
		this.x_coordinate = x_coordinate;
		this.y_coordinate = y_coordinate;
	}
	
	public String return_name () {
		return airport_name;
	}
	
	public double return_x_coordinate () {
		return x_coordinate;
	}
	
    public double return_y_coordinate () {
		return y_coordinate;
	}
}
//////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////
//mission_node : contain all attributions for mission (operator)
class mission_node {
	private String mission;
	private String type;
	private String route;
	private boolean if_return;
	private double radius;
	private double duration;
	
	public mission_node() {
		this.mission = "";
		this.type = "";
		this.route = "";
		this.if_return = true;
		this.radius = Double.MIN_VALUE;
		this.duration = Double.MIN_VALUE;
	}
	
	public mission_node (String mission, String type, String route, boolean if_return, double radius, double duration) {
		this.mission = mission;
		this.type = type;
		this.route = route;
		this.if_return = if_return;
		this.radius = radius;
		this.duration = duration;
	}
	
	public String return_mission () {
		return mission;
	}
	
	public String return_type () {
		return type;
	}
	
	public String return_route () {
		return route;
	}
	
	public boolean return_if_return () {
		return if_return;
	}
	
	public double return_radius () {
		return radius;
	}
	
    public double return_duration () {
		return duration;
	}
}
//////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////
//mission_node : contain all attributions for specific mission (operation)
class specific_mission_node {
	private String mission;
	private String type;
	private String start_point;
	private String connection_point;
	private String destination_point;
	private double start_time;
	private double late_time;
	private double min_altitude;
	private double max_altitude;
	
	public specific_mission_node() {
		this.mission = "";
		this.type = "";
		this.start_point = "";
		this.connection_point = "";
		this.destination_point = "";
		this.start_time = Double.MIN_VALUE;
		this.late_time = Double.MIN_VALUE;
		this.min_altitude = Double.MIN_VALUE;
		this.max_altitude = Double.MIN_VALUE;
	}
	
	public specific_mission_node (String mission, String type, String start_point, String connection_point, String destination_point, 
			double start_time, double late_time, double min_altitude, double max_altitude) {
		this.mission = mission;
		this.type = type;
		this.start_point = start_point;
		this.connection_point = connection_point;
		this.destination_point = destination_point;
		this.start_time = start_time;
		this.late_time = late_time;
		this.min_altitude = min_altitude;
		this.max_altitude = max_altitude;
	}
	
	public String return_mission () {
	    return mission;
	}

	public String return_type () {
	    return type;
	}

	public String return_start_point () {
	    return start_point;
	}
	
	public String return_connection_point () {
	    return connection_point;
	}
	
	public String return_destination_point () {
	    return destination_point;
	}

	public double return_start_time () {
	    return start_time;
	}

	public double return_late_time () {
	    return late_time;
	}
	
	public double return_min_altitude () {
	    return min_altitude;
	}

	public double return_max_altitude () {
	    return max_altitude;
	}
}
//////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////
// Util package : provide general load/save/print function
public class Util {
	
//////////////////////////////////////////////////////////////////
// read uav target basestation from .json
	public long read_uav_count_JSON() {
		long uav_count = 0;
		JSONParser parser = new JSONParser();

		try {     
//		Object obj = parser.parse(new FileReader("./configuration/uav.json"));
			
		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);
			
		Object obj = parser.parse(new FileReader(temp_pre_fix + File.separator + "configuration" + File.separator + "uav.json"));
		JSONObject jsonObject =  (JSONObject) obj;

		uav_count = (long) jsonObject.get("uav count");
//		System.out.println(uav_count);

		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		return uav_count;
	}
	
	public boolean read_if_different_start_time_JSON() {
		
		boolean if_different_start_time = true;
		JSONParser parser = new JSONParser();

		try {     
//		Object obj = parser.parse(new FileReader("./configuration/uav.json"));	
			
			String destination = System.getProperty("user.dir");
			int last_indes = destination.lastIndexOf(File.separator);
			String temp_pre_fix = destination.substring(0, last_indes);
			
		Object obj = parser.parse(new FileReader(temp_pre_fix + File.separator + "configuration" + File.separator + "uav.json"));
		JSONObject jsonObject =  (JSONObject) obj;
		if_different_start_time = (boolean) jsonObject.get("if different start time");
//		System.out.println(if_different_start_time);
		
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		return if_different_start_time;
	}

	public long read_target_count_JSON() {
		long target_count = 0;
		JSONParser parser = new JSONParser();

		try {     
//		Object obj = parser.parse(new FileReader("./configuration/target.json"));
			
			String destination = System.getProperty("user.dir");
			int last_indes = destination.lastIndexOf(File.separator);
			String temp_pre_fix = destination.substring(0, last_indes);
			
		Object obj = parser.parse(new FileReader(temp_pre_fix + File.separator + "configuration" + File.separator + "target.json"));
		JSONObject jsonObject =  (JSONObject) obj;

		target_count = (long) jsonObject.get("target count");
//		System.out.println(target_count);

		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		return target_count;
	}
	
	public long read_basestation_count_JSON() {
		long basestation_count = 0;
		JSONParser parser = new JSONParser();

		try {     
//		Object obj = parser.parse(new FileReader("./configuration/base_station.json"));
			
			String destination = System.getProperty("user.dir");
			int last_indes = destination.lastIndexOf(File.separator);
			String temp_pre_fix = destination.substring(0, last_indes);
			
		Object obj = parser.parse(new FileReader(temp_pre_fix + File.separator + "configuration" + File.separator + "base_station.json"));
		JSONObject jsonObject =  (JSONObject) obj;

		basestation_count = (long) jsonObject.get("basestation count");
//		System.out.println(basestation_count);

		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		return basestation_count;
	}
	
	public long read_channel_count_JSON() {
		long channel_count = 0;
		JSONParser parser = new JSONParser();

		try {     
			
			String destination = System.getProperty("user.dir");
			int last_indes = destination.lastIndexOf(File.separator);
			String temp_pre_fix = destination.substring(0, last_indes);
		
		Object obj = parser.parse(new FileReader(temp_pre_fix + File.separator + "configuration" + File.separator + "base_station.json"));

		JSONObject jsonObject =  (JSONObject) obj;

		channel_count = (long) jsonObject.get("channel count");
//		System.out.println(channel_count);

		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		return channel_count;
	}
	
//////////////////////////////////////////////////////////////////
	
//////////////////////////////////////////////////////////////////
// read and print Transport Network from .json
	public long transport_network_load_policy() {
		return 0;
	}
	
	public Map<String, ArrayList<String>> transport_network_connection() {
		Map<String, ArrayList<String>> airport_connection = new HashMap<String, ArrayList<String>>();
		JSONParser parser = new JSONParser();

		try {     
//		    Object obj = parser.parse(new FileReader("./configuration/transport_network/transport_connection.json"));
			
			String destination = System.getProperty("user.dir");
			int last_indes = destination.lastIndexOf(File.separator);
			String temp_pre_fix = destination.substring(0, last_indes);
			
			Object obj = parser.parse(new FileReader(temp_pre_fix + File.separator + "configuration" + File.separator + "transport_network" + File.separator + "transport_connection.json"));
			JSONObject jsonObject =  (JSONObject) obj;

		    JSONArray connection_config = (JSONArray) jsonObject.get("Connection Config");
            // get all the airports
		    for (int i = 0; i < connection_config.size(); i++) {
		        // obtaining the i-th result
		        JSONObject connection_config_result = (JSONObject) connection_config.get(i);
		        JSONArray connections = (JSONArray) connection_config_result.get("Connection");
                // get all the airport attributions
			    for (int j = 0; j < connections.size(); j++) {
			        // obtaining the j-th result
			        JSONObject connections_result = (JSONObject) connections.get(j);
			        String start = (String) connections_result.get("start");
			        String temp_destination = (String) connections_result.get("destination");
//			        System.out.println(start);
//			        System.out.println(destination);
			        if (!airport_connection.containsKey(start))
			        {
			        	ArrayList<String> dest = new ArrayList<String> ();
			        	dest.add(temp_destination);
			        	airport_connection.put(start, dest);
			        } else {
			        	airport_connection.get(start).add(temp_destination);
			        }
			        
			    }
		    }
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		return airport_connection;
	}
	
	public Map<String, ArrayList<Double>> transport_network_config() {
		Map<String, ArrayList<Double>> airport_list = new HashMap<String, ArrayList<Double>>();
		
		JSONParser parser = new JSONParser();

		try {     
//		    Object obj = parser.parse(new FileReader("./configuration/transport_network/transport_config.json"));
			
			String destination = System.getProperty("user.dir");
			int last_indes = destination.lastIndexOf(File.separator);
			String temp_pre_fix = destination.substring(0, last_indes);
			
		    Object obj = parser.parse(new FileReader(temp_pre_fix + File.separator + "configuration" + File.separator + "transport_network" + File.separator + "transport_config.json"));
			JSONObject jsonObject =  (JSONObject) obj;

		    JSONArray trans_config = (JSONArray) jsonObject.get("Transportation Config");
            // get all the airports
		    for (int i = 0; i < trans_config.size(); i++) {
		        // obtaining the i-th result
		        JSONObject trans_config_result = (JSONObject) trans_config.get(i);
		        JSONArray airports = (JSONArray) trans_config_result.get("Location");
                // get all the airport attributions
			    for (int j = 0; j < airports.size(); j++) {
			        // obtaining the j-th result
			        JSONObject airports_result = (JSONObject) airports.get(j);
			        String airport_name = (String) airports_result.get("name");
//			        System.out.println(airport_name);
			        
			        ArrayList<Double> coordination = new ArrayList<Double>();
			        
			        // get all the geometry attributions
			        JSONArray geometry = (JSONArray) airports_result.get("geometry");
			        for (int k = 0; k < geometry.size(); k++) {
				        JSONObject geometry_result = (JSONObject) geometry.get(j);
				        JSONArray coordinates_collection = (JSONArray) geometry_result.get("coordinates");

		                // get x & y coordinate
				        for (int m = 0; m < coordinates_collection.size(); m++) {
				        	JSONObject coordinates = (JSONObject) coordinates_collection.get(m);
							double x_coordinate = (double) coordinates.get("x");
							double y_coordinate = (double) coordinates.get("y");
//					        System.out.println(x_coordinate);
//					        System.out.println(y_coordinate);
					        coordination.add(x_coordinate);
					        coordination.add(y_coordinate);
				        }
			        }
			        airport_list.put(airport_name, coordination);
			    }
		    }
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		
		return airport_list;
	}

    public void print_trainport_network_config(Map<String, ArrayList<Double>> transport_network) {
    	for (String name: transport_network.keySet()){

            String key = name.toString();
            System.out.println("Location Name : " + key);  
            System.out.println("x coordinate : " + transport_network.get(key).get(0));
            System.out.println("y coordinate : " + transport_network.get(key).get(1));

    	}
    }

    public void print_trainport_network_connection(Map<String, ArrayList<String>> connection_network) {
    	for (String name: connection_network.keySet()){
            String key = name.toString();
            for (String dest: connection_network.get(key)) {
                System.out.println("start : " + key);  
                System.out.println("destination : " + dest);
            }
    	}
    }

    public void print_start_destination_list(Map<airport_node, ArrayList<airport_node>> start_destination_list) {
    	for (airport_node start: start_destination_list.keySet()){
    		
            String start_name = start.return_name().toString();
            double start_x = start.return_x_coordinate();
            double start_y = start.return_y_coordinate();
            
            for (airport_node dest: start_destination_list.get(start)) {
            	
                System.out.println("start : " + start_name + " (" + start_x + " , " + start_y + ")"); 
                
                String dest_name = dest.return_name().toString();
                double dest_x = dest.return_x_coordinate();
                double dest_y = dest.return_y_coordinate();
                
                System.out.println("destination : " + dest_name + " (" + dest_x + " , " + dest_y + ")");
                System.out.println();
            }
    	}
    }
//////////////////////////////////////////////////////////////////
    
//////////////////////////////////////////////////////////////////
// read and print Operator(Mission) from .json
  
    // get all .json files under one specific folder
    public ArrayList<String> read_all_json_files () {
//    	File folder = new File("./configuration/missions/");
    	
		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);
    	
    	File folder = new File(temp_pre_fix + File.separator + "configuration" + File.separator + "missions" + File.separator);
    	File[] listOfFiles = folder.listFiles();
    	ArrayList<String> listOfFiles_json = new ArrayList<String>();

    	    for (int i = 0; i < listOfFiles.length; i++) {
    	      if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".json")) {
    	        //System.out.println("File " + listOfFiles[i].getName());
//    	        listOfFiles_json.add("./configuration/missions/" + listOfFiles[i].getName());
    	        listOfFiles_json.add(temp_pre_fix + File.separator + "configuration" + File.separator + "missions" + File.separator + listOfFiles[i].getName());
    	      } else if (listOfFiles[i].isDirectory()) {
    	        //System.out.println("Directory " + listOfFiles[i].getName());
    	      }
    	    }
    	return listOfFiles_json;
    }
    
    // read mission from all .json files
    public ArrayList<mission_node> load_mission_list() {
    	ArrayList<mission_node> mission_list = new ArrayList<mission_node>();
    	
    	ArrayList<String> listOfFiles_json = read_all_json_files();
    	
    	for (int file_index = 0; file_index < listOfFiles_json.size(); file_index++) {
    		JSONParser parser = new JSONParser();
    		
    		try {     
			    Object obj = parser.parse(new FileReader(listOfFiles_json.get(file_index)));
	
				JSONObject jsonObject =  (JSONObject) obj;
	
			    JSONArray mission_config = (JSONArray) jsonObject.get("Mission");
	            // get all the missions
			    for (int i = 0; i < mission_config.size(); i++) {
			        // obtaining the i-th result
			        JSONObject mission_config_result = (JSONObject) mission_config.get(i);
			        
			        if (mission_config_result.containsKey("delivery")) {
			        	
				        JSONArray delivery_missions = (JSONArray) mission_config_result.get("delivery");
				        
				        // get all the delivery missions
					    for (int j = 0; j < delivery_missions.size(); j++) {
					        // obtaining the j-th result
					        JSONObject delivery_result = (JSONObject) delivery_missions.get(j);
					        String type = (String) delivery_result.get("type");
					        String route = (String) delivery_result.get("route");
					        boolean if_return = (boolean) delivery_result.get("return");
					        double duration = (double) delivery_result.get("duration");
					        
					        // public mission_node (String mission, String type, String route, boolean if_return, double radius, double duration)
					        mission_node temp_mission_node = new mission_node("delivery",type,route,if_return,Double.MIN_VALUE,duration);
					        mission_list.add(temp_mission_node);
					    }
			        }
			        
			        if (mission_config_result.containsKey("surveillance")) {
			        	
				        JSONArray surveillance_missions = (JSONArray) mission_config_result.get("surveillance");
				        
					    // get all the surveillance missions
					    for (int k = 0; k < surveillance_missions.size(); k++) {
					        // obtaining the k-th result
					        JSONObject surveillance_result = (JSONObject) surveillance_missions.get(k);
					        String type = (String) surveillance_result.get("type");
					        String route = (String) surveillance_result.get("route");
					        double radius = (double) surveillance_result.get("radius");
					        double duration = (double) surveillance_result.get("duration");
					        
					        // public mission_node (String mission, String type, String route, boolean if_return, double radius, double duration)
					        mission_node temp_mission_node = new mission_node("surveillance",type,route,true,radius,duration);
					        mission_list.add(temp_mission_node);
					    }
			        }
			    }
			} catch (FileNotFoundException e) {
			e.printStackTrace();
			} catch (IOException e) {
			e.printStackTrace();
			} catch (ParseException e) {
			e.printStackTrace();
			}
    	}
    	return mission_list;
    }
	
    // print mission list
    public void print_mission_list(ArrayList<mission_node> mission_list) {
    	int count = 1;
    	for (mission_node temp : mission_list) {
    		 System.out.println("Mission " + count + " :");
    		 String mission = temp.return_mission().toString();
    		 String type = temp.return_type().toString();
    		 String route = temp.return_route().toString();
    		 boolean if_return = temp.return_if_return();
    		 double radius = temp.return_radius();
    		 double duration = temp.return_duration();
    		 
             System.out.println("mission : " + mission);
             System.out.println("type : " + type);
             System.out.println("route : " + route);
             System.out.println("if_return : " + if_return);
             System.out.println("radius : " + radius);
             System.out.println("duration : " + duration);
             
             System.out.println();
             count++;
    	}
    }
//////////////////////////////////////////////////////////////////
    
//////////////////////////////////////////////////////////////////
//read and print Operation(specific mission) from .json
    public specific_mission_node load_specific_operation(String mode) {
    	
    	String mission = "";
    	String type = "";
    	String start_point = "";
    	String connection_point = "";
    	String destination_point = "";
    	double start_time = Double.MIN_VALUE;
    	double late_time = Double.MIN_VALUE;
    	double min_altitude = Double.MIN_VALUE;
    	double max_altitude = Double.MIN_VALUE;
    	
    	specific_mission_node temp_specific_mission_node = new specific_mission_node();
    	
		JSONParser parser = new JSONParser();
		
		try {     
			String file_name = mode.replaceAll(" ", "_");
//		    Object obj = parser.parse(new FileReader("./configuration/operations/" + file_name + ".json"));
			
			String destination = System.getProperty("user.dir");
			int last_indes = destination.lastIndexOf(File.separator);
			String temp_pre_fix = destination.substring(0, last_indes);
			
		    Object obj = parser.parse(new FileReader(temp_pre_fix + File.separator + "configuration" + File.separator + "operations" + File.separator + file_name + ".json"));
			JSONObject jsonObject =  (JSONObject) obj;

		    JSONArray operation_config = (JSONArray) jsonObject.get("Operation");
            // get all the missions
		    for (int i = 0; i < operation_config.size(); i++) {
		        // obtaining the i-th result
		        JSONObject operation_config_result = (JSONObject) operation_config.get(i);
		        
		        if (operation_config_result.containsKey(mode)) {
		        	
			        JSONArray specific_mission = (JSONArray) operation_config_result.get(mode);
			        
			        // get all the delivery missions
				    for (int j = 0; j < specific_mission.size(); j++) {
				        // obtaining the j-th result
				        JSONObject specific_mission_result = (JSONObject) specific_mission.get(j);
				        
				        if (specific_mission_result.containsKey("mission"))
				            mission = (String) specific_mission_result.get("mission");
				        
				        if (specific_mission_result.containsKey("type"))
				            type = (String) specific_mission_result.get("type");
				        
				        if (specific_mission_result.containsKey("start point"))
				            start_point = (String) specific_mission_result.get("start point");
				        
				        if (specific_mission_result.containsKey("connection point"))
				            connection_point = (String) specific_mission_result.get("connection point");
				        
				        if (specific_mission_result.containsKey("destination point"))
				            destination_point = (String) specific_mission_result.get("destination point");
				        
				        if (specific_mission_result.containsKey("start time"))
				            start_time = (double) specific_mission_result.get("start time");
				        
				        if (specific_mission_result.containsKey("late time"))
				            late_time = (double) specific_mission_result.get("late time");
				        
				        if (specific_mission_result.containsKey("min altitude"))
				            min_altitude = (double) specific_mission_result.get("min altitude");
				        
				        if (specific_mission_result.containsKey("max altitude"))
				            max_altitude = (double) specific_mission_result.get("max altitude");
				        
				        temp_specific_mission_node = new specific_mission_node (mission, type, start_point, connection_point, destination_point, 
				    			start_time, late_time, min_altitude, max_altitude);

				    }
		        }
		    }
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		
    	return temp_specific_mission_node;
    }
	
    public void print_specific_mission_node(specific_mission_node node) {
    	System.out.println("Specific Operation : ");
    	String mission = node.return_mission();
    	String type = node.return_type();
    	String start_point = node.return_start_point();
    	String connection_point = node.return_connection_point();
    	String destination_point = node.return_destination_point();
    	double start_time = node.return_start_time();
    	double late_time = node.return_late_time();
    	double min_altitude = node.return_min_altitude();
    	double max_altitude = node.return_max_altitude();
    	
        System.out.println("mission : " + mission);
        System.out.println("type : " + type);
        System.out.println("start_point : " + start_point);
        System.out.println("connection_point : " + connection_point);
        System.out.println("destination_point : " + destination_point);
        System.out.println("start_time : " + start_time);
        System.out.println("late_time : " + late_time);
        System.out.println("min_altitude : " + min_altitude);
        System.out.println("max_altitude : " + max_altitude);
        
        System.out.println();
    		 


    }

//////////////////////////////////////////////////////////////////
 
//////////////////////////////////////////////////////////////////
//read and print terrain nodes from .json
    public Map<String, terrain_node> terrain_start_location_config() {
		Map<String, terrain_node> terrain_location = new HashMap<String, terrain_node>();
		JSONParser parser = new JSONParser();

		try {     
			
			String destination = System.getProperty("user.dir");
			int last_indes = destination.lastIndexOf(File.separator);
			String temp_pre_fix = destination.substring(0, last_indes);
			
		    Object obj = parser.parse(new FileReader(temp_pre_fix + File.separator+ "configuration" + File.separator + "terrain_configuration" + 
		    File.separator + "location_generation" + File.separator + "start_point" + File.separator + "start_location_probability.json"));
			JSONObject jsonObject =  (JSONObject) obj;

		    JSONArray trans_config = (JSONArray) jsonObject.get("Start Location Config");
            // get all the airports
		    for (int i = 0; i < trans_config.size(); i++) {
		        // obtaining the i-th result
		        JSONObject trans_config_result = (JSONObject) trans_config.get(i);
		        JSONArray terrain_areas = (JSONArray) trans_config_result.get("Location");
                // get all the airport attributions
			    for (int j = 0; j < terrain_areas.size(); j++) {
			        // obtaining the j-th result
			        JSONObject airports_result = (JSONObject) terrain_areas.get(j);
			        String airport_name = (String) airports_result.get("name");
//			        System.out.println(airport_name);
			        double probability = (double) airports_result.get("probability");
			        ArrayList<Double> coordination = new ArrayList<Double>();
			        terrain_node temp_node = new terrain_node();
			        temp_node.set_terrain_area_name(airport_name);
			        temp_node.set_probability(probability);
			        
			        // get all the geometry attributions
			        JSONArray geometry = (JSONArray) airports_result.get("geometry");
			        for (int k = 0; k < geometry.size(); k++) {
				        JSONObject geometry_result = (JSONObject) geometry.get(j);
				        JSONArray coordinates_collection = (JSONArray) geometry_result.get("top_left");

		                // get x & y coordinate
				        for (int m = 0; m < coordinates_collection.size(); m++) {
				        	JSONObject coordinates = (JSONObject) coordinates_collection.get(m);
							double x_coordinate = (double) coordinates.get("x");
							double y_coordinate = (double) coordinates.get("y");

							temp_node.set_top_left_x_coordinate(x_coordinate);
							temp_node.set_top_left_y_coordinate(y_coordinate);
				        }
				        
				        coordinates_collection = (JSONArray) geometry_result.get("bottom_right");

		                // get x & y coordinate
				        for (int m = 0; m < coordinates_collection.size(); m++) {
				        	JSONObject coordinates = (JSONObject) coordinates_collection.get(m);
							double x_coordinate = (double) coordinates.get("x");
							double y_coordinate = (double) coordinates.get("y");

							temp_node.set_bottom_right_x_coordinate(x_coordinate);
							temp_node.set_bottom_right_y_coordinate(y_coordinate);
				        }
			        }
			        terrain_location.put(airport_name, temp_node);
			    }
		    }
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		
		print_terrain_node(terrain_location);
		
		return terrain_location;
	}
    
    public Map<String, terrain_node> terrain_end_location_config() {
		Map<String, terrain_node> terrain_location = new HashMap<String, terrain_node>();
		JSONParser parser = new JSONParser();

		try {     
			
			String destination = System.getProperty("user.dir");
			int last_indes = destination.lastIndexOf(File.separator);
			String temp_pre_fix = destination.substring(0, last_indes);
			
		    Object obj = parser.parse(new FileReader(temp_pre_fix + File.separator + "configuration" + File.separator + "terrain_configuration" + 
		    File.separator + "location_generation" + File.separator + "destination_point" + File.separator + "destination_location_probability.json"));
			JSONObject jsonObject =  (JSONObject) obj;

		    JSONArray trans_config = (JSONArray) jsonObject.get("Destination Location Config");
            // get all the airports
		    for (int i = 0; i < trans_config.size(); i++) {
		        // obtaining the i-th result
		        JSONObject trans_config_result = (JSONObject) trans_config.get(i);
		        JSONArray terrain_areas = (JSONArray) trans_config_result.get("Location");
                // get all the airport attributions
			    for (int j = 0; j < terrain_areas.size(); j++) {
			        // obtaining the j-th result
			        JSONObject airports_result = (JSONObject) terrain_areas.get(j);
			        String airport_name = (String) airports_result.get("name");
//			        System.out.println(airport_name);
			        double probability = (double) airports_result.get("probability");
			        ArrayList<Double> coordination = new ArrayList<Double>();
			        terrain_node temp_node = new terrain_node();
			        temp_node.set_terrain_area_name(airport_name);
			        temp_node.set_probability(probability);
			        
			        // get all the geometry attributions
			        JSONArray geometry = (JSONArray) airports_result.get("geometry");
			        for (int k = 0; k < geometry.size(); k++) {
				        JSONObject geometry_result = (JSONObject) geometry.get(j);
				        JSONArray coordinates_collection = (JSONArray) geometry_result.get("top_left");

		                // get x & y coordinate
				        for (int m = 0; m < coordinates_collection.size(); m++) {
				        	JSONObject coordinates = (JSONObject) coordinates_collection.get(m);
							double x_coordinate = (double) coordinates.get("x");
							double y_coordinate = (double) coordinates.get("y");

							temp_node.set_top_left_x_coordinate(x_coordinate);
							temp_node.set_top_left_y_coordinate(y_coordinate);
				        }
				        
				        coordinates_collection = (JSONArray) geometry_result.get("bottom_right");

		                // get x & y coordinate
				        for (int m = 0; m < coordinates_collection.size(); m++) {
				        	JSONObject coordinates = (JSONObject) coordinates_collection.get(m);
							double x_coordinate = (double) coordinates.get("x");
							double y_coordinate = (double) coordinates.get("y");

							temp_node.set_bottom_right_x_coordinate(x_coordinate);
							temp_node.set_bottom_right_y_coordinate(y_coordinate);
				        }
			        }
			        terrain_location.put(airport_name, temp_node);
			    }
		    }
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		
		print_terrain_node(terrain_location);
		
		return terrain_location;
	}
    
    public void print_terrain_node(Map<String, terrain_node> terrain_node_collection) {
    	for (String name: terrain_node_collection.keySet()){

            String key = name.toString();
            System.out.println("Name : " + key);  
            System.out.println("Top Left coordinate : (" + terrain_node_collection.get(key).return_top_left_x_coordinate() + "," + terrain_node_collection.get(key).return_top_left_y_coordinate() + ")");
            System.out.println("Bottom right coordinate : (" + terrain_node_collection.get(key).return_bottom_right_x_coordinate() + "," + terrain_node_collection.get(key).return_bottom_right_y_coordinate() + ")");
            System.out.println("Probability : " + terrain_node_collection.get(key).return_probability());
            System.out.println();
    	}
    }
}

//////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////