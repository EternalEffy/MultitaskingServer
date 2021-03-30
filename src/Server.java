import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class Server implements Runnable{
    private static Socket clientSocket;
    private static DataInputStream inStream;
    private static DataOutputStream outStream;
    private static FileData myData;
    private static FileData myCatalog;
    private static CRUD crud;
    private int index,port;
    private String fileName,catalogName;
    private String requestFormClient;
    private boolean flag = true;
    private int countRequests = 0;
    private byte[] buffer;
    private JSONObject jsonObject;
    private Exception FileNotFound;
    private CatalogCreator creator;
    private String nameClient;
    private ArrayList<Thread> threads;

    public Server(Socket client) {
        clientSocket = client;
    }

    public int loadFile(String fileName,String listName){
        this.fileName = fileName;
        myData = new FileData();
        System.out.println(ServerMessages.MESSAGE_LOAD_FILE);
        try {
            myData.loadJSON(fileName,listName);
        }catch (Exception e){
            return -1;
        }
        return 0;
    }

    public int loadCatalog(String catalogName,String listName){
        this.catalogName = catalogName;
        myCatalog = new FileData();
        System.out.println(catalogName);
        System.out.println(ServerMessages.MESSAGE_LOAD_FILE);
        try {
            myCatalog.loadJSON(catalogName,listName);
            System.out.println(myCatalog.getListName());
        }
        catch (Exception e){
            return -1;
        }
        return 0;
    }

    public int saveFile(){
        System.out.println(ServerMessages.MESSAGE_SAVE_FILE);
        try {
            myData.saveJSON(crud.getJson(), fileName);
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    public int saveCatalog(){
        System.out.println(ServerMessages.MESSAGE_SAVE_FILE);
        try {
            myCatalog.saveJSON(creator.getJson(), catalogName);
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    public void setThreads(ArrayList<Thread> threads) {
        this.threads =threads;
    }

    public void loadServer() {
        crud = new CRUD(myData.getJson());
        creator = new CatalogCreator(myCatalog.getJson());
        System.out.println("Server started");
        System.out.println("Port: "+port);
        try {
            while(!clientSocket.isClosed()) {
                if (clientSocket.isConnected()) {
                    inStream = new DataInputStream(clientSocket.getInputStream());
                    outStream = new DataOutputStream((clientSocket.getOutputStream()));
                    nameClient=inStream.readUTF();
                    System.out.println(ServerMessages.MESSAGE_ACCESS + clientSocket.getInetAddress()+"clientName: "+nameClient);
                    outStream.writeUTF( nameClient+ ServerMessages.USER_MESSAGE_ACCESS);
                    outStream.flush();
                }

                    if(countRequests == 99){
                        if(saveFile()==0){
                            System.out.println(ServerMessages.MESSAGE_USER_INFO + ServerMessages.MESSAGE_RESULT_YES);
                        }
                        else System.out.println(ServerMessages.MESSAGE_USER_INFO + ServerMessages.MESSAGE_RESULT_NO);
                        countRequests=0;
                    }
                    requestFormClient = inStream.readUTF();
                    countRequests++;
                    jsonObject = new JSONObject(requestFormClient);
                    System.out.println(ServerMessages.MESSAGE_REQUEST+jsonObject+index+" "+nameClient);
                    switch (jsonObject.getString("request")) {
                        case Requests.add:
                            index = Integer.parseInt(inStream.readUTF());
                            add(jsonObject);
                            break;
                        case Requests.get:
                            index = Integer.parseInt(inStream.readUTF());
                            get();
                            break;
                        case Requests.edit:
                            index = Integer.parseInt(inStream.readUTF());
                            edit(jsonObject);
                            break;
                        case Requests.remove:
                            index = Integer.parseInt(inStream.readUTF());
                            remove();
                            break;
                        case Requests.getFile:
                            try {
                                System.out.println("Search file");
                                System.out.println("Making file object name: " + jsonObject.getString("name"));
                                File f = new File("data/"+jsonObject.getString("name"));
                                System.out.println("Path: "+f.getPath());
                                if (f.exists()) {
                                    sendFile(f);
                                } else {
                                    System.out.println("Throw exception");
                                    throw FileNotFound;
                                }
                            } catch (Exception e) {
                                outStream.writeUTF(new JSONObject("{\"request\":\"" + ServerMessages.MESSAGE_REQUEST_NO + "\",\"file\":\"0\"}").toString());
                                outStream.flush();
                            }
                            break;
                        case Requests.stop:
                            flag = false;
                            break;
                        default:
                            outStream.writeUTF(ServerMessages.MESSAGE_ERROR);
                            outStream.flush();
                            System.out.println(ServerMessages.MESSAGE_USER_INFO + ServerMessages.MESSAGE_RESULT_NO);
                    }
            }
        }
        catch (InterruptedIOException e){
            System.out.println("Interrupted");
        }
        catch (IOException e) {
            System.out.println(ServerMessages.MESSAGE_CLIENT_CLOSE+nameClient);
            System.out.println(ServerMessages.MESSAGE_END);
        }
    }

    private void add(JSONObject jsonObject){
        try {
            System.out.println(ServerMessages.MESSAGE_ADD);
            crud.add(jsonObject.getJSONArray(myData.getListName()), myData.getListName());
            System.out.println(ServerMessages.MESSAGE_USER_INFO + ServerMessages.MESSAGE_RESULT_YES);
            outStream.writeUTF(ServerMessages.MESSAGE_USER_INFO + crud.get(index, myData.getListName()));
            outStream.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void get(){
        try{
            System.out.println(ServerMessages.MESSAGE_GET+index);
            crud.get(index, myData.getListName());
            System.out.println(ServerMessages.MESSAGE_USER_INFO + ServerMessages.MESSAGE_RESULT_YES);
            outStream.writeUTF(ServerMessages.MESSAGE_USER_INFO + crud.get(index, myData.getListName()));
            outStream.flush();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void edit(JSONObject jsonObject){
        try {
            System.out.println(ServerMessages.MESSAGE_EDIT);
            crud.edit(index, myData.getListName(), jsonObject.getJSONArray(myData.getListName()));
            System.out.println(ServerMessages.MESSAGE_USER_INFO + ServerMessages.MESSAGE_RESULT_YES);
            outStream.writeUTF(ServerMessages.MESSAGE_USER_INFO + crud.get(index, myData.getListName()));
            outStream.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void remove(){
        try {
            System.out.println(ServerMessages.MESSAGE_REMOVE);
            crud.remove(index, myData.getListName());
            System.out.println(ServerMessages.MESSAGE_USER_INFO + ServerMessages.MESSAGE_RESULT_YES);
            outStream.writeUTF(ServerMessages.MESSAGE_USER_INFO + crud.get(index, myData.getListName()));
            outStream.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendFile(File f){
        try {
                System.out.println("File exist. Sending response to client " + nameClient);
                outStream.writeUTF(new JSONObject("{\"request\":\"OK\",\"file\":\"" + f.length() + "\"}").toString());
                System.out.println(nameClient+" currentClient take response from me");
                outStream.flush();
                buffer = new byte[(int) f.length()];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
                bis.read(buffer, 0, buffer.length);
                System.out.println("Start sending file " + f.getName() + " to client " + nameClient);
                System.out.println(nameClient+" currentClient take file");
                outStream.write(buffer, 0, buffer.length);
                outStream.flush();
                System.out.println("File was send successful to client " + nameClient);
                inStream.close();
                outStream.close();
        }catch (InterruptedIOException e){
            System.out.println("interrupted in SEND FILE");
        }
        catch (IOException e){
            System.out.println("Send file ERROR");
        }
    }


    @Override
    public void run() {
        loadFile("test.json", "userData");
        loadCatalog("Catalog.json", "fileInfo");
        loadServer();
        saveCatalog();
        saveFile();
    }
}

