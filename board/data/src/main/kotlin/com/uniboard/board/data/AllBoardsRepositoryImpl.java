package com.uniboard.board.data;

import com.uniboard.board.domain.AllBoardsRepository;
import com.mongodb.client.*;
import org.bson.Document;


public class AllBoardsRepositoryImpl implements AllBoardsRepository {
    public String url = "mongodb://localhost:27017";
    public String nameDatabase = "CreatedBoards";
    private static boolean hasCollection(final MongoDatabase db, final String collectionName)
    {
        assert db != null;
        assert collectionName != null && !collectionName.isEmpty();
        try (final MongoCursor<String> cursor = db.listCollectionNames().iterator())
        {
            while (cursor.hasNext())
                if (cursor.next().equals(collectionName))
                    return true;
        }
        return false;
    }
    public AllBoardsRepositoryImpl() {
        MongoClient mongoClient = MongoClients.create(url);
        MongoDatabase database = mongoClient.getDatabase(nameDatabase);
        if (!hasCollection(database, "info")) {
            MongoCollection<Document> collection = database.getCollection("info");
            Document doc = new Document("freeId", (long) 0);
            collection.insertOne(doc);
        }
    }

    private long getFreeId(MongoCollection<Document> info){
        return (long) info.find().first().get("freeId");
    }

    private long updateInfo(MongoCollection<Document> info, MongoDatabase database){
        long lastNumber = getFreeId(info);
        while (hasCollection(database, Long.toString(lastNumber))){lastNumber++;}
        Document query = new Document("freeId", lastNumber);
        info.drop();
        info.insertOne(query);
        return lastNumber;
    }
    @Override
    public long add(){
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase(nameDatabase);
            long lastNumber = updateInfo(database.getCollection("info"), database);
            database.createCollection(Long.toString(lastNumber));
            return lastNumber;
        }
    }
    @Override
    public void delete(long idBoard) {
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("CreatedBoards");
            var todoCollection = database.getCollection(Long.toString(idBoard));
            todoCollection.drop();
        }
    }
    @Override
    public boolean exists(long id) {
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("CreatedBoards");
            return hasCollection(database, Long.toString(id));
        }
    }
}
