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
		pushToTotal(cs, a);

		fcsNodeSizes.add(fcs.getNodeSize());
		System.out.println("Filtered Node: " + fcs.getNodeSize());
		System.out.println("Filtered Dir: " + fcs.getClusterRepsSize());

		calcReduceWork(cs, fcs);
		calcPrecision(fcs, a);
		calcRecall(fcs, a);
	}
}
