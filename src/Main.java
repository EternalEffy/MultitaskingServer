import org.json.JSONArray;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.setPort(3312);
        server.loadFile("test.json","userData");
        server.loadCatalog("Catalog.json","fileInfo");
        server.loadServer();
        server.saveCatalog();
        server.saveFile();
    }
}
