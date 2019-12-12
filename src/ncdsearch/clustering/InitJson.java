package ncdsearch.clustering;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InitJson {
	private static final String KEY_RESULT = "Result";
	private static final String KEY_ANSWERS = "answers";
	//private static final String MODE = "DIR";
	private String clusteringStrategy;
	private String distanceAlgorithm;
	private int topN;

	public InitJson(String clusteringStrategy, String distanceAlgorithm, int topN) {
		this.clusteringStrategy = clusteringStrategy;
		this.distanceAlgorithm=distanceAlgorithm;
		this.topN = topN;
	}

	public Clusters converttoClusters(File file) {
		Clusters cs = new Clusters(clusteringStrategy, distanceAlgorithm, topN);
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(file);

			//			List<JsonNode> nodes = new ArrayList<JsonNode>();
			//			for (JsonNode node : root.get(KEY_RESULT)) {
			//				nodes.add(node);
			//			}
			//			List<JsonNode> sortedNode = JsonNodesInfo.getSortedListbyDistance(nodes);
			//			sortedNode.stream().limit(10).forEach(s -> cs.addNode(s));

			for (JsonNode node : root.get(KEY_RESULT)) {
				cs.addNode(node);
			}
			cs.clusteringNode();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return cs;
	}

	public Answers converttoAnswer(File answer, String KEY_NUMBER) {
		Answers a = new Answers();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(answer);
			for (JsonNode node : root.get(KEY_NUMBER).get(KEY_ANSWERS)) {
				a.addNode(node);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return a;
	}
}
