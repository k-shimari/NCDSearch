package ncdsearch.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch.clustering.Answers;
import ncdsearch.clustering.Clusters;
import ncdsearch.clustering.JsonNodeInfo;
import ncdsearch.clustering.JsonNodesInfo;

public class Evaluate {
	protected List<Double> reduceWorks = new ArrayList<Double>();
	protected List<Double> precisions = new ArrayList<Double>();
	protected List<Double> recalls = new ArrayList<Double>();
	protected List<Double> fvalues = new ArrayList<Double>();
	protected int totalCall = 0;
	protected int totalNan = 0;
	protected int topN;
	protected int nonAnswerRepSize = 0;

	protected int totalResultNode = 0;
	protected int totalFilteredNode = 0;
	protected int totalAnswerNode = 0;
	protected int totalPFind = 0;
	protected int totalPAll = 0;
	protected int totalRFind = 0;
	protected int totalRAll = 0;

	public Evaluate(int topN) {
		this.topN = topN;
	}

	public void setTopN(int topN) {
		this.topN = topN;
	}

	public void evaluate(Clusters cs, Answers a) {
		totalCall++;
		nonAnswerRepSize = 0;
		totalResultNode += cs.getNodeSize();
		totalAnswerNode += a.getAllNodeSize();
		System.out.println("TotalNode: " + cs.getNodeSize());
		System.out.println("TotalDir: " + cs.getClusterRepsSize());
		System.out.println("-------");
		printAnswerRank(cs, a);

		//		cs.getRepJsonMap().keySet().forEach(s -> {
		//
		//			System.out.println("rep:" + s);
		//
		//			//s.getValue().forEach(t -> System.out.println(t));
		//
		//		});

		Clusters fcs = getFilteredClusters(cs, a);
		//		fcs.getRepJsonMap().entrySet().forEach(s -> {
		//
		//			System.out.println("rep:" + s);
		//			s.getValue().forEach(t -> System.out.println(t));
		//
		//		});
		System.out.println("Filtered Node: " + fcs.getNodeSize());
		System.out.println("Filtered Dir: " + fcs.getClusterRepsSize());

		calcReduceWork(cs, fcs);
		calcPrecision(fcs, a);
		calcRecall(fcs, a);
		//calcFvalue();
	}

	private void printAnswerRank(Clusters cs, Answers a) {
		List<JsonNode> sortedList = JsonNodesInfo.getSortedListbyDistance(cs.getAllNode());
		for (int i = 0; i < sortedList.size(); i++) {
			if (isContainInAnswer(sortedList.get(i), a.getAllNode())) {
				System.out.println("Rank: " + (i+1));
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
		System.out.println("Ave Reduction rate: " + sum / totalCall);
		sum = 0.0;
		for (double d : precisions) {
			sum += d;
		}
		System.out.println("Ave Precision: " + sum / (totalCall - totalNan));
		sum = 0.0;
		for (double d : recalls) {
			sum += d;
		}
		System.out.println("Ave Recall: " + sum / totalCall);
		//		sum = 0.0;
		//		for (double d : fvalues) {
		//			sum += d;
		//		}
		//		System.out.println("Ave Fvalue: " + sum / totalCall);
		double precision = (double) totalPFind / totalPAll;
		double recall = (double) totalRFind / totalRAll;
		double reduction = 1.0 - (double) totalFilteredNode / totalResultNode;
		System.out.println("Total Reduction rate: " + reduction);
		System.out.println("Total Precision: " + precision);
		System.out.println("Total Recall: " + recall);
		System.out.println("Total F-value: " + 2 * precision * recall / (precision + recall));
		System.out.println("TotalAnswerNode/TotalResultNode:" + totalAnswerNode + "/" + totalResultNode + ":"
				+ (double) totalAnswerNode / totalResultNode);
	}

	public Clusters getFilteredClusters(Clusters cs, Answers a) {
		Clusters fcs = new Clusters();
		for (List<JsonNode> nodes : cs.getClusterReps()) {
			List<JsonNode> sortedNodes = JsonNodesInfo.getSortedListbyDistance(nodes);
			if (isContainInAnswer(nodes, a.getAllNode())) {
			//if (isContainMinNode(nodes, cs.getAllNode())) {
				fcs.addClusterReps(sortedNodes);
				fcs.addAllNode(cs.getRepJsonMap().get(sortedNodes.get(0)));
				for (JsonNode node : sortedNodes) {
					fcs.putRepJsonMap(node, cs.getRepJsonMap().get(node));
				}
			} else {
				nonAnswerRepSize += sortedNodes.size();
			}
		}
		return fcs;
	}

	private boolean isContainInAnswer(JsonNode node, List<JsonNode> answerNodes) {
		for (JsonNode aNode : answerNodes) {
			if (JsonNodeInfo.getNodeFile(node).equals(JsonNodeInfo.getNodeAnswerFile(aNode))) {
				if (JsonNodeInfo.getNodeEndLine(node) >= JsonNodeInfo.getNodeSLine(aNode) &&
						JsonNodeInfo.getNodeStartLine(node) <= JsonNodeInfo.getNodeELine(aNode)) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isContainInAnswer(List<JsonNode> nodes, List<JsonNode> answerNodes) {
		for (JsonNode aNode : answerNodes) {
			for (JsonNode node : nodes) {
				if (JsonNodeInfo.getNodeFile(node).equals(JsonNodeInfo.getNodeAnswerFile(aNode))) {
					if (JsonNodeInfo.getNodeEndLine(node) >= JsonNodeInfo.getNodeSLine(aNode) &&
							JsonNodeInfo.getNodeStartLine(node) <= JsonNodeInfo.getNodeELine(aNode)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isContainMinNode(List<JsonNode> nodes, List<JsonNode> allNode) {
		for (int i = 0; i < this.topN; i++) {
			JsonNode minNode = JsonNodesInfo.getSortedListbyDistance(allNode).get(i);
			if (nodes.contains(minNode))
				return true;
			;
		}

		return false;
	}

	private boolean isContainInResult(JsonNode node, List<JsonNode> resultNodes) {
		for (JsonNode rNode : resultNodes) {
			if (JsonNodeInfo.getNodeFile(rNode).equals(JsonNodeInfo.getNodeAnswerFile(node))) {
				if (JsonNodeInfo.getNodeEndLine(rNode) >= JsonNodeInfo.getNodeSLine(node) &&
						JsonNodeInfo.getNodeStartLine(rNode) <= JsonNodeInfo.getNodeELine(node)) {
					return true;
				}
			}
		}
		return false;
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
			if (isContainInAnswer(node, a.getAllNode())) {
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
			if (isContainInResult(aNode, fcs.getAllNode())) {
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
