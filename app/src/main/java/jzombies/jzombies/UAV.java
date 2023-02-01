package jzombies;

import repast.NS3CommunicatiorHelper;
import repast.UserEquipment;
import repast.UserEquipmentController;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.collections15.list.TreeList;
import org.geotools.referencing.GeodeticCalculator;
import org.json.simple.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import ROCBuilder.SINRResp;
import ROCBuilder.UserEquipmentResp;
import jzombies.Operator;
import jzombies.Util;
import jzombies.specific_mission_node;
import prediction.input.InputFactory;
import prediction.input.SinrPredictionModelInputWrap;
import prediction.model.Model;

import javax.measure.unit.SI;
import util.Utils;

class Operation {
	private specific_mission_node my_specific_mission_node;

	private String mode;
	private String mission;
	private String type;
	private String start_point;
	private String connection_point;
	private String destination_point;
	private double start_time;
	private double late_time;
	private double min_altitude;
	private double max_altitude;

	public Operation() {
		this.mode = "";
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

	public Operation(String mode) {
		if (mode == "all_random") {
			this.mode = mode;
			this.mission = "all_random";
			this.type = "all_random";

			this.start_point = "";
			this.connection_point = "";
			this.destination_point = "";

			Random r_1 = new Random();
			int r_1_min = 1;
			int r_1_max = 101;
			int r_1_start_time = r_1.nextInt(r_1_max - r_1_min) + r_1_min;
			this.start_time = r_1_start_time;

			Random r_2 = new Random();
			int r_2_min = 100;
			int r_2_max = 200;
			int r_2_late_time = r_2.nextInt(r_2_max - r_2_min) + r_2_min;
			this.late_time = r_2_late_time;

			Random r_3 = new Random();
			int r_3_min = 1;
			int r_3_max = 51;
			int r_3_min_altitude = r_3.nextInt(r_3_max - r_3_min) + r_3_min;
			this.min_altitude = r_3_min_altitude;

			Random r_4 = new Random();
			int r_4_min = 50;
			int r_4_max = 100;
			int r_4_max_altitude = r_4.nextInt(r_4_max - r_4_min) + r_4_min;
			this.max_altitude = r_4_max_altitude;

		} else if (mode == "random_start") {
			this.mode = mode;
		} else if (mode == "random_end") {
			this.mode = mode;
		} else if (mode == "probability") {
			this.mode = mode;
			this.mission = "probability";
			this.type = "probability";
		} else {
			Util my_tool = new Util();
			my_specific_mission_node = my_tool.load_specific_operation(mode);

			this.mode = mode;
			this.mission = my_specific_mission_node.return_mission();
			this.type = my_specific_mission_node.return_type();
			this.start_point = my_specific_mission_node.return_start_point();
			this.connection_point = my_specific_mission_node.return_connection_point();
			this.destination_point = my_specific_mission_node.return_destination_point();
			this.start_time = my_specific_mission_node.return_start_time();
			this.late_time = my_specific_mission_node.return_late_time();
			this.min_altitude = my_specific_mission_node.return_min_altitude();
			this.max_altitude = my_specific_mission_node.return_max_altitude();
		}

	}

	public String return_mode() {
		return mode;
	}

	public String return_mission() {
		return mission;
	}

	public String return_type() {
		return type;
	}

	public void set_start_point(String temp_start_point) {
		start_point = temp_start_point;
	}

	public String return_start_point() {
		return start_point;
	}

	public void set_connection_point(String temp_connection_point) {
		connection_point = temp_connection_point;
	}

	public String return_connection_point() {
		return connection_point;
	}

	public void set_destination_point(String temp_destination_point) {
		destination_point = temp_destination_point;
	}

	public String return_destination_point() {
		return destination_point;
	}

	public double return_start_time() {
		return start_time;
	}

	public double return_late_time() {
		return late_time;
	}

	public double return_min_altitude() {
		return min_altitude;
	}

	public double return_max_altitude() {
		return max_altitude;
	}

}

public class UAV {
	// public HashMap<
	public double distance;
	public static final String logPath
		= "/home/rzuo02/work/repast/report/uav.csv";
	private static UAVLogger logger = new UAVLogger(logPath);
	private UserEquipment ue;

	private Geography<Object> geography;
	private GeometryFactory fac;
	// private boolean moved;

	// UAV attributions
	private double height;
	private double speed;
	private boolean if_straight_path;
	private boolean if_routing__path;
	long num_ch;
	private int start_time;
	private double tx_power;
	private double rx_threshold;
	private double RSS;
	private double current_SIR;
	private boolean if_nearest_target;
	private TreeList<Object> targets;
	private int internal_time_step;
	private Base_Station current_basestation;
	private Global_communication current_table;
	private long channel_id;
	private int bs_id;
	private int label;
	private int end_time;
	private int freeze;
	private int level;
	private int id;
	private ArrayList<Integer> level_inf;
	private ArrayList<Double> start_coordinate_pair;
	private ArrayList<ArrayList<Double>> connection_coordinate_pair;
	private ArrayList<Double> end_coordinate_pair;
	private ArrayList<Double> pre_coordinate_pair;
	private ArrayList<Double> start_index_pair;
	private ArrayList<ArrayList<Double>> connection_index_pair;
	private ArrayList<Double> end_index_pair;
	private double x;
	private double y;
	private ArrayList<UAV> TrajectoryList;
	// private int History;
	private int[] History;
	private int[] current_agents;
	private double speedX;
	private double speedY;
	private int directionX;
	private int directionY;
	private int TrajectoryState;
	// Path loss distance parameters
	private double log_distance_loss;
	private boolean movestatus;
	private int cal_count;
	private boolean unmanned;

	// operation for loading specific mission
	private Operation my_operation;
	private Map<String, ArrayList<Double>> airport_list;
	private Operator my_operator;
	private int duration;

	double angle;
	private int current_batch;
	private int turncount;

	// block area
	private Block_area BlockArea;
	private boolean if_cross_BlockArea;

	// Constructor
	public UAV(Geography<Object> geography, double height, double speed,
			boolean if_straight_path, boolean if_routing_path, int start_time,
			double tx_power, double rx_threshold, boolean if_nearest_target,
			String mode, Map<String, ArrayList<Double>> airport_list,
			Operator my_operator,
			int current_batch, int level, boolean unmanned, int id, long num_ch) {
		this.geography = geography;
		this.height = getMeters(height);
		this.speed = speed;
		// System.out.println(if_straight_path);
		this.if_straight_path = if_straight_path;
		this.if_routing__path = if_routing_path;
		this.num_ch = num_ch;
		// Defined in UAVmanagement line 293
		// this.tx_power = tx_power;
		// this.rx_threshold = rx_threshold;
		this.tx_power = 45; // in dB
		this.rx_threshold = 7; // in dB
		this.RSS = -1;
		this.current_SIR = -1;
		this.if_nearest_target = if_nearest_target;
		this.targets = new TreeList();
		this.my_operation = new Operation(mode);
		this.start_time = start_time;// (int) this.my_operation.return_start_time();
		this.airport_list = airport_list;
		this.my_operator = my_operator;
		this.duration = (int) my_operator.find(this.my_operation.return_mission(), this.my_operation.return_type())
				.return_duration();
		this.internal_time_step = -1;
		this.current_basestation = null;
		this.current_table = null;
		this.channel_id = -1;
		this.bs_id = -1;
		this.label = -1;
		this.freeze = 0;
		this.end_time = -1;
		this.current_batch = current_batch;
		this.fac = new GeometryFactory();
		this.x = 0.0;
		this.y = 0.0;
		this.TrajectoryList = new ArrayList<UAV>();
		// this.History = -1;
		this.History = new int[5];
		for (int i = 0; i < 5; i++) {
			this.History[i] = -1;
		}
		this.current_agents = new int[9];
		this.speedX = 0.0;
		this.speedY = 0.0;
		this.directionX = 1;
		this.directionY = 1;
		this.TrajectoryState = -1;
		this.movestatus = false;
		this.turncount = 0;
		this.BlockArea = new Block_area();
		this.if_cross_BlockArea = true;
		this.level = level;
		this.cal_count = 0;
		this.unmanned = unmanned;
		if (id != -1) {
			this.id = id;
		} else {
			this.id = this.hashCode();
		}
		this.ue = new UserEquipment(this.id);
		this.distance = 0.0;

		// if (UAV.os == null) {
		// System.out.println("OS Null!");
		// }
		System.out.println("ID:" + this.id);
		/*
		 * if (UAV.os != null && this.id > -1) {
		 * String uav_id = String.valueOf(this.id);
		 * Geometry pt = geography.getGeometry(this);
		 * String lat = String.valueOf(pt.getCoordinate().y);
		 * String lng = String.valueOf(pt.getCoordinate().x);
		 * String delay = String.valueOf(0)+'s';
		 * System.out.println("lat:"+lat);
		 * byte[] buf = UAV.rocb.buildCreationInfo(delay, uav_id, lat, lng, 5);
		 * try {
		 * UAV.os.write(buf);
		 * } catch (Exception e) {
		 * System.out.println(e);
		 * }
		 * }
		 */
	}

	// Random Constructor
	public UAV(Geography<Object> geography, double height, double speed,
			boolean if_straight_path, boolean if_routing_path, int start_time,
			double tx_power, double rx_threshold, boolean if_nearest_target,
			String mode, Map<String, ArrayList<Double>> airport_list,
			int current_batch, int level, boolean unmanned,int id, long num_ch) {
		this.geography = geography;
		this.height = getMeters(height);
		this.speed = speed;
		// System.out.println(if_straight_path);
		this.if_straight_path = if_straight_path;
		this.if_routing__path = if_routing_path;
		this.num_ch = num_ch;
		// Defined in UAVmanagement line 293
		// this.tx_power = tx_power;
		// this.rx_threshold = rx_threshold;
		this.tx_power = 45; // in dB
		this.rx_threshold = 7; // in dB
		this.RSS = -1;
		this.current_SIR = -1;
		this.if_nearest_target = if_nearest_target;
		this.targets = new TreeList();
		this.my_operation = new Operation(mode);
		this.start_time = start_time;// (int) this.my_operation.return_start_time();
		this.airport_list = airport_list;
		this.internal_time_step = -1;
		this.current_basestation = null;
		this.current_table = null;
		// this.current_table = new Global_communication(1234567);
		this.channel_id = -1;
		this.bs_id = -1;
		this.label = -1;
		this.freeze = 0;
		this.end_time = -1;
		this.current_batch = current_batch;
		this.fac = new GeometryFactory();
		this.x = 0.0;
		this.y = 0.0;
		this.TrajectoryList = new ArrayList<UAV>();
		// this.History = -1;
		this.History = new int[5];
		for (int i = 0; i < 5; i++) {
			this.History[i] = -1;
		}
		this.current_agents = new int[9];
		this.speedX = 0.0;
		this.speedY = 0.0;
		this.directionX = 1;
		this.directionY = 1;
		this.TrajectoryState = -1;
		this.movestatus = false;
		this.turncount = 0;
		this.BlockArea = new Block_area();
		this.if_cross_BlockArea = true;
		this.level = level;
		this.cal_count = 0;
		this.unmanned = unmanned;
		if (id != -1) {
			this.id = id;
		} else {
			this.id = this.hashCode();
		}
		this.ue = new UserEquipment(this.id);
		this.distance = 0.0;

		// if (UAV.os == null) {
		// System.out.println("OS Null!");
		// }
		System.out.println("ID:" + this.id);
		/*
		 * if (UAV.os != null && this.id > -1) {
		 * String uav_id = String.valueOf(this.id);
		 * Geometry pt = geography.getGeometry(this);
		 * String lat = String.valueOf(pt.getCoordinate().y);
		 * String lng = String.valueOf(pt.getCoordinate().x);
		 * String delay = String.valueOf(0)+'s';
		 * System.out.println("ID:"+this.id);
		 * byte[] buf = UAV.rocb.buildCreationInfo(delay, uav_id, lat, lng, 5);
		 * try {
		 * UAV.os.write(buf);
		 * } catch (Exception e) {
		 * System.out.println(e);
		 * }
		 * }
		 */
		// this.current_table.register_uav(this.hashCode());
	}

	public UserEquipment getUe() {
		return ue;
	}

	public void setUe(UserEquipment ue) {
		this.ue = ue;
	}

	public boolean return_unmanned() {
		return this.unmanned;
	}

	public int return_level() {
		return this.level;
	}

	public boolean check_TrajList() {
		return TrajectoryList.isEmpty();
	}

	public boolean add_Tra_UAV(UAV uav) {
		if (TrajectoryList.contains(uav)) {
			return false;
		} else {
			return TrajectoryList.add(uav);
		}
	}

	public void set_UavTraj(int Traj) {
		this.TrajectoryState = Traj;
	}

	public void remove_Tra_UAV(UAV uav) {
		TrajectoryList.remove(uav);
	}

	public void set_speed(double new_speed) {
		this.speed = new_speed;
	}

	public double return_speed() {
		return this.speed;
	}

	public void set_level_inf(ArrayList<Integer> level_inf) {
		this.level_inf = level_inf;
	}

	public ArrayList<Integer> return_level_inf() {
		return this.level_inf;
	}

	public void set_start_coordinate_pair(ArrayList<Double> start_coordinate_pair) {
		this.start_coordinate_pair = start_coordinate_pair;
	}

	public ArrayList<Double> return_start_coordinate_pair() {
		return this.start_coordinate_pair;
	}

	public void set_start_index_pair(ArrayList<Double> start_index_pair) {
		this.start_index_pair = start_index_pair;
	}

	public ArrayList<Double> return_start_index_pair() {
		return this.start_index_pair;
	}

	public void set_connection_coordinate_pair(ArrayList<ArrayList<Double>> connection_coordinate_pair) {
		this.connection_coordinate_pair = connection_coordinate_pair;
	}

	public ArrayList<ArrayList<Double>> return_connection_coordinate_pair() {
		return this.connection_coordinate_pair;
	}

	public void set_connection_index_pair(ArrayList<ArrayList<Double>> connection_index_pair) {
		this.connection_index_pair = connection_index_pair;
	}

	public ArrayList<ArrayList<Double>> return_index_pair() {
		return this.connection_index_pair;
	}

	public void set_end_coordinate_pair(ArrayList<Double> end_coordinate_pair) {
		this.end_coordinate_pair = end_coordinate_pair;
	}

	public ArrayList<Double> return_end_coordinate_pair() {
		return this.end_coordinate_pair;
	}

	public void set_end_index_pair(ArrayList<Double> end_index_pair) {
		this.end_index_pair = end_index_pair;
	}

	public ArrayList<Double> return_end_index_pair() {
		return this.end_index_pair;
	}

	public int return_end_time() {
		return this.end_time;
	}

	public int return_start_time() {
		return this.start_time;
	}

	public int return_internal_time() {
		return this.internal_time_step;
	}

	public Operation return_Operation() {
		return my_operation;
	}

	public int set_internal_time(int time) {
		return this.internal_time_step = time;
	}

	public int return_Id() {
		return this.id;
	}

	public int UAV_baseStation() {
		return this.current_basestation.return_bs_id();
	}

	public long return_channel() {
		return this.channel_id;
	}

	// Calculate the path loss distance between UAV and base station
	// PL(d0) = Path Loss in dB at a distance d0
	// PLd>d0 = Path Loss in dB at an arbitrary distance d
	// n = Path Loss exponent
	// loss = A zero-mean Gaussian distributed random variable (in dB) with standard
	// deviation
	private double cal_path_loss_distance_urban(double d_0, double d, double n, double x) {
		// double PL = 38.02 + 10 * n * Math.log10(d/d_0) + x;
		double f_c = 750; // carrier frequency = 7.5MHz GHz for satellite communication 750MHz for 4G LTE
		double hh = 120; // antenna height, for tmp use
		double d_3D = (Math.sqrt(Math.pow(d, 2) + Math.pow(hh, 2))) / 1000;

		double PL = 28.0 + 22 * Math.log10(d_3D) + 20 * Math.log10(f_c);
		// System.out.print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n");
		// System.out.print("urban\n");
		// System.out.print(PL);

		return PL;
	}

	private double cal_path_loss_distance_rural(double d_0, double d, double n, double x) {
		// double PL = 38.02 + 10 * n * Math.log10(d/d_0) + x;
		double f_c = 1620; // carrier frequency = 7.5MHz GHz for satellite communication 750MHz for 4G LTE
		double hh; // antenna height, for tmp use
		if (this.level == 1) {
			hh = 260;
		} else if (this.level == 2) {
			hh = 640;
		} else if (this.level == 3) {
			hh = 1219;
		} else {
			hh = 3810;
		}
		double d_3D = (Math.sqrt(Math.pow(d, 2) + Math.pow(hh, 2))) / 1000;

		// double PL = 28.0 + 22 * Math.log10(d_3D) + 20 * Math.log10(f_c) ;
		double PL = Math.max(23.9 - 1.8 * Math.log10(hh), 20) * Math.log10(d_3D)
				+ 20 * Math.log10((40 * (Math.PI) * f_c) / 3);
		// System.out.print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n");
		// System.out.print("rural\n");
		// System.out.print(PL);

		return PL;
	}

	// move toward the target
	private void moveTowards_straight(Geometry pt) {

		// move the UAV
		Geometry myPoint = geography.getGeometry(this);

		double dx = pt.getCoordinate().x - myPoint.getCoordinate().x;
		double dy = pt.getCoordinate().y - myPoint.getCoordinate().y;

		if (Math.atan2(dy, dx) < 0) {
			angle = 2 * Math.PI + Math.atan2(dy, dx);
		} else {
			angle = Math.atan2(dy, dx);
		}

		geography.moveByVector(this, this.speed, SI.METER, angle);
	}

	// move toward the target
	private void moveTowards_by_coordinate_straight(Coordinate pt) {

		// move the UAV
		Geometry myPoint = geography.getGeometry(this);

		double dx = pt.x - myPoint.getCoordinate().x;
		double dy = pt.y - myPoint.getCoordinate().y;

		if (Math.atan2(dy, dx) < 0) {
			angle = 2 * Math.PI + Math.atan2(dy, dx);
		} else {
			angle = Math.atan2(dy, dx);
		}

		geography.moveByVector(this, this.speed, SI.METER, angle);
	}

	// move toward the target
	private void moveTowards_Manhattan(Geometry pt) {

		// move the UAV
		Geometry myPoint = geography.getGeometry(this);
		double distance_x = distance2Coordinate(geography, myPoint.getCoordinate().y, pt.getCoordinate().x,
				myPoint.getCoordinate().y, myPoint.getCoordinate().x);
		double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, myPoint.getCoordinate().x,
				myPoint.getCoordinate().y, myPoint.getCoordinate().x);

		if (Math.abs(distance_y) > this.speed) {
			double dx = myPoint.getCoordinate().x - myPoint.getCoordinate().x;
			double dy = pt.getCoordinate().y - myPoint.getCoordinate().y;

			if (Math.atan2(dy, dx) < 0) {
				angle = 2 * Math.PI + Math.atan2(dy, dx);
			} else {
				angle = Math.atan2(dy, dx);
			}

			geography.moveByVector(this, this.speed, SI.METER, angle);
		} else {
			double dx = pt.getCoordinate().x - myPoint.getCoordinate().x;
			double dy = myPoint.getCoordinate().y - myPoint.getCoordinate().y;

			if (Math.atan2(dy, dx) < 0) {
				angle = 2 * Math.PI + Math.atan2(dy, dx);
			} else {
				angle = Math.atan2(dy, dx);
			}
			geography.moveByVector(this, this.speed, SI.METER, angle);
		}

	}

	// move toward the target
	private void moveTowards_by_coordinate_Manhattan(Coordinate pt) {

		// move the UAV
		Geometry myPoint = geography.getGeometry(this);
		double distance_x = distance2Coordinate(geography, myPoint.getCoordinate().y, pt.x, myPoint.getCoordinate().y,
				myPoint.getCoordinate().x);
		double distance_y = distance2Coordinate(geography, pt.y, myPoint.getCoordinate().x, myPoint.getCoordinate().y,
				myPoint.getCoordinate().x);

		if (Math.abs(distance_y) > this.speed) {
			double dx = myPoint.getCoordinate().x - myPoint.getCoordinate().x;
			double dy = pt.y - myPoint.getCoordinate().y;

			if (Math.atan2(dy, dx) < 0) {
				angle = 2 * Math.PI + Math.atan2(dy, dx);
			} else {
				angle = Math.atan2(dy, dx);
			}

			geography.moveByVector(this, this.speed, SI.METER, angle);
			this.distance += this.speed;
		} else {
			double dx = pt.x - myPoint.getCoordinate().x;
			double dy = myPoint.getCoordinate().y - myPoint.getCoordinate().y;

			if (Math.atan2(dy, dx) < 0) {
				angle = 2 * Math.PI + Math.atan2(dy, dx);
			} else {
				angle = Math.atan2(dy, dx);
			}
			geography.moveByVector(this, this.speed, SI.METER, angle);
			this.distance += this.speed;
		}

	}

	// create a communication link between UAV and base station based on the global
	// communication table
	// UAV reports RSS to base stations (corresponding channel), replace old values
	// UAV checks the SIR level
	// if current link provides good service (i.e., SIR > rx_threshold, but not
	// necessarily the maximum SIR), the current link will be kept
	// else UAV check the global communication table to establish a new link

	private void communicate_with_base_station() {
		// need refresh(initialize or update)
		// case 1: keep the same link (sinr > th) and mark label as 1
		// case 2: update link : if succeed: label as 0; otherwise: if link is poor -->
		// label as 2, if no available connection : label as 3
		// label 2 and 3 means satellite links

		if (this.unmanned) {

			// System.out.print("check UAV" + this.unmanned + "\n");

			if (this.freeze == 0) {

				for (Object obj : geography.getAllObjects()) {
					if (obj instanceof Global_communication) {
						Global_communication current_Gtable = (Global_communication) obj;
						current_table = current_Gtable;
						// fetch the current communication table as an object
						break;
					}
				}

				double temp_current_distance = 0;

				Map<Object, Double> unsorted_base_station_distance_map = new HashMap<Object, Double>();

				Geometry pt = geography.getGeometry(this);

				List<Object> base_stations = new ArrayList<Object>();
				for (Object obj : geography.getAllObjects()) {
					if (obj instanceof Base_Station) {
						base_stations.add(obj);
					}
				}

				if (base_stations.size() > 0) {

					for (Object obj : base_stations) {
						Geometry tmp = geography.getGeometry(obj);

						Base_Station temp = (Base_Station) obj;
						// System.out.print("check base station id \n");
						// System.out.print(temp.return_bs_id() + "\n");
						// System.out.print(temp.get_num_of_channels() + "\n");
						//
						// System.out.print(tmp.getCoordinate().x + "\n");
						// System.out.print(tmp.getCoordinate().y + "\n");
						//
						temp.remove_UAV((UAV) this);

						double distance = distance2Coordinate(geography, pt.getCoordinate(), tmp.getCoordinate());

						if (temp.return_bs_id() == this.bs_id) {
							temp_current_distance = distance;
							// System.out.print("+++++++++++distance++++++++++++++++++");
							// System.out.print(distance+"\n");

						}

						unsorted_base_station_distance_map.put(obj, distance);

					}

					boolean refresh;
					if (this.bs_id != -1 && this.channel_id != -1) {
						// System.out.print("check UAV" + this.hashCode() + "\n");
						// System.out.print("check BS" + this.bs_id + "\n");
						// System.out.print("double check BS" + this.current_basestation.return_bs_id()
						// + "\n");
						// System.out.print("check ch" + this.channel_id + "\n");
						// current_table.check();
						// System.out.print("**********************************\n");
						// System.out.print(this.num_ch + "\n");

						String remove_key = Integer.toString(this.current_basestation.return_bs_id())
								+ Long.toString(this.channel_id);
						this.current_basestation.removeLink(remove_key);
						this.current_basestation.remove_UAV((UAV) this);
						current_table.delete_report(this.id);

						// current_table.check();

						// remove record in communication table; remove the record in channel and base
						// station; still keep the record at uav

						// if the uav connected with a base station, check new SINR: new UAV location
						// and new interference
						double tem_rss = -20000;
						double tem_SINR = -200;
						if (this.current_basestation.get_bs_label().equals("rural")) {
							if (temp_current_distance <= 10000) {
								double n = 3;
								tem_rss = this.tx_power
										- this.cal_path_loss_distance_rural(1.00, temp_current_distance, 3, 0);
							} else {
								refresh = true;
							}
						} else {
							if (temp_current_distance <= 4000) {
								tem_rss = this.tx_power
										- this.cal_path_loss_distance_urban(1.00, temp_current_distance, 3, 0);
							} else {
								refresh = true;
							}
						}

						if (tem_rss != -20000) {
							// current_table.add_report(this.bs_id, this.channel_id, this.hashCode(),
							// tem_rss);
							this.RSS = tem_rss;
							tem_SINR = tem_rss - current_table.check_SIR(this.bs_id, this.channel_id, this.id);
						}

						if (tem_SINR >= -16) {
							refresh = false;
							this.label = 1;
							this.current_SIR = tem_SINR;
							String tmp_key = Integer.toString(this.current_basestation.return_bs_id())
									+ Long.toString(this.channel_id);
							this.current_basestation.assignLink(tmp_key);
							this.RSS = tem_rss;
							current_table.add_report(this.bs_id, this.channel_id, this.id, this.RSS);
							edge_operation(base_stations, this.current_basestation, this, this.RSS, this.channel_id,
									this.bs_id);
						} else {
							current_table.delete_report(this.id);
							refresh = true;
						}

					} else {
						refresh = true;
					}

					if (refresh == true) {

						this.current_basestation = null;
						this.bs_id = -1;
						this.channel_id = -1;
						this.current_SIR = -1;

						Map<Object, Double> sorted_base_station_distance_map = sortMapByValues(
								unsorted_base_station_distance_map);

						Object selected_base_station = null;

						this.current_basestation = null;
						this.RSS = -1;
						double cal_rss = -1;
						double SINR_tmp = -20000;
						double RSS_bs = -20000;
						int bs_id_tmp = -1;
						long ch_id_tmp = -1;
						// int count_check = 0;

						for (Map.Entry<Object, Double> entry : sorted_base_station_distance_map.entrySet()) {
							// for(int count = 0; count < 10; count ++) {

							selected_base_station = entry.getKey();

							double distance = Math.floor(entry.getValue());

							Base_Station temp = (Base_Station) selected_base_station;

							if (temp.get_bs_label().equals("rural")) {

								if (distance <= 10000) {
									double n = generate_n_by_probability(entry.getValue());
									this.log_distance_loss = this.cal_path_loss_distance_rural(1.00, distance, 3, 0);
									cal_rss = this.tx_power - this.log_distance_loss;

								} else {
									this.log_distance_loss = 100000;
									this.label = 6;
								}

							} else {

								if (distance <= 4000) {
									double n = generate_n_by_probability(entry.getValue());
									this.log_distance_loss = this.cal_path_loss_distance_urban(1.00, distance, 3, 0);
									cal_rss = this.tx_power - this.log_distance_loss;

								} else {
									this.log_distance_loss = 100000;
									this.label = 6;
								}

							}

							// only when the uav is in the coverage, we calculate the RSS
							if (this.log_distance_loss != 100000) {
								RSS_bs = this.tx_power - this.log_distance_loss;

								List<Integer> ch_list = new ArrayList<>();
								for (int ch_ = 0; ch_ < temp.get_num_of_channels(); ch_++) {
									ch_list.add(ch_);
								}
								// System.out.print("before" + ch_list + "\n");
								// int iuu;
								// iuu = ch_list.get(0);
								// System.out.print("before" + iuu + "\n");
								Collections.shuffle(ch_list);
								// System.out.print("after" + ch_list + "\n");
								// iuu = ch_list.get(0);
								// System.out.print("after" + iuu + "\n");
								//

								// current_table.add_report(this.bs_id, this.channel_id, this.hashCode(),
								// RSS_bs);
								for (int ch_count = 0; ch_count < temp.get_num_of_channels(); ch_count++) {
									// long ch_count_id = ch_count;
									long ch_count_id = ch_list.get(ch_count);
									String tmp_findCh_key = Integer.toString(temp.return_bs_id())
											+ Long.toString(ch_count_id);
									// System.out.print("check channel table \n");
									// System.out.print(tmp_findCh_key + "\n");
									// System.out.print(temp.checkChannel(tmp_findCh_key) + "\n");
									// System.out.print("check UAV" + this.hashCode() + "\n");
									// System.out.print("check BS" + temp.return_bs_id() + "\n");
									// System.out.print("check ch" + ch_count_id + "\n");
									// System.out.print("check ch num" + temp.get_current_num_UAV() + "\n");
									if (temp.get_current_num_UAV() < temp.get_num_of_channels()) {
										if (temp.checkChannel(tmp_findCh_key) == false) {

											double SINR_tmp_ch = 0;

											SINR_tmp_ch = RSS_bs - current_table.check_SIR(temp.return_bs_id(),
													ch_count_id, this.id);
											if (SINR_tmp_ch > SINR_tmp) {
												this.current_basestation = (Base_Station) selected_base_station;
												SINR_tmp = SINR_tmp_ch;

												bs_id_tmp = temp.return_bs_id();
												ch_id_tmp = ch_count_id;
												this.RSS = RSS_bs;
											}
										}

									} else {
										ch_id_tmp = -1;
									}
								}
							}

							// count}

							this.bs_id = bs_id_tmp;
							this.channel_id = ch_id_tmp;
							this.current_SIR = SINR_tmp;

						}
						if (this.current_SIR > -16 && this.bs_id != -1 && this.channel_id != -1) {
							this.label = 0;
							String tmp_key = Integer.toString(this.current_basestation.return_bs_id())
									+ Long.toString(this.channel_id);
							this.current_basestation.assignLink(tmp_key);
							this.RSS = RSS_bs;
							// System.out.print("bs-----------------------------------\n");
							// System.out.print(this.bs_id + "\n");
							// System.out.print("channel-----------------------------------\n");
							// System.out.print( this.channel_id+ "\n");
							// System.out.print("rss-----------------------------------\n");
							// System.out.print(this.RSS+ "\n");
							// System.out.print("SINR-----------------------------------\n");
							// System.out.print(this.current_SIR+ "\n");
							current_table.add_report(this.bs_id, this.channel_id, this.id, this.RSS);
							current_table.check();
							edge_operation(base_stations, selected_base_station, this, this.RSS, this.channel_id,
									this.bs_id);
						} else {
							if (this.bs_id == -1) {
								this.bs_id = -1;
								this.channel_id = -1;
								this.label = 2;
								this.current_SIR = -1;
								this.freeze = 5;
							} else {
								this.bs_id = -1;
								this.channel_id = -1;
								this.label = 3;
								this.current_SIR = -1;
								this.freeze = 5;
							}
						}
					}
				}
			} else {
				this.freeze -= 1;
				// System.out.print(this.freeze);
				this.bs_id = -1;
				this.channel_id = -1;
				this.label = 4;
				this.current_SIR = -1;

			}
		}

	}

	private double compute_angle(UAV uav) {
		double res;
		Geometry pt = geography.getGeometry(uav);
		double x = uav.return_end_coordinate_pair().get(0);
		double y = uav.return_end_coordinate_pair().get(1);

		Coordinate target = new Coordinate(x, y);

		double distance = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x, target.y,
				target.x);
		double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
				pt.getCoordinate().y, target.x);
		// double distance_y = distance2Coordinate(geography, pt.getCoordinate().y,
		// pt.getCoordinate().x, target.y, pt.getCoordinate().x);

		res = uav.directionY * Math.acos(distance_x / distance);
		return res;
	}

	private void compute_attract(UAV uav) {
		Geometry pt = geography.getGeometry(uav);
		double x = uav.return_end_coordinate_pair().get(0);
		double y = uav.return_end_coordinate_pair().get(1);

		Coordinate target = new Coordinate(x, y);
		double ang;

		double distance = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x, target.y,
				target.x);
		double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
				pt.getCoordinate().y, target.x);
		// double distance_y = distance2Coordinate(geography, pt.getCoordinate().y,
		// pt.getCoordinate().x, target.y, pt.getCoordinate().x);
		if (uav.directionX > 0) {
			ang = Math.acos(distance_x / distance);
		} else {
			ang = Math.PI - Math.acos(distance_x / distance);
		}

		double Yatx = 15 * distance * Math.cos(ang);
		double Yaty = 15 * distance * Math.sin(ang);
	}

	private void compute_repulsion(UAV uav) {
		Geometry pt = geography.getGeometry(uav);
		// System.out.println("In:");
		System.out.println("Y:" + pt.getCoordinate().y);
		// System.out.println(uav.hashCode());
		uav.TrajectoryState = -1;
		// -111.266107, 48.994677, -109.805044, 48.027456,
		short uav_x = (short) (distance2Coordinate(geography, 47.936572, -103.4359855, 47.936572, pt.getCoordinate().x)
				/ (18));
		short uav_y = (short) (distance2Coordinate(geography, 47.936572, -103.4359855, pt.getCoordinate().y,
				-103.4359855) / (18));
		//
		// short uav_x = (short) (distance2Coordinate(geography, -76.2597052,
		// 43.0802922, -76.2597052,
		// pt.getCoordinate().y) / (18));
		// short uav_y = (short) (distance2Coordinate(geography, -76.2597052,
		// 43.0802922, pt.getCoordinate().x, 43.0802922)
		// / (18));
		// System.out.println("Index:"+uav_x+","+uav_y);
		double x = uav.return_end_coordinate_pair().get(0);
		double y = uav.return_end_coordinate_pair().get(1);

		Coordinate target = new Coordinate(x, y);

		short target_x = (short) (distance2Coordinate(geography, 47.936572,
				-103.4359855, 47.936572, target.x) / (18));
		short target_y = (short) (distance2Coordinate(geography, 47.936572,
				-103.4359855, target.y, -103.4359855) / (18));
		// short target_x = (short) (distance2Coordinate(geography, -76.2597052,
		// 43.0802922, -76.2597052, target.y)
		// / (18));
		// short target_y = (short) (distance2Coordinate(geography, -76.2597052,
		// 43.0802922, target.x, 43.0802922) / (18));
		// System.out.println("Index:"+target_x+","+target_y);
		double distance_target = Math
				.sqrt((uav_x - target_x) * (uav_x - target_x) + (uav_y - target_y) * (uav_y - target_y));
		// double Yatx=90*(target_x - uav_x)/distance_target;
		// double Yaty=90*(target_y - uav_y)/distance_target;
		double Yatx = 10 * (target_x - uav_x) / distance_target;
		double Yaty = 10 * (target_y - uav_y) / distance_target;
		int i = 0;
		int m = 5;
		double Po = 200;
		int len = this.TrajectoryList.size();
		double[] Yrerx = new double[len];
		double[] Yrery = new double[len];
		// System.out.println("Len:"+len);
		// for (UAV uavt : this.TrajectoryList) {
		//// System.out.println("UAVID:" + uavt.hashCode());
		// }
		if (len > 0) {
			for (UAV uavt : this.TrajectoryList) {
				Geometry pto = geography.getGeometry(uavt);
				if (pto == null) {
					continue;
				}
				uavt.set_UavTraj(1);
				uavt.TrajectoryState = 1;
				short ouav_x = (short) (distance2Coordinate(geography, 47.936572,
						-103.4359855, 47.936572, pto.getCoordinate().x) / (18));
				short ouav_y = (short) (distance2Coordinate(geography, 47.936572,
						-103.4359855, pto.getCoordinate().y, -103.4359855) / (18));
				// short ouav_x = (short) (distance2Coordinate(geography, -76.2597052,
				// 43.0802922, -76.2597052,
				// pto.getCoordinate().y) / (18));
				// short ouav_y = (short) (distance2Coordinate(geography, -76.2597052,
				// 43.0802922, pto.getCoordinate().x,
				// 43.0802922) / (18));
				// System.out.println("Index:"+ouav_x+","+ouav_y);
				// double x1 = uavt.return_end_coordinate_pair().get(0);
				// double y1 = uavt.return_end_coordinate_pair().get(1);
				//
				// Coordinate target1 = new Coordinate(x1, y1);
				//
				// short target_x1 = (short)(distance2Coordinate(geography, -76.2597052,
				// 43.0802922, -76.2597052, target1.y)/ (18));
				// short target_y1 = (short) (distance2Coordinate(geography, -76.2597052,
				// 43.0802922, target1.x, 43.0802922) / (18));
				// System.out.println("Index:"+target_x1+","+target_y1);
				double distance = (Math.sqrt((uav_x - ouav_x) * (uav_x - ouav_x) + (uav_y - ouav_y) * (uav_y - ouav_y))
						- 50);
				double repulse = m * (1 / distance - 1 / Po) * (1 / (distance * distance)) / 0.001;
				Yrerx[i] = repulse * (uav_x - ouav_x) / (distance);
				Yrery[i] = repulse * (uav_y - ouav_y) / (distance);
				i++;
			}
			if (i == 0)
				return;
			double sum_x = (Yatx / 9);
			double sum_y = (Yaty / 9);

			for (i = 0; i < len; i++) {
				sum_x += (Yrerx[i] / 9);
				sum_y += (Yrery[i] / 9);
			}

			// add block repulsion here
			// double[] distance2block = uav.BlockArea.uav2block_distance(uav_x, uav_y,
			// target_x, target_y, (short)55);
			// if(distance2block[0] > 0 ) {
			// double block_distance = distance2block[0] - 50;
			// double rep =
			// m*(1/block_distance-1/Po)*(1/(block_distance*block_distance))/0.001;
			// sum_x += (1 * rep * distance2block[1] / block_distance)/9;
			// sum_y += (1 * rep * distance2block[2] / block_distance)/9;
			//// System.out.println("-----++-----the force with block in x:" + (1 * rep *
			// distance2block[1] / block_distance)/9);
			//// System.out.println("-----++-----the force with block in y:" + (1 * rep *
			// distance2block[2] / block_distance)/9);
			// }

			if (sum_x > 3) {
				sum_x = 3;
			} else if (sum_x < -3) {
				sum_x = -3;
			}
			if (sum_y > 3) {
				sum_y = 3;
			} else if (sum_y < -3) {
				sum_y = -3;
			}
			if (Math.abs(sum_x) < 0.2) {
				if (sum_x > 0) {
					sum_x += 0.7;
				} else if (sum_x < 0) {
					sum_x -= 0.7;
				}
			}
			if (Math.abs(sum_y) < 0.2) {
				if (sum_y > 0) {
					sum_y += 0.7;
				} else if (sum_y < 0) {
					sum_y -= 0.7;
				}
			}
			short res_x = (short) (sum_x + uav_x);
			short res_y = (short) (sum_y + uav_y);
			// double new_speed = Math.sqrt(sum_x*sum_x+sum_y*sum_y);
			// double new_angle=0 ;

			// System.out.println("Final Force:" + res_x + "," + res_y);
			if (res_x <= 700 && res_x > 0 && res_y <= 1400 && res_y > 0) {
				save_speed(sum_x * 18, sum_y * 18);
				if (sum_y >= 0) {
					geography.moveByVector(uav, Math.abs(sum_y * 18), SI.METER, 0);// y+1
				} else {
					geography.moveByVector(uav, Math.abs(sum_y * 18), SI.METER, Math.PI);// ;x-1
				}
				if (sum_x >= 0) {
					geography.moveByVector(uav, Math.abs(sum_x * 18), SI.METER, Math.PI * 3 / 2);// x-1
				} else {
					geography.moveByVector(uav, Math.abs(sum_x * 18), SI.METER, Math.PI / 2);
				}
				// if(sum_y >= 0) {
				// geography.moveByVector(uav, Math.abs(sum_y*90), SI.METER, 0);//y+1
				// }
				// else {
				// geography.moveByVector(uav, Math.abs(sum_y*90), SI.METER, Math.PI);//;x-1
				// }
				// if(sum_x >= 0) {
				// geography.moveByVector(uav, Math.abs(sum_x*90), SI.METER, Math.PI*3/2);//x-1
				// }
				// else {
				// geography.moveByVector(uav, Math.abs(sum_x*90), SI.METER, Math.PI/2);
				// }
				uav.cal_count++;
				uav.movestatus = true;
				uav.TrajectoryState = 1;
				uav.if_cross_BlockArea = true;
				// double dy = sum_y;
				// double dx = 0;
				System.out.println("Moved-conflict");
				// if (Math.atan2(dy, dx) < 0) {
				// new_angle = 2 * Math.PI + Math.atan2(dy, dx);
				// } else {
				// new_angle = Math.atan2(dy, dx);
				// }
				// System.out.println(new_angle);
				// if(sum_x<0) new_angle = Math.PI - new_angle;
				// geography.moveByVector(uav, sum_x*18, SI.METER, 4.71);
				Geometry pt1 = geography.getGeometry(uav);
				// -111.266107, 48.994677
				short newuav_x = (short) (distance2Coordinate(geography, 47.936575,
						-103.4359855, 47.93657, pt1.getCoordinate().x) / (18));
				short newuav_y = (short) (distance2Coordinate(geography, 47.93657,
						-103.4359855, pt1.getCoordinate().y, -103.4359855) / (18));
				// short newuav_x = (short) (distance2Coordinate(geography, -76.2597052,
				// 43.0802922, -76.2597052,
				// pt1.getCoordinate().y) / (18));
				// short newuav_y = (short) (distance2Coordinate(geography, -76.2597052,
				// 43.0802922, pt1.getCoordinate().x,
				// 43.0802922) / (18));
				// System.out.println("Final:" + newuav_x + "," + newuav_y);
				// System.out.println("Cur Time:"+this.internal_time_step);

			}
		}
	}

	// TODO: Wtf is this 
	private boolean communicate_with_agent_traj(int AgentId) {
		boolean res = false;
		if (this.unmanned) {
			Geometry pt = geography.getGeometry(this);
			List<Object> agents = new ArrayList<Object>();
			for (Object obj : geography.getAllObjects()) {
				if (obj instanceof Agent) {
					agents.add(obj);
				}
			}
			if (CheckAgents(AgentId)) { // In same Agent Area
				for (Object obj : agents) {
					Agent temp = (Agent) obj;// Agent Information
					if (temp.return_Agent_id() == this.current_agents[0]) {
						temp.remove_UAV(this, this.level);
					}
				}
				this.TrajectoryList = new ArrayList<UAV>();
				AddAgents(AgentId);
				for (Object obj : agents) {
					Agent temp = (Agent) obj;// Agent Information
					for (int i = 0; i < 9; i++) {
						if (temp.return_Agent_id() == this.current_agents[i]) {
							ArrayList<UAV> current_uavs_list = temp.return_current_uavs_list(this.level);
							for (UAV other_uav : current_uavs_list) {
								Geometry tp = geography.getGeometry(other_uav);
								if (CheckTrajectory_traj(this, other_uav)) {
									res = true;
								}
							}
							if (i == 0) {
								temp.add_UAV(this, this.level);
							}
						}
					}
				}
			} else {
				for (Object obj : agents) {
					Agent temp = (Agent) obj;// Agent Information
					if (temp.return_Agent_id() == this.current_agents[0]) {
						temp.remove_UAV(this, this.level);
					}
				}
				this.TrajectoryList = new ArrayList<UAV>();
				AddAgents(AgentId);
				for (Object obj : agents) {
					Agent temp = (Agent) obj;// Agent Information
					for (int i = 0; i < 9; i++) {
						if (temp.return_Agent_id() == this.current_agents[i]) {
							ArrayList<UAV> current_uavs_list = temp.return_current_uavs_list(this.level);
							for (UAV other_uav : current_uavs_list) {
								Geometry tp = geography.getGeometry(other_uav);
								if (CheckTrajectory_traj(this, other_uav)) {
									res = true;
								}
							}
							if (i == 0) {
								temp.add_UAV(this, this.level);
							}
						}
					}
				}
			}
		}
		return res;
	}

	private void communicate_with_agent(int AgentId) {
		if (this.unmanned) {
			Geometry pt = geography.getGeometry(this);
			List<Object> agents = new ArrayList<Object>();
			for (Object obj : geography.getAllObjects()) {
				if (obj instanceof Agent) {
					agents.add(obj);
				}
			}
			if (CheckAgents(AgentId) && this.movestatus == false) { // In same Agent Area
				this.TrajectoryState = -1;
				for (Object obj : agents) {
					Agent temp = (Agent) obj;// Agent Information
					if (temp.return_Agent_id() == this.current_agents[0]) {
						temp.remove_UAV(this, this.level);
					}
				}
				if (!this.check_TrajList()) {
					for (UAV uavt : this.TrajectoryList) {
						uavt.remove_Tra_UAV(this);
						if (uavt.check_TrajList()) {
							uavt.TrajectoryState = -1;
						}
						// this.remove_Tra_UAV(uavt);
					}
					// System.out.println("UAV:" + this.TrajectoryList.size());
				}
				this.TrajectoryList = new ArrayList<UAV>();
				AddAgents(AgentId);
				// System.out.println("==============================");
				for (Object obj : agents) {
					Agent temp = (Agent) obj;// Agent Information
					// if (temp.return_Flag() == -1) {
					for (int i = 0; i < 9; i++) {
						if (temp.return_Agent_id() == this.current_agents[i]) {
							ArrayList<UAV> current_uavs_list = temp.return_current_uavs_list(this.level);
							for (UAV other_uav : current_uavs_list) {
								Geometry tp = geography.getGeometry(other_uav);
								if (CheckTrajectory(this, other_uav)) {
									other_uav.add_Tra_UAV(this);
									this.add_Tra_UAV(other_uav);
									this.TrajectoryState = 1;
									other_uav.set_UavTraj(1);

								}
							}
							if (i == 0)
								temp.add_UAV(this, this.level);
						}
					}
					// }
				}
				if (!this.check_TrajList() && this.unmanned) {
					compute_repulsion(this);
					// System.out.println("Out");
					// System.out.println(this.hashCode());
				} else {
					this.TrajectoryState = -1;
				}
			} else if (this.TrajectoryState == 1 && this.movestatus == false) {
				if (!this.check_TrajList()) {
					for (UAV uavt : this.TrajectoryList) {
						uavt.remove_Tra_UAV(this);
						if (uavt.check_TrajList()) {
							uavt.TrajectoryState = -1;
						}
					}
				}
				this.TrajectoryList = new ArrayList<UAV>();
				for (Object obj : agents) {
					Agent temp = (Agent) obj;// Agent Information
					// if (temp.return_Flag() == -1) {
					for (int i = 0; i < 9; i++) {
						if (temp.return_Agent_id() == this.current_agents[i]) {
							ArrayList<UAV> current_uavs_list = temp.return_current_uavs_list(this.level);
							for (UAV other_uav : current_uavs_list) {
								Geometry tp = geography.getGeometry(other_uav);
								if (CheckTrajectory(this, other_uav)) {
									other_uav.add_Tra_UAV(this);
									this.add_Tra_UAV(other_uav);
									this.TrajectoryState = 1;
									other_uav.set_UavTraj(1);
								}
							}
							if (i == 0)
								temp.add_UAV(this, this.level);
						}
					}
					// }
				}
				if (!this.check_TrajList() && this.unmanned) {
					compute_repulsion(this);
					// this.TrajectoryState = 1;
					// System.out.println("Need Calculate");
				} else {
					this.TrajectoryState = -1;
				}
			}
		}
	}

	private boolean CheckAgents(int AgentId) {
		// System.out.println("current agents
		// id:"+this.current_agents[0]+","+this.current_agents[1]);
		if (this.current_agents[0] != AgentId) {
			return true;
		}
		return false;
	}

	private void AddAgents(int AgentId) {
		this.current_agents[0] = AgentId;
		this.current_agents[1] = AgentId - 101;
		this.current_agents[2] = AgentId - 100;
		this.current_agents[3] = AgentId - 99;
		this.current_agents[4] = AgentId - 1;
		this.current_agents[5] = AgentId + 1;
		this.current_agents[6] = AgentId + 99;
		this.current_agents[7] = AgentId + 100;
		this.current_agents[8] = AgentId + 101;
		for (int i = 1; i < 9; i++) {
			if (this.current_agents[i] <= 100)
				this.current_agents[i] = 0;
		}
	}

	// private static final double EARTH_RADIUS = 6371393; //
	// Ã¥Â¹Â³Ã¥ï¿½â€¡Ã¥ï¿½Å Ã¥Â¾â€ž,Ã¥ï¿½â€¢Ã¤Â½ï¿½Ã¯Â¼Å¡m

	/**
	 * Ã©â‚¬Å¡Ã¨Â¿â€¡ABÃ§â€šÂ¹Ã§Â»ï¿½Ã§ÂºÂ¬Ã¥ÂºÂ¦Ã¨Å½Â·Ã¥ï¿½â€“Ã¨Â·ï¿½Ã§Â¦Â»
	 * 
	 * @param pointA AÃ§â€šÂ¹(Ã§Â»ï¿½Ã¯Â¼Å’Ã§ÂºÂ¬)
	 * @param pointB BÃ§â€šÂ¹(Ã§Â»ï¿½Ã¯Â¼Å’Ã§ÂºÂ¬)
	 * @return Ã¨Â·ï¿½Ã§Â¦Â»(Ã¥ï¿½â€¢Ã¤Â½ï¿½Ã¯Â¼Å¡Ã§Â±Â³)
	 */
	public static double getDistance(double lon_1, double lat_1, double lon_2, double lat_2) {
		// Ã§Â»ï¿½Ã§ÂºÂ¬Ã¥ÂºÂ¦Ã¯Â¼Ë†Ã¨Â§â€™Ã¥ÂºÂ¦Ã¯Â¼â€°Ã¨Â½Â¬Ã¥Â¼Â§Ã¥ÂºÂ¦Ã£â‚¬â€šÃ¥Â¼Â§Ã¥ÂºÂ¦Ã§â€�Â¨Ã¤Â½Å“Ã¥ï¿½â€šÃ¦â€¢Â°Ã¯Â¼Å’Ã¤Â»Â¥Ã¨Â°Æ’Ã§â€�Â¨Math.cosÃ¥â€™Å’Math.sin
		double radiansAX = Math.toRadians(lon_1); // AÃ§Â»ï¿½Ã¥Â¼Â§Ã¥ÂºÂ¦
		double radiansAY = Math.toRadians(lat_1); // AÃ§ÂºÂ¬Ã¥Â¼Â§Ã¥ÂºÂ¦
		double radiansBX = Math.toRadians(lon_2); // BÃ§Â»ï¿½Ã¥Â¼Â§Ã¥ÂºÂ¦
		double radiansBY = Math.toRadians(lat_2); // BÃ§ÂºÂ¬Ã¥Â¼Â§Ã¥ÂºÂ¦

		// Ã¥â€¦Â¬Ã¥Â¼ï¿½Ã¤Â¸Â­Ã¢â‚¬Å“cosÃŽÂ²1cosÃŽÂ²2cosÃ¯Â¼Ë†ÃŽÂ±1-ÃŽÂ±2Ã¯Â¼â€°+sinÃŽÂ²1sinÃŽÂ²2Ã¢â‚¬ï¿½Ã§Å¡â€žÃ©Æ’Â¨Ã¥Ë†â€ Ã¯Â¼Å’Ã¥Â¾â€”Ã¥Ë†Â°Ã¢Ë†Â AOBÃ§Å¡â€žcosÃ¥â‚¬Â¼
		double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX)
				+ Math.sin(radiansAY) * Math.sin(radiansBY);
		// System.out.println("cos = " + cos); // Ã¥â‚¬Â¼Ã¥Å¸Å¸[-1,1]
		double acos = Math.acos(cos); // Ã¥ï¿½ï¿½Ã¤Â½â„¢Ã¥Â¼Â¦Ã¥â‚¬Â¼
		// System.out.println("acos = " + acos); // Ã¥â‚¬Â¼Ã¥Å¸Å¸[0,Ã�â‚¬]
		// System.out.println("Ã¢Ë†Â AOB = " + Math.toDegrees(acos)); //
		// Ã§ï¿½Æ’Ã¥Â¿Æ’Ã¨Â§â€™ Ã¥â‚¬Â¼Ã¥Å¸Å¸[0,180]
		return EARTH_RADIUS * acos; // Ã¦Å“â‚¬Ã§Â»Ë†Ã§Â»â€œÃ¦Å¾Å“
	}

	private static final double EARTH_RADIUS = 6378137.0;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double GetDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		// s = Math.round(s * 10000) / 10000;
		return s;
	}

	private boolean CheckTrajectory_traj(UAV current_uav, UAV other_uav) {
		// System.out.println("In: check");

		double other_x, other_y;

		int newdirectionX = 1;
		int newdirectionY = 1;
		Geometry cuav = geography.getGeometry(current_uav);
		Geometry ouav = geography.getGeometry(other_uav);

		if (cuav == null) {
			System.out.println("Error");
			return false;
		}
		if (ouav == null) {
			System.out.println("Error1");
			return false;
		}

		if (current_uav.internal_time_step == other_uav.internal_time_step) {
			other_x = ouav.getCoordinate().x;
			other_y = ouav.getCoordinate().y;
		} else {
			return false;
		}

		if (cuav.getCoordinate().x < other_x) { // Positive Value means East, -77/west -76/east
			newdirectionX = -1;
		}
		if (cuav.getCoordinate().y > other_y) { // Positive Value means South, 42/South 43/North
			newdirectionY = -1;
		}
		double distance_x = newdirectionX * distance2Coordinate(geography, cuav.getCoordinate().y,
				cuav.getCoordinate().x, cuav.getCoordinate().y, other_x);
		double distance_y = newdirectionY * distance2Coordinate(geography, cuav.getCoordinate().y,
				cuav.getCoordinate().x, other_y, cuav.getCoordinate().x);
		double newdistance_x = newdirectionX
				* GetDistance(cuav.getCoordinate().y, cuav.getCoordinate().x, cuav.getCoordinate().y, other_x);
		double newdistance_y = newdirectionY
				* GetDistance(cuav.getCoordinate().y, cuav.getCoordinate().x, other_y, cuav.getCoordinate().x);
		double speed_x = other_uav.directionX * other_uav.speedX - current_uav.directionX * current_uav.speedX;
		double speed_y = other_uav.directionY * other_uav.speedY - current_uav.directionY * current_uav.speedY;
		double time = (newdistance_x * speed_x + newdistance_y * speed_y) / (speed_x * speed_x + speed_y * speed_y);

		int thres;
		int thres_time;
		if (other_uav.unmanned) {
			thres = 90;
			thres_time = 10;
		} else {
			thres = 180;
			thres_time = 10;
		}
		for (int i = 0; i < 5; i++) {
			if (current_uav.History[i] != -1) {
				thres = 540;
				thres_time = 30;
				break;
			}
		}

		if (time < 0)
			return false;
		else if (time > thres_time)
			return false;

		double x = current_uav.return_connection_coordinate_pair().get(0).get(0);
		double y = current_uav.return_connection_coordinate_pair().get(0).get(1);

		Coordinate target = new Coordinate(x, y);

		double target_distance = distance2Coordinate(geography, cuav.getCoordinate().y, cuav.getCoordinate().x,
				target.y, target.x);

		// if((time * 18) >= target_distance) return false;
		if ((time * current_uav.speed) >= target_distance)
			return false;

		double res_x = newdistance_x - time * speed_x;
		double res_y = newdistance_y - time * speed_y;

		if (Math.abs(res_x * res_x + res_y * res_y) < thres * thres) {
			current_uav.TrajectoryState = 1;
			other_uav.TrajectoryState = 1;
			return true;
		}
		return false;
	}

	private boolean CheckTrajectory(UAV current_uav, UAV other_uav) {
		// System.out.println("In: check");
		// System.out.println(other_uav.hashCode());
		double other_x, other_y;
		// System.out.println("Time:");
		// System.out.println(other_uav.internal_time_step);
		// System.out.println(other_uav.movestatus);
		// System.out.println(current_uav.internal_time_step);
		// if(current_uav.internal_time_step != other_uav.internal_time_step) return
		// false;
		int newdirectionX = 1;
		int newdirectionY = 1;
		Geometry cuav = geography.getGeometry(current_uav);
		Geometry ouav = geography.getGeometry(other_uav);
		if (other_uav.movestatus) {

			if (current_uav.internal_time_step == other_uav.internal_time_step) {
				other_x = other_uav.pre_coordinate_pair.get(0);
				other_y = other_uav.pre_coordinate_pair.get(1);

			} else {
				other_x = ouav.getCoordinate().x;
				other_y = ouav.getCoordinate().y;
				// System.out.println(other_x);
				// System.out.println(other_y);
			}
		} else {

			if (current_uav.internal_time_step == other_uav.internal_time_step) {
				other_x = ouav.getCoordinate().x;
				other_y = ouav.getCoordinate().y;
			} else {
				// System.out.println("=.=.=.= HErre");
				return false;
			}
		}
		// System.out.println("Current
		// Pos:"+ouav.getCoordinate().x+","+ouav.getCoordinate().y);
		// System.out.println("Current
		// Pos1:"+cuav.getCoordinate().x+","+cuav.getCoordinate().y);
		if (cuav == null) {
			System.out.println("Error");
			return false;
		}
		if (ouav == null) {
			System.out.println("Error1");
			return false;
		}
		if (cuav.getCoordinate().x < other_x) { // Positive Value means East, -77/west -76/east
			newdirectionX = -1;
		}
		if (cuav.getCoordinate().y > other_y) { // Positive Value means South, 42/South 43/North
			newdirectionY = -1;
		}
		double distance_x = newdirectionX * distance2Coordinate(geography, cuav.getCoordinate().y,
				cuav.getCoordinate().x, cuav.getCoordinate().y, other_x);
		double distance_y = newdirectionY * distance2Coordinate(geography, cuav.getCoordinate().y,
				cuav.getCoordinate().x, other_y, cuav.getCoordinate().x);
		double newdistance_x = newdirectionX
				* GetDistance(cuav.getCoordinate().y, cuav.getCoordinate().x, cuav.getCoordinate().y, other_x);
		double newdistance_y = newdirectionY
				* GetDistance(cuav.getCoordinate().y, cuav.getCoordinate().x, other_y, cuav.getCoordinate().x);
		double speed_x = other_uav.directionX * other_uav.speedX - current_uav.directionX * current_uav.speedX;
		double speed_y = other_uav.directionY * other_uav.speedY - current_uav.directionY * current_uav.speedY;
		double time = (newdistance_x * speed_x + newdistance_y * speed_y) / (speed_x * speed_x + speed_y * speed_y);

		int thres;
		int thres_time;
		if (other_uav.unmanned) {
			thres = 90;
			thres_time = 10;
		} else {
			thres = 180;
			thres_time = 10;
		}
		for (int i = 0; i < 5; i++) {
			if (current_uav.History[i] != -1) {
				thres = 540;
				thres_time = 30;
				break;
			}
		}

		if (time < 0)
			return false;
		else if (time > thres_time)
			return false;

		double x = current_uav.return_end_coordinate_pair().get(0);
		double y = current_uav.return_end_coordinate_pair().get(1);

		Coordinate target = new Coordinate(x, y);

		double target_distance = distance2Coordinate(geography, cuav.getCoordinate().y, cuav.getCoordinate().x,
				target.y, target.x);

		// if((time * 18) >= target_distance) return false;
		if ((time * current_uav.speed) >= target_distance)
			return false;

		double res_x = newdistance_x - time * speed_x;
		double res_y = newdistance_y - time * speed_y;

		if (Math.abs(res_x * res_x + res_y * res_y) < thres * thres) {
			// System.out.println("Trajectory");
			// System.out.println("resX:"+res_x);
			// System.out.println("resY:"+res_y);
			// System.out.println("Current
			// Pos:"+cuav.getCoordinate().y+","+cuav.getCoordinate().x);
			//// System.out.println("Current
			// Pos:"+current_uav.return_end_coordinate_pair().get(1)+","+current_uav.return_end_coordinate_pair().get(0));
			// System.out.println("Current
			// Pos:"+ouav.getCoordinate().y+","+ouav.getCoordinate().x);
			//// System.out.println("Current
			// Pos:"+other_uav.return_end_coordinate_pair().get(1)+","+other_uav.return_end_coordinate_pair().get(0));
			// System.out.println("Time:"+time);
			current_uav.TrajectoryState = 1;
			other_uav.TrajectoryState = 1;
			// other_uav.set_UavTraj(1);
			// System.out.println("distance :"+distance_x+","+distance_y);
			// System.out.println("speed :"+speed_x+","+speed_y);
			// System.out.println("distance :"+newdistance_x+","+newdistance_y);
			return true;
		}
		return false;
	}

	private void normal_step(UAV curr_uav) {
		// get the grid location of this UAV
		Geometry pt = geography.getGeometry(curr_uav);
		if (curr_uav.my_operation.return_type().contains("return")) {
			if (curr_uav.my_operation.return_connection_point().isEmpty()) {
				if (this.my_operation.return_destination_point() != "") {
					double x = curr_uav.airport_list.get(curr_uav.my_operation.return_destination_point()).get(0);
					double y = curr_uav.airport_list.get(curr_uav.my_operation.return_destination_point()).get(1);

					List<Object> Target_collection = new ArrayList<Object>();
					for (Object obj : geography.getAllObjects()) {
						if (obj instanceof Target) {
							Target_collection.add(obj);
						}
					}

					Geometry target = null;

					for (Object temp : Target_collection) {
						target = geography.getGeometry(temp);
						if (target.getCoordinate().x == x && target.getCoordinate().y == y)
							break;
					}

					double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							pt.getCoordinate().y, target.getCoordinate().x);
					double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							target.getCoordinate().y, pt.getCoordinate().x);

					if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
						this.my_operation.set_destination_point("");
						clear_all_communication_link(curr_uav);
						// remove
						Context context = ContextUtils.getContext(this);
						this.end_time = this.internal_time_step;
						geography.move(this, target);
						save_location("-1");
						context.remove(this);
					} else {
						communicate_with_base_station();
						if (this.if_straight_path) {
							moveTowards_straight(target);
						} else {
							moveTowards_Manhattan(target);
						}
						if (this.internal_time_step % 1 == 0) {
							save_location("1");
						}

					}
				} else {
					double x = curr_uav.airport_list.get(curr_uav.my_operation.return_start_point()).get(0);
					double y = curr_uav.airport_list.get(curr_uav.my_operation.return_start_point()).get(1);

					List<Object> Target_collection = new ArrayList<Object>();
					for (Object obj : geography.getAllObjects()) {
						if (obj instanceof Target) {
							Target_collection.add(obj);
						}
					}

					Geometry target = null;

					for (Object temp : Target_collection) {
						target = geography.getGeometry(temp);
						if (target.getCoordinate().x == x && target.getCoordinate().y == y)
							break;
					}
					double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							pt.getCoordinate().y, target.getCoordinate().x);
					double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							target.getCoordinate().y, pt.getCoordinate().x);
					if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
						clear_all_communication_link(curr_uav);
						// remove
						Context context = ContextUtils.getContext(this);
						this.end_time = this.internal_time_step;

						geography.move(this, target);
						save_location("-1");
						context.remove(this);
					} else {
						communicate_with_base_station();
						if (this.if_straight_path) {
							moveTowards_straight(target);
						} else {
							moveTowards_Manhattan(target);
						}
						if (this.internal_time_step % 1 == 0) {
							save_location("1");
						}

					}
				}

			} else {
				double x = curr_uav.airport_list.get(curr_uav.my_operation.return_connection_point()).get(0);
				double y = curr_uav.airport_list.get(curr_uav.my_operation.return_connection_point()).get(1);

				List<Object> Target_collection = new ArrayList<Object>();
				for (Object obj : geography.getAllObjects()) {
					if (obj instanceof Target) {
						Target_collection.add(obj);
					}
				}

				Geometry target = null;

				for (Object temp : Target_collection) {
					target = geography.getGeometry(temp);
					if (target.getCoordinate().x == x && target.getCoordinate().y == y)
						break;
				}

				double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						pt.getCoordinate().y, target.getCoordinate().x);
				double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						target.getCoordinate().y, pt.getCoordinate().x);
				if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
					this.my_operation.set_connection_point("");
					clear_all_communication_link(curr_uav);
					// remove
					Context context = ContextUtils.getContext(this);
					this.end_time = this.internal_time_step;

					geography.move(this, target);
					save_location("-1");
					context.remove(this);
				} else {
					communicate_with_base_station();
					if (this.if_straight_path) {
						moveTowards_straight(target);
					} else {
						moveTowards_Manhattan(target);
					}
					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}

				}
			}

		} else {
			if (this.my_operation.return_connection_point().isEmpty()) {
				double x = curr_uav.airport_list.get(curr_uav.my_operation.return_destination_point()).get(0);
				double y = curr_uav.airport_list.get(curr_uav.my_operation.return_destination_point()).get(1);

				List<Object> Target_collection = new ArrayList<Object>();
				for (Object obj : geography.getAllObjects()) {
					if (obj instanceof Target) {
						Target_collection.add(obj);
					}
				}

				Geometry target = null;

				for (Object temp : Target_collection) {
					target = geography.getGeometry(temp);
					if (target.getCoordinate().x == x && target.getCoordinate().y == y)
						break;
				}

				double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						pt.getCoordinate().y, target.getCoordinate().x);
				double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						target.getCoordinate().y, pt.getCoordinate().x);
				if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
					clear_all_communication_link(curr_uav);
					// remove
					Context context = ContextUtils.getContext(this);
					this.end_time = this.internal_time_step;

					geography.move(this, target);
					save_location("-1");
					context.remove(this);
				} else {
					communicate_with_base_station();
					if (this.if_straight_path) {
						moveTowards_straight(target);
					} else {
						moveTowards_Manhattan(target);
					}
					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}

				}
			} else {
				double x = curr_uav.airport_list.get(curr_uav.my_operation.return_connection_point()).get(0);
				double y = curr_uav.airport_list.get(curr_uav.my_operation.return_connection_point()).get(1);

				List<Object> Target_collection = new ArrayList<Object>();
				for (Object obj : geography.getAllObjects()) {
					if (obj instanceof Target) {
						Target_collection.add(obj);
					}
				}

				Geometry target = null;

				for (Object temp : Target_collection) {
					target = geography.getGeometry(temp);
					if (target.getCoordinate().x == x && target.getCoordinate().y == y)
						break;
				}

				double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						pt.getCoordinate().y, target.getCoordinate().x);
				double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						target.getCoordinate().y, pt.getCoordinate().x);
				if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
					this.my_operation.set_connection_point("");
					clear_all_communication_link(curr_uav);
					// remove
					Context context = ContextUtils.getContext(this);
					this.end_time = this.internal_time_step;

					geography.move(this, target);
					save_location("-1");
					context.remove(this);
				} else {
					communicate_with_base_station();
					if (this.if_straight_path) {
						moveTowards_straight(target);
					} else {
						moveTowards_Manhattan(target);
					}
					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}

				}
			}
		}
	}

	private void probability_generation_step(UAV curr_uav) {
		// get the grid location of this UAV
		Geometry pt = geography.getGeometry(curr_uav);
		if (curr_uav.my_operation.return_type().contains("return")) {
			if (curr_uav.return_connection_coordinate_pair().isEmpty()) {
				if (!curr_uav.return_end_coordinate_pair().isEmpty()) {
					double x = curr_uav.return_end_coordinate_pair().get(0);
					double y = curr_uav.return_end_coordinate_pair().get(1);

					Coordinate target = new Coordinate(x, y);
					double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							pt.getCoordinate().y, target.x);
					double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							target.y, pt.getCoordinate().x);

					if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
						this.end_time = this.internal_time_step;

						Coordinate connection_coord = new Coordinate(x, y);
						Point geom_connection = fac.createPoint(connection_coord);
						geography.move(this, geom_connection);

						save_location("-1");

						curr_uav.return_end_coordinate_pair().clear();
						clear_all_communication_link(curr_uav);
						// remove
						Context context = ContextUtils.getContext(this);
						context.remove(this);
					} else {
						communicate_with_base_station();
						if(this.id != 999) {
							if (this.if_straight_path) {
								moveTowards_by_coordinate_straight(target);
							} else {
								moveTowards_by_coordinate_Manhattan(target);
							}
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
							}
						}
						if (this.internal_time_step % 1 == 0) {
							save_location("1");
						}

					}
				} else {
					double x = curr_uav.return_start_coordinate_pair().get(0);
					double y = curr_uav.return_start_coordinate_pair().get(1);
					// System.out.print("Here!");
					Coordinate target = new Coordinate(x, y);

					double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							pt.getCoordinate().y, target.x);
					double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							target.y, pt.getCoordinate().x);

					if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
						this.end_time = this.internal_time_step;
						if(this.id != 999) {
							if (this.if_straight_path) {
								moveTowards_by_coordinate_straight(target);
							} else {
								moveTowards_by_coordinate_Manhattan(target);
							}
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
							}
						}
						save_location("-1");

						clear_all_communication_link(curr_uav);
						// remove
						Context context = ContextUtils.getContext(this);
						context.remove(this);
					} else {
						communicate_with_base_station();
						if(this.id != 999) {
							if (this.if_straight_path) {
								moveTowards_by_coordinate_straight(target);
							} else {
								moveTowards_by_coordinate_Manhattan(target);
							}
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
							}
						}

					}
				}

			} else {
				double x = curr_uav.return_connection_coordinate_pair().get(0).get(0);
				double y = curr_uav.return_connection_coordinate_pair().get(0).get(1);

				Coordinate target = new Coordinate(x, y);

				double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						pt.getCoordinate().y, target.x);
				double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x, target.y,
						pt.getCoordinate().x);
				System.out.print("Here!");
				if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
					communicate_with_base_station();

					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}
					curr_uav.return_connection_coordinate_pair().remove(0);
				} else {
					communicate_with_base_station();
					if(this.id != 999) {
						if (this.if_straight_path) {
							moveTowards_by_coordinate_straight(target);
						} else {
							moveTowards_by_coordinate_Manhattan(target);
						}
						if (this.internal_time_step % 1 == 0) {
							save_location("1");
						}
					}
					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}

				}
			}
		//* else for mode not 'return'
		} else {
			//* This branch means destination coordinates is the last waypoint.
			if (curr_uav.return_connection_coordinate_pair().isEmpty()) {
				double x = curr_uav.return_end_coordinate_pair().get(0);
				double y = curr_uav.return_end_coordinate_pair().get(1);
				ArrayList<Double> coordinate_pair = new ArrayList<Double>();
				coordinate_pair.add(pt.getCoordinate().x);
				coordinate_pair.add(pt.getCoordinate().y);
				this.pre_coordinate_pair = coordinate_pair;
				Coordinate target = new Coordinate(x, y);

				double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						pt.getCoordinate().y, target.x);
				double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x, target.y,
						pt.getCoordinate().x);
				this.speedX = (distance_x / Math.sqrt(distance_x * distance_x + distance_y * distance_y)) * this.speed;
				this.speedY = (distance_y / Math.sqrt(distance_x * distance_x + distance_y * distance_y)) * this.speed;
				if (pt.getCoordinate().x > target.x) { // Positive Value means East, -77/west -76/east
					this.directionX = -1;
				}
				if (pt.getCoordinate().y < target.y) { // Positive Value means South, 42/South 43/North
					this.directionY = -1;
				}

				int AgentId = ((int) (distance_y / 90) / 20 + 1) * 100 + ((int) (distance_x / 90) / 20 + 1);
				//

				this.movestatus = false;
				if (this.if_routing__path && this.if_straight_path) {
					communicate_with_agent(AgentId);
				}

				// if(!this.check_TrajList()) {
				// for (UAV uavt : this.TrajectoryList) {
				// uavt.set_UavTraj(1);
				// }
				// }
				// else {
				// this.TrajectoryState = -1;
				// }

				if (this.movestatus == true) {
					if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
						this.end_time = this.internal_time_step;
						if (curr_uav.unmanned) {
							save_location("-1");
						}
						clear_all_communication_link(curr_uav);
						remove_agent_uav(curr_uav);
						// remove
						Context context = ContextUtils.getContext(this);
						context.remove(this);
						NS3CommunicatiorHelper ns3CommunicatiorHelper
							= new NS3CommunicatiorHelper();
						ns3CommunicatiorHelper.sendDeletionReq(Integer.toString(this.id));
						UserEquipmentController userEquipmentController
							= (UserEquipmentController) context
								.getObjects(UserEquipmentController.class).get(0);
						userEquipmentController.getContainer().remove(ue);
					} else {
						communicate_with_base_station();
						if (this.id == 999 && this.internal_time_step % 5 == 0) {
							List<Object> base_stations = new ArrayList<Object>();
							for (Object obj : geography.getAllObjects()) {
								if (obj instanceof Base_Station) {
									base_stations.add(obj);
								}
							}
							for (Object obj : base_stations) {
								Geometry tmp = geography.getGeometry(obj);

								Base_Station temp = (Base_Station) obj;
							}

						}

						if (this.internal_time_step % 1 == 0) {
							if (curr_uav.unmanned) {
								save_location("1");
								logger.save(this.toString());
							}
						}
					}
				}
				if (this.movestatus == false) {
					// this.TrajectoryState = -1;
					if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
						this.end_time = this.internal_time_step;
						if(this.id != 999) {
							if (this.if_straight_path) {
								moveTowards_by_coordinate_straight(target);
							} else {
								moveTowards_by_coordinate_Manhattan(target);
							}
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
							}
						}
						if (curr_uav.unmanned) {
							save_location("-1");
							logger.save(this.toString());
						
						}

						clear_all_communication_link(curr_uav);
						remove_agent_uav(curr_uav);
						// remove
						Context context = ContextUtils.getContext(this);
						context.remove(this);
						NS3CommunicatiorHelper ns3CommunicatiorHelper
							= new NS3CommunicatiorHelper();
						ns3CommunicatiorHelper.sendDeletionReq(Integer.toString(this.id));
						UserEquipmentController userEquipmentController
							= (UserEquipmentController) context
								.getObjects(UserEquipmentController.class).get(0);
						userEquipmentController.getContainer().remove(ue);
					} else {
						communicate_with_base_station();
						if(this.id != 999) {
							if (this.if_straight_path) {
								moveTowards_by_coordinate_straight(target);
							} else {
								moveTowards_by_coordinate_Manhattan(target);
								NS3CommunicatiorHelper ns3CommunicatiorHelper
									= new NS3CommunicatiorHelper();
								Coordinate coor = geography.getGeometry(this).getCoordinate();
								ns3CommunicatiorHelper.sendActionReq(
									Integer.toString(this.id),
									Double.toString(coor.y),
									Double.toString(coor.x));
							}
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
								logger.save(this.toString());
							}
						}
						if (this.id == 999 && this.internal_time_step % 5 == 0) {
							String delay = String.valueOf(this.internal_time_step) + "s";
							List<Object> base_stations = new ArrayList<Object>();
							for (Object obj : geography.getAllObjects()) {
								if (obj instanceof Base_Station) {
									base_stations.add(obj);
								}
							}
							for (Object obj : base_stations) {
								Geometry tmp = geography.getGeometry(obj);

								Base_Station temp = (Base_Station) obj;
							}

						}
						if (this.internal_time_step % 1 == 0) {
							if (curr_uav.unmanned) {
								save_location("1");
							}
						}
					}
					this.movestatus = true;
				} //* Movestatus = false
			//* Connection waypoint not empty
			//* This branch means there are waypoints remaining to be reached.
			} else {
				if (this.unmanned) {
					double x = curr_uav.return_connection_coordinate_pair().get(0).get(0);
					double y = curr_uav.return_connection_coordinate_pair().get(0).get(1);

					Coordinate target = new Coordinate(x, y);
					double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							pt.getCoordinate().y, target.x);
					double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							target.y, pt.getCoordinate().x);
					this.speedX = (distance_x / Math.sqrt(distance_x * distance_x + distance_y * distance_y))
							* this.speed;
					this.speedY = (distance_y / Math.sqrt(distance_x * distance_x + distance_y * distance_y))
							* this.speed;
					if (pt.getCoordinate().x > target.x) { // Positive Value means East, -77/west -76/east
						this.directionX = -1;
					}
					if (pt.getCoordinate().y < target.y) { // Positive Value means South, 42/South 43/North
						this.directionY = -1;
					}
					this.movestatus = false;
					double distance_xx = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							pt.getCoordinate().y, -103.0440522);
					double distance_yy = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							48.0454928, pt.getCoordinate().x);
					int AgentId = ((int) (distance_yy / 90) / 20 + 1) * 100 + ((int) (distance_xx / 90) / 20 + 1);
					////
					// if(this.id == 0) {
					// System.out.println("Id:"+this.id);
					// }
					if (this.id == 999 && this.internal_time_step % 5 == 0) {
						String delay = String.valueOf(this.internal_time_step) + "s";
						List<Object> base_stations = new ArrayList<Object>();
						for (Object obj : geography.getAllObjects()) {
							if (obj instanceof Base_Station) {
								base_stations.add(obj);
							}
						}
						for (Object obj : base_stations) {
							Geometry tmp = geography.getGeometry(obj);

							Base_Station temp = (Base_Station) obj;

						}

					}

					boolean states = communicate_with_agent_traj(AgentId);

					if (states) {
						this.TrajectoryState = 1;
						Random r = new Random();
						int change = r.nextInt(5) + 1;
						if (change > 3) {
							curr_uav.set_speed(18 * change);
							System.out.println("State=============" + this.speed);
							communicate_with_base_station();
							if(this.id != 999) {
								if (this.if_straight_path) {
									moveTowards_by_coordinate_straight(target);
								} else {
									moveTowards_by_coordinate_Manhattan(target);
									NS3CommunicatiorHelper ns3CommunicatiorHelper
										= new NS3CommunicatiorHelper();
									Coordinate coor = geography.getGeometry(this).getCoordinate();
									ns3CommunicatiorHelper.sendActionReq(
										Integer.toString(this.id),
										Double.toString(coor.y),
										Double.toString(coor.x));
								}
								if (this.internal_time_step % 1 == 0) {
									save_location("1");
								}
							}
							curr_uav.set_speed(18);
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
								logger.save(this.toString());
							}
						} else {
							communicate_with_base_station();
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
								logger.save(this.toString());
							}
						}
						this.TrajectoryState = -1;
					} else {
						if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
							communicate_with_base_station();
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
								logger.save(this.toString());
							}
							curr_uav.return_connection_coordinate_pair().remove(0);
						} else {
							communicate_with_base_station();
							if(this.id != 999) {
								if (this.if_straight_path) {
									moveTowards_by_coordinate_straight(target);
								} else {
									moveTowards_by_coordinate_Manhattan(target);
									Context context = ContextUtils.getContext(this);
									NS3CommunicatiorHelper ns3CommunicatiorHelper
										= new NS3CommunicatiorHelper();
									Coordinate coor = geography.getGeometry(this).getCoordinate();
									ns3CommunicatiorHelper.sendActionReq(
										Integer.toString(this.id),
										Double.toString(coor.y),
										Double.toString(coor.x));
								}
								if (this.internal_time_step % 1 == 0) {
									save_location("1");
								}
							}
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
								logger.save(this.toString());
							}
						}
						this.TrajectoryState = -1;
					}
				//* Manned
				} else {
					double x = curr_uav.return_connection_coordinate_pair().get(0).get(0);
					double y = curr_uav.return_connection_coordinate_pair().get(0).get(1);
					ArrayList<Double> coordinate_pair = new ArrayList<Double>();
					coordinate_pair.add(pt.getCoordinate().x);
					coordinate_pair.add(pt.getCoordinate().y);
					this.pre_coordinate_pair = coordinate_pair;
					// System.out.println("LCCCCCCCCCCCCCCCC5");
					Coordinate target = new Coordinate(x, y);
					double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							pt.getCoordinate().y, target.x);
					double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							target.y, pt.getCoordinate().x);
					this.speedX = (distance_x / Math.sqrt(distance_x * distance_x + distance_y * distance_y))
							* this.speed;
					this.speedY = (distance_y / Math.sqrt(distance_x * distance_x + distance_y * distance_y))
							* this.speed;
					if (pt.getCoordinate().x > target.x) { // Positive Value means East, -77/west -76/east
						this.directionX = -1;
					}
					if (pt.getCoordinate().y < target.y) { // Positive Value means South, 42/South 43/North
						this.directionY = -1;
					}

					int AgentId = ((int) (distance_y / 90) / 20 + 1) * 100 + ((int) (distance_x / 90) / 20 + 1);
					// System.out.println("Agent Id:" + AgentId);
					if (this.id == 0) {
						System.out.println("Agent Id:" + this.id);
					}
					if (this.id == 999 && this.internal_time_step % 5 == 0) {
						String delay = String.valueOf(this.internal_time_step) + "s";
						List<Object> base_stations = new ArrayList<Object>();
						for (Object obj : geography.getAllObjects()) {
							if (obj instanceof Base_Station) {
								base_stations.add(obj);
							}
						}
						for (Object obj : base_stations) {
							Geometry tmp = geography.getGeometry(obj);

							Base_Station temp = (Base_Station) obj;

						}

					}

					this.movestatus = false;
					if (this.if_straight_path) {
						communicate_with_agent(AgentId);
					}
					// if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
					if (!curr_uav.return_connection_coordinate_pair().isEmpty()) {
						double next_x = curr_uav.return_connection_coordinate_pair().get(0).get(0);
						double next_y = curr_uav.return_connection_coordinate_pair().get(0).get(1);
						double next_speed = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
								next_y, next_x);
						curr_uav.set_speed(next_speed);
						communicate_with_base_station();
						if(this.id != 999) {
							if (this.if_straight_path) {
								moveTowards_by_coordinate_straight(target);
							} else {
								moveTowards_by_coordinate_Manhattan(target);
							}
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
							}
						}
					}
					communicate_with_base_station();
					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}
					curr_uav.return_connection_coordinate_pair().remove(0);
					List<Object> agents = new ArrayList<Object>();
					for (Object obj : geography.getAllObjects()) {
						if (obj instanceof Agent) {
							agents.add(obj);
						}
					}
					for (Object obj : agents) {
						Agent temp = (Agent) obj;// Agent Information
						for (int i = 0; i < 9; i++) {
							if (temp.return_Agent_id() == this.current_agents[i]) {
								temp.remove_UAV(curr_uav, curr_uav.level);
							}
						}
					}
					curr_uav.level = curr_uav.return_level_inf().get(0);
					curr_uav.return_level_inf().remove(0);
					for (Object obj : agents) {
						Agent temp = (Agent) obj;// Agent Information
						for (int i = 0; i < 9; i++) {
							if (temp.return_Agent_id() == this.current_agents[i]) {
								temp.add_UAV(curr_uav, curr_uav.level);
							}
						}
					}
				}

				// }
				// else {
				// communicate_with_base_station();
				// if (this.if_straight_path) {
				// moveTowards_by_coordinate_straight(target);
				// } else {
				// moveTowards_by_coordinate_Manhattan(target);
				// }
				// if (this.internal_time_step % 1 ==0) {
				//// save_location("1");
				// }
				//
				// }
			}
		}
	}

	private void probability_generation_index_step(UAV curr_uav) {
		// get the grid location of this UAV
		Geometry pt = geography.getGeometry(curr_uav);
		if (curr_uav.my_operation.return_type().contains("return")) {
			if (curr_uav.return_index_pair().isEmpty()) {
				if (!curr_uav.return_end_index_pair().isEmpty()) {
					double x = curr_uav.return_end_index_pair().get(0);
					double y = curr_uav.return_end_index_pair().get(1);
					Coordinate target = new Coordinate(x, y);
					double distance_x = x - curr_uav.x;
					double distance_y = y - curr_uav.y;

					if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
						this.end_time = this.internal_time_step;

						Coordinate connection_coord = new Coordinate(x, y);
						Point geom_connection = fac.createPoint(connection_coord);
						geography.move(this, geom_connection);

						save_location("-1");

						curr_uav.return_end_coordinate_pair().clear();
						clear_all_communication_link(curr_uav);
						// remove
						Context context = ContextUtils.getContext(this);
						context.remove(this);
					} else {
						communicate_with_base_station();
						if(this.id != 999) {
							if (this.if_straight_path) {
								moveTowards_by_coordinate_straight(target);
							} else {
								moveTowards_by_coordinate_Manhattan(target);
							}
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
							}
						}
						if (this.internal_time_step % 1 == 0) {
							save_location("1");
						}

					}
				} else {
					double x = curr_uav.return_start_coordinate_pair().get(0);
					double y = curr_uav.return_start_coordinate_pair().get(1);

					Coordinate target = new Coordinate(x, y);

					double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							pt.getCoordinate().y, target.x);
					double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
							target.y, pt.getCoordinate().x);

					if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
						this.end_time = this.internal_time_step;
						if(this.id != 999) {
							if (this.if_straight_path) {
								moveTowards_by_coordinate_straight(target);
							} else {
								moveTowards_by_coordinate_Manhattan(target);
							}
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
							}
						}
						save_location("-1");

						clear_all_communication_link(curr_uav);
						// remove
						Context context = ContextUtils.getContext(this);
						context.remove(this);
					} else {
						communicate_with_base_station();
						if(this.id != 999) {
							if (this.if_straight_path) {
								moveTowards_by_coordinate_straight(target);
							} else {
								moveTowards_by_coordinate_Manhattan(target);
							}
							if (this.internal_time_step % 1 == 0) {
								save_location("1");
							}
						}
						if (this.internal_time_step % 1 == 0) {
							save_location("1");
						}

					}
				}

			} else {
				double x = curr_uav.return_connection_coordinate_pair().get(0).get(0);
				double y = curr_uav.return_connection_coordinate_pair().get(0).get(1);

				Coordinate target = new Coordinate(x, y);

				double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						pt.getCoordinate().y, target.x);
				double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x, target.y,
						pt.getCoordinate().x);

				if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
					communicate_with_base_station();

					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}

					curr_uav.return_connection_coordinate_pair().remove(0);
				} else {
					communicate_with_base_station();
					if(this.id != 999) {
						if (this.if_straight_path) {
							moveTowards_by_coordinate_straight(target);
						} else {
							moveTowards_by_coordinate_Manhattan(target);
						}
						if (this.internal_time_step % 1 == 0) {
							save_location("1");
						}
					}
					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}

				}
			}

		} else {
			if (curr_uav.return_connection_coordinate_pair().isEmpty()) {

				double x = curr_uav.return_end_coordinate_pair().get(0);
				double y = curr_uav.return_end_coordinate_pair().get(1);

				Coordinate target = new Coordinate(x, y);

				double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						pt.getCoordinate().y, target.x);
				double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x, target.y,
						pt.getCoordinate().x);

				if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
					this.end_time = this.internal_time_step;
					if(this.id != 999) {
						if (this.if_straight_path) {
							moveTowards_by_coordinate_straight(target);
						} else {
							moveTowards_by_coordinate_Manhattan(target);
						}
						if (this.internal_time_step % 1 == 0) {
							save_location("1");
						}
					}

					save_location("-1");

					clear_all_communication_link(curr_uav);
					// remove
					Context context = ContextUtils.getContext(this);
					context.remove(this);
				} else {
					communicate_with_base_station();
					if(this.id != 999) {
						if (this.if_straight_path) {
							moveTowards_by_coordinate_straight(target);
						} else {
							moveTowards_by_coordinate_Manhattan(target);
						}
						if (this.internal_time_step % 1 == 0) {
							save_location("1");
						}
					}
					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}

				}
			} else {
				double x = curr_uav.return_connection_coordinate_pair().get(0).get(0);
				double y = curr_uav.return_connection_coordinate_pair().get(0).get(1);

				Coordinate target = new Coordinate(x, y);

				double distance_x = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x,
						pt.getCoordinate().y, target.x);
				double distance_y = distance2Coordinate(geography, pt.getCoordinate().y, pt.getCoordinate().x, target.y,
						pt.getCoordinate().x);

				if (distance_x < curr_uav.speed && distance_y < curr_uav.speed) {
					communicate_with_base_station();

					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}

					curr_uav.return_connection_coordinate_pair().remove(0);

				} else {
					communicate_with_base_station();
					if(this.id != 999) {
						if (this.if_straight_path) {
							moveTowards_by_coordinate_straight(target);
						} else {
							moveTowards_by_coordinate_Manhattan(target);
						}
						if (this.internal_time_step % 1 == 0) {
							save_location("1");
						}
					}
					if (this.internal_time_step % 1 == 0) {
						save_location("1");
					}

				}
			}
		}
	}

	// UAV's move function
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		this.internal_time_step++;
		if (this.internal_time_step == 0) {
			save_location("1");
		}
		if (this.my_operation.return_mode() == "all_random") {
			normal_step(this);
		} else if (this.my_operation.return_mode() == "probability") {
			probability_generation_step(this);
		} else {
			normal_step(this);
		}
	}

	// find single target or find nearest target
	private Geometry find_target() {
		// get the grid location of this UAV
		Geometry pt = geography.getGeometry(this);

		if (targets.isEmpty()) {
			for (Object obj : geography.getAllObjects()) {
				if (obj instanceof Target) {
					targets.add(obj);
				}
			}
		}

		Geometry target = null;
		if (if_nearest_target == false) {

			target = geography.getGeometry(targets.get(0));
		} else {

			double minimum = Double.MAX_VALUE;
			for (Object tar : targets) {
				Geometry tmp = geography.getGeometry((Target) tar);

				double distance = pt.distance(tmp);
				if (distance < minimum) {
					target = geography.getGeometry(tar);
					minimum = distance;
				}
			}
		}
		return target;
	}

	private void remove_agent_uav(Object uav) {
		List<Object> agents = new ArrayList<Object>();

		for (Object obj : geography.getAllObjects()) {
			if (obj instanceof Agent) {
				agents.add(obj);
			}
		}

		for (Object obj : agents) {
			Agent temp = (Agent) obj;
			UAV cuav = (UAV) uav;
			temp.remove_UAV(cuav, cuav.level);
		}

	}

	private void clear_all_communication_link(Object uav) {
		// System.out.print("check uav state" + this.unmanned + "\n");
		if (this.unmanned) {
			List<Object> base_stations = new ArrayList<Object>();

			current_table.delete_report(uav.hashCode());

			for (Object obj : geography.getAllObjects()) {
				if (obj instanceof Base_Station) {
					base_stations.add(obj);
				}
			}

			Context<Object> context = ContextUtils.getContext(base_stations.get(0));

			for (Object obj : base_stations) {
				Base_Station temp = (Base_Station) obj;
				temp.remove_UAV((UAV) uav);
			}
		}
	}

	// generate n by using the distance and probability
	private double generate_n_by_probability(double distance) {

		int probability = 0;

		if (distance <= 30000.00) {
			probability = 90;
		} else if (30000.00 < distance && distance < 60000.00) {
			probability = 80;
		} else {
			probability = 70;
		}

		Random r = new Random();
		int Low = 1;
		int High = 101;
		int Result = r.nextInt(High - Low) + Low;

		if (Result < probability) {
			return 3;
		} else {
			return 3.5;
		}
	}

	// add/remove channels && add/remove edges
	private void edge_operation(List<Object> base_stations, Object selected_base_station, Object uav, double rss,
			long cc_id, int bb_id) {

		Context<Object> context = ContextUtils.getContext(selected_base_station);

		Base_Station temp_base_station = (Base_Station) selected_base_station;
		// System.out.print("+++++++++++++++++++++++++++++++++++++++++");
		// System.out.print(temp_base_station.return_bs_id());

		// get all base stations
		// one uav can only have one communication link
		// remove the current uav from the specific base station channels
		for (Object obj : base_stations) {
			Base_Station temp = (Base_Station) obj;
			temp.remove_UAV((UAV) uav);
		}

		// add communication links to net
		// good net : curr < 5
		// fair net : 5 < = curr < 8
		// bad net : curr == 8
		// add this uav to the specific base station channels
		int current_num_of_uav = temp_base_station.get_current_num_UAV();

		// add uav into specific base station channel
		temp_base_station.add_UAV((UAV) uav);
	}

	// sort map by value
	private static Map<Object, Double> sortMapByValues(Map<Object, Double> aMap) {

		Set<Entry<Object, Double>> mapEntries = aMap.entrySet();

		// used linked list to sort, because insertion of elements in linked list is
		// faster than an array list.
		List<Entry<Object, Double>> aList = new LinkedList<Entry<Object, Double>>(mapEntries);

		// sorting the List
		Collections.sort(aList, new Comparator<Entry<Object, Double>>() {

			@Override
			public int compare(Entry<Object, Double> ele1, Entry<Object, Double> ele2) {

				return ele1.getValue().compareTo(ele2.getValue());
			}
		});

		// Storing the list into Linked HashMap to preserve the order of insertion.
		Map<Object, Double> aMap2 = new LinkedHashMap<Object, Double>();
		for (Entry<Object, Double> entry : aList) {
			aMap2.put(entry.getKey(), entry.getValue());
		}

		return aMap2;
	}

	private static double distance2Coordinate(Geography g, double lat_1, double lon_1, double lat_2, double lon_2) {
		GeodeticCalculator calculator = new GeodeticCalculator(g.getCRS());
		calculator.setStartingGeographicPoint(lon_1, lat_1);
		calculator.setDestinationGeographicPoint(lon_2, lat_2);
		return calculator.getOrthodromicDistance();
	}

	private static double distance2Coordinate(Geography g, Coordinate c1, Coordinate c2) {
		GeodeticCalculator calculator = new GeodeticCalculator(g.getCRS());
		calculator.setStartingGeographicPoint(c1.x, c1.y);
		calculator.setDestinationGeographicPoint(c2.x, c2.y);
		return calculator.getOrthodromicDistance();
	}

	// save UAV speed in each time tick
	private void save_speed(double X_speed, double Y_speed) {
		int id = this.id;
		String mission = this.my_operation.return_mission();

		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);

		String fileName = temp_pre_fix + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(current_batch) + File.separator + "uav_speed" + ".csv";
		Geometry myPoint = geography.getGeometry(this);

		try {
			PrintWriter writer_uav_speed = new PrintWriter(new FileOutputStream(new File(fileName), true));
			StringBuilder uav_speed_string = new StringBuilder();

			Double x_coordinate = BigDecimal.valueOf(myPoint.getCoordinate().x).setScale(7, RoundingMode.HALF_UP)
					.doubleValue();
			Double y_coordinate = BigDecimal.valueOf(myPoint.getCoordinate().y).setScale(7, RoundingMode.HALF_UP)
					.doubleValue();

			int flag = 1;
			for (UAV uavt : this.TrajectoryList) {
				if (!uavt.unmanned) {
					flag = 2;
					Geometry mavPoint = geography.getGeometry(uavt);
					Double xm_coordinate = BigDecimal.valueOf(mavPoint.getCoordinate().x)
							.setScale(7, RoundingMode.HALF_UP).doubleValue();
					Double ym_coordinate = BigDecimal.valueOf(mavPoint.getCoordinate().y)
							.setScale(7, RoundingMode.HALF_UP).doubleValue();
					uav_speed_string.append(internal_time_step);
					uav_speed_string.append(',');
					uav_speed_string.append(uavt.hashCode());
					uav_speed_string.append(',');
					uav_speed_string.append(ym_coordinate);
					uav_speed_string.append(',');
					uav_speed_string.append(xm_coordinate);
					uav_speed_string.append(',');
					uav_speed_string.append(0);
					uav_speed_string.append(',');
					uav_speed_string.append(0);
					uav_speed_string.append(',');
					uav_speed_string.append(0);
					uav_speed_string.append(',');
					uav_speed_string.append(0);
					uav_speed_string.append(',');
					uav_speed_string.append(id);
					uav_speed_string.append(',');
					uav_speed_string.append(flag);
					uav_speed_string.append('\n');
				}
			}

			for (UAV uavt : this.TrajectoryList) {
				uav_speed_string.append(internal_time_step);
				uav_speed_string.append(',');
				uav_speed_string.append(id);
				uav_speed_string.append(',');
				uav_speed_string.append(y_coordinate);
				uav_speed_string.append(',');
				uav_speed_string.append(x_coordinate);
				uav_speed_string.append(',');
				uav_speed_string.append(this.directionX * this.speedX);
				uav_speed_string.append(',');
				uav_speed_string.append(this.directionY * this.speedY);
				uav_speed_string.append(',');
				uav_speed_string.append(X_speed);
				uav_speed_string.append(',');
				uav_speed_string.append(Y_speed);
				uav_speed_string.append(',');
				uav_speed_string.append(uavt.hashCode());
				uav_speed_string.append(',');
				uav_speed_string.append(flag);
				// if(uavt.unmanned)
				//
				// else
				// uav_speed_string.append(0);
				// uav_speed_string.append(',');
				// uav_speed_string.append(uavt.current_agents[0]);
				uavt.TrajectoryState = 1;
				uav_speed_string.append('\n');
			}

			writer_uav_speed.write(uav_speed_string.toString());

			writer_uav_speed.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Geography<Object> getGeography() {
		return geography;
	}

	// save connections
	private void save_communication() {
		// if(this.current_SIR < 2) {
		// this.current_basestation = null;
		// this.channel_id = -1;
		// this.label = 2;
		// }
		int uav_id = this.id;
		long channel_id = this.channel_id;
		int bs_id = this.bs_id;
		// if (this.current_basestation != null) {
		// bs_id = this.current_basestation.return_bs_id();
		// }
		// int bs_id = this.current_basestation.return_bs_id();
		int RSS = 0;
		double SIR_RECORD = this.current_SIR;
		// if(this.current_basestation.return_bs_id() == 0) {
		// this.label = 5;
		// }
		// else {
		// this.label = 3;
		// }
		// this.label = 0;
		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);

		String fileName = temp_pre_fix + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(current_batch) + File.separator + "uav_communucation" + ".csv";
		Geometry myPoint = geography.getGeometry(this);
		try {
			PrintWriter writer_uav_communication = new PrintWriter(new FileOutputStream(new File(fileName), true));
			StringBuilder uav_communication_string = new StringBuilder();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			String time = format.format(new Date());
			uav_communication_string.append(internal_time_step);
			uav_communication_string.append(',');
			uav_communication_string.append(uav_id);
			uav_communication_string.append(',');
			uav_communication_string.append(bs_id);
			uav_communication_string.append(',');
			uav_communication_string.append(channel_id);
			uav_communication_string.append(',');
			uav_communication_string.append(SIR_RECORD);
			uav_communication_string.append(',');
			uav_communication_string.append(this.label);
			uav_communication_string.append('\n');
			writer_uav_communication.write(uav_communication_string.toString());
			writer_uav_communication.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// save UAV location in each time tick
	private void save_location(String finished) {
		save_communication();
		if (finished.equals("-1") == true) {
			if (this.current_basestation != null) {
				String tmp_key = Integer.toString(this.current_basestation.return_bs_id())
						+ Long.toString(this.channel_id);
				this.current_basestation.removeLink(tmp_key);
				// this.current_basestation.removeLink(this.channel_id);
				current_table.delete_report(this.id);
			}
		}

		int id = this.id;
		double tt_sinr = this.current_SIR;
		int current_lable = this.label;
		String mission = this.my_operation.return_mission();

		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);

		int level = this.return_level();

		String fileName = temp_pre_fix + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(current_batch) + File.separator + "uav_index" + ".csv";
		String fileName_coordinate = temp_pre_fix + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(current_batch) + File.separator + "uav_coordinate_reactive" + ".csv";
		Geometry myPoint = geography.getGeometry(this);
		try {
			PrintWriter writer_uav_index = new PrintWriter(new FileOutputStream(new File(fileName), true));
			PrintWriter writer_uav_coordinate = new PrintWriter(
					new FileOutputStream(new File(fileName_coordinate), true));
			StringBuilder uav_index_string = new StringBuilder();
			StringBuilder uav_coordinate_string = new StringBuilder();

			// Double x_coordinate = BigDecimal.valueOf(myPoint.getCoordinate().x).setScale(7, RoundingMode.HALF_UP)
					// .doubleValue();
			Double x_coordinate = myPoint.getCoordinate().x;
			Double y_coordinate = myPoint.getCoordinate().y;

			// -76.2597052, 43.0802922
			// double y = distance2Coordinate(geography, 43.0802922, -76.2597052,
			// 43.0802922, myPoint.getCoordinate().x);
			// double x = distance2Coordinate(geography, 43.0802922, -76.2597052,
			// myPoint.getCoordinate().y, -76.2597052);
			//
			double y = distance2Coordinate(geography, 47.936572, -103.4359855, 47.936572, myPoint.getCoordinate().x);
			double x = distance2Coordinate(geography, 47.936572, -103.4359855, myPoint.getCoordinate().y, -103.4359855);

			Double loss = BigDecimal.valueOf(this.log_distance_loss).setScale(7, RoundingMode.HALF_UP).doubleValue();
			if (this.TrajectoryState == 1 && finished != "-1")
				finished = Integer.toString(2);
			for (int i = 1; i < 5; i++) {
				if (0 < this.History[i]) {
					finished = Integer.toString(2);
				}
			}
			if (this.current_basestation == null) {
				uav_index_string.append(internal_time_step);
				uav_index_string.append(',');
				uav_index_string.append(id);
				uav_index_string.append(',');
				uav_index_string.append(y);
				uav_index_string.append(',');
				uav_index_string.append(x);
				uav_index_string.append(',');
				uav_index_string.append("999");
				uav_index_string.append(',');
				uav_index_string.append("-1");
				uav_index_string.append(',');
				uav_index_string.append(finished);
				uav_index_string.append(',');
				uav_index_string.append(current_lable);
				uav_index_string.append(',');
				uav_index_string.append(this.unmanned);
				uav_index_string.append(',');
				uav_index_string.append(level);
				uav_index_string.append(',');
				// uav_index_string.append('\n');
				// writer_uav_index.write(uav_index_string.toString());
				// writer.write(String.format(formatStr, internal_time_step, id, y, x, 999,
				// "-1", finished));
				uav_coordinate_string.append(internal_time_step);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(id);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(y_coordinate);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(x_coordinate);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append("999");
				uav_coordinate_string.append(',');
				uav_coordinate_string.append("-1");
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(finished);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(this.TrajectoryState);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(this.unmanned);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(level);
				uav_coordinate_string.append(',');
				for (int i = 0; i < 4; i++) {
					this.History[i] = this.History[i + 1];
				}
				int flag = (int) this.TrajectoryState;
				if (flag == 1) {
					for (UAV cuav : this.TrajectoryList) {
						if (!cuav.unmanned) {
							flag = 2;
							break;
						}
					}
					uav_coordinate_string.append(flag);
					uav_index_string.append(flag);
					// this.History = flag;
					this.History[4] = flag;
				} else {
					// uav_coordinate_string.append(flag);
					// uav_index_string.append(flag);
					// this.History = -1;
					for (int i = 0; i < 4; i++) {
						if (flag < this.History[i]) {
							flag = this.History[i];
						}
					}
					if (internal_time_step > 1550 && internal_time_step < 1560)
						System.out.println("********************************************************Flag: " + flag);
					uav_coordinate_string.append(flag);
					uav_index_string.append(flag);
					this.History[4] = -1;
				}

				uav_coordinate_string.append('\n');
				uav_index_string.append('\n');
				writer_uav_index.write(uav_index_string.toString());
				writer_uav_coordinate.write(uav_coordinate_string.toString());
			} else {
				uav_index_string.append(internal_time_step);
				uav_index_string.append(',');
				uav_index_string.append(id);
				uav_index_string.append(',');
				uav_index_string.append(y);
				uav_index_string.append(',');
				uav_index_string.append(x);
				uav_index_string.append(',');
				uav_index_string.append(loss);
				uav_index_string.append(',');
				uav_index_string.append(this.current_basestation.return_bs_id());
				uav_index_string.append(',');
				uav_index_string.append(finished);
				uav_index_string.append(',');
				uav_index_string.append(current_lable);
				uav_index_string.append(',');
				uav_index_string.append(this.unmanned);
				uav_index_string.append(',');
				uav_index_string.append(level);
				uav_index_string.append(',');
				// uav_index_string.append('\n');
				// writer_uav_index.write(uav_index_string.toString());

				uav_coordinate_string.append(internal_time_step);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(id);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(y_coordinate);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(x_coordinate);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(loss);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(this.current_basestation.return_bs_id());
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(finished);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(this.TrajectoryState);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(this.unmanned);
				uav_coordinate_string.append(',');
				uav_coordinate_string.append(level);
				uav_coordinate_string.append(',');
				for (int i = 0; i < 4; i++) {
					this.History[i] = this.History[i + 1];
				}
				int flag = (int) this.TrajectoryState;
				if (flag == 1) {
					for (UAV cuav : this.TrajectoryList) {
						if (!cuav.unmanned) {
							flag = 2;
							break;
						}
					}
					if (internal_time_step > 1550 && internal_time_step < 1560)
						System.out.println(
								"****************************************************************Flag: " + flag);
					uav_coordinate_string.append(flag);
					uav_index_string.append(flag);
					// this.History = flag;
					this.History[4] = flag;
				} else {
					// uav_coordinate_string.append(flag);
					// uav_index_string.append(flag);
					// this.History = -1;
					for (int i = 0; i < 4; i++) {
						if (flag < this.History[i]) {
							flag = this.History[i];
						}
					}
					uav_coordinate_string.append(flag);
					uav_index_string.append(flag);
					this.History[4] = -1;
				}
				uav_coordinate_string.append('\n');
				uav_index_string.append('\n');
				writer_uav_index.write(uav_index_string.toString());
				writer_uav_coordinate.write(uav_coordinate_string.toString());

			}

			writer_uav_index.close();
			writer_uav_coordinate.close();
			this.movestatus = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private double getMiles(double i) {
		return i * 0.000621371192;
	}

	private double getMeters(double i) {
		return i * 1609.344;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(internal_time_step) + ",");
		sb.append(Integer.toString(id) + ",");
		sb.append(Double.toString(Math.log10(ue.getSinr()) * 10.0) + ",");
		sb.append(Double.toString(ue.getSinr()) + ",");
		sb.append(Double.toString(ue.getAttachedBaseStationID()) + ",");
		sb.append(Double.toString(ue.getDistance()));
		/* 
		List<float[]> wrap = genInputWithCurrentLocation();
		double sinr = -1.0;
		try {
			// sinr = Model.getInstance().calcWeightedSINR(wrap.x.array(),
			// 	wrap.numberOfUeAttachedToInterferenceBS,
			// 	wrap.distanceToAttachedBS, 15);
			sinr = Model.getInstance().calcPreictedSinr(wrap.get(0), wrap.get(1));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		sb.append(Float.toString(wrap.get(0)[0]) + ",");
		sb.append(Double.toString(sinr) + ",");
		// sb.append(Double.toString(ue.getDistance()) + ",");
		// sb.append(Double.toString(wrap.distanceToAttachedBS));
		*/
		return sb.toString();
	}

	public List<float[]> genInputWithCurrentLocation() {
		Geometry myPoint = geography.getGeometry(this);
		Double lng = BigDecimal.valueOf(myPoint.getCoordinate().x)
			.setScale(7, RoundingMode.HALF_UP).doubleValue();
		Double lat = BigDecimal.valueOf(myPoint.getCoordinate().y)
			.setScale(7, RoundingMode.HALF_UP).doubleValue();
		ArrayList<Double> coor = new ArrayList<>();
		coor.add(lng);
		coor.add(lat);
		List<float[]> xs = InputFactory.produceInput(ue, coor);
		return xs;
	}

}

class UAVLogger {
	private Path path;
	public static final String header = "Timestamp,Repast ID,Ue SINR(dB),"
		+ "Ue SINR(Linear),"
		+ "Ue Attached BS ID,Ue Distance";
	public UAVLogger(String path) {
		this.path = Paths.get(path);
		save(Arrays.asList(header));
	}

	private void save(List<String> lines, OpenOption... options) {
		try {
			Files.write(path, lines, StandardCharsets.UTF_8, options);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void save(String line) {
		save(Arrays.asList(line), StandardOpenOption.APPEND);
	}
}

