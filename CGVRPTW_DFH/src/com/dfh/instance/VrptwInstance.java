package com.dfh.instance;

import com.dfh.parameters.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 * Instance class holds all the information about the problem, customers, depots, vehicles.
 * It offers functions to grab all the data from a file print it formated and all the function
 * needed for the initial solution.
 */
public class VrptwInstance {
	private int vehiclesNr;
	private int customersNr;
	private ArrayList<Customer> customers; 		// vector of customers;
	private Depot depot;       	// depot;
	private double vehiclesCapacity;
	private double[][] distances;
	public Parameters parameters;
	private HashMap<Integer, Node> Nodes;
	
	public VrptwInstance() {
		customers 	= new ArrayList<>();
		Nodes       = new HashMap<Integer, Node>();
	}
	
	public VrptwInstance(Parameters theParm) {
		customers 	= new ArrayList<>();
		Nodes       = new HashMap<Integer, Node>();
		this.parameters = theParm;
	}
	
	
	public HashMap<Integer, Node> getNodes(){
		return this.Nodes;
	}
	
	public void setNodes(HashMap<Integer, Node> nodes){
		this.Nodes = nodes;
	}
	
	/**
	 * 
	 * @return distances as a double matrix
	 */
	
	public double[][] getDistances()
	{
		return distances;
	}
	
	/**
	 * Returns the time necessary to travel from node 1 to node 2
	 * @param node1
	 * @param node2
	 * @return
	 */
	public double getTravelTime(int v1, int v2) {
		return this.distances[v1][v2];
	}
	
	/**
	 * Read from file the problem data: D and Q, customers data
	 * and depots data. After the variables are populated
	 * calculates the distances, assign customers to depot
	 * and calculates angles
	 * @param filename
	 */
	public void readInstanceFromFile(String filename) {
		try {		
			Scanner in = new Scanner(new FileReader(filename));
			
			// skip unusefull lines
			in.nextLine(); // skip filename
			in.nextLine(); // skip empty line
			in.nextLine(); // skip vehicle line
			in.nextLine();
			vehiclesNr	= in.nextInt();
			
			// read Q
			vehiclesCapacity = in.nextInt();
			
			// skip unusefull lines
			in.nextLine();
			in.nextLine();
			in.nextLine();
			in.nextLine();
			in.nextLine();
			
			// read depots data
			Node nodeDepot = new Node();
			nodeDepot.setNumber(in.nextInt());
			nodeDepot.setXCoordinate(in.nextDouble());
			nodeDepot.setYCoordinate(in.nextDouble());
			nodeDepot.setDemand(in.nextDouble());
			nodeDepot.setStartTw(in.nextInt());
			nodeDepot.setEndTw(in.nextInt());
			nodeDepot.setServiceDuration(in.nextDouble());
			Nodes.put(nodeDepot.getNumber(), nodeDepot);
			
			depot = new Depot(nodeDepot);
			depot.setCapacity(Double.MAX_VALUE);
			
			
			// read customers data
			customersNr = 0;
			while(in.hasNextInt())
			{			
				Node node = new Node();
				node.setNumber(in.nextInt());
				node.setXCoordinate(in.nextDouble());
				node.setYCoordinate(in.nextDouble());
				node.setDemand(in.nextDouble());
				node.setStartTw(in.nextInt());
				node.setEndTw(in.nextInt());
				node.setServiceDuration(in.nextDouble());
				Customer customer = new Customer(node);
				Nodes.put(node.getNumber(), node);
				// add customer to customers list
				customers.add(customer);
				customersNr++;
			}// end for customers
			in.close();
			calculateDistances();
			calculateAngles();
			sortCustomersByAngles();
			
			
		} catch (FileNotFoundException e) {
			// File not found
			System.out.println("File not found!");
			System.exit(-1);
		}
	}
	
	/**
	 * Order for each depot the list containing the assigned customers based on angles
	 */
	public void sortCustomersByAngles() {
		QuickSort.sort(customers);
	}
	/**
	 * Get the customer number found at the passed position
	 * @param index
	 * @return
	 */
	public int getNumberOfCustomerAt(int index) {
		return customers.get(index).getNumber();
	}
	

	
	public String routesToString(Route[][] routes) {
		StringBuffer print = new StringBuffer();
		print.append("------------Routes-----------\n");
		for(int i =0; i < routes.length; ++i) {
			
			for (int j = 0; j < routes[i].length; ++j) {
				print.append((routes[i][j].getCustomersLength()) + " " + routes[i][j].getDepotNr());
				for(int k = 0; k < routes[i][j].getCustomersLength(); ++k) {
					print.append(" " + routes[i][j].getCustomerNr(k));
				}// end for customers
				print.append("\n");
			}// end for vehicles
		}// end for depots
		print.append("------------Routes-----------\n");
		return print.toString();
	}// end method printRoutes
	
	

	
	
	/**
	 * Print for the list of customers their number on a row separated by space
	 * Used for debugging
	 */
	public String customersNumberToString() {
		StringBuffer print = new StringBuffer();
		print.append("Customers:");
		for (int i = 0; i < customers.size(); ++i) {
			print.append(" " + customers.get(i).getNumber());
		}
		print.append("\n");
		return print.toString();
	}
	
	/**
	 * Calculate the symmetric euclidean matrix of costs
	 */
	public void calculateDistances() {
		//System.out.println("depotsNr = "+depotsNr+" customersNr = "+customersNr);
		distances = new double[1+customersNr][1+customersNr];
		for (int i = 0; i  < 1 + customersNr - 1; ++i)
			for (int j = i + 1; j < 1 + customersNr ; ++j) {
				//case both customers
				if(i  >= 1 && j >= 1){
					int c1 = i - 1;
					int c2 = j - 1;
					distances[i][j] = Math.sqrt(Math.pow(customers.get(c1).getXCoordinate() - customers.get(c2).getXCoordinate(), 2)
							+ Math.pow(customers.get(c1).getYCoordinate() - customers.get(c2).getYCoordinate(), 2));
					distances[i][j] = Math.floor(distances[i][j] * 100) / 100; //精确到小数点后两位
					distances[j][i] = distances[i][j];

				// case depot and customer 
				}else if(i  < 1 && j >= 1){
					int c = j - 1;
					distances[i][j] = Math.sqrt(Math.pow(customers.get(c).getXCoordinate() - depot.getXCoordinate(), 2)
							+ Math.pow(customers.get(c).getYCoordinate() - depot.getYCoordinate(), 2));
					distances[i][j] = Math.floor(distances[i][j] * 100) / 100;
					distances[j][i] = distances[i][j];
				}
			}
	}
	
	/**
	 * Calculates the angles between customers and depots
	 */
	public void calculateAngles() {
		double angles;
		for (int i = 0; i < customersNr; ++i) {
			angles = Math.atan2(customers.get(i).getYCoordinate() - depot.getYCoordinate(), customers.get(i).getXCoordinate() - depot.getXCoordinate());
			customers.get(i).setAnglesToDepot(angles);
		}
	}
	
	/**
	 * @return distances as a string
	 */
	public String distancesToString() {
		StringBuffer print = new StringBuffer();
		for	(int i = 0; i < customersNr + 1; ++i) {
			for	(int j = 0; j < customersNr + 1; ++j)
				print.append(distances[i][j] + " ");
			print.append("\n");
		}
		return print.toString();
	}
	
	
	/**
	 * @return all the customers as string
	 */
	public String customersToString() {
		StringBuffer print = new StringBuffer();
		for (int i = 0; i < customersNr; ++i) {
			print.append(customers.get(i).toString());
		}
		return print.toString();
	}
	
	/**
	 * 
	 * @param index
	 * @return the formated string with the angles of assigned customers to depot
	 */
	public String customersAnglesToString() {
		StringBuffer print = new StringBuffer();
		print.append("\nDepot---AssignedCustomers-------------\nCustomerNumber\t\tCustomerAngle\n");
		for(Customer customer : this.customers) {
			print.append("\t" + customer.getNumber() + "\t\t\t\t" + customer.getAnglesToDepot() + "\n");
		}
		print.append("---------------------------------------------------\n");
		return print.toString();
	}
	
	/**
	 * @param costs the costs to set
	 */
	public void setCosts(double[][] costs) {
		this.distances = costs;
	}



	/**
	 * @return the vehiclesNr
	 */
	public int getVehiclesNr() {
		return vehiclesNr;
	}


	/**
	 * @param vehiclesNr the vehiclesNr to set
	 */
	public void setVehiclesNr(int vehiclesNr) {
		this.vehiclesNr = vehiclesNr;
	}


	/**
	 * @return the customersNr
	 */
	public int getCustomersNr() {
		return customersNr;
	}


	/**
	 * @param customersNr the customersNr to set
	 */
	public void setCustomersNr(int customersNr) {
		this.customersNr = customersNr;
	}

	public Depot getDepot() {
		return depot;
	}


	public double getVehiclesCapacity() {
		return vehiclesCapacity;
	}

	public Customer getCustomer(int gene) {
		// TODO Auto-generated method stub
		return customers.get(gene);
	}
	
	
	public ArrayList<Customer> getCustomers() {
		// TODO Auto-generated method stub
		return customers;
	}
	
	/**
	 * @return the parameters
	 */
	public Parameters getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
	
}
