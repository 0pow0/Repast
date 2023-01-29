package jzombies;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.geotools.referencing.GeodeticCalculator;

import com.google.flatbuffers.FlatBufferBuilder;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import ROC.MessageType;
import ROCBuilder.Message;
import ROCBuilder.UserEquipmentReq;
import ROCBuilder.CreationReq;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.gis.Geography;
import repast.simphony.util.ContextUtils;
import util.AppConf;
import repast.BaseStationContainer;
import repast.BaseStationController;
import repast.NS3CommunicatiorHelper;
import repast.NS3Communicator;
import repast.UserEquipment;
import repast.UserEquipmentController;

/**
 * @author ziyizhao
 *
 */
public class UAVmanagement {
	private Geography<Object> geography;
	private GeometryFactory fac;
	private ArrayList<UAV> uavs_list;
	private int internal_time_step;
	private Map<String, ArrayList<Double>> airport_list;
	int uav_mission_index = 1;
	private HashMap<UAV, String> id_index_map = new HashMap<>();
	private Map<String, terrain_node> terrain_start_location;
	private Map<String, terrain_node> terrain_end_location;
	private int current_batch;
	private int uav_generation_rate;
	private boolean if_straight;
	private boolean if_routing;
	private Deconfliction my_Deconfliction;
	public int fail_count;
	private int simulation_time;
	private Random seed;
	long num_ch;
	private BaseStationController baseStationController;
	private NS3CommunicatiorHelper ns3CommunicatiorHelper;

	public UAVmanagement(Geography<Object> geography, GeometryFactory fac, ArrayList<UAV> uavs_list,
			Map<String, ArrayList<Double>> airport_list,
			Map<String, terrain_node> terrain_start_location, Map<String, terrain_node> terrain_end_location,
			int current_batch, int uav_generation_rate, boolean if_straight, boolean if_routing, int simulation_time,
			int c_seed, long num_ch,
			BaseStationController baseStationController) {
		this.geography = geography;
		this.fac = fac;
		this.uavs_list = uavs_list;
		this.airport_list = airport_list;
		this.internal_time_step = -1;
		this.terrain_start_location = terrain_start_location;
		this.terrain_end_location = terrain_end_location;
		this.current_batch = current_batch;
		this.uav_generation_rate = uav_generation_rate;
		this.if_straight = if_straight;
		this.if_routing = if_routing;
		this.num_ch = num_ch;
		this.baseStationController = baseStationController;
		this.ns3CommunicatiorHelper = new NS3CommunicatiorHelper();
		// my_Deconfliction = new Deconfliction(geography, 1, -76.2597052, 43.0802922, -76.0186519, 42.9749775,
				// simulation_time);
				 
		my_Deconfliction = new Deconfliction(geography, 1, -103.03918, 48.03193, -102.85917, 48.00326,
				simulation_time, baseStationController);
		// Montana state, Liberty, Hill, Blaine, Philips and Chouteau county
		// my_Deconfliction = new Deconfliction(geography, 1, -111.266107, 48.994677,
		// -109.805044, 48.027456, simulation_time);
		// my_Deconfliction = new Deconfliction(geography, 1, -111.266107, 48.994677,
		// -111.035044, 47.907456, simulation_time);
		this.seed = new Random(c_seed);
		this.fail_count = 0;
		this.simulation_time = simulation_time;
	}

	public ArrayList<UAV> return_current_uavs_list() {
		return uavs_list;
	}

	public Map<String, terrain_node> return_terrain_start_location() {
		return terrain_start_location;
	}

	public Map<String, terrain_node> return_terrain_end_location() {
		return terrain_end_location;
	}

	public void set_block_area(short start_x, short end_x, short start_y, short end_y, int duration) {
		my_Deconfliction.set_block_area(start_x, end_x, start_y, end_y, duration);
	}

	public void count_current_UAV(int currenttime) {

		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);

		String uav_count_report = temp_pre_fix + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(current_batch) + File.separator + "uav_count_report" + ".csv";

		ArrayList<Object> current_UAVs = new ArrayList<Object>();
		if (this.internal_time_step % 10 == 0) {
			for (Object obj : this.geography.getAllObjects()) { // grid.getObjects()
				if (obj instanceof UAV) {
					current_UAVs.add(obj);
				}
			}

			if (!current_UAVs.isEmpty()) {
				try {
					PrintWriter writer_uav_count_report = new PrintWriter(new FileOutputStream(new File(uav_count_report), true));
					StringBuilder uav_count_report_string = new StringBuilder();
					uav_count_report_string.append(currenttime);
					uav_count_report_string.append(',');
					uav_count_report_string.append(current_UAVs.size());
					uav_count_report_string.append('\n');
					writer_uav_count_report.write(uav_count_report_string.toString());
					writer_uav_count_report.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void record(HashMap<UAV, String> id_index_map) {
		ArrayList<UAV> removed_uav = new ArrayList<UAV>();
		for (UAV curr_uav : this.id_index_map.keySet()) {
			if (curr_uav.return_end_time() != -1) {
				record_uav_information(curr_uav, this.id_index_map.get(curr_uav));
				removed_uav.add(curr_uav);
			}
		}

		for (UAV curr_uav : removed_uav) {
			this.id_index_map.remove(curr_uav);
		}
	}

	private void record_uav_information(UAV curr_uav, String connection_point) {

		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);

		String fileName_mission = temp_pre_fix + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(current_batch) + File.separator + "mission_report" + ".csv";

		try {
			PrintWriter writer_mission = new PrintWriter(new FileOutputStream(new File(fileName_mission), true));
			StringBuilder mission_string = new StringBuilder();
			if (curr_uav.return_Operation().return_mode() != "probability") {
				mission_string.append(this.uav_mission_index);
				mission_string.append(',');
				mission_string.append(curr_uav.return_Operation().return_mission());
				mission_string.append(',');
				mission_string.append(curr_uav.hashCode());
				mission_string.append(',');
				mission_string.append(curr_uav.return_start_time());
				mission_string.append(',');
				mission_string.append(curr_uav.return_end_time());
				mission_string.append(',');
				mission_string.append(curr_uav.return_end_time() - curr_uav.return_start_time());
				mission_string.append(',');
				mission_string.append(curr_uav.return_Operation().return_start_point());
				mission_string.append(',');
				mission_string.append(connection_point);
				mission_string.append(',');
				mission_string.append(curr_uav.return_Operation().return_destination_point());
				mission_string.append('\n');
				writer_mission.write(mission_string.toString());
			} else {
				Double start_x = BigDecimal.valueOf(curr_uav.return_start_coordinate_pair().get(0))
						.setScale(7, RoundingMode.HALF_UP)
						.doubleValue();
				Double start_y = BigDecimal.valueOf(curr_uav.return_start_coordinate_pair().get(1))
						.setScale(7, RoundingMode.HALF_UP)
						.doubleValue();
				Double end_x = BigDecimal.valueOf(curr_uav.return_end_coordinate_pair().get(0))
						.setScale(7, RoundingMode.HALF_UP)
						.doubleValue();
				Double end_y = BigDecimal.valueOf(curr_uav.return_end_coordinate_pair().get(1))
						.setScale(7, RoundingMode.HALF_UP)
						.doubleValue();
				String start_location = "(" + start_x + ":" + start_y + ")";
				String destination_location = "(" + end_x + ":" + end_y + ")";
				mission_string.append(this.uav_mission_index);
				mission_string.append(',');
				mission_string.append(curr_uav.return_Operation().return_mission());
				mission_string.append(',');
				mission_string.append(curr_uav.hashCode());
				mission_string.append(',');
				mission_string.append(curr_uav.return_start_time());
				mission_string.append(',');
				mission_string.append(curr_uav.return_end_time());
				mission_string.append(',');
				mission_string.append(curr_uav.return_end_time() - curr_uav.return_start_time());
				mission_string.append(',');
				mission_string.append(start_location);
				mission_string.append(',');
				mission_string.append(connection_point);
				mission_string.append(',');
				mission_string.append(destination_location);
				mission_string.append('\n');
				writer_mission.write(mission_string.toString());
			}

			writer_mission.close();
			this.uav_mission_index++;
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void dynamic_add_uav() {
		this.internal_time_step++;
		count_current_UAV(this.internal_time_step);

		// ? What is id_index_map do?
		record(this.id_index_map);

		ArrayList<UAV> current_start_uavs_list = new ArrayList<UAV>();
		for (UAV temp : this.uavs_list) {
			if (temp.return_start_time() == this.internal_time_step) {
				current_start_uavs_list.add(temp);
			}
		}

		for (UAV temp : current_start_uavs_list) {
			this.uavs_list.remove(temp);
		}

		if (!current_start_uavs_list.isEmpty()) {

			// //////////////////////////////////////////////////////////////////
			// // Move all the elements
			for (UAV temp_uav : current_start_uavs_list) {

				///////////////////////////
				// deconfliction
				boolean if_routing_success = false;
				if (if_routing && !if_straight && temp_uav.return_unmanned()) {
					// System.out.println("with routing");
					if_routing_success = my_Deconfliction.FillOutArray(temp_uav, current_batch);
				} else {
					// System.out.println("without routing");
					if_routing_success = true;
				}

				//////////////////////////
				if (if_routing_success) {
					System.out.println("Success");
					Context<Object> context = ContextUtils.getContext(this);
					context.add(temp_uav);

					Coordinate coord_uav = new Coordinate(temp_uav.return_start_coordinate_pair().get(0),
							temp_uav.return_start_coordinate_pair().get(1));
					Point geom_uav = fac.createPoint(coord_uav);
					this.geography.move(temp_uav, geom_uav);
					// System.out.println(temp_uav.return_index_pair().size());
					String start_location = "(" + temp_uav.return_start_coordinate_pair().get(0) + ","
							+ temp_uav.return_start_coordinate_pair().get(1) + ")";
					String connection_location = " ";
					String destination_location = "(" + temp_uav.return_end_coordinate_pair().get(0) + ","
							+ temp_uav.return_end_coordinate_pair().get(1) + ")";
					System.out.println("Probability Generation : " + start_location + "->" +
					connection_location + "->" + destination_location);
					this.id_index_map.put(temp_uav, "N/A");

					/*
		 			 * Routing Option 
					 * Routing with SINR
					 */
					if (AppConf.getInstance().getBoolean("routingWithSINRPrediction")) {
						Coordinate coor = geography.getGeometry(temp_uav).getCoordinate();
						ns3CommunicatiorHelper.sendCreationReq(
							Integer.toString(temp_uav.return_Id()),
							Double.toString(coor.y),
							Double.toString(coor.x),
							0);
						UserEquipmentController userEquipmentController
							= (UserEquipmentController) context
								.getObjects(UserEquipmentController.class).get(0);
						userEquipmentController.getContainer().add(temp_uav.getUe());
					}
				} else {
					this.fail_count++;
					System.out.println("Fail Count " + fail_count);
				}
				// System.out.println("Routing Fail:" + this.fail_count);
				// System.out.println("");
			}
			// //////////////////////////////////////////////////////////////////

		}
		current_start_uavs_list.clear();

		if (this.internal_time_step % uav_generation_rate == 0) {
			System.out.print("current time\n");
			System.out.print(this.internal_time_step + "\n");
			//////////////////////////////////////////////////////////////////
			// UAV probability generation
			for (String area_name : this.terrain_start_location.keySet()) {

				ArrayList<Double> start_coordinate_pair = area_generate_start_points(terrain_start_location.get(area_name),
						this.seed);
				System.out.println(start_coordinate_pair);

				if (!start_coordinate_pair.isEmpty()) {

					// Random r_1 = new Random();
					// double low = 27;
					// double high = 45;

					double speed = 18;

					Random r = new Random();
					int Low = 1;
					int High = 4;
					int level = this.seed.nextInt(High - Low) + Low;
					// int level = r.nextInt(High-Low) + Low;

					boolean unmanned = true;
					// Random r11 = new Random();
					// int Low11 = 0;
					// int High11 = 10;
					// int man = r.nextInt(High11-Low11) + Low11;
					// if(man==8) {
					// System.out.println("Manned");
					// unmanned = false;
					// }
					UAV temp_uav = new UAV(geography, 400, speed, this.if_straight, this.if_routing, this.internal_time_step, 0,
							0, true, "probability", null, current_batch, level, unmanned, -1, this.num_ch);
					temp_uav.set_internal_time(this.internal_time_step);

					double T_distance = 0;

					ArrayList<Double> end_coordinate_pair;
					ArrayList<ArrayList<Double>> connection_coordinate_pair;
					ArrayList<ArrayList<Double>> connection_index_pair;
					// do {
					// end_coordinate_pair = area_generate_end_points(this.terrain_end_location);
					// connection_coordinate_pair = new ArrayList<ArrayList<Double>>();
					// connection_index_pair = new ArrayList<ArrayList<Double>>();
					// short start_x = (short) (distance2Coordinate(geography,
					// 48.994677,-111.266107, start_coordinate_pair.get(1), -111.266107) / (18 *
					// 5));
					// short start_y = (short) (distance2Coordinate(geography,
					// 48.994677,-111.266107, 48.994677, start_coordinate_pair.get(0)) / (18 * 5));
					// short end_x = (short) (distance2Coordinate(geography, 48.994677,-111.266107,
					// end_coordinate_pair.get(1), -111.266107) / (18 * 5));
					// short end_y = (short) (distance2Coordinate(geography, 48.994677,-111.266107,
					// 48.994677, end_coordinate_pair.get(0)) / (18 * 5));
					//
					// T_distance = Math.sqrt(Math.pow((Math.abs(start_x - end_x) - 2) * 90, 2) +
					// Math.pow((Math.abs(start_y - end_y) - 2) * 90, 2));
					//// System.out.print("*******************************\n");
					//// System.out.print(T_distance);
					// }while(81000 > T_distance);
					// System.out.print("########################\n");
					// System.out.print(T_distance);
					//

					end_coordinate_pair = area_generate_end_points(this.terrain_end_location, this.seed);
					connection_coordinate_pair = new ArrayList<ArrayList<Double>>();
					connection_index_pair = new ArrayList<ArrayList<Double>>();
					// System.out.println("Out");
					// System.out.println(end_coordinate_pair.get(0));
					// System.out.println(end_coordinate_pair.get(1));
					if (!if_straight) {
						ArrayList<Double> connection_coord_pair = new ArrayList<Double>();
						connection_coord_pair.add(start_coordinate_pair.get(0));
						connection_coord_pair.add(end_coordinate_pair.get(1));
						connection_coordinate_pair.add(connection_coord_pair);
					}
					temp_uav.set_start_coordinate_pair(start_coordinate_pair);
					temp_uav.set_connection_coordinate_pair(connection_coordinate_pair);
					temp_uav.set_connection_index_pair(connection_index_pair);
					temp_uav.set_end_coordinate_pair(end_coordinate_pair);
					// System.out.println("Result"+temp_uav.return_connection_coordinate_pair().get(0));

					///////////////////////////
					// deconfliction
					boolean if_routing_success = false;

					if (if_routing && !if_straight && temp_uav.return_unmanned()) {
						// System.out.println("with routing");
						ArrayList<Double> connection_coordinate = area_generate_end_points(this.terrain_end_location, this.seed);
						connection_coordinate_pair.add(connection_coordinate);
						temp_uav.set_connection_coordinate_pair(connection_coordinate_pair);
						// temp_uav.stop_x = (short) (distance2Coordinate(geography, -76.2597052,
						// 43.0802922, -76.2597052, connection_coordinate.get(1)) / (18 * 5));;
						// temp_uav.stop_y = (short) (distance2Coordinate(geography, -76.2597052,
						// 43.0802922, connection_coordinate.get(0), 43.0802922) / (18 * 5));

						if_routing_success = my_Deconfliction.FillOutArray(temp_uav, current_batch);
					} else {
						// System.out.println("without routing");
						if_routing_success = true;
					}

					//////////////////////////
					if (if_routing_success) {
						Context<Object> context = ContextUtils.getContext(this);
						context.add(temp_uav);
						Coordinate coord_uav = new Coordinate(start_coordinate_pair.get(0), start_coordinate_pair.get(1));
						Point geom_uav = fac.createPoint(coord_uav);
						this.geography.move(temp_uav, geom_uav);
						// System.out.println(temp_uav.return_index_pair().size());
						String start_location = "(" + start_coordinate_pair.get(0) + "," + start_coordinate_pair.get(1) + ")";
						String connection_location = " ";
						String destination_location = "(" + end_coordinate_pair.get(0) + "," + end_coordinate_pair.get(1) + ")";
						// System.out.println("Probability Generation : " + start_location + "->" +
						// connection_location + "->" + destination_location);
						this.id_index_map.put(temp_uav, "N/A");


						Coordinate coor = geography.getGeometry(temp_uav).getCoordinate();
						ns3CommunicatiorHelper.sendCreationReq(
							Integer.toString(temp_uav.return_Id()),
							Double.toString(coor.y),
							Double.toString(coor.x),
							0);
						UserEquipmentController userEquipmentController
							= (UserEquipmentController) context
								.getObjects(UserEquipmentController.class).get(0);
						userEquipmentController.getContainer().add(temp_uav.getUe());
					} else {
						this.fail_count++;
						System.out.println("Fail Count " + fail_count);
					}
					// System.out.println("Routing Fail:" + this.fail_count);
					// System.out.println("");
				}
			}
		}

		//////////////////////////////////////////////////////////////////

		if (this.internal_time_step == this.simulation_time - 1) {
			copy_folders_temp_to_local(this.current_batch);
		}

	}

	public void copy_folders_temp_to_local(int current_batch) {
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(".." + File.separator + "config.props"))) {
			while ((line = br.readLine()) != null) {
				if (line.contains("model.archive")) {
					break;
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		line = line.substring(line.indexOf("=") + 2, line.indexOf("output"));
		// System.out.println("**********************************************************************************************");
		// System.out.println("Copying temp to local: " + line);
		// System.out.println("**********************************************************************************************");
		String destination = line + "report" + File.separator + "batch_" + current_batch;
		File destDir = new File(destination);

		String source = System.getProperty("user.dir");
		int last_indes = source.lastIndexOf(File.separator);
		String temp_pre_fix = source.substring(0, last_indes);
		source = temp_pre_fix + File.separator + "report" + File.separator + "batch_" + current_batch;

		File srcDir = new File(source);

		// try {
		// 	FileUtils.copyDirectory(srcDir, destDir);
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }
	}

	private static double distance2Coordinate(Geography g, double lat_1, double lon_1, double lat_2, double lon_2) {
		GeodeticCalculator calculator = new GeodeticCalculator(g.getCRS());
		calculator.setStartingGeographicPoint(lon_1, lat_1);
		calculator.setDestinationGeographicPoint(lon_2, lat_2);
		return calculator.getOrthodromicDistance();
	}

	public ArrayList<Double> area_generate_start_points(terrain_node node, Random r_1) {
		ArrayList<Double> coordinate_pair = new ArrayList<Double>();
		double probability = node.return_probability();

		// System.out.println("r_1:" + probability);
		double rr1 = r_1.nextDouble();
		double rr2 = r_1.nextDouble();
		double rr3 = r_1.nextDouble();
		// System.out.println("r_11:" + rr1);
		// System.out.println("r_12:" + rr2);
		// System.out.println("r_13:" + rr3);
		double low = 0;
		double high = 1;
		double random_probability = (high - low) * rr1 + low;

		if (random_probability <= probability) {
			System.out.println(node);
			double x_min = node.return_top_left_x_coordinate();
			double x_max = node.return_bottom_right_x_coordinate();

			double y_min = node.return_bottom_right_y_coordinate();
			double y_max = node.return_top_left_y_coordinate();

			// Random r_2 = new Random(11);
			// Random r_3 = new Random(22);

			double random_x = (x_max - x_min) * rr2 + x_min;
			double random_y = (y_max - y_min) * rr3 + y_min;

			coordinate_pair.add(random_x);
			coordinate_pair.add(random_y);
		}
		return coordinate_pair;
	}

	public ArrayList<Double> area_generate_end_points(Map<String, terrain_node> terrain_end_location, Random r_1) {

		ArrayList<Double> coordinate_pair = new ArrayList<Double>();

		double totalWeight = 0.0d;
		for (String end_key : terrain_end_location.keySet()) {
			totalWeight += terrain_end_location.get(end_key).return_probability();
		}
		// Now choose a random item
		String randomIndex = "";
		double random = r_1.nextDouble() * totalWeight;

		for (String end_key : terrain_end_location.keySet()) {
			random -= terrain_end_location.get(end_key).return_probability();
			if (random <= 0.0d) {
				randomIndex = end_key;
				break;
			}
		}
		terrain_node temp_node = terrain_end_location.get(randomIndex);

		double x_min = temp_node.return_top_left_x_coordinate();
		double x_max = temp_node.return_bottom_right_x_coordinate();

		double y_min = temp_node.return_bottom_right_y_coordinate();
		double y_max = temp_node.return_top_left_y_coordinate();

		// Random r_2 = new Random();
		// Random r_3 = new Random();
		double rr1 = r_1.nextDouble();
		double rr2 = r_1.nextDouble();
		// double rr3= r_1.nextDouble();

		double random_x = (x_max - x_min) * rr1 + x_min;
		double random_y = (y_max - y_min) * rr2 + y_min;
		// System.out.println("In");
		// System.out.println(random_x);
		// System.out.println(random_y);

		coordinate_pair.add(random_x);
		coordinate_pair.add(random_y);

		return coordinate_pair;
	}

	public int return_internal_time() {
		return internal_time_step;
	}

}
