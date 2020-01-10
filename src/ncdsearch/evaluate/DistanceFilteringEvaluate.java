package ncdsearch.evaluate;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch.clustering.Answers;
import ncdsearch.clustering.Clusters;
import ncdsearch.clustering.JsonNodeInfo;

public class DistanceFilteringEvaluate extends Evaluate {

	public DistanceFilteringEvaluate(String checkN, int clusterTopN) {
		super(checkN, clusterTopN);
	}

	public void evaluate(Clusters fcs, Clusters cs, Answers a) {
		if (isDistance) {
			allTopN=0;
			for (JsonNode node : cs.getAllNode()) {
				if(JsonNodeInfo.getNodeDistance(node)<=distanceThreshold) {
					allTopN++;
				}
			}
		}
		totalCall++;
		nonAnswerRepSize = 0;
		totalResultNode += cs.getNodeSize();
		totalAnswerNode += a.getAllNodeSize();
		System.out.println("TotalNode: " + cs.getNodeSize());
		System.out.println("TotalDir: " + cs.getClusterRepsSize());
		System.out.println("-------");

		fcsNodeSizes.add(fcs.getNodeSize());
		System.out.println("Filtered Node: " + fcs.getNodeSize());
		System.out.println("Filtered Dir: " + fcs.getClusterRepsSize());

		calcReduceWork(cs, fcs);
		calcPrecision(fcs, a);
		calcRecall(fcs, a);
	}
}
