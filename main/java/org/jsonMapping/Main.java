package org.jsonMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.gson.*;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
	static Map sourceKeyValueMap = new LinkedHashMap();
	static Map targetKeyValueMap = new LinkedHashMap();
	static Map targetSourceMap = new LinkedHashMap();
	static boolean targetFlag = false, finalKey = false;
	private static final ObjectMapper mapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		try {
			String sourceFile = "src/main/java/org/mapperFile/SourceJson.json";
			String targetFile = "src/main/java/org/mapperFile/TargetJson.json";
			Map finalKeyValueMap = new LinkedHashMap();
			// source call
			readJson(sourceFile);
			System.out.println("\n sourceKeyValueMap =\n" + sourceKeyValueMap);
			System.out.println("\n");
			// target Call
			targetFlag = true;
			readJson(targetFile);
			System.out.println("\ntargetSourceMap = \n" + targetSourceMap);
			System.out.println("\n TargetKeyValueMap =\n" + targetKeyValueMap);
			if (sourceKeyValueMap.isEmpty() || targetSourceMap.isEmpty()) {
				System.out.println("file Contents are null");
			} else {
				Set keys = targetSourceMap.keySet();
				// printing the elements with keys of Target KeyValue Map
				for (Object key : keys) {
					String targetValue = Arrays.stream(targetSourceMap.get(key).toString().split("\\$.")).toList()
							.get(1);
					finalKeyValueMap.put(key, sourceKeyValueMap.get(targetValue));
				}
				System.out.println("\n finalKeyValueMap : \n" + finalKeyValueMap);

				//WRITING TO OUTPUT FILE
				FileReader read = new FileReader(targetFile);
				JSONTokener tokener = new JSONTokener(read);
				Object json = new org.json.JSONObject(tokener);

				JSONIterator jsonIterator = new JSONIterator();
				Object obj=jsonIterator.iterateJSON(json,finalKeyValueMap);

				Gson gson = new GsonBuilder().setPrettyPrinting().create();

				JsonElement je =  com.google.gson.JsonParser.parseString(obj.toString());
				String prettyJsonString = gson.toJson(je);

				FileWriter file = new FileWriter("src/main/java/org/mapperFile/outputJson.json");
				file.write(prettyJsonString);
				System.out.println(prettyJsonString);
				file.close();
			}
		} catch (Exception e) {
			System.out.println("Exception occurred with File");
		}
	}
	private static void traverse(JsonNode node, StringBuilder sb) {
		if (node.getNodeType() == JsonNodeType.OBJECT) {
			traverseObject(node, sb);
		} else if (node.getNodeType() == JsonNodeType.ARRAY) {
			traverseArray(node, sb);
		} else {
			throw new RuntimeException("Not yet implemented");
		}
	}
	private static void traverseArray(JsonNode node, StringBuilder sb) {
		if (node.fieldNames().hasNext()) {
			traverseObject(node, sb);
		} else {
			for (JsonNode j : node) {
				traverseObject(j, sb);
			}
		}
	}
	private static boolean traversable(JsonNode node) {
		return node.getNodeType() == JsonNodeType.OBJECT || node.getNodeType() == JsonNodeType.ARRAY;
	}
	private static void printNode(JsonNode node, String keyName, StringBuilder sb) {
		if (traversable(node)) {
			sb.append(keyName + ".");
		} else {
			sb.append(keyName);
			// adding keys and values to the Source Key-Value Map
			if (!targetFlag) {
				sourceKeyValueMap.put(sb.toString(),
						node.getNodeType() == JsonNodeType.NUMBER ? node.longValue() : node.textValue());
			} else {
				targetKeyValueMap.put(sb.toString(), node);
			}
		}
	}
	private static void traverseObject(JsonNode node, StringBuilder sb) {
		node.fieldNames().forEachRemaining((String fieldName) -> {
			// System.out.println("node : "+node+ "| fieldname : "+fieldName);
			StringBuilder sb2 = new StringBuilder(sb);
			JsonNode childNode = node.get(fieldName);
			printNode(childNode, fieldName, sb2);
			// checking if childNode is further traversable or not
			if (traversable(childNode)) {
				traverse(childNode, sb2);
			}
			// adding keys and values to the Target Key-Value Map
			if (targetFlag && (childNode.getNodeType() != JsonNodeType.ARRAY
					&& childNode.getNodeType() != JsonNodeType.OBJECT))
				targetSourceMap.put(fieldName,
						childNode.getNodeType() == JsonNodeType.NUMBER ? childNode.longValue() : childNode.textValue());
		});
	}
	private static JsonNode readJson(String file) throws IOException {
		JSONParser jsonParser = new JSONParser();
		JsonNode sourceJsonList = null;
		try {
			// Read JSON file
			FileReader reader = new FileReader(file);
			ObjectMapper obj = new ObjectMapper();
			sourceJsonList = obj.readTree(reader);
			StringBuilder sb = new StringBuilder();
			traverse(sourceJsonList, sb);
			return sourceJsonList;
		} catch (Exception e) {
			System.out.println("File Not Found " + file);
			throw e;
		}
	}
}