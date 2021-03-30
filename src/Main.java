
public class Main {
    public static void main(String[] args) {
        MultithreadedServer server = new MultithreadedServer(3312);
        server.startMultithreadedServer();
    }
}
