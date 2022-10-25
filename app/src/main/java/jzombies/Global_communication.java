/**
 * 
 */
package jzombies;


import java.io.File;
import java.util.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Map.Entry;

import com.vividsolutions.jts.geom.Geometry;

import repast.simphony.engine.schedule.ScheduledMethod;



public class Global_communication {
	
	private int num_BS;
	private long num_channel;
	private double noise;
	static HashMap<String, Double> Comm_Table = new HashMap<String, Double>();
	private int internal_time_step;
	
	
	public Global_communication(long base_station_channels) {
		this.num_BS = 10;
		this.num_channel = base_station_channels;
		int id = 1234445;
		this.noise = -60;
//		System.out.print("**********************************\n");
		
		System.out.print(id);
	
		this.internal_time_step = -1;
		String l = Integer.toString(id);
		double x = -1;
		
		String tmp_key_l = Integer.toString(-1) + Long.toString(-1) + Integer.toString(-233);
		double value_l = 0;
		double tmp_value_l = value_l;
				
		Comm_Table.put(tmp_key_l, tmp_value_l);

		
	}
	
	public void register_uav(int uav_id) {
		double tmp_value = 0;
		
		
		
		for(int i = 0; i < this.num_BS; i++) {
			for(int j = 0; j < this.num_channel; j++) {
				String tmp_key = String.format("%03d", this.num_BS) + String.format("%03d", this.num_channel) + Integer.toString(uav_id);
				Comm_Table.put(tmp_key, tmp_value);
			}
		}
	}
	
	public void add_report(int BS_id, long ch_id, int uav_id, double RSS) {
		
//		System.out.print("**********************************\n");
//		System.out.print(BS_id + "\n");
//		System.out.print(ch_id + "\n");
//		System.out.print(uav_id + "\n");
		
		String tmp_key = String.format("%03d", BS_id) + String.format("%03d", ch_id) + Integer.toString(uav_id);
//		System.out.print(tmp_key + "\n");
		//		String tmp_key = Integer.toString(BS_id) + Long.toString(ch_id) + Integer.toString(uav_id);
		double value = Math.pow(10,RSS/10);
		double tmp_value = value;
				
		Comm_Table.put(tmp_key, tmp_value);
		
   	
		
	}
	
	public void check() {
//		System.out.println("///////////////////////////////////////////////////////////////////////////////\n");
//		System.out.println("check table here\n");
//		System.out.print(Comm_Table + "\n");
		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);
		int current_batch = 1;

		String fileName= temp_pre_fix + File.separator + "report" + File.separator + "batch_" + Integer.toString(current_batch) + File.separator + "comm_table" + ".csv";		
		
		
		try {
			
			PrintWriter writer_table = new PrintWriter(new FileOutputStream(new File(fileName),true));
			
    		PrintWriter writer_basestation = new PrintWriter(new FileOutputStream(new File(fileName),true));
    		StringBuilder table_string = new StringBuilder();
    		
    		table_string.append(this.internal_time_step);
    		table_string.append(",");
    		table_string.append(Comm_Table);
    		table_string.append("\n");
    		writer_table.write(table_string.toString());
    		
    		
    		writer_table.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
		
		
	}

	public void delete_report(int uav_id) {
		String target = Integer.toString(uav_id); 
		Iterator<String> iterator = Comm_Table.keySet().iterator();
		
		
		for (Iterator<Map.Entry<String, Double>> it = Comm_Table.entrySet().iterator(); it.hasNext();){
		    Map.Entry<String, Double> item = it.next();
		    
//		    System.out.print("table before" + Comm_Table + "\n");
		   
		    String tmp_key = item.getKey();
			String cut = tmp_key.substring(6, tmp_key.length()); 
//			System.out.print("**********************************\n");
//			System.out.print("target" + target + "\n");
//			System.out.print("cut" + cut + "\n");
//			System.out.print("tmp key" + tmp_key + "\n");
			if (cut.equals(target)) {
//				System.out.print("record deleted \n");
				it.remove();
//			    System.out.print("table after" + Comm_Table + "\n");
			}
		    
		    
		}
		
//		while(iterator.hasNext()) {
//			String tmp_key = (String) iterator.next();
//			String cut = tmp_key.substring(2, tmp_key.length()); 
//			System.out.print("target" + target + "\n");
//			System.out.print("cut" + cut + "\n");
//			if (cut.equals(target)) {
//				System.out.print("record deleted\n");
//				Comm_Table.remove(tmp_key);
//			}
//		}

	}
	
	public void update_report(int BS_id, long ch_id, int uav_id, double RSS) {
//		String tmp_key = Integer.toString(BS_id) + Long.toString(ch_id) + Integer.toString(uav_id);
		String tmp_key = String.format("%03d", BS_id) + String.format("%03d", ch_id) + Integer.toString(uav_id);
		double value = Math.pow(10,RSS/10);
//		dB to linear
		double tmp_value = value;
				
		Comm_Table.replace(tmp_key, tmp_value);
	}
	
	
	public double check_SIR(int BS_id, long ch_id, int uav_id) {
		
//		String the_key = Integer.toString(BS_id) + Long.toString(ch_id) + Integer.toString(uav_id);
		String the_key = String.format("%03d", BS_id) + String.format("%03d", ch_id) + Integer.toString(uav_id);
		
		String target = String.format("%03d", ch_id); 
		double noise_linear = Math.pow(10, this.noise/10);
		double interference = 0;
		double interference_dB = 0;
					
		Iterator<String> iterator = Comm_Table.keySet().iterator();

		while(iterator.hasNext()) {
			String tmp_key = (String) iterator.next();
			
			String cut = tmp_key.substring(3, 6); 
//			System.out.print("####################################\n");
//			System.out.print("cut" + cut+ "\n");
//			System.out.print("target" + target+ "\n");

			if (cut.equals(target)==true) {
				if (tmp_key.equals(the_key) == false) {
					
					interference += Comm_Table.get(tmp_key);
					
//					System.out.print("tmp_key" + tmp_key + "\n");
//					
//					System.out.print("value" + Comm_Table.get(tmp_key) + "\n");
//					
				}
			}
		}
		interference_dB = 10*Math.log10(interference);
		if(interference_dB == Double.POSITIVE_INFINITY) {
			interference_dB = 100;
			System.out.print(interference_dB);
		}
		
			
		return interference_dB;
	}
	
//	@ScheduledMethod(start = 1, interval = 1)
//	public void step() {
//		this.internal_time_step++;
//		
//	}
	
	
}
