package ncdsearch.clustering;

import java.io.File;
import java.nio.file.Paths;

import ncdsearch.clustering.debug.OutputClusters;
import ncdsearch.evaluate.DistanceFilteringEvaluate;
import ncdsearch.evaluate.Evaluate;
import ncdsearch.evaluate.IdealEvaluate;

public class Main {
	private static String clusteringStrategy = "EXSH";
	private static String distanceAlgorithm = "ncd";
	private static String checkN = "Top1";
	private static double exDistanceThreshold = 0.3;
	private static double clusterDistance = 0;

	//Optional Param
	private static final int REPN = 10;
	private static final int CLUSTERTOPN = 10000;
	//	private static String distanceAlgorithm = "DIR";
	//	private static final int TOPN = 1100;
	private static final int CLUSTER_NUM = 5;
	//	private static int[] topNList = { 1, 43, 11, 4, 13, 16, 14, 11, 30, 8, 6, 43, 10, 10, 10, 5, 37, 12, 17, 52, 2, 4,
	//			11, 11, 5, 13, 52, 12, 538, 112, 86, 13, 2, 4, 14, 156, 32, 2, 11, 12, 240, 11, 10, 10, 13, 13, 3, 21, 6, 5,
	//			10, 4, 33 };//TOP10
	//	private static int[] topNList = { 1, 22, 6, 4, 7, 16, 6, 10, 19, 5, 6, 43, 5, 7, 5, 5, 37, 7, 17, 52, 2, 4, 5, 8, 5,
	//			7, 16, 5, 538, 112, 86, 6, 2, 4, 7, 156, 5, 2, 5, 12, 226, 7, 5, 5, 13, 5, 3, 21, 5, 5, 5, 4, 15 };//TOP5

	public static void main(String[] args) {
		//		try {
		//			BufferedWriter bw = Files.newBufferedWriter(Paths.get("F://ncdsearch/rereresult.txt"));
		//			bw.close();
		//		} catch (IOException e) {
		//			// TODO 自動生成された catch ブロック
		//			e.printStackTrace();
		//		}
		//String ID= args[1];
		//String ID = "4";
		if (args.length > 1) {
			checkN = args[1];
			exDistanceThreshold = Double.parseDouble(args[2]);
			clusteringStrategy = args[3];
		}
		if (args.length > 4) {
			clusterDistance = Double.parseDouble(args[4]);
		}
		//callEvaluate(args[0]);
		callDistanceFilteringEvaluate(args[0]);
		//callIdealEvaluate(args[0]);
	}

	private static void callEvaluate(String path) {
		Evaluate e = new Evaluate(checkN, CLUSTERTOPN);
		evaluate(path, e);
		//e.printAverage();
		printLogs(e);
	}

	private static void callIdealEvaluate(String path) {
		IdealEvaluate e = new IdealEvaluate(checkN, CLUSTERTOPN);
		evaluate(path, e);
		printLogs(e);
	}

	private static void callDistanceFilteringEvaluate(String path) {
		DistanceFilteringEvaluate e = new DistanceFilteringEvaluate(checkN, CLUSTERTOPN);
		evaluate(path, e);
		printLogs(e);
	}

	private static void evaluate(String path, Evaluate e) {
		for (int ID = 1; ID <= 53; ID++) {
			System.out.println("------------------");
			System.out.println("ID:" + ID);
			String answerJson = Paths.get(path, ("queries.json")).toAbsolutePath().toString();
			String inputJson = Paths.get(path, ("result/zip-0.6-fast-k0-" + ID + ".json")).toAbsolutePath()
					//String inputJson = Paths.get(path, ("result/lzjd-0.5-fast-k0-" + ID + ".json")).toAbsolutePath()
					.toString();
			InitJson ij = new InitJson(clusteringStrategy, distanceAlgorithm, REPN, CLUSTER_NUM, exDistanceThreshold,
					clusterDistance);
			Clusters cs = ij.converttoClusters(new File(inputJson));
			Answers a = ij.converttoAnswer(new File(answerJson), String.valueOf(ID));

			//output(cs);
			//e.setTopN(topNList[ID - 1]);
			//if(cs.getNodeSize()>10)
			e.evaluate(cs, a);
		}
	}

	private static void evaluate(String path, DistanceFilteringEvaluate e) {
		for (int ID = 1; ID <= 53; ID++) {
			System.out.println("------------------");
			System.out.println("ID:" + ID);
			String answerJson = Paths.get(path, ("queries.json")).toAbsolutePath().toString();
			String inputJson = Paths.get(path, ("result/zip-0.6-fast-k0-" + ID + ".json")).toAbsolutePath()
					//String inputJson = Paths.get(path, ("result/lzjd-0.5-fast-k0-" + ID + ".json")).toAbsolutePath()
					.toString();
			InitJson ij = new InitJson("NO", distanceAlgorithm, REPN, CLUSTER_NUM, 0, 0);
			Clusters cs = ij.converttoClusters(new File(inputJson));
			Answers a = ij.converttoAnswer(new File(answerJson), String.valueOf(ID));

			InitJson fij = new InitJson("EXDF", distanceAlgorithm, REPN, CLUSTER_NUM, exDistanceThreshold,
					clusterDistance);
			Clusters fcs = fij.converttoClusters(new File(inputJson));

			//output(cs);
			//e.setTopN(topNList[ID - 1]);
			//if(cs.getNodeSize()>10)
			e.evaluate(fcs, cs, a);
		}
	}

	private static void evaluate(String path, IdealEvaluate e) {
		for (int ID = 1; ID <= 53; ID++) {
			System.out.println("------------------");
			System.out.println("ID:" + ID);
			String answerJson = Paths.get(path, ("queries.json")).toAbsolutePath().toString();
			String inputJson = Paths.get(path, ("result/zip-0.5-fast-k0-" + ID + ".json")).toAbsolutePath()
					.toString();
			InitJson ij = new InitJson(clusteringStrategy, distanceAlgorithm, REPN, CLUSTER_NUM, exDistanceThreshold,
					clusterDistance);
			Clusters cs = ij.converttoClusters(new File(inputJson));
			Answers a = ij.converttoAnswer(new File(answerJson), String.valueOf(ID));

			e.evaluate(cs, a);
		}
	}

	private static void output(Clusters cs) {
		OutputClusters o = new OutputClusters(cs);
		//o.output();
		o.outputSorted();
	}

	private static void printLogs(Evaluate e) {
		System.out.println("------------------");
		System.out.println("Total:");
		e.printAll();
		System.err.println(distanceAlgorithm + ", " + clusteringStrategy + ", Dis" + exDistanceThreshold + ", " + clusterDistance);
//		System.err.println(distanceAlgorithm + ", " + clusteringStrategy + ", " + checkN + ", " + exDistanceThreshold
//		+ ", " + clusterDistance
//		);
		System.err.println("------------------");
		e.printAverage();
	}
}
