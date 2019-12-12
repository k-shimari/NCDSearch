package ncdsearch.evaluate;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch.clustering.Answers;
import ncdsearch.clustering.Clusters;
import ncdsearch.clustering.JsonNodesInfo;

public class IdealEvaluate extends Evaluate{
	public IdealEvaluate(int topN) {
		super(topN);
		// TODO 自動生成されたコンストラクター・スタブ
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
		for (List<JsonNode> nodes : cs.getRepJsonMap().values()) {
			List<JsonNode> sortedNodes = JsonNodesInfo.getSortedListbyDistance(nodes);
			if (isContainInAnswer(nodes, a.getAllNode())) {
				ics.addClusterReps(sortedNodes);
				ics.addAllNode(nodes);
				for (JsonNode node : sortedNodes) {
					ics.putRepJsonMap(node, cs.getRepJsonMap().get(node));
				}
			} else {
				nonAnswerRepSize += topN;
			}
		}
		return ics;
	}
}
