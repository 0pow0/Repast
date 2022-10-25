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
import java.util.HashMap;

import com.vividsolutions.jts.geom.Geometry;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.grid.Grid;

public class Base_Station {
	
	private Geography<Object> geography;
	
//  Base Station attributions
	private long num_of_channels;
	private double antenna_height;
	private double tx_power;
	private double rx_threshold;
    private ArrayList<UAV> current_channels;
	private int internal_time_step;
	private int current_batch;
	private int bs_id;
	private int noise;
	private String bs_label;
	static HashMap<String, Boolean> Channel_table = new HashMap<String, Boolean>();
	
	public Base_Station(Geography<Object> geography,
			            long base_station_channels, double antenna_height, double tx_power, double rx_threshold, int current_batch, int bs_id){
		this.geography = geography;
		
		this.antenna_height = antenna_height;
		this.tx_power = tx_power;
		this.rx_threshold = rx_threshold;
		this.current_channels = new ArrayList<UAV>();
		this.internal_time_step = -1;
		this.current_batch = current_batch;
		this.bs_id = bs_id;
		this.noise = -60;
		this.num_of_channels = base_station_channels;
		int factor = 1;
		
		if(this.bs_id == 2) {
			this.num_of_channels = base_station_channels*factor;
		}
		if(this.bs_id == 3) {
			this.num_of_channels = base_station_channels*factor;
		}
		if(this.bs_id == 4) {
			this.num_of_channels = base_station_channels*factor;
		}
		if(this.bs_id == 1) {
			this.num_of_channels = base_station_channels*factor;
		}
		
		if(this.bs_id < 18) {
			this.bs_label = "urban";
		}else {
			this.bs_label = "rural";
		}
		
		System.out.print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n");
		System.out.print(this.bs_id);
		System.out.print(this.bs_label);
		
		for(int i = 0; i < this.num_of_channels; i++) {
			long ch_id = i;
			String tmp_key = Integer.toString(bs_id) + Long.toString(ch_id);
			Channel_table.put(tmp_key, false);
		}
	}
	
	public double get_BS_x(int id) {
		if(id == 0) {
			return 2885.655317;
		}else if(id == 1) {
			return 12509.28222;
		}else if(id == 2) {
			return 10557.91175;
		}else if(id == 3) {
			return 13137.86021;
		}else if(id == 4) {
			return 16576.28419;
		}else if(id == 5) {
			return 3108.786425;
		}else if(id == 6) {
			return 10404.2445;
		}else if(id == 7) {
			return 7121.872493;
		}else if(id == 8) {
			return 10091.29913;
		}else if(id == 9) {
			return 5753.101704;
		}else {
			return -1;
		}
	
	}
	
	public double get_BS_y(int id) {
		if(id == 0) {
			return 6006.973667;
		}else if(id == 1) {
			return 7244.440137;
		}else if(id == 2) {
			return 9062.886008;
		}else if(id == 3) {
			return 2919.61815;
		}else if(id == 4) {
			return 4513.434292;
		}else if(id == 5) {
			return 2132.251543;
		}else if(id == 6) {
			return 724.2344417;
		}else if(id == 7) {
			return 1308.967245;
		}else if(id == 8) {
			return 3777.428258;
		}else if(id == 9) {
			return 7787.607056;
		}else {
			return -1;
		}
		
	}
	
	public long get_num_of_channels() {
		return this.num_of_channels;
	}
	
	public String get_bs_label() {
		return this.bs_label;
	}
	
	public int return_bs_id() {
		return this.bs_id;
	}
	
	public boolean add_UAV(UAV uav) {
		if (current_channels.contains(uav)) {
			return false;
		} else {
			return current_channels.add(uav);
		}
	}
	
	public void remove_UAV(UAV uav) {
		current_channels.remove(uav);
	}
	
	public int get_current_num_UAV() {
		return current_channels.size();
	}
   
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		this.internal_time_step++;
		save_location();
	}
	
	public void assignLink(String key) {
		Channel_table.put(key, true);
	}
	
	public void removeLink(String key) {
		Channel_table.put(key, false);
	}
	
	public boolean checkChannel(String key) {
		return Channel_table.get(key);
	}
	
    // save Basestation location in each time tick
    private void save_location() {
    	int id = this.bs_id;
    	
		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);
		
    	String fileName= temp_pre_fix + File.separator + "report" + File.separator + "batch_" + Integer.toString(current_batch) + File.separator + "basestation" + ".csv";
    	Geometry myPoint = geography.getGeometry(this);
    	try {
    		PrintWriter writer_basestation = new PrintWriter(new FileOutputStream(new File(fileName),true));
    		StringBuilder basestations_string = new StringBuilder();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    		String time = format.format(new Date());
    		basestations_string.append(internal_time_step);
    		basestations_string.append(',');
    		basestations_string.append(id);
    		basestations_string.append(',');
    		basestations_string.append(this.current_channels.size());
    		basestations_string.append('\n');
    		writer_basestation.write(basestations_string.toString());
    		writer_basestation.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}
