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

import org.apache.commons.math3.analysis.function.Abs;

import com.vividsolutions.jts.geom.Geometry;

//import antlr.collections.List;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.grid.Grid;

public class Block_area {
	
	private Geography<Object> geography;
	
	private short[] start_x;
	private short[] start_y;
	private short[] end_x;
	private short[] end_y;
	private int[] start_time;
	private int[] duration_time;
	private int num;
	
	
//	public Block_area(Geography<Object> geography, short start_x, short end_x, short start_y, short end_y, int block_start_time, int duration){
//		this.geography = geography;
////		this.num_of_channels = base_station_channels;
//		this.start_x = start_x;
//		this.start_y = start_y;
//		this.end_x = end_x;
//		this.end_y = end_y;
//		this.start_time = block_start_time;
//		this.duration_time = duration;
//	}
	
	public Block_area() {
		this.start_x = new short[2];
		this.start_y = new short[2];
		this.end_x = new short[2];
		this.end_y = new short[2];
		this.start_x[0] = (short)45;
		this.end_x[0] = (short)105;
		this.start_y[0] = (short)495;		
		this.end_y[0] = (short)605;
		this.start_x[1] = (short)275;
		this.end_x[1] = (short)305;
		this.start_y[1] = (short)515;		
		this.end_y[1] = (short)590;
		this.num = 2;
	}
	
//	public short return_start_x() {
//		return this.start_x;
//	}
//	
//	public short return_start_y() {
//		return this.start_y;
//	}
//	
//	public short return_end_x() {
//		return this.end_x;
//	}
//	
//	public short return_end_y() {
//		return this.end_y;
//	}
//	
	private boolean between(short x, short a1, short a2) {
		short min = (short) Math.min(a1, a2);
		short max = (short) Math.min(a1, a2);
		if(x > min - 10 && x < max + 10)
			return true;
		return false;		
	}
	
	private double[] which_line(short x1, short y1, short x2, short y2, short threshold, short[][] lines) {
		float k2=Float.MAX_VALUE;  // for UAV path
		boolean flag2=false;
		if(x1 == x2) {flag2=true;}			
		else {
			k2 = (float)(y1 - y2) / (float)(x1 - x2);
			System.out.println("----------k2: " + k2);
			}
		double[] result = new double[2];
		short line_index = -1;  // the first line that UAV intersects
		double distance = Float.MAX_VALUE;  // the distance between uav and the nearest intersect point with block zone
		
		for(int i = 0; i < 4; i++) {
			float x = 0, y=0;
			float k1=Float.MAX_VALUE;  // for block area boundary			
			boolean flag1=false;
			
			if(lines[i][0] == lines[i][2])  
				flag1=true;
			else {
				k1 = (float)(lines[i][1] - lines[i][3]) / (float)(lines[i][0] - lines[i][2]);
				}
			
			if(k1==k2 || (flag1 == true && flag2 == true))
				continue;
				
			if(flag1) {
				x = lines[i][0];
				if (k2==0)
					y = y1;
				else
					y = k2*(x-x2) + y2;				
			}else if(flag2){
				x= x1;
				if(k1==0)
					y = lines[i][1];
				else
					y=k1*(x-lines[i][2])+lines[i][3];	
			}else {
				if(k1==0){
					y=lines[i][1];
					x=(y-y2)/k2+x2;
				}else if(k2==0){
					y=y1;
					x=(y-lines[i][3])/k1+lines[i][2];
				}else{
					x=(k1*lines[i][2]-k2*x2+y2-lines[i][3])/(k1-k2);
					y=k1*(x-lines[i][2])+lines[i][3];
				}
			}
//			System.out.println("----------the intersection point:" + x +","+ y+", index = " + i);
			
			// if the point of intersection on the boundary line
			short x_ceil = (short) Math.max(lines[i][0], lines[i][2]);
			short x_floor = (short) Math.min(lines[i][0], lines[i][2]);
			short y_ceil = (short) Math.max(lines[i][1], lines[i][3]);
			short y_floor = (short) Math.min(lines[i][1], lines[i][3]);
//			System.out.println("----------x_ceil="+x_ceil + ", x_fool=" + x_floor+ ", y_c="+ y_ceil + ", y_f=" + y_floor);
			short xu_ceil = (short) Math.max(x1, x2);
			short xu_floor = (short) Math.min(x1, x2);
			short yu_ceil = (short) Math.max(y1, y2);
			short yu_floor = (short) Math.min(y1, y2);
			if(x<=x_ceil &&x>=x_floor && y<=y_ceil && y>=y_floor && x<=xu_ceil && x>=xu_floor && y<= yu_ceil && y>=yu_floor) {
				if(distance > (double)Math.sqrt((x-x1)*(x-x1) + (y-y1)*(y-y1))){					
					distance = (double)Math.sqrt((x-x1)*(x-x1) + (y-y1)*(y-y1));							
					line_index = (short) i;	
					System.out.println("----------the index of line:" + i);
					System.out.println("----------the intersection point:" + x +","+ y+", index = " + i);
					System.out.println("----------the smaller distance between uav and intersection point:" + distance);		
				}
			}						
		}
		result[0] = line_index;
		result[1] = distance;
		return result;
		
	}
	
	public double[] block_repulse(short x1, short y1, short x2, short y2, short threshold) {
		short reduce_distance = 15;
		double distance_target = Math.sqrt((x1 - x2)*(x1 - x2)+(y1 - y2)*(y1 - y2));
		double Yatx1= (10*(x2 - x1)/distance_target)/ 9;
		double Yaty1=(10*(y2 - y1)/distance_target)/9;					
		int m = 5;
		double Po = 200;	
		// block_rep[0]: the smallest distance between UAV and no-fly zone
		// block_rep[1]: sum_x
		// blick_rep[2]: sum_y
		double block_rep[] = new double[3];
		double dis=Double.MAX_VALUE;
		double[] firesult = new double[2];
		int index = 0;
		for(int i = 0; i< this.num;i++) {
			short lines[][] = { {this.start_x[i], this.start_y[i], this.end_x[i], this.start_y[i]},
					{this.start_x[i], this.end_y[i], this.end_x[i], this.end_y[i]},
					{this.start_x[i], this.start_y[i], this.start_x[i], this.end_y[i]}, 
					{this.end_x[i], this.start_y[i], this.end_x[i], this.end_y[i]}};
			double[] res = which_line(x1, y1, x2, y2, threshold, lines);
			if(dis>res[1]) {
				firesult = res;
				index = i;
				dis = res[1];
			}
		}
		short line_index = (short)firesult[0];
		short lines[][] = { {this.start_x[index], this.start_y[index], this.end_x[index], this.start_y[index]},
				{this.start_x[index], this.end_y[index], this.end_x[index], this.end_y[index]},
				{this.start_x[index], this.start_y[index], this.start_x[index], this.end_y[index]}, 
				{this.end_x[index], this.start_y[index], this.end_x[index], this.end_y[index]}};
		if(line_index == -1) {
			System.out.println(" ========== uav does not cross the block area." );
			block_rep[0] = -1;
			block_rep[1] = -1;
			block_rep[2] = -1;			
			return block_rep;}	
		else {
			double dist = 0;
			double uav2endpoint1, uav2endpoint2, lineLength;
			lineLength = Math.sqrt((lines[line_index][0] - lines[line_index][2])*(lines[line_index][0] - lines[line_index][2]) + (lines[line_index][1] - lines[line_index][3])*(lines[line_index][1] - lines[line_index][3]));
			uav2endpoint1 = Math.sqrt((x1 - lines[line_index][0])*(x1 - lines[line_index][0]) + (y1 - lines[line_index][1])*(y1 - lines[line_index][1]));
			uav2endpoint2 = Math.sqrt((x1 - lines[line_index][2])*(x1 - lines[line_index][2]) + (y1 - lines[line_index][3])*(y1 - lines[line_index][3]));
			System.out.println(" ========== uav2endpoint1=" + uav2endpoint1 );
			System.out.println(" ========== uav2endpoint2=" + uav2endpoint2 );
			System.out.println(" ========== length=" + lineLength );
			if(uav2endpoint1 * uav2endpoint1 > uav2endpoint2*uav2endpoint2 + lineLength*lineLength ||
					uav2endpoint2 * uav2endpoint2 > uav2endpoint1*uav2endpoint1 + lineLength*lineLength) {
				block_rep[0] = Math.min(uav2endpoint1, uav2endpoint2);
				if(block_rep[0] > threshold) {
					block_rep[0] = -1;
					block_rep[1] = -1;
					block_rep[2] = -1;
					return block_rep;
				}
				else {
//					double block_distance = block_rep[0] - reduce_distance;
					if(lines[line_index][0] == lines[line_index][2]) {
						block_rep[1] = 0.0;
						block_rep[2] = Yaty1;
						return block_rep;
					}
					else {
						block_rep[1] = Yatx1;
						block_rep[2] = 0.0;
						return block_rep;
					}
				}
			}
					
			double p = (uav2endpoint1 + uav2endpoint2 + lineLength) /2;
			double s = Math.sqrt(p*(p - uav2endpoint1) * (p - uav2endpoint2)*(p-lineLength));
			dist = 2 * s / lineLength;
			double dist_x, dist_y;
			if(dist > threshold) {
				block_rep[0] = -1;
				block_rep[1] = -1;
				block_rep[2] = -1;
				return block_rep;
			}
			block_rep[0] = dist;
			double block_distance = block_rep[0] - reduce_distance;
			if(lines[line_index][0] == lines[line_index][2]) {				
				block_rep[1] = 0.0;
				if(between(x2, lines[line_index][0], lines[line_index][2])) {	
					if(uav2endpoint1 >= uav2endpoint2) {
						block_rep[2] = 0.5 * dist;
					}
					else
						block_rep[2] = -0.5 * dist;
					return block_rep;
				}
				else {										
					block_rep[2] = Yaty1;
					return block_rep;
				}	
			}
			else{
				block_rep[2] = 0.0;
				if(between(y2, lines[line_index][1], lines[line_index][3])) {
					if(uav2endpoint1 >= uav2endpoint2) {
						block_rep[1] = 0.5 * dist;
					}
					else
						block_rep[1] = -0.5 * dist;
					return block_rep;
				}
				else {					
					block_rep[1] = Yatx1;
					return block_rep;
				}	
			}			
		}	

	} 
	
	
//	(x1, y1) is the location of UAV
//	(x2, y2) is the location of UAV's target
	public double[] uav2block_distance(short x1, short y1, short x2, short y2, short threshold) {
//		System.out.println( "----------x1=" + x1 + ", y1=" + y1 + ",  x2="+x2 + ", y2="+ y2);
		double[] result = new double[3];
		double dis=Double.MAX_VALUE;
		double[] firesult = new double[2];
		int index = 0;
		for(int i = 0; i< this.num;i++) {
			short lines[][] = { {this.start_x[i], this.start_y[i], this.end_x[i], this.start_y[i]},
					{this.start_x[i], this.end_y[i], this.end_x[i], this.end_y[i]},
					{this.start_x[i], this.start_y[i], this.start_x[i], this.end_y[i]}, 
					{this.end_x[i], this.start_y[i], this.end_x[i], this.end_y[i]}};
			double[] res = which_line(x1, y1, x2, y2, threshold, lines);
			if(dis>res[1]) {
				firesult = res;
				index = i;
				dis = res[1];
			}
		}
		short line_index = (short)firesult[0];
		short lines[][] = { {this.start_x[index], this.start_y[index], this.end_x[index], this.start_y[index]},
				{this.start_x[index], this.end_y[index], this.end_x[index], this.end_y[index]},
				{this.start_x[index], this.start_y[index], this.start_x[index], this.end_y[index]}, 
				{this.end_x[index], this.start_y[index], this.end_x[index], this.end_y[index]}};
		System.out.println("----------the final index of line:" + line_index);
		

		
		if(line_index == -1) {
			System.out.println(" ----------uav does not cross the block area." );
			result[0] = -1;
			result[1] = -1;
			result[2] = -1;			
			return result;
		}			
		else {
			double dist = 0;
			double uav2endpoint1, uav2endpoint2, lineLength;
			lineLength = Math.sqrt((lines[line_index][0] - lines[line_index][2])*(lines[line_index][0] - lines[line_index][2]) + (lines[line_index][1] - lines[line_index][3])*(lines[line_index][1] - lines[line_index][3]));
			uav2endpoint1 = Math.sqrt((x1 - lines[line_index][0])*(x1 - lines[line_index][0]) + (y1 - lines[line_index][1])*(y1 - lines[line_index][1]));
			uav2endpoint2 = Math.sqrt((x1 - lines[line_index][2])*(x1 - lines[line_index][2]) + (y1 - lines[line_index][3])*(y1 - lines[line_index][3]));
			System.out.println(" ----------uav2endpoint1=" + uav2endpoint1 );
			System.out.println(" ----------uav2endpoint2=" + uav2endpoint2 );
			System.out.println(" ----------length=" + lineLength );
			
			if(uav2endpoint1 * uav2endpoint1 > uav2endpoint2*uav2endpoint2 + lineLength*lineLength) {	
				if(uav2endpoint2 > threshold) {
					result[0] = -1;
					result[1] = -1;
					result[2] = -1;			
					return result;
				}
				else {
					result[0] = uav2endpoint2;
					result[1] = x1 - lines[line_index][2];
					result[2] = y1 - lines[line_index][3];
				}
			}
			if(uav2endpoint2 * uav2endpoint2 > uav2endpoint1*uav2endpoint1 + lineLength*lineLength) {
				if(uav2endpoint1 > threshold) {
					result[0] = -1;
					result[1] = -1;
					result[2] = -1;			
					return result;
				}
				else {
					result[0] = uav2endpoint1;
					result[1] = x1 - lines[line_index][0];
					result[2] = y1 - lines[line_index][1];
					return result;
				}			
			}
			double p = (uav2endpoint1 + uav2endpoint2 + lineLength) /2;
			double s = Math.sqrt(p*(p - uav2endpoint1) * (p - uav2endpoint2)*(p-lineLength));
			dist = 2 * s / lineLength;
			double dist_x, dist_y;
			if(dist > threshold) {
				result[0] = -1;
				result[1] = -1;
				result[2] = -1;			
				return result;
			}
			result[0] = dist;
			if(lines[line_index][0] == lines[line_index][2]) {	
				result[1] = x1 - lines[line_index][0];
				if(between(x2, lines[line_index][0], lines[line_index][2])) {					
					result[2] =  0.5 * dist;
					return result;
				}
				else {										
					result[2] = 0.0;
					return result;
				}	
			}
			else{
				result[2] = y1 - lines[line_index][1];
				if(between(y2, lines[line_index][1], lines[line_index][3])) {
					result[1] = 0.5 * dist;
					return result;
				}
				else {
					result[1] = 0.0;
					return result;
				}
			}				
		}		
			

	}
	
}
