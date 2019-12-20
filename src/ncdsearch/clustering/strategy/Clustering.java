package ncdsearch.clustering.strategy;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class Clustering {

	protected int topN;
	protected List<JsonNode> allNode;
	protected String strategy;

	public Clustering(int topN, List<JsonNode> allNode, String strategy) {
		this.topN = topN;
		this.allNode = allNode;
		this.strategy = strategy;
	}

	public abstract List<List<JsonNode>> clustering();

}
