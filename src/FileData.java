import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileData {


    private JSONObject json;
    private String fileName;
    private String listName;
    private byte[] b;

    public String getListName() {
        return listName;
    }

    public int loadJSON(String fileName,String listName) {
        this.fileName = fileName;
        this.listName = listName;
        try {
            json = new JSONObject(new BufferedReader( new FileReader(fileName)).readLine());
        } catch (FileNotFoundException e) {
            try {
                Files.createFile(Paths.get(fileName));
                FileWriter file = new FileWriter(fileName);
                file.write(new JSONObject("{\"" + listName + "\":[]}").toString());
                file.flush();
                file.close();
                json = new JSONObject(new BufferedReader( new FileReader (fileName)).readLine());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
        return 0;
    }

    public int saveJSON(JSONObject json,String fileName) {
        try {
            new FileOutputStream(fileName).write(json.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
        return 0;
    }

    public JSONObject getJson(){
        return json;
    }

}
