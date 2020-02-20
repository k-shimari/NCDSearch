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

	private void printResult(Clusters cs, Answers a, Clusters fcs) {
//		PrintRank p =new PrintRank();
//		p.printAnswerRank(cs, a);
//		p.printNodeRank(cs, fcs);
		calcReduceWork(cs, fcs);
		calcPrecision(fcs, a);
		calcRecall(fcs, a);
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

	public void calcReduceWork(Clusters cs, Clusters fcs) {
		System.out.println(cs.getNodeSize() + "+" + fcs.getNodeSize() + "+" + nonAnswerRepSize);
		data.totalFilteredNode += fcs.getNodeSize() + nonAnswerRepSize;
		double reduceWork = (double) (cs.getNodeSize() - fcs.getNodeSize() - nonAnswerRepSize) / cs.getNodeSize();
		data.reduceWorks.add(reduceWork);
		System.out.println("Reduction rate: " + reduceWork);
	}


	//TODO fix at denominator
	public void calcPrecision(Clusters fcs, Answers a) {
		int size = 0;
		for (JsonNode node : fcs.getAllNode()) {
			if (CompareNodes.isContainInAnswer(node, a.getAllNode())) {
				size++;
			}
		}
		System.out.println(size + "/" + fcs.getNodeSize());
		//System.out.println("Precision: " + (double) size);
		double precision = (double) size / fcs.getNodeSize();
		data.totalPFind += size;
		data.totalPAll += fcs.getNodeSize();
		if (fcs.getNodeSize() != 0) {
			data.precisions.add(precision);
			System.out.println("Precision: " + precision);
		} else {
			data.totalNan++;
			System.out.println("Precision: NAN");
		}
	}

	public void calcRecall(Clusters fcs, Answers a) {
		int size = 0;
		for (JsonNode aNode : a.getAllNode()) {
			if (CompareNodes.isContainInResult(aNode, fcs.getAllNode())) {
				size++;
			}
		}
		System.out.println(size + "/" + a.getAllNodeSize());
		data.totalRFind += size;
		data.totalRAll += a.getAllNodeSize();
		double recall = (double) size / a.getAllNodeSize();
		data.recalls.add(recall);
		System.out.println("Recall: " + recall);
	}

	//	private void calcFvalue() {
	//		double precision = precisions.get(totalCall - 1);
	//		double recall = recalls.get(totalCall - 1);
	//		double fvalue = 2 * precision * recall / (precision + recall);
	//		fvalues.add(fvalue);
	//		System.out.println("Fvalue: " + fvalue);
	//
	//	}

	public EvaluateData getData() {
		return data;
	}

}
