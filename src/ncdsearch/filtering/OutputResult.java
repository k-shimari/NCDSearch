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
		addElementToJson();
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*@TODO jsonのTotalでのRankのアルゴリズムを考える
	 *
	 * */
	private void addElementToJson() {
		int clusterID = 1;
		int rankTotal = 1;
		for (List<JsonNode> list : clusters.getClusterReps()) {
			int rankInCluster = 1;
			for (JsonNode node : clusters.getRepJsonMap().get(list.get(0))) {
				((ObjectNode) node).put("clusterID", clusterID);
				if (filteredClusters.getAllNode().contains(node)) {
					((ObjectNode) node).put("shouldCheck", "true");
				} else {
					((ObjectNode) node).put("shouldCheck", "false");
				}
				((ObjectNode) node).put("RankInCluster", rankInCluster);
				((ObjectNode) node).put("RankTotal", rankTotal);
				rankInCluster++;
				rankTotal++;
			}
			clusterID++;
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
