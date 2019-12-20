package ncdsearch.clustering.strategy;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class Shortest extends DistanceClustering {

	public Shortest(int topN, List<JsonNode> allNode, String strategy, int clusterNum) {
		super(topN, allNode, strategy, clusterNum);
	}

	@Override
	protected double calcDistance(Cluster c1, Cluster c2) {
		return c1.getMinDistance(c2);
	}

}
