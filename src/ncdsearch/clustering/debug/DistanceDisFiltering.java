package ncdsearch.clustering.debug;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch.clustering.JsonNodeInfo;
import ncdsearch.clustering.strategy.Cluster;
import ncdsearch.clustering.strategy.Component;

public class DistanceDisFiltering extends DistanceFiltering{

	public DistanceDisFiltering(List<JsonNode> allNode, String strategy, int clusterNum, double exDistanceThreshold,
			double clusterDistance) {
		super(allNode, strategy, clusterNum, exDistanceThreshold, clusterDistance);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	protected void addJsonNode(List<Cluster> allClusterList) {
		for (int i = 0; i < totalVertexNumber; i++) {
			for (Component co : clusterMap.get(i).getComponents()) {
				if (JsonNodeInfo.getNodeDistance(co.getJsonNode()) <= exDistanceThreshold) {
					allClusterList.add(clusterMap.get(i));
					removedFlagMap[i] = true;
				}
			}
		}
	}
}
