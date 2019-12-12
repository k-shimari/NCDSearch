package ncdsearch_clustering.evaluate;

import ncdsearch_clustering.clustering.Answers;
import ncdsearch_clustering.clustering.Clusters;

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
}
