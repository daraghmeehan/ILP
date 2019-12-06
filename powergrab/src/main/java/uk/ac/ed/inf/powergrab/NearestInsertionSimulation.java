package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

public class NearestInsertionSimulation  extends PowerGrabSimulation {
	
	private int[] basicOrder;
	private int[] twoOptOptimisedOrder;
	private int[] threeOptOptimisedOrder;
	
	public NearestInsertionSimulation(Position startingPosition, List<ChargingStation> testStations) {
		super(startingPosition, testStations);
	}
	
	protected List<int[]> chooseStationOrders() {
		
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

	@Override
	public void report() {
		
		System.out.println("Estimated nearest insertion distance before optimisation: "
				+ StatefulDrone.calculateTotalRouteDistance(this.basicOrder, super.getDistanceMatrix()));
		System.out.println("Estimated distance after 2-opt optimisation: "
				+ StatefulDrone.calculateTotalRouteDistance(this.twoOptOptimisedOrder, super.getDistanceMatrix()));
		System.out.println("Estimated distance after 3-opt optimisation: "
				+ StatefulDrone.calculateTotalRouteDistance(this.threeOptOptimisedOrder, super.getDistanceMatrix()));
	}
	
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
			
			// found closestStation
			// inserting closestStation into stationOrder?
			int bestInsertionPosition = 0;
			double smallestDistanceIncrease = distanceMatrix[0][closestStation + 1]
					+ distanceMatrix[closestStation + 1][stationOrder[0] + 1] - distanceMatrix[0][stationOrder[0] + 1];
			
			for (int positionToCheck = 1; positionToCheck <= numberOfStationsVisited; positionToCheck++) {
				if (positionToCheck != numberOfStationsVisited) {
					double distance = distanceMatrix[stationOrder[positionToCheck - 1] + 1][closestStation + 1]
							+ distanceMatrix[closestStation + 1][stationOrder[positionToCheck] + 1]
									- distanceMatrix[stationOrder[positionToCheck - 1] + 1][stationOrder[positionToCheck] + 1];
					if (distance < smallestDistanceIncrease) {
						bestInsertionPosition = positionToCheck;
						smallestDistanceIncrease = distance;
					}
				} else {
					double distance = distanceMatrix[stationOrder[positionToCheck - 1] + 1][closestStation + 1];
					if (distance < smallestDistanceIncrease) {
						bestInsertionPosition = positionToCheck;
						smallestDistanceIncrease = distance;
					}
				}
			}
			
			stationOrder = NearestInsertionSimulation.insertClosestStation(closestStation, bestInsertionPosition, stationOrder);
			visitedStations[closestStation] = true;
			numberOfStationsVisited++;
		}
		
		return stationOrder;
	}

	private static int[] insertClosestStation(int closestStation, int bestInsertionPosition, int[] stationOrder) {
		
		int n = stationOrder.length;
		
		for (int i = n - 1; i > bestInsertionPosition; i--) {
			stationOrder[i] = stationOrder[i - 1];
		}
		
		stationOrder[bestInsertionPosition] = closestStation;
		
		return stationOrder;
	}
	
//	private static int closestStationToCycle()

}
