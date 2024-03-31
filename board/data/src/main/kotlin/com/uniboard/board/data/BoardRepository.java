package org.example;

import com.mongodb.DBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BoardRepository {
    public String url = "mongodb://localhost:27017";
    public String nameDatabase = "CreatedBoards";
    MongoClient mongoClient;

    public BoardRepository() {
        mongoClient = MongoClients.create(url);
    }

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


    public BoardObject fromDocumentToBoardObject(Document document){
        BoardObject boardObject = new BoardObject();
        boardObject.id = (String) document.get("id");
        boardObject.state = (String) document.get("state");
        return boardObject;
    }


    public List<BoardObject> all(long boardId){
        MongoDatabase database = mongoClient.getDatabase(nameDatabase);
        MongoCollection<Document> collection = database.getCollection(Long.toString(boardId));
        List<Document> documents = collection.find().into(new ArrayList<>());
        return documents.stream().map(this::fromDocumentToBoardObject).collect(Collectors.toList());
    }


    public void add(long boardId, BoardObject element) {
        MongoDatabase database = mongoClient.getDatabase(nameDatabase);
        MongoCollection<Document> collection = database.getCollection(Long.toString(boardId));
        Document doc = new Document("id", element.id).append("state", element.state);
        collection.insertOne(doc);
    }


    public String get(long boardId, String id){
        MongoDatabase database = mongoClient.getDatabase(nameDatabase);
        MongoCollection<Document> collection = database.getCollection(Long.toString(boardId));
        return (String) collection.find(Filters.eq("id", id)).first().get("state");
    }


    public void delete(long boardId, String id){
        MongoDatabase database = mongoClient.getDatabase(nameDatabase);
        MongoCollection<Document> collection = database.getCollection(Long.toString(boardId));
        collection.deleteOne(Filters.eq("id", id));
    }

    public void set(long boardId, BoardObject element) {
        MongoDatabase database = mongoClient.getDatabase(nameDatabase);
        MongoCollection<Document> collection = database.getCollection(Long.toString(boardId));
        delete(boardId, element.id);
        add(boardId, element);
    }
}
