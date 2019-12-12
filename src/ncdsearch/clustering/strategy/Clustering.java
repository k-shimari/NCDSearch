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
	int topN;
	List<JsonNode> allNode;
	String strategy;

	public Clustering(int topN, List<JsonNode> allNode, String strategy) {
		this.topN = topN;
		this.allNode = allNode;
		this.strategy = strategy;
	}

	public abstract List<List<JsonNode>> clustering();

}
