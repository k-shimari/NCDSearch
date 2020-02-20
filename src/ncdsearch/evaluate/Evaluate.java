package ncdsearch.evaluate;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch.clustering.Answers;
import ncdsearch.clustering.Clusters;
import ncdsearch.clustering.JsonNodeInfo;

public class Evaluate {

	protected int clusterTopN;
	protected int allTopN;
	protected double distanceThreshold;
	protected boolean isRemoveClustering;
	protected boolean isDistance;
	protected int nonAnswerRepSize = 0;
	protected EvaluateData data = new EvaluateData();

	public Evaluate(String checkN, int clusterTopN, boolean isRemoveClustering) {
		this.allTopN = setAllTopN(checkN);
		this.clusterTopN = clusterTopN;
		this.isRemoveClustering = isRemoveClustering;
	}

	private int setAllTopN(String checkN) {
		if (checkN.startsWith("Top")) {
			return Integer.parseInt(checkN.substring("Top".length()));
		} else if (checkN.startsWith("Dis")) {
			this.isDistance = true;
			this.distanceThreshold = Double.parseDouble(checkN.substring("Dis".length()));
			return 1;
		} else {
			return 10;
		}
	}

	public void evaluate(Clusters cs, Answers a) {
		/*Distance to TopN*/
		if (isDistance) {
			setTopN(cs);
		}
		pushToTotal(cs, a);
		Clusters fcs;
		Filtering f = new Filtering(allTopN, clusterTopN, isRemoveClustering);
		if (isRemoveClustering) {
			fcs = f.getRemovedFilteredClusters(cs, a);
		} else {
			fcs = f.getFilteredClusters(cs, a);
		}
		data.fcsNodeSizes.add(fcs.getNodeSize());

		System.out.println("Filtered Node: " + fcs.getNodeSize());
		System.out.println("Filtered Dir: " + fcs.getClusterRepsSize());

		printResult(cs, a, fcs);
	}

	protected void printResult(Clusters cs, Answers a, Clusters fcs) {
		//		PrintRank p =new PrintRank();
		//		p.printAnswerRank(cs, a);
		//		p.printNodeRank(cs, fcs);
		data.calcReduceWork(cs, fcs, nonAnswerRepSize);
		data.calcPrecision(fcs, a);
		data.calcRecall(fcs, a);
		//calcFvalue();
	}

	protected void pushToTotal(Clusters cs, Answers a) {
		data.totalCall++;
		nonAnswerRepSize = 0;
		data.totalResultNode += cs.getNodeSize();
		data.totalAnswerNode += a.getAllNodeSize();
		System.out.println("TotalNode: " + cs.getNodeSize());
		System.out.println("TotalDir: " + cs.getClusterRepsSize());
		System.out.println("-------");
	}

	protected void setTopN(Clusters cs) {
		allTopN = 0;
		if (isRemoveClustering) {
			for (JsonNode node : cs.getAllNode()) {
				if (JsonNodeInfo.getNodeDistance(node) > distanceThreshold) {
					allTopN++;
				}
			}
		} else {
			for (JsonNode node : cs.getAllNode()) {
				if (JsonNodeInfo.getNodeDistance(node) <= distanceThreshold) {
					allTopN++;
				}
			}
		}
	}

	public EvaluateData getData() {
		return data;
	}

}
