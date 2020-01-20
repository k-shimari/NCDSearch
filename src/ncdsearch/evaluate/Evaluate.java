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

	//	public void setAllTopN(int allTopN) {
	//		this.checkN = allTopN;
	//	}

	public void evaluate(Clusters cs, Answers a) {
		/*Distance to TopN*/
		if (isDistance) {
			setTopN(cs);
		}
		totalCall++;
		nonAnswerRepSize = 0;
		totalResultNode += cs.getNodeSize();
		totalAnswerNode += a.getAllNodeSize();
		System.out.println("TotalNode: " + cs.getNodeSize());
		System.out.println("TotalDir: " + cs.getClusterRepsSize());
		System.out.println("-------");
		//printAnswerRank(cs, a);

		//		cs.getRepJsonMap().keySet().forEach(s -> {
		//
		//			System.out.println("rep:" + s);
		//
		//			//s.getValue().forEach(t -> System.out.println(t));
		//
		//		});

		Clusters fcs;
		if (isRemoveClustering) {
			fcs = getRemovedFilteredClusters(cs, a);
		} else {
			fcs = getFilteredClusters(cs, a);
		}
		fcsNodeSizes.add(fcs.getNodeSize());
		//printRank(cs, fcs);
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

	private void setTopN(Clusters cs) {
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

	private void printAnswerRank(Clusters cs, Answers a) {
		List<JsonNode> sortedList = JsonNodesInfo.getSortedListbyDistance(cs.getAllNode());
		for (int i = 0; i < sortedList.size(); i++) {
			if (isContainInAnswer(sortedList.get(i), a.getAllNode())) {
				System.out.println("Answer Rank: " + (i + 1));
			}
		}
	}

	private void printRank(Clusters cs, Clusters fcs) {
		System.out.println("----");
		List<JsonNode> sortedList = JsonNodesInfo.getSortedListbyDistance(cs.getAllNode());
		for (JsonNode node : fcs.getAllNode()) {
			for (int i = 0; i < sortedList.size(); i++) {
				if (node.equals(sortedList.get(i))) {
					System.out.println("Rank: " + (i + 1));
				}
			}
		}
	}

	private void printRank(Clusters cs, List<JsonNode> nodeList) {
		System.out.println("----");
		List<JsonNode> sortedList = JsonNodesInfo.getSortedListbyDistance(cs.getAllNode());
		for (JsonNode node : nodeList) {
			for (int i = 0; i < sortedList.size(); i++) {
				if (node.equals(sortedList.get(i))) {
					System.out.println("Rank: " + (i + 1));
					break;
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

	public Clusters getFilteredClusters(Clusters cs, Answers a) {
		Clusters fcs = new Clusters();
		for (List<JsonNode> nodes : cs.getClusterReps()) {
			List<JsonNode> sortedNodes = JsonNodesInfo.getSortedListbyDistance(nodes);
			if (isContainMinNode(nodes, cs.getAllNode())) {
				//if (isContainInAnswer(nodes, a.getAllNode())) {
				fcs.addClusterReps(sortedNodes);

				addNode(cs, fcs, sortedNodes);

				for (JsonNode node : sortedNodes) {
					fcs.putRepJsonMap(node, cs.getRepJsonMap().get(node));
				}
				//} else {
				//	nonAnswerRepSize += sortedNodes.size();
				//}
			}
			//printRank(cs, nodes);
		}
		return fcs;
	}

	public Clusters getRemovedFilteredClusters(Clusters cs, Answers a) {
		Clusters fcs = new Clusters();
		for (List<JsonNode> nodes : cs.getClusterReps()) {
			List<JsonNode> sortedNodes = JsonNodesInfo.getSortedListbyDistance(nodes);
			if (!isContainMaxNode(nodes, cs.getAllNode())) {
				//if (isContainInAnswer(nodes, a.getAllNode())) {
				fcs.addClusterReps(sortedNodes);

				addNode(cs, fcs, sortedNodes);

				for (JsonNode node : sortedNodes) {
					fcs.putRepJsonMap(node, cs.getRepJsonMap().get(node));
				}
			}
		}
		return fcs;
	}

	protected void addNode(Clusters cs, Clusters fcs, List<JsonNode> sortedNodes) {
		//fcs.addAllNode(cs.getRepJsonMap().get(sortedNodes.get(0)));
		List<JsonNode> list = new ArrayList<>(cs.getRepJsonMap().get(sortedNodes.get(0)));
		fcs.addAllNode(list.subList(0, Math.min(list.size(), clusterTopN)));
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
		for (int i = 0; i < this.allTopN; i++) {
			JsonNode minNode = JsonNodesInfo.getSortedListbyDistance(allNode).get(i);
			if (nodes.contains(minNode)) {
				return true;
			}
		}
		return false;
	}

	private boolean isContainMaxNode(List<JsonNode> nodes, List<JsonNode> allNode) {
		for (int i = allNode.size() - 1; i >= allNode.size() - this.allTopN; i--) {
			JsonNode maxNode = JsonNodesInfo.getSortedListbyDistance(allNode).get(i);
			if (nodes.contains(maxNode)) {
				return true;
			}
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
