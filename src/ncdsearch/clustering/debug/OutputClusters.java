package ncdsearch.clustering.debug;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import ncdsearch.clustering.Clusters;
import ncdsearch.clustering.JsonNodesInfo;

public class OutputClusters {
	private Clusters clusters;

	public OutputClusters(Clusters cs) {
		this.clusters = cs;
	}

	public void output() {
		try {
			BufferedWriter bw = Files.newBufferedWriter(Paths.get("F://ncdsearch/rereresult.txt"),
					StandardOpenOption.APPEND);
			bw.write("---------------------");
			bw.write("\n");
			for (List<JsonNode> list : clusters.getClusterReps()) {

				//			System.out.println("---------------------");
				//			System.out.println("rep:::" + list.get(0));
				//			clusters.getRepJsonMap().get(list.get(0)).forEach(s -> System.out.println(s));

				bw.write("---------------------");
				bw.write("\n");
				//bw.write("rep:::" + list.get(0).get("Tokens").asText());
				bw.write("rep:::" + list.get(0).toString());
				bw.write("\n");
				clusters.getRepJsonMap().get(list.get(0)).forEach(s -> {
					try {
						bw.write(s.toString());
						//bw.write(s.get("Tokens").asText());
						bw.write("\n");
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				});
			}
			bw.write("//////////////////////////////////////////");
			bw.write("\n");
			bw.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}


	public void outputSorted() {
		try {
			BufferedWriter bw = Files.newBufferedWriter(Paths.get("F://ncdsearch/rereresult.txt"),
					StandardOpenOption.APPEND);
			bw.write("---------------------");
			bw.write("\n");
			for (List<JsonNode> list : clusters.getClusterContents()) {
				List<JsonNode> sortedlist= JsonNodesInfo.getSortedListbyDistance(list);
				//			System.out.println("---------------------");
				//			System.out.println("rep:::" + list.get(0));
				//			clusters.getRepJsonMap().get(list.get(0)).forEach(s -> System.out.println(s));

				bw.write("---------------------");
				bw.write("\n");
				//bw.write("rep:::" + list.get(0).get("Tokens").asText());

				sortedlist.forEach(s -> {
					try {
						bw.write(s.toString());
						//bw.write(s.get("Tokens").asText());
						bw.write("\n");
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				});
			}
			bw.write("//////////////////////////////////////////");
			bw.write("\n");
			bw.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}



}
