package ncdsearch.clustering.debug;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch.clustering.strategy.Cluster;
import ncdsearch.clustering.strategy.DistanceClustering;

public class NoClustering extends DistanceClustering {

	public NoClustering(int topN, List<JsonNode> allNode, String strategy, int clusterNum) {
		super(topN, allNode, strategy, clusterNum);
	}

	@Override
	public List<List<JsonNode>> clustering() {
		List<List<JsonNode>> nodeList = new ArrayList<>();
		for (JsonNode node : allNode) {
			List<JsonNode> list = new ArrayList<>();
			list.add(node);
			nodeList.add(list);
		}
		return nodeList;
	}

	@Override
	protected void update() {

	}

	@Override
	protected double calcDistance(Cluster c1, Cluster c2) {
		return 0;
	}
}