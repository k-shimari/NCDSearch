package ncdsearch.clustering.strategy;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ncdsearch_clustering.old_strategy.IClusteringStrategy;

public class Longest implements IClusteringStrategy {
	private TIntObjectHashMap<TIntDoubleHashMap> distanceMap;
	private TIntDoubleHashMap minDistanceMap;
	private TIntObjectHashMap<Cluster> clusterMap;
	private int totalVertexNumber;
	private boolean[] removedFlagMap;
	//	private boolean[][] removedEdgeFlagMap;

	public Longest() {
		totalVertexNumber = 0;
	}

	@Override
	public List<Cluster> clustering(List<Component> fragments) {
		distanceMap = new TIntObjectHashMap<>();
		minDistanceMap = new TIntDoubleHashMap();
		clusterMap = new TIntObjectHashMap<>();

		totalVertexNumber = fragments.size();
		removedFlagMap = new boolean[totalVertexNumber];
		Arrays.fill(removedFlagMap, false);
		createInitialClusters(fragments);
		int mapSize = totalVertexNumber;
		System.err.println("initial clusters : " + mapSize);
		int idx = 0;
		while (mapSize > 5) {
			idx++;
			update();
			int count = 0;
			for (boolean flag : removedFlagMap) {
				if (!flag)
					count++;
			}
			mapSize = count;
			//				System.err.println("before : " + beforeMax);
			//				System.err.println("current : " + maxDeltaModularity);
		}

		System.err.println("iterate count : " + idx);
		List<Cluster> finalCluster = new ArrayList<>();

		for (int i = 0; i < totalVertexNumber; i++) {
			if (!removedFlagMap[i]) {
				finalCluster.add(clusterMap.get(i));
			}
		}

		return finalCluster;
	}

	private List<Cluster> createInitialClusters(List<Component> fragments) {
		List<Cluster> clusters = new ArrayList<>();
		int index = 0;
		for (Component fragment : fragments) {
			Cluster cluster = new Cluster(fragment);
			clusterMap.put(index++, cluster);
			clusters.add(cluster);
		}
		calcInitialDistances(clusters);
		return clusters;
	}

	private void calcInitialDistances(List<Cluster> clusters) {
		int arraySize = clusters.size();
		System.err.println("arraySize: " + arraySize);

		for (int i = 0; i < arraySize; i++) {
			Cluster target = clusters.get(i);
			TIntDoubleHashMap map = new TIntDoubleHashMap();
			double min = Double.MAX_VALUE;
			for (int j = 0; j < arraySize; j++) {
				if (i < j) {
					Cluster cluster = clusters.get(j);
					double distance = calcMaxDistance(target, cluster);
					map.put(j, distance);
					if (distance < min)
						min = distance;
					//						System.err.print(distance + ",");
				} else if (i > j) {
					double distance = distanceMap.get(j).get(i);
					map.put(j, distance);
					if (distance < min)
						min = distance;
					//							System.err.print(distance + ",");
				}
				//else {
				//	System.err.print("aaa,");
				//}
			}
			//			System.err.println();
			distanceMap.put(i, map);
			minDistanceMap.put(i, min);
		}
	}

	/**
	 * To be fixed
	 */
	private void setMinDistance() {
		for (int i = 0; i < totalVertexNumber; i++) {
			if (!removedFlagMap[i]) {
				double min = Double.MAX_VALUE;
				TIntDoubleHashMap innerMap = distanceMap.get(i);
				for (int j = 0; j < totalVertexNumber; j++) {
					if (i != j && !removedFlagMap[j]) {
						double distance = innerMap.get(j);
						if (distance < min) {
							min = distance;
						}
					}
				}
				minDistanceMap.put(i, min);
			}
		}
	}

	private void update() {
		//			System.err.println("[UPDATE]");
		/*search minDistance in maxDistanceMap*/
		double minDistance = Double.MAX_VALUE;
		int minI = -1;
		for (int i = 0; i < totalVertexNumber; i++) {
			if (!removedFlagMap[i]) {
				double distance = minDistanceMap.get(i);
				if (distance < minDistance) {
					minDistance = distance;
					minI = i;
				}
			}
		}
		int minJ = -1;
		TIntDoubleHashMap iMap = distanceMap.get(minI);

		for (int j = 0; j < totalVertexNumber; j++) {
			if (!removedFlagMap[j] && j != minI && minDistance == iMap.get(j)) {
				minJ = j;
			}
		}
		TIntDoubleHashMap jMap = distanceMap.get(minJ);
		for (int k = 0; k < totalVertexNumber; k++) {
			if (!removedFlagMap[k] && k != minI && k != minJ) {
				//					System.err.println("maxi = " + maxI + " maxj = " + maxJ +" k = " + k);
				//					double ivalue = iMap.get(k);
				//					double jvalue = jMap.get(k);
				//					System.err.println("iMap.get(k) = " + ivalue + " jMap.get(k) = " + jvalue);
				jMap.put(k, Math.max(iMap.get(k), jMap.get(k)));
			}
		}
		/*remove clusterI and combine I to J as J*/
		distanceMap.put(minJ, jMap);
		clusterMap.get(minJ).combine(clusterMap.get(minI));
		removedFlagMap[minI] = true;
		//			System.err.println("before max map");
		//			for(int i = 0; i < totalEdgeNumber; i++) {
		//				System.err.print(maxDeltaModularityMap.get(i) + " ");
		//			}
		//			System.err.println();
		setMinDistance();
		//			for(int i = 0; i < totalEdgeNumber; i++) {
		//				System.err.print(maxDeltaModularityMap.get(i) + " ");
		//			}
		//			System.err.println();
		for (int i = 0; i < totalVertexNumber; i++) {
			if (!removedFlagMap[i]) {
				double distance = minDistanceMap.get(i);
				if (distance > minDistance) {
					minDistance = distance;
				}
			}
		}
		//	return maxDeltaModularity;
	}

	private double calcMaxDistance(Cluster c1, Cluster c2) {
		return c1.getMaxDistance(c2);
	}

}
