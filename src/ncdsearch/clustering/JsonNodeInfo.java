package ncdsearch.clustering;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeInfo {
	public static String getNodeDir(JsonNode node) {
		String fullDir = node.get("DirName").asText();
		return fullDir.substring(fullDir.indexOf("src") + 4).replace("\\", "/");

	}

	public static String getNodeFile(JsonNode node) {
		String fullFile = node.get("FileName").asText();
		return fullFile.substring(fullFile.indexOf("src") + 4).replace("\\", "/");
	}

	public static String getNodeAnswerFile(JsonNode node) {

		return node.get("path").asText() + "/" + node.get("file").asText().toString();
	}


	public static double getNodeDistance(JsonNode node) {
		return node.get("Distance").asDouble();
	}

	public static int getNodeStartLine(JsonNode node) {
		return node.get("StartLine").asInt();
	}

	public static int getNodeEndLine(JsonNode node) {
		return node.get("EndLine").asInt();
	}

	public static int getNodeSLine(JsonNode node) {
		return node.get("sline").asInt();
	}

	public static int getNodeELine(JsonNode node) {
		return node.get("eline").asInt();
	}



}
