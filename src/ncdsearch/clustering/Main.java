package ncdsearch.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ncdsearch.clustering.debug.OutputClusters;
import ncdsearch.evaluate.Evaluate;
import ncdsearch.evaluate.IdealEvaluate;

public class Main {
	private static String clusteringStrategy = "SH";
	private static String distanceAlgorithm = "tld";
	private static final int TOPN = 3;

	public static void main(String[] args) {
		try {
			BufferedWriter bw = Files.newBufferedWriter(Paths.get("F://ncdsearch/rereresult.txt"));
			bw.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		//String ID= args[1];
		//String ID = "4";
		if (args.length > 1)
			setStrategy(args[1]);
		callEvaluate(args[0]);
		//callIdealEvaluate(args[0]);
	}

	private static void callEvaluate(String path) {
		Evaluate e = new Evaluate(TOPN);
		evaluate(path, e);
		//e.printAverage();
		printLogs(e);
	}

	private static void callIdealEvaluate(String path) {
		IdealEvaluate e = new IdealEvaluate(TOPN);
		evaluate(path, e);
		printLogs(e);
	}

	private static void evaluate(String path, Evaluate e) {
		for (int ID = 1; ID <= 53; ID++) {
			System.out.println("------------------");
			System.out.println("ID:" + ID);
			String answerJson = Paths.get(path, ("queries.json")).toAbsolutePath().toString();
			String inputJson = Paths.get(path, ("result/zip-0.5-fast-k0-" + ID + ".json")).toAbsolutePath()
					.toString();
			InitJson ij = new InitJson(clusteringStrategy, distanceAlgorithm, TOPN);
			Clusters cs = ij.converttoClusters(new File(inputJson));
			Answers a = ij.converttoAnswer(new File(answerJson), String.valueOf(ID));

			output(cs);

			e.evaluate(cs, a);
		}
	}

	private static void evaluate(String path, IdealEvaluate e) {
		for (int ID = 1; ID <= 32; ID++) {
			System.out.println("------------------");
			System.out.println("ID:" + ID);
			String answerJson = Paths.get(path, ("queries.json")).toAbsolutePath().toString();
			String inputJson = Paths.get(path, ("result/zip-0.5-fast-k0-" + ID + ".json")).toAbsolutePath()
					.toString();
			InitJson ij = new InitJson(clusteringStrategy, distanceAlgorithm, TOPN);
			Clusters cs = ij.converttoClusters(new File(inputJson));
			Answers a = ij.converttoAnswer(new File(answerJson), String.valueOf(ID));

			e.evaluate(cs, a);
		}
	}

	private static void output(Clusters cs) {
		OutputClusters o = new OutputClusters(cs);
		o.output();
	}



	private static void printLogs(Evaluate e) {
		System.out.println("------------------");
		System.out.println("Total:");
		e.printAll();
		System.out.println("------------------");
		e.printAverage();
	}

	private static void setStrategy(String s) {
		clusteringStrategy = s;
	}
}