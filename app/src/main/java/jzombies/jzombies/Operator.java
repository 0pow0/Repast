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


public class Operator {
	
	private ArrayList<mission_node> mission_list = new ArrayList<mission_node>();
	
	public Operator(String mode) {
		
		Util mytool = new Util();
		if (mode == "load") {
			Util my_tool = new Util();
			mission_list = my_tool.load_mission_list();
		} 
	}
	
	public ArrayList<mission_node> return_mission_list() {
		return mission_list;
	}
	
	public mission_node find(String mission, String type) {
		mission_node temp = new mission_node();
		for (mission_node node : mission_list) {
			if (node.return_mission() == mission && node.return_type() == type) {
				temp = node;
			}
		}	
		return temp;
	}

}
