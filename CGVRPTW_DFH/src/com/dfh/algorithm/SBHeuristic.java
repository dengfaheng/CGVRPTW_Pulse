package com.dfh.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dfh.instance.Customer;
import com.dfh.instance.Route;
import com.dfh.instance.VrptwInstance;

public class SBHeuristic {
	private List<Route> searchRoutes;	
	public VrptwInstance vrptwInstance;

	public SBHeuristic(VrptwInstance vrptwInstance){
		this.vrptwInstance = vrptwInstance;
		searchRoutes = new ArrayList<>();
		buildRoutesAngles();
	}
	
	public Route simpleHeuristic(Map<Integer,Double> rmpDualValues) {
		double reducedCost = Integer.MAX_VALUE;
		Route routeNeedToRemove = null;
		Route bestRoute = null;
		//System.out.println("searchRoutes size = "+searchRoutes.size());
		
		for(Route r : searchRoutes) {
			double rdCost = calculateReducedCost(r, rmpDualValues);
			if(rdCost < reducedCost) {
				reducedCost = rdCost;
				bestRoute = r;
				routeNeedToRemove = r;
			}
		}
		searchRoutes.remove(routeNeedToRemove);
		return bestRoute;
		//System.out.println("searchRoutes = "+searchRoutes.size());
	}
	
	public void buildRoutesAngles() {

		//simple heuristic for building routes
		for(int i = 0; i < vrptwInstance.getCustomersNr(); ++i) {
			int lastSize = 0;
			for(int j = 2; j <= vrptwInstance.getCustomersNr(); ++j) {				
				if(j == 2) {
					Route oneRoute = buildOneLegalRoute(i, j, vrptwInstance.getCustomers());
					lastSize = oneRoute.getCustomersLength();
					searchRoutes.add(oneRoute);
				}else {
					Route oneRoute = buildOneLegalRoute(i, j, vrptwInstance.getCustomers());
					int thisSize = oneRoute.getCustomersLength();
					
					if(lastSize != thisSize) {
						searchRoutes.add(oneRoute);
						lastSize = thisSize;
					}
					
				}
			}
		}
		
	}
	
	
	public double calculateReducedCost(Route route, Map<Integer,Double> price) {
		if(route.isEmpty()) {
			System.out.println(" route is empty, what the hell are you doing ??? ");
			return 0;
		}else {
			double reducedCost = 0;
			double cBaj = 0;
			for(int cust : price.keySet()) {
				cBaj += price.get(cust) * route.a(cust);
			}
			reducedCost = route.getCost().getTravelTime() - cBaj;
			return reducedCost;
		}
	}
	
	/**
	 * Build the a route satisfy time windows constrain
	 */
	public Route buildOneLegalRoute(int startCust, int maxRouteSize, ArrayList<Customer> candidateCustomers) {
		Route currentRoute = new Route(); // stores the pointer to the current route
		Customer customerChosenPtr; // stores the pointer to the customer chosen from depots list assigned customers
		int customersNr;
		int startCustomer;
		int customerChosen; // serve to cycle j, j+1, ... assignedcustomersnr, 0, ... j-1

		// cycle the list of depots
			
		currentRoute.setDepot(this.vrptwInstance.getDepot());
		
		customersNr = candidateCustomers.size(); //tutti e "100" customer dell'istanza in ingresso
		if(startCust != -1) { //quindi il primo customer 锟� sempre randomico!!! fatto una sola volta nella prima iterazione (primo deposito) perch锟� le operazioni di get e set vengono fatte solamente qui (1 chiamata per ciascun metodo)
			startCustomer = startCust;
		}else{
			startCustomer = this.vrptwInstance.parameters.getRandom().nextInt(customersNr);
		}
		int chosenCustomersNr = 0;
		// cycle the entire list of customers starting from the randomly chosen one
		for (int j = startCustomer; j < customersNr + startCustomer; ++j) {
			// serve to cycle j, j+1, ... assignedcustomersnr, 0, ... j-1
			customerChosen = j % customersNr; //buffer circolare
			// stores the pointer to the customer chosen from depots list assigned customers
			customerChosenPtr = candidateCustomers.get(customerChosen); //si va a prendere il customer scelto all'indirizzo i-esimo nell'array
			// accept on the route only if satisfy the load and timeWindows
			if (customerChosenPtr.getDemand() + currentRoute.getLoad() <= vrptwInstance.getVehiclesCapacity() )  { 
				if(tryInsertBestTravelEndTW(currentRoute, customerChosenPtr, false)) {
					currentRoute.addLoad(customerChosenPtr.getDemand());
					chosenCustomersNr++;
				}
			}
			// cycle the routes until the maxRouteSize
			if(chosenCustomersNr >= maxRouteSize) {
				break;
			}
		} // end for customer list
		currentRoute.evaluateRoute(vrptwInstance);
		return currentRoute;
	}
	
	
	private boolean tryInsertBestTravelEndTW(Route route, Customer customerChosenPtr, boolean isForce) {
		int position = 0;
		if(route.isEmpty()){
			// add on first position
			position = 0;
		}else {
			// first position
			if(customerChosenPtr.getEndTw() <= route.getCustomer(0).getEndTw()) {
				position = 0;
			}
			
			// at the end
			if(route.getCustomer(route.getCustomersLength() - 1).getEndTw() <= customerChosenPtr.getEndTw()){
				position = route.getCustomersLength();
			}
			
			// try between each customer
			for(int i = 0; i < route.getCustomersLength() - 1; ++i) {
				if(route.getCustomer(i).getEndTw() <= customerChosenPtr.getEndTw() && customerChosenPtr.getEndTw() <= route.getCustomer(i + 1).getEndTw()) {
					position = i + 1;
				}
			}
		}
		
		route.addCustomer(new Customer(customerChosenPtr), position);
		if(isForce) {
			return true;
		}

		// try to insert
		double totalTime = 0;
    	double twViol = 0;
    	Customer customerK;
		
		if(!route.isEmpty()){
	    	// sum distances between each node in the route
			for (int k = 0; k < route.getCustomersLength(); ++k){
				// get the actual customer
				customerK = route.getCustomer(k);
				// add travel time to the route
				if(k == 0){
					totalTime += this.vrptwInstance.getTravelTime(route.getDepotNr(), customerK.getNumber());
				}else{
					totalTime += this.vrptwInstance.getTravelTime(route.getCustomerNr(k -1), customerK.getNumber());
				} // end if else
				
				totalTime = Math.max(customerK.getStartTw(), totalTime);
				// add time window violation if any
				twViol = Math.max(0, totalTime - customerK.getEndTw());
				
				if(twViol > 0) {
					route.removeCustomer(position);
					return false;
				}
				
				// add the service time to the total
				totalTime += customerK.getServiceDuration();				
			} // end for customers
			
			// add the distance to return to depot: from last node to depot
			totalTime += this.vrptwInstance.getTravelTime(route.getLastCustomerNr(), route.getDepotNr());
			// add the depot time window violation if any
			twViol = Math.max(0, totalTime - route.getDepot().getEndTw());
			
			if(twViol > 0) {
				route.removeCustomer(position);
				return false;
			}
									
		} // end if route not empty

		return true;
	}


}
