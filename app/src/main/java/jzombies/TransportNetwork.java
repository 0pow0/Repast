/**
 * 
 */
package jzombies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyizhao
 *
 */

public class TransportNetwork {
	private Map<airport_node, ArrayList<airport_node>> start_destination_list = new HashMap<airport_node, ArrayList<airport_node>>();
	private Map<String, ArrayList<Double>> airport_list = new HashMap<String, ArrayList<Double>> ();
	private Map<String, ArrayList<String>> airport_connection = new HashMap<String, ArrayList<String>> ();
	
	public TransportNetwork(String mode) {
		Util mytool = new Util();
		
		if (mode == "load") {
			airport_list = mytool.transport_network_config();
			airport_connection = mytool.transport_network_connection();
			start_destination_list = merge_transport_info(airport_list, airport_connection);

		} else if (mode == "random") {
			long number_of_airport = mytool.transport_network_load_policy();
			
		} else if (mode == "mix") {
			long number_of_airport = mytool.transport_network_load_policy();
			
			airport_list = mytool.transport_network_config();
			airport_connection = mytool.transport_network_connection();
			start_destination_list = merge_transport_info(airport_list, airport_connection);
			
		}
	}
	
	public Map<String, ArrayList<Double>> return_airport_list() {
		return airport_list;
	}
	
	public Map<String, ArrayList<String>> return_airport_connection() {
		return airport_connection;
	}
	
	public Map<airport_node, ArrayList<airport_node>> return_start_destination_list() {
		return start_destination_list;
	}
	
	public Map<airport_node, ArrayList<airport_node>> merge_transport_info(Map<String, ArrayList<Double>> airport_list, Map<String, ArrayList<String>> airport_connection) {
		
    	for (String name: airport_connection.keySet()){
            String key = name.toString();
            if (airport_list.containsKey(key)) {
                airport_node temp_start_node = new airport_node(key, airport_list.get(key).get(0), airport_list.get(key).get(1));
                ArrayList<airport_node> temp_list = new ArrayList<airport_node>();
                for (String dest: airport_connection.get(key)) {
                	if (airport_list.containsKey(dest)) {
                   	 airport_node temp_dest_node = new airport_node(dest, airport_list.get(dest).get(0), airport_list.get(dest).get(1));
                   	 temp_list.add(temp_dest_node);
                	}
                }
                start_destination_list.put(temp_start_node, temp_list);
            }
    	}
		return start_destination_list;
	}

}
