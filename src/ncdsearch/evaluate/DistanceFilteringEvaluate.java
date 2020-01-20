package ncdsearch.evaluate;

import ncdsearch.clustering.Answers;
import ncdsearch.clustering.Clusters;

public class DistanceFilteringEvaluate extends Evaluate {

	public DistanceFilteringEvaluate(String checkN, int clusterTopN, boolean isRemoveClustering) {
		super(checkN, clusterTopN, isRemoveClustering);
	}

	public void evaluate(Clusters fcs, Clusters cs, Answers a) {
//		if (isDistance) {
//			setTopN(cs);
//		}
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
