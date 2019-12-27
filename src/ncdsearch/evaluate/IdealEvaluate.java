package ncdsearch.evaluate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch.clustering.Answers;
import ncdsearch.clustering.Clusters;
import ncdsearch.clustering.JsonNodesInfo;



public class IdealEvaluate extends Evaluate {
	public IdealEvaluate(int allTopN, int clusterTopN) {
		super(allTopN,clusterTopN);
	}

	@Override
	public void evaluate(Clusters cs, Answers a) {
		totalCall++;
		nonAnswerRepSize = 0;
		totalResultNode += cs.getNodeSize();
		totalAnswerNode += a.getAllNodeSize();
		System.out.println("TotalNode: " + cs.getNodeSize());
		System.out.println("TotalDir: " + cs.getClusterRepsSize());
		System.out.println("-------");

		Clusters ics = getIdealClusters(cs, a);
		System.out.println("Filtered Node: " + ics.getNodeSize());
		System.out.println("Filtered Dir: " + ics.getClusterRepsSize());

		calcReduceWork(cs, ics);
		calcPrecision(ics, a);
		calcRecall(ics, a);
		//calcFvalue();

	}

	public Clusters getIdealClusters(Clusters cs, Answers a) {
		Clusters ics = new Clusters();
		Set<List<JsonNode>> set = new HashSet<>();
		for (List<JsonNode> nodes : cs.getRepJsonMap().values()) {
			if (set.contains(nodes)) continue;
			List<JsonNode> sortedNodes = JsonNodesInfo.getSortedListbyDistance(nodes);
			if (isContainInAnswer(nodes, a.getAllNode())) {
				ics.addClusterReps(sortedNodes);
				ics.addAllNode(nodes);
				for (JsonNode node : sortedNodes) {
					ics.putRepJsonMap(node, cs.getRepJsonMap().get(node));
				}
			} else {
				nonAnswerRepSize += Math.min(allTopN, nodes.size());
			}
			set.add(nodes);
		}
		return ics;
	}
}
