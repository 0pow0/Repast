package jzombies;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.swing.JPanel;

import repast.BaseStationContainer;
import repast.BaseStationController;
import repast.NS3CommunicatiorHelper;
import repast.NS3Communicator;
import repast.UserEquipmentController;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.space.graph.Network;
import util.AppConf;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import au.com.bytecode.opencsv.CSVReader;

//import repast.simphony.ui.RSApplication;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.commons.io.FileUtils;
import org.geotools.referencing.GeodeticCalculator;

public class JZombiesBuilder implements ContextBuilder<Object> {

	public String copy_folders_local_to_temp() {
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
		System.out
				.println("**********************************************************************************************");
		System.out.println("Copying local to temp: " + line);
		System.out
				.println("**********************************************************************************************");
		String source = line + "configuration";
		File srcDir = new File(source);

		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);
		// destination = temp_pre_fix + File.separator + "configuration";

		// File destDir = new File(destination);

		// try {
		// 	FileUtils.copyDirectory(srcDir, destDir);
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }
		return temp_pre_fix;
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
		System.out
				.println("**********************************************************************************************");
		System.out.println("Copying temp to local: " + line);
		System.out
				.println("**********************************************************************************************");
		String destination = line + "report";
		File destDir = new File(destination);

		String source = System.getProperty("user.dir");
		int last_indes = source.lastIndexOf(File.separator);
		String temp_pre_fix = source.substring(0, last_indes);
		source = temp_pre_fix + File.separator + "report" + File.separator + "batch_" + current_batch;

		File srcDir = new File(source);

		try {
			FileUtils.copyDirectory(srcDir, destDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Context build(Context<Object> context) {
		String temp_destination = copy_folders_local_to_temp();
		// TODO Auto-generated method stub
		context.setId("jzombies");
		System.out.println("Working Directory = " + System.getProperty("user.dir"));

		//////////////////////////////////////////////////////////////////
		// Initialize parameters

		Parameters params = RunEnvironment.getInstance().getParameters();

		// boolean if_Config_from_File = params.getBoolean("Config_from_File");

		long UAVCount;
		int simulation_time;
		boolean if_different_start_time;
		boolean if_straight;
		long targetCount;
		long basestationCount;
		long base_station_channels;
		int numberofruntime;
		int uav_generation_rate;
		int current_random;
		boolean if_routing;
		String mav_trajectory_type;
		int mav_generation_rate;
		boolean if_load_mav;

		// if (if_Config_from_File) {
		// UAVCount = mytool.read_uav_count_JSON();
		// if_different_start_time = mytool.read_if_different_start_time_JSON();
		////
		//// targetCount = mytool.read_target_count_JSON();
		////
		// basestationCount = mytool.read_basestation_count_JSON();
		// base_station_channels = mytool.read_channel_count_JSON();
		// }
		// else {
		UAVCount = 0;// params.getInteger("UAVCount");
		simulation_time = params.getInteger("simulation_time");
		if_different_start_time = true; // params.getBoolean("if_different_start_time");
		if_routing = true;

		if (params.getString("if_straight").toLowerCase().equals("straight")) {
			System.out.print("************** if_straight ***************");
			if_straight = true;
			System.out.print(if_straight);
		} else {
			if_straight = false;
		}

		if (params.getString("if_routing").toLowerCase().equals("yes")) {
			System.out.print("************** if_routing ***************");
			if_routing = true;
			System.out.print(if_routing);
		} else {
			if_routing = false;
		}

		if (params.getString("if_load_mav").toLowerCase().equals("yes")) {
			System.out.print("************** if_load_mav ***************");
			if_load_mav = true;
			System.out.print(if_routing);
		} else {
			if_load_mav = false;
		}

		basestationCount = 10; // params.getInteger("basestationCount");
		base_station_channels = params.getInteger("ChannelsCount");
		numberofruntime = params.getInteger("NumberOfRunTime");
		uav_generation_rate = params.getInteger("UAVsGenerationRate");
		current_random = params.getInteger("randomSeed");
		mav_trajectory_type = params.getString("MAV_trajectory");
		mav_generation_rate = params.getInteger("MAVsGenerationRate");

		current_random = -460698188;
		// temp_destination = params.getString("output_path");
		// }
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// initialize record

		create_folder(
				temp_destination + File.separator + "report" + File.separator + "batch_" + Integer.toString(numberofruntime));
		System.out.println("10月30" + temp_destination);
		String fileName_simulation_parameters = temp_destination + File.separator + "report" + File.separator
				+ "simulation_parameters" + ".csv";
		String fileName_basestation = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "basestation" + ".csv";
		String fileName_mission = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "mission_report" + ".csv";
		String fileName_uav_index = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "uav_index" + ".csv";
		String fileName_uav_speed = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "uav_speed" + ".csv";
		String fileName_uav_communication = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "uav_communucation" + ".csv";
		String fileName_uav_coordinate = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "uav_coordinate_reactive" + ".csv";
		String fileName_uav_count_report = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "uav_count_report" + ".csv";
		String fileName_random_seed_report = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "random_seed_report" + ".csv";
		String fileName_BS_coord = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "BS_coord" + ".csv";
		String fileName_table = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "comm_table" + ".csv";
		String fileName_SINR = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "uav_SINR" + ".csv";
		String fileName_Throughput = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "uav_Throughput" + ".csv";
		String fileName_Enb = temp_destination + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(numberofruntime) + File.separator + "bs_Enb" + ".csv";

		try {
			PrintWriter writer_simulation_parameters = new PrintWriter(
					new FileOutputStream(new File(fileName_simulation_parameters), true));
			PrintWriter writer_basestation = new PrintWriter(new File(fileName_basestation));
			PrintWriter writer_mission = new PrintWriter(new File(fileName_mission));
			PrintWriter writer_uav_index = new PrintWriter(new File(fileName_uav_index));
			PrintWriter writer_uav_speed = new PrintWriter(new File(fileName_uav_speed));
			PrintWriter writer_uav_communication = new PrintWriter(new File(fileName_uav_communication));
			PrintWriter writer_uav_coordinate = new PrintWriter(new File(fileName_uav_coordinate));
			PrintWriter writer_uav_count_report = new PrintWriter(new File(fileName_uav_count_report));
			PrintWriter writer_random_seed_report = new PrintWriter(new File(fileName_random_seed_report));
			PrintWriter writer_BS_coord = new PrintWriter(new File(fileName_BS_coord));
			PrintWriter writer_table = new PrintWriter(new File(fileName_table));
			PrintWriter writer_SINR = new PrintWriter(new File(fileName_SINR));
			PrintWriter writer_Throughput = new PrintWriter(new File(fileName_Throughput));
			PrintWriter writer_Enb = new PrintWriter(new File(fileName_Enb));
			// PrintWriter writer_basestation_coord = new PrintWriter(new
			// File(fileName_BS_coord));

			StringBuilder run_string = new StringBuilder();
			StringBuilder basestations_string = new StringBuilder();
			StringBuilder channels_string = new StringBuilder();
			StringBuilder generation_rate_string = new StringBuilder();
			StringBuilder routing_string = new StringBuilder();
			StringBuilder trajectory_type_string = new StringBuilder();
			StringBuilder BS_coord_string = new StringBuilder();
			StringBuilder table_string = new StringBuilder();

			BS_coord_string.append("x");
			BS_coord_string.append(',');
			BS_coord_string.append("y");
			BS_coord_string.append("\n");
			writer_BS_coord.write(BS_coord_string.toString());

			table_string.append("Time_Step");
			table_string.append(",");
			table_string.append("table");
			table_string.append("\n");
			writer_table.write(table_string.toString());

			run_string.append("run");
			run_string.append(',');
			run_string.append(numberofruntime);
			run_string.append('\n');
			writer_simulation_parameters.write(run_string.toString());

			basestations_string.append("basestations");
			basestations_string.append(',');
			basestations_string.append(basestationCount);
			basestations_string.append('\n');
			writer_simulation_parameters.write(basestations_string.toString());

			channels_string.append("channels");
			channels_string.append(',');
			channels_string.append(base_station_channels);
			channels_string.append('\n');
			writer_simulation_parameters.write(channels_string.toString());

			generation_rate_string.append("generation_rate");
			generation_rate_string.append(',');
			generation_rate_string.append(uav_generation_rate);
			generation_rate_string.append('\n');
			writer_simulation_parameters.write(generation_rate_string.toString());

			if (if_routing) {
				routing_string.append("routing");
				routing_string.append(',');
				routing_string.append("yes");
				routing_string.append('\n');
				writer_simulation_parameters.write(routing_string.toString());
			} else {
				routing_string.append("routing");
				routing_string.append(',');
				routing_string.append("no");
				routing_string.append('\n');
				writer_simulation_parameters.write(routing_string.toString());
			}
			if (if_straight) {
				trajectory_type_string.append("trajectory_type");
				trajectory_type_string.append(',');
				trajectory_type_string.append("straight");
				trajectory_type_string.append('\n');
				writer_simulation_parameters.write(trajectory_type_string.toString());
			} else {
				trajectory_type_string.append("trajectory_type");
				trajectory_type_string.append(',');
				trajectory_type_string.append("manhattan");
				trajectory_type_string.append('\n');
				writer_simulation_parameters.write(trajectory_type_string.toString());
			}

			StringBuilder uav_count_report_string = new StringBuilder();
			StringBuilder basestation_string = new StringBuilder();
			StringBuilder mission_string = new StringBuilder();
			StringBuilder uav_index_string = new StringBuilder();
			StringBuilder uav_coordinate_string = new StringBuilder();
			StringBuilder uav_speed_string = new StringBuilder();
			StringBuilder uav_communication_string = new StringBuilder();

			uav_count_report_string.append("Time_Step");
			uav_count_report_string.append(',');
			uav_count_report_string.append("The_Number_of_UAVs");
			uav_count_report_string.append('\n');
			writer_uav_count_report.write(uav_count_report_string.toString());

			basestation_string.append("Time_Step");
			basestation_string.append(',');
			basestation_string.append("Basestation_ID");
			basestation_string.append(',');
			basestation_string.append("The_Number_of_Occupied_Channels");
			basestation_string.append('\n');
			writer_basestation.write(basestation_string.toString());

			mission_string.append("Mission_ID");
			mission_string.append(',');
			mission_string.append("Mission_Type");
			mission_string.append(',');
			mission_string.append("UAV_ID");
			mission_string.append(',');
			mission_string.append("Start_Time");
			mission_string.append(',');
			mission_string.append("End_Time");
			mission_string.append(',');
			mission_string.append("Flight_Time");
			mission_string.append(',');
			mission_string.append("Start_Location");
			mission_string.append(',');
			mission_string.append("Connection_Location");
			mission_string.append(',');
			mission_string.append("End_Location");
			mission_string.append('\n');
			writer_mission.write(mission_string.toString());

			uav_speed_string.append("Time_Step");
			uav_speed_string.append(',');
			uav_speed_string.append("UAV_ID");
			uav_speed_string.append(',');
			uav_speed_string.append("Latitude");
			uav_speed_string.append(',');
			uav_speed_string.append("Longitude");
			uav_speed_string.append(',');
			uav_speed_string.append("Old_X_speed");
			uav_speed_string.append(',');
			uav_speed_string.append("Old_Y_speed");
			uav_speed_string.append(',');
			uav_speed_string.append("X_speed");
			uav_speed_string.append(',');
			uav_speed_string.append("Y_speed");
			uav_speed_string.append(',');
			uav_speed_string.append("Conflict_UAV");
			uav_speed_string.append(',');
			uav_speed_string.append("Flag");
			uav_speed_string.append('\n');
			writer_uav_speed.write(uav_speed_string.toString());

			uav_communication_string.append("Time_Step");
			uav_communication_string.append(",");
			uav_communication_string.append("UAV_ID");
			uav_communication_string.append(",");
			uav_communication_string.append("BaseStation");
			uav_communication_string.append(",");
			uav_communication_string.append("Channel");
			uav_communication_string.append(",");
			uav_communication_string.append("SIR");
			uav_communication_string.append(",");
			uav_communication_string.append("Label");
			uav_communication_string.append("\n");
			writer_uav_communication.write(uav_communication_string.toString());

			uav_index_string.append("Time_Step");
			uav_index_string.append(',');
			uav_index_string.append("UAV_ID");
			uav_index_string.append(',');
			uav_index_string.append("X_Distance");
			uav_index_string.append(',');
			uav_index_string.append("Y_Distance");
			uav_index_string.append(',');
			uav_index_string.append("Signal_strength");
			uav_index_string.append(',');
			uav_index_string.append("Current_Basestation");
			uav_index_string.append(',');
			uav_index_string.append("Finished");
			uav_index_string.append(",");
			uav_index_string.append("SINR");
			uav_index_string.append(",");
			uav_index_string.append("Unmanned");
			uav_index_string.append(",");
			uav_index_string.append("Level");
			uav_index_string.append(',');
			uav_index_string.append("Flag");
			uav_index_string.append('\n');
			writer_uav_index.write(uav_index_string.toString());

			uav_coordinate_string.append("TimeStep");
			uav_coordinate_string.append(',');
			uav_coordinate_string.append("ID");
			uav_coordinate_string.append(',');
			uav_coordinate_string.append("Latitude");
			uav_coordinate_string.append(',');
			uav_coordinate_string.append("Longitude");
			uav_coordinate_string.append(',');
			uav_coordinate_string.append("SignalStrength");
			uav_coordinate_string.append(',');
			uav_coordinate_string.append("CurrentBasestation");
			uav_coordinate_string.append(',');
			uav_coordinate_string.append("finished");
			uav_coordinate_string.append(',');
			uav_coordinate_string.append("Trajectory");
			uav_coordinate_string.append(',');
			uav_coordinate_string.append("UnManned");
			uav_coordinate_string.append(',');
			uav_coordinate_string.append("Level");
			uav_coordinate_string.append(',');
			uav_coordinate_string.append("Flag");
			uav_coordinate_string.append('\n');
			writer_uav_coordinate.write(uav_coordinate_string.toString());

			StringBuilder SINR_string = new StringBuilder();
			StringBuilder Throughput_string = new StringBuilder();
			StringBuilder Enb_string = new StringBuilder();

			SINR_string.append("TimeStep");
			SINR_string.append(',');
			SINR_string.append("ID");
			SINR_string.append(',');
			SINR_string.append("SINR");
			SINR_string.append(',');
			SINR_string.append("Distance");
			SINR_string.append(',');
			SINR_string.append("CQI");
			SINR_string.append(',');
			SINR_string.append("eNB#");
			SINR_string.append('\n');
			writer_SINR.write(SINR_string.toString());

			Throughput_string.append("TimeStep");
			Throughput_string.append(',');
			Throughput_string.append("ID");
			Throughput_string.append(',');
			Throughput_string.append("Throughput [Tx, Rx]");
			Throughput_string.append(',');
			Throughput_string.append("Time Stamp");
			Throughput_string.append(',');
			Throughput_string.append("Total bytes");
			Throughput_string.append(',');
			Throughput_string.append("No connection");
			Throughput_string.append('\n');
			writer_Throughput.write(Throughput_string.toString());

			Enb_string.append("TimeStep");
			Enb_string.append(',');
			Enb_string.append("Used_RB");
			Enb_string.append(',');
			Enb_string.append("eNB#");
			Enb_string.append('\n');
			writer_Enb.write(Enb_string.toString());

			writer_simulation_parameters.close();
			writer_uav_count_report.close();
			writer_basestation.close();
			writer_mission.close();
			writer_uav_index.close();
			writer_uav_speed.close();
			writer_uav_communication.close();
			writer_uav_coordinate.close();
			writer_random_seed_report.close();
			writer_BS_coord.close();
			writer_table.close();
			writer_SINR.close();
			writer_Throughput.close();
			writer_Enb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		record_random_seed(numberofruntime, current_random);
		//////////////////////////////////////////////////////////////////

		GeographyParameters<Object> params_geo = new GeographyParameters<Object>();
		GeographyFactory geo_factory = GeographyFactoryFinder.createGeographyFactory(null);

		Geography<Object> geography = geo_factory.createGeography("geography", context, params_geo);

		GeometryFactory fac = new GeometryFactory();

		String mode = "load";

		//* See Comments for TransportNetwork
		TransportNetwork my_transport_network = new TransportNetwork(mode);
		//* Return Nodes in TransportNetwork 
		Map<String, ArrayList<Double>> airport_list = my_transport_network.return_airport_list();
		//* Not used
		Operator my_operator = new Operator(mode);

		Util mytool = new Util();

		//* Launching and landing area loaded at here 
		Map<String, terrain_node> terrain_start_location = mytool.terrain_start_location_config();
		Map<String, terrain_node> terrain_end_location = mytool.terrain_end_location_config();

		// mytool.print_trainport_network_config(my_transport_network.return_airport_list());
		// mytool.print_trainport_network_connection(my_transport_network.return_airport_connection());

		// mytool.print_start_destination_list(my_transport_network.return_start_destination_list());
		//
		// mytool.print_mission_list(my_operator.return_mission_list());
		//
		// ArrayList<String> missions = new ArrayList();
		// missions.add("book delivery erie walmart");
		// missions.add("book delivery fairmount walmart");
		// missions.add("food delivery fayetteville wegmans");
		// missions.add("food delivery onondaga wegmans");
		// missions.add("paper delivery syracuse university");
		// missions.add("home delivery nobhill apartment");
		// missions.add("product delivery fayetteville onondaga wegmans");
		// missions.add("product delivery erie fairmount walmart");
		// missions.add("payment send onondaga wegmans");
		// missions.add("payment send fayetteville wegmans");
		// for (String name : missions) {
		// mytool.print_specific_mission_node(mytool.load_specific_operation(name));
		// }

		ArrayList<UAV> uavs_list = new ArrayList<UAV>();

		//* Load from configuration/operation.csv to generate UAVs. 
		// add all the elements
		// try {
		// 	uavs_list = read_operation(geography, if_straight, numberofruntime, base_station_channels);
		// } catch (FileNotFoundException e) {
		// 	e.printStackTrace();
		// }

		// add BaseStations
		// for (int i = 0; i < basestationCount; i++) {
		// context.add(new Base_Station(geography, base_station_channels, 0, 0, 0,
		// numberofruntime, i));
		// }

		// add Manned UAVs
		try {
			if (if_load_mav) {
				uavs_list = read_manned(geography, if_straight, numberofruntime, mav_trajectory_type, mav_generation_rate,
						base_station_channels);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// add UAVs

		// for (String name : missions) {
		//
		// Random r_1 = new Random();
		// double low = 27;
		// double high = 45;
		// double speed = (high - low) * r_1.nextDouble() + low;
		//
		// Random r_2 = new Random();
		// int min = 1;
		// int max = 1001;
		// int start_time = r_2.nextInt(max-min) + min;
		//
		// if (!if_different_start_time) {
		// start_time = 0;
		// }
		// uavs_list.add(new UAV(geography, 400, speed, if_straight, start_time, 0, 0,
		// true, name
		// , my_transport_network.return_airport_list(), my_operator, numberofruntime));
		//
		// }
		//
		// for (int i = 0; i < UAVCount; i++) {
		//
		// Random r_1 = new Random();
		// double low = 27;
		// double high = 45;
		//// double speed = (high - low) * r_1.nextDouble() + low;
		// double speed = 18;
		//
		// Random r_2 = new Random();
		// int min = 1;
		// int max = 1001;
		// int start_time = r_2.nextInt(max-min) + min;
		//
		// if (!if_different_start_time) {
		// start_time = 0;
		// }
		// uavs_list.add(new UAV(geography, 400, speed, if_straight, start_time, 0, 0,
		// true, "all_random"
		// , my_transport_network.return_airport_list(), numberofruntime));
		//
		// }

		//////////////////////////////////////////////////////////////////

		// UAVmanagement my_uav_management = new UAVmanagement(geography, fac,
		// uavs_list, airport_list, terrain_start_location, terrain_end_location,
		// numberofruntime, uav_generation_rate, if_straight, if_routing,
		// simulation_time, current_random, base_station_channels);
		// my_uav_management.set_block_area((short)40, (short)70, (short)60, (short)100,
		// simulation_time);
		// my_uav_management.set_block_area((short)55, (short)90, (short)104,
		// (short)124, simulation_time);
		// my_uav_management.set_block_area((short)60, (short)100, (short)0, (short)16,
		// simulation_time);
		// my_uav_management.set_block_area((short)0, (short)30, (short)0, (short)16,
		// simulation_time);
		//////////////////////////////////////////////////////////////////
		//? Why add Target
		// Add Targets
		for (int i = 0; i < airport_list.size(); i++) {
			context.add(new Target(geography));
		}
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// //add BaseStations
		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);

		System.out.print("************** Load Basestation ***************");

		/**
		 * * Load Base station configuration from configuration/nd0.csv file.
		 * * Not need any more since BaseStationContainer will handle this.
		 * * [Begin]
		 */
		Scanner scanner;
		try {
			scanner = new Scanner(new File(temp_pre_fix + File.separator + "configuration" + File.separator + "nd0.csv"));
			scanner.useDelimiter(",");
			// remove the first line
			if (scanner.hasNext()) {
				scanner.nextLine();
			}
			// get all operations
			int count = 0;

			while (scanner.hasNext()) {
				List<String> line = parseLine(scanner.nextLine());
				// System.out.println("Operation [id= " + line.get(1) +
				// ", Latitude= " + line.get(2) + " , Longitude=" + line.get(3) + " , level=" +
				// line.get(5) +"]");
				int id = Integer.valueOf(line.get(1));
				System.out.println("Adding");
				context.add(new Base_Station(geography, base_station_channels, 0, 0, 0, numberofruntime, id));
			}
			scanner.close();

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		//* [End]

		// for (int i = 0; i < basestationCount; i++) {
		// context.add(new Base_Station(geography, base_station_channels, 0, 0, 0,
		// numberofruntime, i));
		// }
		// //////////////////////////////////////////////////////////////////

		//* No more communication needed at Repast side, since NS3 handle this.
		// add communication table
		context.add(new Global_communication(base_station_channels));

		//* Agent used for Artificial Potential Field.
		// add Agents
		for (int x = 1; x < 9; x++) {
			for (int y = 1; y < 13; y++) {
				int AgentId = y * 100 + x;
				context.add(new Agent(geography, numberofruntime, AgentId));
			}
		}
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// Add Block Zone
		// context.add(new Block_area(geography,(short)275, (short)305, (short)515,
		// (short)590, 0, simulation_time));
		// context.add(new Block_area(geography,(short)50, (short)100, (short)500,
		// (short)600, 0, simulation_time));
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// Add UAVmanagement
		// context.add(my_uav_management);
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// Move element to the position

		//////////////////////////////////////////////////////////////////
		// Move all the elements
		for (Object obj : context) {

			// Geometry pt = geography.getGeometry(obj);

			Coordinate coord = new Coordinate(0, 0);
			Point geom = fac.createPoint(coord);
			geography.move(obj, geom);

		}

		//* [Begin] Move Nodes in TransportNetwork to according location.
		// Move Targets
		List<Object> Target_collection = new ArrayList<Object>();
		for (Object obj : geography.getAllObjects()) { // grid.getObjects()
			if (obj instanceof Target) {
				Target_collection.add(obj);
			}
		}

		Collection<ArrayList<Double>> airport_coordination_set = airport_list.values();

		ArrayList<ArrayList<Double>> airport_coordination_list = new ArrayList<ArrayList<Double>>();

		for (ArrayList<Double> temp : airport_coordination_set) {
			airport_coordination_list.add(temp);
		}

		for (int target_index = 0; target_index < Target_collection.size(); target_index++) {

			Coordinate coord_target = new Coordinate(airport_coordination_list.get(target_index).get(0),
					airport_coordination_list.get(target_index).get(1));
			Point geom_target = fac.createPoint(coord_target);
			geography.move(Target_collection.get(target_index), geom_target);

		}
		//* [End] Move Nodes in TransportNetwork to according location.
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// Move basestations
		List<Object> basestation_collection = new ArrayList<Object>();
		// for (int i = 0; i < basestationCount; i++) {
		// for (Object obj : geography.getAllObjects()) { // grid.getObjects()
		// if (obj instanceof Base_Station) {
		// if(((Base_Station) obj).return_bs_id() == i) {
		// basestation_collection.add(obj);
		// }
		// }
		// }
		// }

		// ArrayList<UAV> uavs_list = new ArrayList<UAV>();

		// String destination = System.getProperty("user.dir");
		// int last_indes = destination.lastIndexOf(File.separator);
		// String temp_pre_fix = destination.substring(0, last_indes);

		//* [Begin] Move base station to corresponding location
		/* 
		Scanner scanner;
		try {
			scanner = new Scanner(new File(temp_pre_fix + File.separator + "configuration" + File.separator + "nd0.csv"));
			scanner.useDelimiter(",");
			// remove the first line
			if (scanner.hasNext()) {
				scanner.nextLine();
			}
			// get all operations
			int count = 0;

			int i = 0;
			while (scanner.hasNext()) {
				List<String> line = parseLine(scanner.nextLine());
				// System.out.println("Operation [id= " + line.get(1) +
				// ", Latitude= " + line.get(2) + " , Longitude=" + line.get(3) + " , level=" +
				// line.get(5) +"]");
				int id = Integer.valueOf(line.get(1));
				for (Object obj : geography.getAllObjects()) { // grid.getObjects()
					if (obj instanceof Base_Station) {
						if (((Base_Station) obj).return_bs_id() == id) {
							basestation_collection.add(obj);
						}
					}
				}
				Coordinate coord_basestation = new Coordinate(Double.valueOf(line.get(6)), Double.valueOf(line.get(5)));
				Point geom_basestation = fac.createPoint(coord_basestation);
				geography.move(basestation_collection.get(i), geom_basestation);
				i++;
			}
			scanner.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/	
		//* [End] Move base station to corresponding location

		// UAV temp_uav = new UAV(geography,400, 18, if_straight,
		// 	true, 0, 0, 0,
		// 	true, "probability", null,
		// 	numberofruntime, 1, true, 627, base_station_channels);
		// ArrayList<Double> start_coordinate_pair = new ArrayList<Double>();
		// ArrayList<Double> end_coordinate_pair = new ArrayList<Double>();
		// temp_uav.set_internal_time(0);
		// start_coordinate_pair.add(-103.01318);
		// start_coordinate_pair.add(48.01468);
		// end_coordinate_pair.add(-102.89041);
		// end_coordinate_pair.add(48.01662);
		// temp_uav.set_start_coordinate_pair(start_coordinate_pair);
		// temp_uav.set_end_coordinate_pair(end_coordinate_pair);
		// ArrayList<Double> connection_coord_pair = new ArrayList<Double>();
		// connection_coord_pair.add(start_coordinate_pair.get(0));
		// connection_coord_pair.add(end_coordinate_pair.get(1));
		// ArrayList<ArrayList<Double>> connection_coordinate_pair
		// 	= new ArrayList<ArrayList<Double>>();
		// ArrayList<ArrayList<Double>> connection_index_pair
		// 	= new ArrayList<ArrayList<Double>>();
		// connection_coordinate_pair.add(connection_coord_pair);
		// temp_uav.set_connection_coordinate_pair(connection_coordinate_pair);
		// temp_uav.set_connection_index_pair(connection_index_pair);
		// uavs_list.add(temp_uav);

		/**
		 * * Launching individual UAV 
		 */
		List<String[]> uavStartEndLocations = readUavStartEndLocations(
			"/home/rzuo02/work/repast/app/src/main/"
			+ "resources/repast/start_end_pair_2.csv");
		int n = uavStartEndLocations.size();
		for (int i = 1; i < n; ++i) {
			String[] line = uavStartEndLocations.get(i);
			System.out.println(Arrays.toString(line));
			double startLng = Double.parseDouble(line[0]);
			double startLat = Double.parseDouble(line[1]);
			double endLng = Double.parseDouble(line[2]);
			double endLat = Double.parseDouble(line[3]);
			UAV uav = uavFactory(geography, i, startLng, startLat, endLng, endLat);
			uavs_list.add(uav);
		}

		// Coordinate coord_basestation_0 = new Coordinate(-102.939365, 47.980315);
		// Point geom_basestation_0 = fac.createPoint(coord_basestation_0);
		// geography.move(basestation_collection.get(0), geom_basestation_0);
		//
		// Coordinate coord_basestation_1 = new Coordinate(-103.199096, 47.909160);
		// Point geom_basestation_1 = fac.createPoint(coord_basestation_1);
		// geography.move(basestation_collection.get(1), geom_basestation_1);
		//
		// Coordinate coord_basestation_2 = new Coordinate(-102.972714, 48.038580);
		// Point geom_basestation_2 = fac.createPoint(coord_basestation_2);
		// geography.move(basestation_collection.get(2), geom_basestation_2);
		//
		// Coordinate coord_basestation_3 = new Coordinate(-102.863662, 48.081737);
		// Point geom_basestation_3 = fac.createPoint(coord_basestation_3);
		// geography.move(basestation_collection.get(3), geom_basestation_3);
		//
		// Coordinate coord_basestation_4 = new Coordinate(-102.801057, 47.976792);
		// Point geom_basestation_4 = fac.createPoint(coord_basestation_4);
		// geography.move(basestation_collection.get(4), geom_basestation_4);
		//
		// Coordinate coord_basestation_5 = new Coordinate(-102.7192251, 47.801858);
		// Point geom_basestation_5 = fac.createPoint(coord_basestation_5);
		// geography.move(basestation_collection.get(5), geom_basestation_5);
		//
		// Coordinate coord_basestation_6 = new Coordinate(-102.93947012, 47.802282);
		// Point geom_basestation_6 = fac.createPoint(coord_basestation_6);
		// geography.move(basestation_collection.get(6), geom_basestation_6);
		//
		// Coordinate coord_basestation_7 = new Coordinate(-103.21845, 47.795237);
		// Point geom_basestation_7 = fac.createPoint(coord_basestation_7);
		// geography.move(basestation_collection.get(7), geom_basestation_7);
		//
		// Coordinate coord_basestation_8 = new Coordinate(-103.246529, 47.804211);
		// Point geom_basestation_8 = fac.createPoint(coord_basestation_8);
		// geography.move(basestation_collection.get(8), geom_basestation_8);
		//
		// Coordinate coord_basestation_9 = new Coordinate(-103.262916, 47.79904);
		// Point geom_basestation_9 = fac.createPoint(coord_basestation_9);
		// geography.move(basestation_collection.get(9), geom_basestation_9);
		//

		//* [Begin] Seems like reading another base station configuration, not needed.
		List<Object> base_stations = new ArrayList<Object>();

		for (int i = 0; i < basestationCount; i++) {

			for (Object obj : geography.getAllObjects()) { // grid.getObjects()
				if (obj instanceof Base_Station) {
					if (((Base_Station) obj).return_bs_id() == i) {
						Geometry tmp = geography.getGeometry(obj);
						// -76.2597052, 43.0802922

						// short basestation_x = (short) (distance2Coordinate(geography, -76.2597052,
						// 43.0802922, -76.2597052, tmp.getCoordinate().y) / (18));
						// short basestation_y = (short) (distance2Coordinate(geography, -76.2597052,
						// 43.0802922, tmp.getCoordinate().x, 43.0802922) / (18));
						double basestation_x = distance2Coordinate(geography, 48.0802922, -103.2597052, 48.0802922,
								tmp.getCoordinate().x);
						double basestation_y = distance2Coordinate(geography, 48.0802922, -103.2597052, tmp.getCoordinate().y,
								-103.2597052);

						String TMP_fileName_BS_coord = temp_destination + File.separator + "report" + File.separator + "batch_"
								+ Integer.toString(numberofruntime) + File.separator + "BS_coord" + ".csv";

						// String TMP_fileName_BS_coord =temp_destination + File.separator + "report" +
						// File.separator + "batch_" + Integer.toString(numberofruntime) +
						// File.separator + "BS_coord" + ".csv";
						try {
							PrintWriter TMP_writer_BS_coord = new PrintWriter(
									new FileOutputStream(new File(TMP_fileName_BS_coord), true));
							// PrintWriter writer_BS_coord = new PrintWriter(new
							// File(TMP_fileName_BS_coord));

							StringBuilder TMP_BS_coord_string = new StringBuilder();

							TMP_BS_coord_string.append(basestation_x);
							TMP_BS_coord_string.append(',');
							TMP_BS_coord_string.append(basestation_y);
							TMP_BS_coord_string.append("\n");
							TMP_writer_BS_coord.write(TMP_BS_coord_string.toString());
							TMP_writer_BS_coord.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			}

			// for (Object obj : base_stations)
			// {
			//
			// Geometry tmp = geography.getGeometry(obj);
			// //-76.2597052, 43.0802922
			//
			//
			//// short basestation_x = (short) (distance2Coordinate(geography, -76.2597052,
			// 43.0802922, -76.2597052, tmp.getCoordinate().y) / (18));
			//// short basestation_y = (short) (distance2Coordinate(geography, -76.2597052,
			// 43.0802922, tmp.getCoordinate().x, 43.0802922) / (18));
			// double basestation_x = distance2Coordinate(geography, 43.0802922,
			// -76.2597052, 43.0802922, tmp.getCoordinate().x);
			// double basestation_y = distance2Coordinate(geography, 43.0802922,
			// -76.2597052, tmp.getCoordinate().y, -76.2597052);
			//
			//
			// String TMP_fileName_BS_coord = temp_destination + File.separator + "report" +
			// File.separator + "batch_" + Integer.toString(numberofruntime) +
			// File.separator + "BS_coord" + ".csv";
			//
			//// String TMP_fileName_BS_coord =temp_destination + File.separator + "report"
			// + File.separator + "batch_" + Integer.toString(numberofruntime) +
			// File.separator + "BS_coord" + ".csv";
			// try {
			// PrintWriter TMP_writer_BS_coord = new PrintWriter(new FileOutputStream(new
			// File(TMP_fileName_BS_coord),true));
			// // PrintWriter writer_BS_coord = new PrintWriter(new
			// File(TMP_fileName_BS_coord));
			//
			// StringBuilder TMP_BS_coord_string = new StringBuilder();
			//
			// TMP_BS_coord_string.append(basestation_x);
			// TMP_BS_coord_string.append(',');
			// TMP_BS_coord_string.append(basestation_y);
			// TMP_BS_coord_string.append("\n");
			// TMP_writer_BS_coord.write(TMP_BS_coord_string.toString());
			// TMP_writer_BS_coord.close();
			// }catch (IOException e) {
			// e.printStackTrace();
			// }
			// }

		}
		//* [End] Seems like reading another base station configuration, not needed.

		NS3CommunicatiorHelper ns3CommunicatiorHelper
			= new NS3CommunicatiorHelper();

		BaseStationController baseStationController = new BaseStationController();
		context.add(baseStationController);

		//FIXME: Argument list is too long
		UAVmanagement my_uav_management = new UAVmanagement(geography, fac, uavs_list, airport_list, terrain_start_location,
				terrain_end_location, numberofruntime, uav_generation_rate, if_straight, if_routing, simulation_time,
				current_random, base_station_channels, baseStationController);
		context.add(my_uav_management);

		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters para1 = ScheduleParameters.createRepeating(1, 1);
		schedule.schedule(para1 , baseStationController, "update");

		UserEquipmentController userEquipmentController = new UserEquipmentController();
		context.add(userEquipmentController);
		ScheduleParameters para2 = ScheduleParameters.createRepeating(1, 1);
		schedule.schedule(para2 , userEquipmentController, "update");

		// ns3CommunicatiorHelper.sendCreationReq(
			// Integer.toString(temp_uav.return_Id()),
			// Double.toString(48.01468),
			// Double.toString(-103.01318),
			// 0);
		// userEquipmentController.getContainer().add(temp_uav.getUe());

		/*
		 * Routing Option 
		 * Routing without SINR
		 */
		if (!AppConf.getInstance().getBoolean("routingWithSINRPrediction")) {
			for (UAV uav : uavs_list) {
				List<Double> startPair = uav.return_start_coordinate_pair();
				double startLng = startPair.get(0);
				double startLat = startPair.get(1);
				ns3CommunicatiorHelper.sendCreationReq(
					Integer.toString(uav.return_Id()),
					Double.toString(startLat),
					Double.toString(startLng),
					0);
				userEquipmentController.getContainer().add(uav.getUe());
			}
		}

		if (RunEnvironment.getInstance().isBatch()) {
			// RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			System.out.println("sim end time" + simulation_time);
			RunEnvironment.getInstance().endAt(simulation_time);
		}

		return context;
	}

	private List<String[]> readUavStartEndLocations(String csvPath) {
		try {
			CSVReader reader = new CSVReader(new FileReader(csvPath));
			return reader.readAll();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	} 

	private UAV uavFactory(Geography<Object> geography, int id,
		double startLng, double startLat, double endLng, double endLat) {
		UAV uav = new UAV(geography,400, 18, false,
			true, 0, 0, 0,
			true, "probability", null,
			1, 1, true, id, 8);
		ArrayList<Double> start_coordinate_pair = new ArrayList<Double>();
		ArrayList<Double> end_coordinate_pair = new ArrayList<Double>();
		uav.set_internal_time(0);
		start_coordinate_pair.add(startLng);
		start_coordinate_pair.add(startLat);
		end_coordinate_pair.add(endLng);
		end_coordinate_pair.add(endLat);
		uav.set_start_coordinate_pair(start_coordinate_pair);
		uav.set_end_coordinate_pair(end_coordinate_pair);
		ArrayList<Double> connection_coord_pair = new ArrayList<Double>();
		connection_coord_pair.add(start_coordinate_pair.get(0));
		connection_coord_pair.add(end_coordinate_pair.get(1));
		ArrayList<ArrayList<Double>> connection_coordinate_pair
			= new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> connection_index_pair
			= new ArrayList<ArrayList<Double>>();
		connection_coordinate_pair.add(connection_coord_pair);
		uav.set_connection_coordinate_pair(connection_coordinate_pair);
		uav.set_connection_index_pair(connection_index_pair);
		return uav;
	}

	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';

	public static List<String> parseLine(String cvsLine) {
		return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators) {
		return parseLine(cvsLine, separators, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

		List<String> result = new ArrayList<>();

		// if empty, return!
		if (cvsLine == null && cvsLine.isEmpty()) {
			return result;
		}

		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;

		char[] chars = cvsLine.toCharArray();

		for (char ch : chars) {

			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				} else {

					// Fixed : allow "" in custom quote enclosed
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					} else {
						curVal.append(ch);
					}

				}
			} else {
				if (ch == customQuote) {

					inQuotes = true;

					// Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && customQuote == '\"') {
						curVal.append('"');
					}

					// double quotes in column will hit this!
					if (startCollectChar) {
						curVal.append('"');
					}

				} else if (ch == separators) {

					result.add(curVal.toString());

					curVal = new StringBuffer();
					startCollectChar = false;

				} else if (ch == '\r') {
					// ignore LF characters
					continue;
				} else if (ch == '\n') {
					// the end, break!
					break;
				} else {
					curVal.append(ch);
				}
			}

		}

		result.add(curVal.toString());

		return result;
	}

	public ArrayList<UAV> read_manned(Geography<Object> geography, boolean if_straight, int current_batch,
			String mav_trajectory_type, int mav_generation_rate, long base_station_channels) throws FileNotFoundException {
		ArrayList<UAV> uavs_list = new ArrayList<UAV>();

		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);

		System.out.print("************** Double Check MAV ***************");
		System.out.print(mav_trajectory_type);
		System.out.print(mav_generation_rate);
		System.out.print("mav_" + mav_trajectory_type + "_" + mav_generation_rate + ".csv");

		// Scanner scanner = new Scanner(new File(temp_pre_fix + File.separator +
		// "configuration" + File.separator + "operation.csv"));
		Scanner scanner = new Scanner(new File(temp_pre_fix + File.separator + "configuration" + File.separator + "mav_"
				+ mav_trajectory_type + "_" + mav_generation_rate + ".csv"));
		scanner.useDelimiter(",");
		// remove the first line
		if (scanner.hasNext()) {
			scanner.nextLine();
		}
		// get all operations
		int count = 0;

		double speed = 0;
		double x1 = 0, x2 = 0;
		double y1 = 0, y2 = 0;
		while (scanner.hasNext()) {
			List<String> line = parseLine(scanner.nextLine());
			// System.out.println("Operation [id= " + line.get(1) +
			// ", Latitude= " + line.get(2) + " , Longitude=" + line.get(3) + " , level=" +
			// line.get(5) +"]");
			int level = Integer.valueOf(line.get(5));
			int id = Integer.valueOf(line.get(1));
			UAV curr_uav = null;
			ArrayList<ArrayList<Double>> connection_coordinate_pair = new ArrayList<ArrayList<Double>>();
			ArrayList<Double> start_coordinate_pair = new ArrayList<Double>();
			ArrayList<Double> end_coordinate_pair = new ArrayList<Double>();
			ArrayList<Integer> level_inf = new ArrayList<Integer>();
			boolean flag = false;
			if (uavs_list.isEmpty()) {
				start_coordinate_pair.add(Double.valueOf(line.get(3)));
				start_coordinate_pair.add(Double.valueOf(line.get(2)));
				UAV temp_uav = new UAV(geography, 400, speed, if_straight, false, Integer.valueOf(line.get(0)), 0, 0, true,
						"probability", null, current_batch, level, false, id, base_station_channels);
				temp_uav.set_internal_time(Integer.valueOf(line.get(0)));
				temp_uav.set_start_coordinate_pair(start_coordinate_pair);
				uavs_list.add(temp_uav);
			} else {
				for (UAV temp_uav : uavs_list) {
					if (temp_uav.return_Id() == id) {
						curr_uav = temp_uav;
						flag = true;
						break;
					}
				}
				if (flag) {
					if (Double.valueOf(line.get(4)) == -1) {
						System.out.println("Last one");
						end_coordinate_pair.add(Double.valueOf(line.get(3)));
						end_coordinate_pair.add(Double.valueOf(line.get(2)));
						curr_uav.set_end_coordinate_pair(end_coordinate_pair);
						level_inf = curr_uav.return_level_inf();
						level_inf.add(level);
						curr_uav.set_level_inf(level_inf);
					} else {
						ArrayList<Double> connect_coordinate_pair = new ArrayList<Double>();
						connect_coordinate_pair.add(Double.valueOf(line.get(3)));
						connect_coordinate_pair.add(Double.valueOf(line.get(2)));
						if (curr_uav.return_connection_coordinate_pair() != null)
							connection_coordinate_pair = curr_uav.return_connection_coordinate_pair();
						// System.out.println(connection_coordinate_pair);
						connection_coordinate_pair.add(connect_coordinate_pair);
						if (curr_uav.return_level_inf() != null)
							level_inf = curr_uav.return_level_inf();
						// System.out.println(connection_coordinate_pair);
						level_inf.add(level);
						curr_uav.set_connection_coordinate_pair(connection_coordinate_pair);
						curr_uav.set_level_inf(level_inf);
						if (curr_uav.return_speed() == 0) {
							double start_x = curr_uav.return_start_coordinate_pair().get(0);
							double start_y = curr_uav.return_start_coordinate_pair().get(1);
							double next_x = Double.valueOf(line.get(3));
							double next_y = Double.valueOf(line.get(2));
							double dis = distance2Coordinate(geography, start_y, start_x, next_y, next_x);
							double new_speed = dis;
							System.out.println(next_y);
							System.out.println(next_x);
							curr_uav.set_speed(new_speed);
						}
					}

				} else {
					start_coordinate_pair.add(Double.valueOf(line.get(3)));
					start_coordinate_pair.add(Double.valueOf(line.get(2)));
					UAV temp_uav = new UAV(geography, 400, speed, if_straight, false, Integer.valueOf(line.get(0)), 0, 0, true,
							"probability", null, current_batch, level, false, id, base_station_channels);
					temp_uav.set_internal_time(Integer.valueOf(line.get(0)));
					temp_uav.set_start_coordinate_pair(start_coordinate_pair);
					uavs_list.add(temp_uav);
				}
			}

		}
		scanner.close();
		return uavs_list;
	}

	// if (count == 0) {
	//
	// x1 = Double.valueOf(line.get(3));
	// y1 = Double.valueOf(line.get(2));
	// }
	// else if(Double.valueOf(line.get(4)) == -1){
	// System.out.println("Last one");
	// end_coordinate_pair.add(Double.valueOf(line.get(3)));
	// end_coordinate_pair.add(Double.valueOf(line.get(2)));
	// }
	// else {

	// x2 = Double.valueOf(line.get(3));
	// y2 = Double.valueOf(line.get(2));
	// }
	// if(count == 1) {
	// double dis = distance2Coordinate(geography, y1, x1, y2, x2);
	// speed = dis/9;
	// System.out.println(speed);
	// }
	// count++;
	// }

	//
	//
	// temp_uav.set_connection_index_pair(connection_index_pair);
	//

	private static double distance2Coordinate(Geography g, double lat_1, double lon_1, double lat_2, double lon_2) {
		GeodeticCalculator calculator = new GeodeticCalculator(g.getCRS());
		calculator.setStartingGeographicPoint(lon_1, lat_1);
		calculator.setDestinationGeographicPoint(lon_2, lat_2);
		return calculator.getOrthodromicDistance();
	}

	public ArrayList<UAV> read_operation(Geography<Object> geography, boolean if_straight, int current_batch,
			long base_station_channels) throws FileNotFoundException {
		ArrayList<UAV> uavs_list = new ArrayList<UAV>();

		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);

		Scanner scanner = new Scanner(
				new File(temp_pre_fix + File.separator + "configuration" + File.separator + "operation.csv"));
		scanner.useDelimiter(",");
		// remove the first line
		if (scanner.hasNext()) {
			scanner.nextLine();
		}
		// get all operations
		while (scanner.hasNext()) {
			List<String> line = parseLine(scanner.nextLine());
			System.out.println("Operation [id= " + line.get(1) +
					", Latitude= " + line.get(2) + " , Longitude=" + line.get(3) + " , level=" + line.get(5) + "]");
			int level = Integer.valueOf(line.get(5));
			int id = Integer.valueOf(line.get(1));
			UAV curr_uav = null;
			ArrayList<ArrayList<Double>> connection_coordinate_pair = new ArrayList<ArrayList<Double>>();
			ArrayList<Double> start_coordinate_pair = new ArrayList<Double>();
			ArrayList<Double> end_coordinate_pair = new ArrayList<Double>();
			ArrayList<Integer> level_inf = new ArrayList<Integer>();
			boolean flag = false;
			if (uavs_list.isEmpty()) {
				// System.out.println("Empty add");
				start_coordinate_pair.add(Double.valueOf(line.get(3)));
				start_coordinate_pair.add(Double.valueOf(line.get(2)));
				UAV temp_uav = new UAV(geography, 400, 18, if_straight, false, Integer.valueOf(line.get(0)), 0, 0, true,
						"probability", null, current_batch, level, true, id, base_station_channels);
				temp_uav.set_internal_time(Integer.valueOf(line.get(0)));
				temp_uav.set_start_coordinate_pair(start_coordinate_pair);
				uavs_list.add(temp_uav);
				// System.out.println("IDDD"+temp_uav.return_Id());
			} else {
				for (UAV temp_uav : uavs_list) {
					if (temp_uav.return_Id() == id) {
						// System.out.println("Find");
						curr_uav = temp_uav;
						flag = true;
						break;
					}
				}
				if (flag) {
					if (Double.valueOf(line.get(4)) == -1) {
						System.out.println("Last one");
						end_coordinate_pair.add(Double.valueOf(line.get(3)));
						end_coordinate_pair.add(Double.valueOf(line.get(2)));
						curr_uav.set_end_coordinate_pair(end_coordinate_pair);
						level_inf = curr_uav.return_level_inf();
						level_inf.add(level);
						curr_uav.set_level_inf(level_inf);
					} else {
						// System.out.println("Appending");
						ArrayList<Double> connect_coordinate_pair = new ArrayList<Double>();
						connect_coordinate_pair.add(Double.valueOf(line.get(3)));
						connect_coordinate_pair.add(Double.valueOf(line.get(2)));
						if (curr_uav.return_connection_coordinate_pair() != null)
							connection_coordinate_pair = curr_uav.return_connection_coordinate_pair();
						// System.out.println(connection_coordinate_pair);
						connection_coordinate_pair.add(connect_coordinate_pair);
						if (curr_uav.return_level_inf() != null)
							level_inf = curr_uav.return_level_inf();
						// System.out.println(connection_coordinate_pair);
						level_inf.add(level);
						curr_uav.set_connection_coordinate_pair(connection_coordinate_pair);
						curr_uav.set_level_inf(level_inf);
						// if(curr_uav.return_speed() == 0) {
						// double start_x = curr_uav.return_start_coordinate_pair().get(0);
						// double start_y = curr_uav.return_start_coordinate_pair().get(1);
						// double next_x = Double.valueOf(line.get(3));
						// double next_y = Double.valueOf(line.get(2));
						// double dis = distance2Coordinate(geography, start_y, start_x, next_y,
						// next_x);
						// double new_speed = dis;
						// System.out.println(next_y);
						// System.out.println(next_x);
						// curr_uav.set_speed(new_speed);
						// }
					}

				} else {
					start_coordinate_pair.add(Double.valueOf(line.get(3)));
					start_coordinate_pair.add(Double.valueOf(line.get(2)));
					UAV temp_uav = new UAV(geography, 400, 18, if_straight, false, Integer.valueOf(line.get(0)), 0, 0, true,
							"probability", null, current_batch, level, true, id, base_station_channels);
					temp_uav.set_internal_time(Integer.valueOf(line.get(0)));
					temp_uav.set_start_coordinate_pair(start_coordinate_pair);
					uavs_list.add(temp_uav);
				}
			}

		}
		scanner.close();
		return uavs_list;
	}

	private void create_folder(String folder_name) {
		File theDir = new File(folder_name);
		System.out.println(folder_name);
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + theDir.getName());
			boolean result = false;

			try {
				theDir.mkdirs();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				System.out.println("DIR created");
			}
		}
	}

	void record_random_seed(int current_batch, int random_seed) {

		String destination = System.getProperty("user.dir");
		int last_indes = destination.lastIndexOf(File.separator);
		String temp_pre_fix = destination.substring(0, last_indes);

		String uav_count_report = temp_pre_fix + File.separator + "report" + File.separator + "batch_"
				+ Integer.toString(current_batch) + File.separator + "random_seed_report" + ".csv";
		StringBuilder uav_count_report_string = new StringBuilder();

		try {
			PrintWriter writer_random_seed_report = new PrintWriter(new FileOutputStream(new File(uav_count_report), true));
			uav_count_report_string.append(current_batch);
			uav_count_report_string.append(',');
			uav_count_report_string.append(random_seed);
			uav_count_report_string.append('\n');
			writer_random_seed_report.write(uav_count_report_string.toString());

			writer_random_seed_report.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
