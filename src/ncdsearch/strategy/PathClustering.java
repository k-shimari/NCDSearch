package ncdsearch_clustering.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch_clustering.clustering.JsonNodeInfo;

public class PathClustering extends Clustering {

	private Map<String, List<JsonNode>> pathJsonMap = new HashMap<>();
	private String pathType;

	public PathClustering(int topN, List<JsonNode> allNode, String pathType) {
		super(topN, allNode);
		this.pathType = pathType;
	}

	@Override
	public List<List<JsonNode>> clustering() {
		for (JsonNode node : allNode) {
			String path = pathType.equals("DIR") ? JsonNodeInfo.getNodeDir(node) : JsonNodeInfo.getNodeFile(node);
			if (pathJsonMap.containsKey(path)) {
				List<JsonNode> list = pathJsonMap.get(path);
				list.add(node);
				pathJsonMap.put(path, list);
			} else {
				List<JsonNode> list = new ArrayList<>();
				list.add(node);
				pathJsonMap.put(path, list);
			}
		}
		return new ArrayList<>(pathJsonMap.values());
	}
}
