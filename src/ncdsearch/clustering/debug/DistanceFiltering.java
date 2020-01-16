package ncdsearch.clustering.debug;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import gnu.trove.map.hash.TIntDoubleHashMap;
import ncdsearch.clustering.JsonNodeInfo;
import ncdsearch.clustering.strategy.Cluster;
import ncdsearch.clustering.strategy.Component;
import ncdsearch.clustering.strategy.DistanceClustering;

public class DistanceFiltering extends DistanceClustering {
	private double clusterDistance;

	public DistanceFiltering(List<JsonNode> allNode, String strategy, int clusterNum, double exDistanceThreshold,
			double clusterDistance) {
		super(allNode, strategy, clusterNum, exDistanceThreshold);
		this.clusterDistance = clusterDistance;
	}

	@Override
	public List<List<JsonNode>> clustering() {
		init();
		/*add element whose distance is less than threshold*/
		List<Cluster> allClusterList = new ArrayList<>();

		/*TopN or DistanceN*/
		addJsonNodebyTopN(allClusterList);
		//addJsonNodebyDistance(allClusterList);

		/*add element near fragment selected previous block*/
		for (int i = 0; i < totalVertexNumber; i++) {
			if (removedFlagMap[i])
				continue;
			TIntDoubleHashMap innerMap = distanceMap.get(i);
			for (int j = 0; j < totalVertexNumber; j++) {
				if (i != j) {
					double distance = innerMap.get(j);
					//need additional param
					if (distance <= clusterDistance) {
						allClusterList.add(clusterMap.get(i));
						removedFlagMap[j] = true;
					}
				}
			}
		}

		/*push element to filteredlist*/
		List<List<JsonNode>> nodeList = new ArrayList<>();
		this.allNode.clear();
		for (int i = 0; i < totalVertexNumber; i++) {
			if (removedFlagMap[i]) {
				for (Component co : clusterMap.get(i).getComponents()) {
					this.allNode.add(co.getJsonNode());
					List<JsonNode> list = new ArrayList<>();
					list.add(co.getJsonNode());
					nodeList.add(list);
				}
			}
		}
		return nodeList;
	}

	private void addJsonNodebyDistance(List<Cluster> allClusterList) {
		for (int i = 0; i < totalVertexNumber; i++) {
			for (Component co : clusterMap.get(i).getComponents()) {
				if (JsonNodeInfo.getNodeDistance(co.getJsonNode()) <= exDistanceThreshold) {
					allClusterList.add(clusterMap.get(i));
					removedFlagMap[i] = true;
				}
			}
		}
	}

	private void addJsonNodebyTopN(List<Cluster> allClusterList) {
		int topN = (int) exDistanceThreshold;
		for (int i = 0; i < totalVertexNumber; i++) {
			for (Component co : clusterMap.get(i).getComponents()) {
				if (allClusterList.size() == 0) {
					allClusterList.add(clusterMap.get(i));
					removedFlagMap[i] = true;
				} else {
					double distance = JsonNodeInfo.getNodeDistance(co.getJsonNode());
					boolean nonAdded = true;
					for (int j = 0; j < allClusterList.size() && j < topN && nonAdded; j++) {
						for (Component c : allClusterList.get(j).getComponents()) {
							if (distance < JsonNodeInfo.getNodeDistance(c.getJsonNode())) {
								allClusterList.add(j, clusterMap.get(i));
								removedFlagMap[i] = true;
								nonAdded = false;
							}
						}
					}
					if (allClusterList.size() > topN) {
						allClusterList.remove(topN);
					}
				}
			}
		}
	}

	public List<List<JsonNode>> exClustering() {
		return clustering();
	}

	@Override
	protected void update() {

	}

	@Override
	protected double calcDistance(Cluster c1, Cluster c2) {
		return c1.getMinDistance(c2);
	}
}
