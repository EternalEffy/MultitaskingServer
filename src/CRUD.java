import org.json.JSONArray;
import org.json.JSONObject;

public class CRUD {
    private JSONObject json;

    public CRUD(JSONObject json){
        this.json = json;
    }

    public JSONObject getJson(){
        return json;
    }

    public int add(JSONArray userInfo, String listName){
        if(userInfo.equals(null)) return -1;
        json.getJSONArray(listName).put(json.getJSONArray(listName).length(),
                new JSONObject(new User(userInfo.getString(0), userInfo.getString(1), userInfo.getString(2), userInfo.getString(3))));
        return 0;
    }

    public String get(int index, String listName){
        return index>json.getJSONArray(listName).length()-1? null : json.getJSONArray(listName).getJSONObject(index).toString();
    }

    public int edit(int index, String listName, JSONArray userInfo){
        if(index>json.getJSONArray(listName).length()-1)  return -1;
        if(userInfo.equals(null)) return -1;
        json.getJSONArray(listName).put(index,new JSONObject(new User(userInfo.getString(0), userInfo.getString(1), userInfo.getString(2), userInfo.getString(3))));
        return index;
    }

    public int remove(int index,String listName){
        if(index>json.getJSONArray(listName).length()) return -1;

        json.getJSONArray(listName).remove(index);
        return index;
    }

}