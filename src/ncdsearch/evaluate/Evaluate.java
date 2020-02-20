package ncdsearch.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch.clustering.Answers;
import ncdsearch.clustering.Clusters;
import ncdsearch.clustering.JsonNodeInfo;

public class Evaluate {
	protected List<Double> reduceWorks = new ArrayList<Double>();
	protected List<Double> precisions = new ArrayList<Double>();
	protected List<Double> recalls = new ArrayList<Double>();
	protected List<Double> fvalues = new ArrayList<Double>();
	protected List<Integer> fcsNodeSizes = new ArrayList<Integer>();
	protected int totalCall = 0;
	protected int totalNan = 0;
	protected int clusterTopN;

	protected int allTopN;
	protected double distanceThreshold;
	protected boolean isRemoveClustering;
	protected boolean isDistance;

	protected int nonAnswerRepSize = 0;

	protected int totalResultNode = 0;
	protected int totalFilteredNode = 0;
	protected int totalAnswerNode = 0;
	protected int totalPFind = 0;
	protected int totalPAll = 0;
	protected int totalRFind = 0;
	protected int totalRAll = 0;

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
		fcsNodeSizes.add(fcs.getNodeSize());

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
		totalCall++;
		nonAnswerRepSize = 0;
		totalResultNode += cs.getNodeSize();
		totalAnswerNode += a.getAllNodeSize();
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





	public void printAll() {
		System.out.println("--------------");
		System.out.println("Reduce Works");
		Collections.sort(reduceWorks);
		reduceWorks.forEach(s -> System.out.println(s));

		System.out.println("--------------");
		System.out.println("Precision");
		Collections.sort(precisions);
		precisions.forEach(s -> System.out.println(s));

		System.out.println("--------------");
		System.out.println("Recall");
		Collections.sort(recalls);
		recalls.forEach(s -> System.out.println(s));

		System.out.println("--------------");
		System.out.println("Filtered Node Size");
		fcsNodeSizes.forEach(s -> System.out.println(s));

		//		System.out.println("--------------");
		//		System.out.println("F-Value");
		//		Collections.sort(fvalues);
		//		fvalues.forEach(s -> System.out.println(s));

	}

	public void printAverage() {
		double sum = 0.0;
		for (double d : reduceWorks) {
			sum += d;
		}
		//System.err.println("Ave Reduction rate: " + sum / totalCall);
		System.err.println(sum / totalCall);
		sum = 0.0;
		for (double d : precisions) {
			sum += d;
		}
		//System.err.println("Ave Precision: " + sum / (totalCall - totalNan));
		System.err.println(sum / (totalCall - totalNan));
		sum = 0.0;
		for (double d : recalls) {
			sum += d;
		}
		//System.err.println("Ave Recall: " + sum / totalCall);
		System.err.println(sum / totalCall);
		//		sum = 0.0;
		//		for (double d : fvalues) {
		//			sum += d;
		//		}
		//		System.out.println("Ave Fvalue: " + sum / totalCall);
		double precision = (double) totalPFind / totalPAll;
		double recall = (double) totalRFind / totalRAll;
		double reduction = 1.0 - (double) totalFilteredNode / totalResultNode;
		//		System.err.println("Total Reduction rate: " + reduction);
		//		System.err.println("Total Precision: " + precision);
		//		System.err.println("Total Recall: " + recall);
		//		System.err.println("Total F-value: " + 2 * precision * recall / (precision + recall));
		//		System.err.println("TotalCheckedNode: " + totalFilteredNode);
		//		System.err.println("TotalAnswerNode/TotalResultNode:" + totalAnswerNode + "/" + totalResultNode + ": "
		//				+ (double) totalAnswerNode / totalResultNode);
		System.err.println(reduction);
		System.err.println(precision);
		System.err.println(recall);
		System.err.println(2 * precision * recall / (precision + recall));
		System.err.println(totalFilteredNode);
		//		System.err.println(totalAnswerNode + "/" + totalResultNode + ": "
		//				+ (double) totalAnswerNode / totalResultNode);
	}



	public void calcReduceWork(Clusters cs, Clusters fcs) {
		System.out.println(cs.getNodeSize() + "+" + fcs.getNodeSize() + "+" + nonAnswerRepSize);
		totalFilteredNode += fcs.getNodeSize() + nonAnswerRepSize;
		double reduceWork = (double) (cs.getNodeSize() - fcs.getNodeSize() - nonAnswerRepSize) / cs.getNodeSize();
		reduceWorks.add(reduceWork);
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
		totalPFind += size;
		totalPAll += fcs.getNodeSize();
		if (fcs.getNodeSize() != 0) {
			precisions.add(precision);
			System.out.println("Precision: " + precision);
		} else {
			totalNan++;
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
		totalRFind += size;
		totalRAll += a.getAllNodeSize();
		double recall = (double) size / a.getAllNodeSize();
		recalls.add(recall);
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

}
