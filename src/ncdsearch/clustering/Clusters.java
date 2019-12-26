package ncdsearch.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch.clustering.debug.NoClustering;
import ncdsearch.clustering.strategy.Average;
import ncdsearch.clustering.strategy.Clustering;
import ncdsearch.clustering.strategy.GroupAverage;
import ncdsearch.clustering.strategy.Longest;
import ncdsearch.clustering.strategy.NewmanFast;
import ncdsearch.clustering.strategy.PathClustering;
import ncdsearch.clustering.strategy.Shortest;

public class Clusters {

	protected List<JsonNode> allNode = new ArrayList<>();
	protected List<List<JsonNode>> clusterReps = new ArrayList<>();
	protected List<List<JsonNode>> clusterContents = new ArrayList<>();
	protected Map<JsonNode, List<JsonNode>> repJsonMap = new HashMap<>();

	private String clustringStrategy;
	private String distanceAlgorithm;
	private int topN;
	private int clusterNum;

	public Clusters(String strategy, String distanceAlgorithm, int topN, int clusterNum) {
		this.clustringStrategy = strategy;
		this.distanceAlgorithm = distanceAlgorithm;
		this.topN = topN;
		this.clusterNum = clusterNum;
	}

	public Clusters() {
	}

	public void addNode(JsonNode node) {
		allNode.add(node);
	}

	public void addAllNode(List<JsonNode> nodes) {
		allNode.addAll(nodes);
	}

	public List<JsonNode> getAllNode() {
		return allNode;
	}

	public int getNodeSize() {
		return allNode.size();
	}

	public List<List<JsonNode>> getClusterReps() {
		return clusterReps;
	}

	public List<List<JsonNode>> getClusterContents() {
		return clusterContents;
	}

	public void addClusterReps(List<JsonNode> rep) {
		clusterReps.add(rep);
	}

	public int getClusterRepsSize() {
		return clusterReps.size();
	}

	public Map<JsonNode, List<JsonNode>> getRepJsonMap() {
		return repJsonMap;
	}

	public void putRepJsonMap(JsonNode node, List<JsonNode> list) {
		repJsonMap.put(node, list);
	}

	public void clusteringNode() {
		clustering();
		ClusterRepresent c = new ClusterRepresent(topN);
		for (List<JsonNode> nodes : clusterContents) {
			List<JsonNode> reps = c.getClusterReps(nodes);
			clusterReps.add(reps);
			for (JsonNode node : reps) {
				repJsonMap.put(node, nodes);
			}
		}
	}

	public void clustering() {
		Clustering c;
		if (clustringStrategy.startsWith("EX")) {
			if (clustringStrategy.equals("EXSH")) {
				c = new Shortest(topN, allNode, distanceAlgorithm, clusterNum);
			} else if (clustringStrategy.equals("EXLO")) {
				c = new Longest(topN, allNode, distanceAlgorithm, clusterNum);
			} else if (clustringStrategy.equals("EXGA")) {
				c = new GroupAverage(topN, allNode, distanceAlgorithm, clusterNum);
			} else if (clustringStrategy.equals("EXAV")) {
				c = new Average(topN, allNode, distanceAlgorithm, clusterNum);
			} else {
				System.err.println("Not Supported Strategy: " + clustringStrategy);
				System.exit(0);
				c = new NoClustering(topN, allNode, distanceAlgorithm, clusterNum);
			}
			clusterContents = c.exClustering();
		} else {
			if (clustringStrategy.equals("DIR") || clustringStrategy.equals("FILE")) {
				c = new PathClustering(topN, allNode, clustringStrategy);
			} else if (clustringStrategy.equals("SH")) {
				c = new Shortest(topN, allNode, distanceAlgorithm, clusterNum);
			} else if (clustringStrategy.equals("LO")) {
				c = new Longest(topN, allNode, distanceAlgorithm, clusterNum);
			} else if (clustringStrategy.equals("GA")) {
				c = new GroupAverage(topN, allNode, distanceAlgorithm, clusterNum);
			} else if (clustringStrategy.equals("AV")) {
				c = new Average(topN, allNode, distanceAlgorithm, clusterNum);
			} else if (clustringStrategy.equals("NF")) {
				c = new NewmanFast(topN, allNode, distanceAlgorithm);
			} else {
				System.err.println("Not Supported Strategy: " + clustringStrategy);
				System.err.println("No Clustering: ");
				c = new NoClustering(topN, allNode, distanceAlgorithm, clusterNum);
			}
			clusterContents = c.clustering();
		}
	}
}
