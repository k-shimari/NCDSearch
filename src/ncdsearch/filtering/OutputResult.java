package ncdsearch.filtering;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ncdsearch.clustering.Answers;
import ncdsearch.clustering.Clusters;

public class OutputResult {
	Clusters clusters;
	Answers answers;
	Clusters filteredClusters;
	String dir;

	public OutputResult(Clusters cs, Clusters fcs, String dir) {
		this.clusters = cs;
		this.filteredClusters = fcs;
		this.dir = dir;
	}

	public OutputResult(Clusters cs, Answers a, Clusters fcs, String dir) {
		this.clusters = cs;
		this.answers = a;
		this.filteredClusters = fcs;
		this.dir = dir;
	}

	public void print() {

		/* クラスタID（順序関係あり，昇順）
		 * クラスタ内でのランク
		 * 総合ランク
		 * filteredか否か
		 * を追加
		 * */






		int index[] = new int[1];
		index[0] = 1;


		for (List<JsonNode> list : clusters.getClusterReps()) {
			clusters.getRepJsonMap().get(list.get(0)).forEach(node -> {
				((ObjectNode) node).put("ClusterID", index[0]);
				if (filteredClusters.getAllNode().contains(node)) {
					((ObjectNode) node).put("ShouldCheck", "true");
				} else {
					((ObjectNode) node).put("ShouldCheck", "false");
				}
			});
			index[0]++;
		}










		for (List<JsonNode> list : clusters.getClusterReps()) {
			clusters.getRepJsonMap().get(list.get(0)).forEach(node -> {
				((ObjectNode) node).put("clusterID", index[0]);
				if (filteredClusters.getAllNode().contains(node)) {
					((ObjectNode) node).put("shouldCheck", "true");
				} else {
					((ObjectNode) node).put("shouldCheck", "false");
				}
			});
			index[0]++;
		}
		System.out.println("a");
		ResultJson rj = new ResultJson(clusters.getAllNode());
		try {
			ObjectMapper mapper = new ObjectMapper();
			List<String> lines = new ArrayList<>();
			lines.add(mapper.writeValueAsString((Object) rj));
			Path path = Paths.get(dir, "result-rank.json");
			if (Files.exists(path)) {
				Files.delete(path);
			}
			Files.createFile(path);
			Files.write(path, lines, Charset.forName("UTF-8"), StandardOpenOption.WRITE);

			//
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public class ResultJson {
		List<JsonNode> Result;

		public ResultJson(List<JsonNode> Result) {
			this.Result = Result;
		}

		public List<JsonNode> getResult() {
			return Result;
		}
	}
}
