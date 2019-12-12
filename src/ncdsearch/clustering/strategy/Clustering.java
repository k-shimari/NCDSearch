package ncdsearch.clustering.strategy;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class Clustering {

	/**
	 * Cluster result fragments.
	 * @author ito-k
	 * @param fragments
	 * @return clusters
	 */
	String strategy;
	int topN;
	List<JsonNode> allNode;

	public Clustering(String mode, int topN, List<JsonNode> allNode) {
		this.strategy = mode;
		this.topN = topN;
		this.allNode = allNode;
	}

	public Clustering(int topN, List<JsonNode> allNode) {
		this.topN = topN;
		this.allNode = allNode;
	}

	public abstract List<List<JsonNode>> clustering();

}
