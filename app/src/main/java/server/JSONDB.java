package server;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.not;
import static com.mongodb.client.model.Updates.*;

import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import utils.ConfigReader;

interface JSONDB {
  JSONObject remove(JSONObject json);

  JSONObject remove(String key);

  void create(JSONObject json);

  void update(JSONObject json);

  JSONObject read(JSONObject json);

  JSONObject read(String key);

  void addFilter(String key, String val);

  void clearFilters();

  void clear();

  JSONObject toJSON();
}

class MongoJSONDB implements JSONDB {
  private String lookupkey;
  private MongoClient mongoClient;
  private MongoDatabase database;
  private MongoCollection<Document> recipeCollection;
  private Bson filter;

  MongoJSONDB(String collection, String lookupKey) {
    ConfigReader configReader = new ConfigReader();
    this.lookupkey = lookupKey;
    this.mongoClient = MongoClients.create(configReader.getMongoDBURI());
    this.database = mongoClient.getDatabase(configReader.getMongoDBDatabase());
    this.recipeCollection = database.getCollection(collection);
    /* create a catchall filter by finding every entry that doesnt match a long random string */
    clearFilters();
  }

  public JSONObject remove(JSONObject json) {
    return remove(json.getString(lookupkey));
  }

  public JSONObject remove(String key) {
    JSONObject old = read(key);
    recipeCollection.deleteMany(and(filter, eq(lookupkey, key)));
    return old;
  }

  public void clearFilters() {
    /* create a catchall filter by finding every entry that doesnt match a long random string */
    this.filter = not(eq(lookupkey, "kjanfo;ifijo;ijqwpqwejpqwejqwipeqjweqw"));
  }

  public void create(JSONObject json) {
    Document d = Document.parse(json.toString());
    recipeCollection.insertOne(d);
  }

  public void update(JSONObject json) {
    this.remove(json);
    this.create(json);
  }

  public JSONObject read(JSONObject json) {
    return read(json.getString(lookupkey));
  }

  public JSONObject read(String key) {
    // TODO this will throw exception if doesnt exist
    System.out.println("looking up " + key + " in database");
    Document lookup = recipeCollection.find(eq(lookupkey, key)).first();
    if (lookup == null) {
      return null;
    } else {
      return new JSONObject(lookup.toJson());
    }
  }

  public void addFilter(String key, String val) {
    this.filter = and(this.filter, eq(key, val));
  }

  public void clear() {
    recipeCollection.deleteMany(filter);
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    FindIterable<Document> iterable = recipeCollection.find(filter);
    for (Document d : iterable) {
      JSONObject entry = new JSONObject(d.toJson());
      String key = d.getString(lookupkey);
      json.put(key, entry);
    }
    return json;
  }
}
