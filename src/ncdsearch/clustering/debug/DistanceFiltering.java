package ncdsearch.clustering.debug;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import gnu.trove.map.hash.TIntDoubleHashMap;
import ncdsearch.clustering.strategy.Cluster;
import ncdsearch.clustering.strategy.Component;
import ncdsearch.clustering.strategy.DistanceClustering;

public abstract class DistanceFiltering extends DistanceClustering {
	private double clusterDistance;
	protected abstract void addJsonNode(List<Cluster> allClusterList);

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
		addJsonNode(allClusterList);

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
