package org.jsonMapping;
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
        try {
            JsonNode sourceJsonData =  readSourceJson();
            JsonNode targetJsonData =  readTargetJson();
            if (sourceJsonData == null || targetJsonData == null){
                System.out.println("file Contents are null");
            }
            else{
                System.out.println("source :"+ sourceJsonData.toPrettyString());
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
        }
        catch (Exception e){
            System.out.println("Exception occurred with File");
        }
    }
    private static JsonNode readSourceJson() throws IOException {
        JSONParser jsonParser = new JSONParser();
        JsonNode sourceJsonList = null;
        try
        {
            //Read JSON file
            FileReader reader = new FileReader("src/main/java/org/mapperFile/SourceJson.json");
            ObjectMapper obj = new ObjectMapper();
            sourceJsonList= obj.readTree(reader);
            return sourceJsonList;
        } catch (Exception e) {
            System.out.println("Source File Not Found");
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
            System.out.println("Target File Not Found");
            throw e;
        }
    }
}