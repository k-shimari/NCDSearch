package ncdsearch.evaluate.output;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import ncdsearch.clustering.Answers;
import ncdsearch.clustering.Clusters;

public class OutputResult {
	Clusters clusters;
	Answers answers;
	Clusters filteredClusters;
	String dir;

	public OutputResult(Clusters cs, Answers a, Clusters fcs, String dir) {
		this.clusters = cs;
		this.answers = a;
		this.filteredClusters = fcs;
		this.dir = dir;
	}

	public void print() {

		/*これを参考に
		 * クラスタID（順序関係あり、昇順）を付加
		 * クラスタ内でのランク
		 * 総合ランク
		 *
		 *
		 * */
		//		int index[] = new int[1];
//		index[0] = 1;
//		for (List<JsonNode> list : clusters.getClusterReps()) {
//			clusters.getRepJsonMap().get(list.get(0)).forEach(node -> {
//				((ObjectNode) node).put("clusterID", index[0]);
//			});
//			index[0]++;
//		}


		System.out.println("a");
		Path path = Paths.get(dir, "result-rank.json");
		if (Files.exists(path)) {
			Files.delete(path);
		}
		Files.createFile(path);
		Files.write(path, lines, Charset.forName("UTF-8"),
				StandardOpenOption.WRITE);

	}

}
