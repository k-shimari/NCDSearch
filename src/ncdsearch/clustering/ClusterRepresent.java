package ncdsearch.clustering;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class ClusterRepresent {
	private int topN;

	public ClusterRepresent(int topN) {
		this.topN = topN;
	}

	public List<JsonNode> getClusterReps(List<JsonNode> nodes) {
		List<JsonNode> sortedList = JsonNodesInfo.getSortedListbyDistance(nodes);
		if (sortedList.size() <= topN) {
			return sortedList;
		} else {
			List<JsonNode> list = new ArrayList<>(sortedList);
			return list.subList(0, topN);
		}
	}

	//	@Override
	//	public void clustering() {
	//		for (JsonNode node : clusters.getAllNode()) {
	//			String path = strategy.equals("DIR") ? JsonNodeInfo.getNodeDir(node) : JsonNodeInfo.getNodeFile(node);
	//			boolean isContain = strategy.equals("DIR")
	//					? JsonNodesInfo.getNodesListDirs(clusters.getClusterReps()).contains(path)
	//					: JsonNodesInfo.getNodesListFiles(clusters.getClusterReps()).contains(path);
	//			if (isContain) {
	//				updateRepJsonMap(node);
	//			} else {
	//				List<JsonNode> list = new ArrayList<>();
	//				list.add(node);
	//				clusters.getRepJsonMap().put(node, list);
	//				List<JsonNode> list2 = new ArrayList<>();
	//				list2.add(node);
	//				clusters.getClusterReps().add(list2);
	//			}
	//		}
	//	}

	//	private void updateClusterReps(JsonNode node, List<JsonNode> clusterNodes) {
	//	for (List<JsonNode> list : clusters.getClusterReps()) {
	//		if (list.contains(clusterNodes.get(topN - 1))) {
	//			clusters.getClusterReps().remove(list);
	//			list.remove(clusterNodes.get(topN - 1));
	//			list.add(node);
	//			clusters.getClusterReps().add(list);
	//			break;
	//		}
	//	}
	//}
	//
	//private void addNodetoMap(JsonNode node, List<JsonNode> clusterNodes) {
	//	List<JsonNode> list = clusters.getRepJsonMap().get(clusterNodes.get(0));
	//	list.add(node);
	//	clusters.getRepJsonMap().put(node, list);
	//}
	//
	///*TODO optimize getClusterReps*/
	//private void updateRepJsonMap(JsonNode node) {
	//	List<JsonNode> clusterNodes = strategy.equals("DIR")
	//			? JsonNodesInfo.getRepNodeListbyDir(node, clusters.getClusterReps())
	//			: JsonNodesInfo.getRepNodeListbyFile(node, clusters.getClusterReps());
	//	if (clusterNodes.size() >= topN) {
	//		if (JsonNodeInfo.getNodeDistance(node) < JsonNodeInfo.getNodeDistance(clusterNodes.get(topN - 1))) {
	//			addNodetoMap(node, clusterNodes);
	//			updateClusterReps(node, clusterNodes);
	//			clusters.getRepJsonMap().remove(clusterNodes.get(topN - 1));
	//
	//		} else {
	//			List<JsonNode> list = clusters.getRepJsonMap().get(clusterNodes.get(0));
	//			list.add(node);
	//			clusters.getRepJsonMap().put(clusterNodes.get(0), list);
	//		}
	//	} else {
	//		for (List<JsonNode> list : clusters.getClusterReps()) {
	//			if (list.contains(clusterNodes.get(0))) {
	//				clusters.getClusterReps().remove(list);
	//				list.add(node);
	//				clusters.getClusterReps().add(list);
	//				break;
	//			}
	//		}
	//		addNodetoMap(node, clusterNodes);
	//	}
	//}
}
