public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.loadFile("test.json");
        server.setPort(3312);
        server.loadServer();
        server.saveFile();
    }
}
