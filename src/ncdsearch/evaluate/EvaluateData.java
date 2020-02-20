package ncdsearch.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvaluateData {
	public List<Double> reduceWorks = new ArrayList<>();
	public List<Double> precisions = new ArrayList<>();
	public List<Double> recalls = new ArrayList<>();
	public List<Double> fvalues = new ArrayList<>();
	public List<Integer> fcsNodeSizes = new ArrayList<>();
	public int totalCall = 0;
	public int totalNan = 0;
	public int totalResultNode = 0;
	public int totalFilteredNode = 0;
	public int totalAnswerNode = 0;
	public int totalPFind = 0;
	public int totalPAll = 0;
	public int totalRFind = 0;
	public int totalRAll = 0;

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



}