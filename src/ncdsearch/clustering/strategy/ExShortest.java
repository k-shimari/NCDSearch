package ncdsearch.clustering.strategy;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class ExShortest extends Shortest {

	private static final double threshold = 0.25;

	public ExShortest(int topN, List<JsonNode> allNode, String strategy, int clusterNum) {
		super(topN, allNode, strategy, clusterNum);
	}

	@Override
	public List<List<JsonNode>> clustering() {
		init();
		int mapSize = totalVertexNumber;
		double minDistance = 0.0;
		System.err.println("initial clusters : " + mapSize);
		int idx = 1;
		while (minDistance <= threshold && idx != totalVertexNumber) {
			idx++;
			update();
			minDistance = getMinDistance();
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

	private double getMinDistance() {
		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < totalVertexNumber; i++) {
			if (!removedFlagMap[i]) {
				double distance = minDistanceMap.get(i);
				if (distance < minDistance) {
					minDistance = distance;
				}
			}
		}
		return minDistance;
	}
}
