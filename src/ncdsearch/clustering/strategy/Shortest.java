package ncdsearch.clustering.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class Shortest extends Clustering {
	protected TIntObjectHashMap<TIntDoubleHashMap> distanceMap;
	protected TIntDoubleHashMap minDistanceMap;
	protected TIntObjectHashMap<Cluster> clusterMap;

	protected int clusterNum;
	protected int totalVertexNumber;
	protected boolean[] removedFlagMap;

	public Shortest(int topN, List<JsonNode> allNode, String strategy, int clusterNum) {
		super(topN, allNode, strategy);
		this.clusterNum = clusterNum;
	}

	@Override
	public List<List<JsonNode>> clustering() {
		init();
		int mapSize = totalVertexNumber;
		System.err.println("initial clusters : " + mapSize);
		int idx = 0;
		while (mapSize > clusterNum) {
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

		List<List<JsonNode>> nodeList = new ArrayList<>();
		for (int i = 0; i < totalVertexNumber; i++) {
			if (!removedFlagMap[i]) {
				Cluster c = clusterMap.get(i);
				List<JsonNode> list = new ArrayList<>();
				for (Component co : c.getComponents()) {
					list.add(co.getJsonNode());
				}
				nodeList.add(list);

			}
		}
		return nodeList;
	}

	protected void init() {
		distanceMap = new TIntObjectHashMap<>();
		minDistanceMap = new TIntDoubleHashMap();
		clusterMap = new TIntObjectHashMap<>();

		totalVertexNumber = allNode.size();
		//this.clusterNum = allNode.size() / 5 + 1;

		removedFlagMap = new boolean[totalVertexNumber];
		Arrays.fill(removedFlagMap, false);

		List<Component> components = new ArrayList<>();
		for (JsonNode node : allNode) {
			components.add(new Component(node, strategy));
		}
		createInitialClusters(components);
	}



	protected List<Cluster> createInitialClusters(List<Component> nodes) {
		List<Cluster> clusters = new ArrayList<>();
		int index = 0;
		for (Component node : nodes) {
			Cluster cluster = new Cluster(node);
			clusterMap.put(index++, cluster);
			clusters.add(cluster);
		}
		calcInitialDistances(clusters);
		return clusters;
	}

	protected void calcInitialDistances(List<Cluster> clusters) {
		int arraySize = clusters.size();
		for (int i = 0; i < arraySize; i++) {
			Cluster target = clusters.get(i);
			TIntDoubleHashMap map = new TIntDoubleHashMap();
			double min = Double.MAX_VALUE;
			for (int j = 0; j < arraySize; j++) {
				if (i < j) {
					Cluster cluster = clusters.get(j);
					double distance = calcMinDistance(target, cluster);
					map.put(j, distance);
					if (distance < min)
						min = distance;
					//							System.err.print(distance + ",");
				} else if (i > j) {
					double distance = distanceMap.get(j).get(i);
					map.put(j, distance);
					if (distance < min)
						min = distance;
					//							System.err.print(distance + ",");
				}
			}
			//				System.err.print("aaa ,");
			//				System.err.println();
			distanceMap.put(i, map);
			minDistanceMap.put(i, min);
		}
	}

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

	protected void update() {
		//			System.err.println("[UPDATE]");
		double minDistance = Double.MAX_VALUE;
		int minI = -1;

		/*get the most minimum node-node from minD map*/
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
				jMap.put(k, Math.min(iMap.get(k), jMap.get(k)));
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
	}

	protected double calcMinDistance(Cluster c1, Cluster c2) {
		return c1.getMinDistance(c2);
	}

}
