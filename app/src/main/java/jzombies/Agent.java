package jzombies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.vividsolutions.jts.geom.Geometry;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.grid.Grid;

public class Agent {
	
	private Geography<Object> geography;
	
//  Base Station attributions
//	private long num_of_channels;
    private ArrayList<ArrayList<UAV>> current_UAVs;
	private int internal_time_step;
	private int current_batch;
	private int Agent_id;
	private int flag;
	
	public Agent(Geography<Object> geography, int current_batch, int Agent_id){
		this.geography = geography;
//		this.num_of_channels = base_station_channels;
		this.current_UAVs = new ArrayList<ArrayList<UAV>>();
		for(int i=1; i < 5; i++) {
			ArrayList<UAV> LevelArrayList = new ArrayList<UAV>();
			this.current_UAVs.add(LevelArrayList);
		}
		this.internal_time_step = -1;
		this.current_batch = current_batch;
		this.Agent_id = Agent_id;
		this.flag = -1;
	}
	
//	public long get_num_of_channels() {
//		return this.num_of_channels;
//	}
	
	public ArrayList<UAV> return_current_uavs_list(int level) {
		return current_UAVs.get(level-1);
	}
	
	public int return_Agent_id() {
		return this.Agent_id;
	}
	
	public int return_Flag() {
		return this.flag;
	}
	
	public void set_Flag(int flag) {
		this.flag = flag;
	}
	
	public boolean add_UAV(UAV uav, int level) {
		if (current_UAVs.get(level-1).contains(uav)) {
			return false;
		} else {
			return current_UAVs.get(level-1).add(uav);
		}
	}
	
	public void remove_UAV(UAV uav, int level) {
		current_UAVs.get(level-1).remove(uav);
	}
	
	public int get_current_num_UAV(int level) {
		return current_UAVs.get(level-1).size();
	}
   
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		this.internal_time_step++;
//		save_location();
	}
	
    // save Basestation location in each time tick
//    private void save_location() {
//    	int id = this.bs_id;
//    	
//		String destination = System.getProperty("user.dir");
//		int last_indes = destination.lastIndexOf(File.separator);
//		String temp_pre_fix = destination.substring(0, last_indes);
//		
//    	String fileName= temp_pre_fix + File.separator + "report" + File.separator + "batch_" + Integer.toString(current_batch) + File.separator + "Agent" + ".csv";
//    	Geometry myPoint = geography.getGeometry(this);
//    	try {
//    		PrintWriter writer_basestation = new PrintWriter(new FileOutputStream(new File(fileName),true));
//    		StringBuilder basestations_string = new StringBuilder();
//    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
//    		String time = format.format(new Date());
//    		basestations_string.append(internal_time_step);
//    		basestations_string.append(',');
//    		basestations_string.append(id);
//    		basestations_string.append(',');
//    		basestations_string.append(this.current_channels.size());
//    		basestations_string.append('\n');
//    		writer_basestation.write(basestations_string.toString());
//    		writer_basestation.close();
//    	} catch (IOException e) {
//    		e.printStackTrace();
//    	}
//    }
}
