import com.mongodb.MongoClient;

/**
 * Created by ai-lab on 27/07/16.
 */
public class MongoConnection {

    private static MongoConnection instance = null;
    private MongoClient client;

    private MongoConnection() {
        this.client = new MongoClient();
    }

    public static synchronized MongoConnection getInstance() {
        if (instance == null)
            instance = new MongoConnection();
        return instance;
    }

    public synchronized MongoClient getClient() {
        return this.client;
    }

    public synchronized void close() {
        this.client.close();
        instance = null;
    }
}
