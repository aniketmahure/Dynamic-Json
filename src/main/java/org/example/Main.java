package org.example;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode sourceJsonData =  readSourceJson();
        System.out.println("source :"+ sourceJsonData.toPrettyString());
        JsonNode targetJsonData =  readTargetJson();
        System.out.println("initial target :"+targetJsonData.toPrettyString());

        Iterator<String> itr = targetJsonData.fieldNames();
        Map targetKeyMap = new LinkedHashMap<>();
        while (itr.hasNext()) {  //to get the key fields
            String key_field = itr.next();
            targetKeyMap.put(targetJsonData.get(key_field).toString(), key_field.toString());
        }

        //Fetching values from source
        JSONObject jsonObject = new JSONObject();
        FileWriter file = new FileWriter("src/main/java/org/mapperFile/TargetJson.json");
        for (JsonNode a: targetJsonData){
            int levelCount = Arrays.stream(a.toString().split("\\.|\"")).toList().size()-1;
            //Mapping Values if level of Json is 2 in Target Json
            if (levelCount > 2){
                JsonNode sourceValue = sourceJsonData.get(Arrays.stream(a.toString().split("\\.|\"")).toList().get(levelCount-1)).get(Arrays.stream(a.toString().split("\\.|\"")).toList().get(levelCount));

                jsonObject.put(targetKeyMap.get(a.toString()),sourceValue);
            }
            //Mapping Values if level of Json is 1 in Target Json
            else {
                jsonObject.put(2,sourceJsonData.get(Arrays.stream(a.toString().split("\\.|\"")).toList().get(levelCount)));
            }
        }

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        System.out.println("final target :"+json);
        file.write(json);
        file.close();
    }
    private static JsonNode readSourceJson() throws Exception {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("src/main/java/org/mapperFile/SourceJson.json"))
        {
            //Read JSON file
            ObjectMapper obj = new ObjectMapper();
            JsonNode sourceJsonList= obj.readTree(reader);
            return sourceJsonList;
        } catch (IOException e) {
            throw e;
        }
    }
    private static JsonNode readTargetJson() throws Exception {
        try (FileReader reader = new FileReader("src/main/java/org/mapperFile/TargetJson.json"))
        {
            //Read Target JSON file
            ObjectMapper obj = new ObjectMapper();
            JsonNode targetJsonList= obj.readTree(reader);
            return targetJsonList;
        } catch (IOException e) {
            throw e;
        }
    }
}