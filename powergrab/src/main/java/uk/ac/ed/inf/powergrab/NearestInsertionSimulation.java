package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

/*
 * A basic simulation that uses the nearest insertion algorithm to decide its route order.
 */
public class NearestInsertionSimulation extends PowerGrabSimulation {
	
	private int[] basicOrder;
	private int[] twoOptOptimisedOrder;
	private int[] threeOptOptimisedOrder;
	
	public NearestInsertionSimulation(Position startingPosition, List<ChargingStation> testStations) {
		super(startingPosition, testStations);
	}
	
	/*
	 * Chooses the route order using the nearest insertion algorithm.
	 */
	@Override
	public List<int[]> chooseStationOrders() {
		
		List<int[]> stationOrders = new ArrayList<int[]>(3);
		double[][] distanceMatrix = super.getDistanceMatrix();
		
		basicOrder = NearestInsertionSimulation.calculateNearestInsertionOrder(
				distanceMatrix);
		stationOrders.add(basicOrder);
		
		twoOptOptimisedOrder = StatefulDrone.twoOptOptimise(basicOrder, distanceMatrix);
		stationOrders.add(twoOptOptimisedOrder);
		
		threeOptOptimisedOrder = StatefulDrone.threeOptOptimise(basicOrder, distanceMatrix);
		stationOrders.add(threeOptOptimisedOrder);
		
		return stationOrders;
		
	}
	
	/*
	 * The report phase of the basic simulation.
	 */
	@Override
	public void report() {
		
		System.out.println("Estimated nearest insertion distance before optimisation: "
				+ StatefulDrone.calculateTotalRouteDistance(this.basicOrder, super.getDistanceMatrix()));
		System.out.println("Estimated distance after 2-opt optimisation: "
				+ StatefulDrone.calculateTotalRouteDistance(this.twoOptOptimisedOrder, super.getDistanceMatrix()));
		System.out.println("Estimated distance after 3-opt optimisation: "
				+ StatefulDrone.calculateTotalRouteDistance(this.threeOptOptimisedOrder, super.getDistanceMatrix()));
	}
	
	/*
	 * Calculates the nearest insertion route order.
	 */
	private static int[] calculateNearestInsertionOrder(double[][] distanceMatrix) {
		
		int n = distanceMatrix[0].length - 1;
		int[] stationOrder = new int[n];
		boolean[] visitedStations = new boolean[n];
		int numberOfStationsVisited = 0;
		
		int initialMinStation = 0;
		double initialMinDistance = distanceMatrix[0][0];
		
		for (int stationNumber = 0; stationNumber < n; stationNumber++) {
			double distance = distanceMatrix[0][stationNumber + 1];
			if (distance < initialMinDistance) {
				initialMinStation = stationNumber;
				initialMinDistance = distance;
			}
		}
		
		stationOrder[0] = initialMinStation;
		visitedStations[initialMinStation] = true;
		numberOfStationsVisited++;
		
		for (int i = 1; i < n; i++) {
			
			int closestStation = -1;//??
			double minDistance = Double.POSITIVE_INFINITY;
			
			for (int stationNumber = 0; stationNumber < n; stationNumber++) {
				if (visitedStations[stationNumber]) {
					continue;
				}
				double initDistance = distanceMatrix[0][stationNumber + 1];
				if (initDistance < minDistance) {
					closestStation = stationNumber;
					minDistance = initDistance;
				}
				for (int indexToCheck = 0; indexToCheck < numberOfStationsVisited; indexToCheck++) {
					int stationToCheck = stationOrder[indexToCheck];
					double distance = distanceMatrix[stationToCheck + 1][stationNumber + 1];
					if (distance < minDistance) {
						closestStation = stationNumber;
						minDistance = distance;
					}
				}
			}
			
			int bestInsertionPosition = PowerGrabSimulation.findBestInsertionPosition(stationOrder, distanceMatrix,
					closestStation, numberOfStationsVisited);
			
			stationOrder = PowerGrabSimulation.insertStationIntoRouteOrder(closestStation, bestInsertionPosition, stationOrder);
			visitedStations[closestStation] = true;
			numberOfStationsVisited++;
		}
		
		return stationOrder;
	}

}
