package org.example; // Ensure this matches your package structure

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.io.FileNotFoundException;

public class YouTubeTrender {

    public static void test1() throws FileNotFoundException {
        System.out.println("Performing Test 1");
        String filename = "youtubedata_malformed.json"; // Adjusted path
        int expectedSize = 50;

        System.out.println("Testing the file: " + filename);
        System.out.println("Expecting size of: " + expectedSize);

        // Read data from resources
        InputStream inputStream = YouTubeTrender.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            System.out.println("File not found!");
            return;
        }

        JsonReader jsonReader = Json.createReader(inputStream);
        JsonObject jobj = jsonReader.readObject();

        // Read the values of the item field
        JsonArray items = jobj.getJsonArray("items");

        System.out.println("Size of input: " + items.size());
        System.out.println("Success: " + (expectedSize == items.size()));
    }

    public static void test2() {
        System.out.println("Performing Test 2");
        String filename = "youtubedata_15_50.json"; // Adjusted path
        int expectedSize = 50;

        System.out.println("Testing the file: " + filename);
        System.out.println("Expecting size of: " + expectedSize);

        // Read data from resources
        InputStream inputStream = YouTubeTrender.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            System.out.println("File not found!");
            return;
        }

        JsonReader jsonReader = Json.createReader(inputStream);
        JsonObject jobj = jsonReader.readObject();

        // Read the values of the item field
        JsonArray items = jobj.getJsonArray("items");

        System.out.println("Size of input: " + items.size());
        System.out.println("Success: " + (expectedSize == items.size()));
    }

    public static void main(String[] args) {
        System.out.println("YouTube Trender Application");

        try {
            test1();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        test2();
    }
}
