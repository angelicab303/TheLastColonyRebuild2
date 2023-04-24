package com.mygdx.game;

import java.io.FileReader;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class JSONLevelReader {
    public JSONLevelReader() {
        try {
            // Read the JSON file into a FileReader object
            FileReader fileReader = new FileReader("input.json");

            // Send the fileReader to a new JsonReader object
            JsonReader jsonReader = new JsonReader();
            JsonValue json = jsonReader.parse(fileReader);

            // Get JSON layers of the map
            JsonValue layers = json.get("layers");



            // Loop through each of the layers
            for (int i = 0; i < layers.size; i++) {
                // Loop through the layers data and retrieve each integer value
                for (int j = 0; j < layers.get(i).size; j++) {
                    int dataValue = layers.getInt(j);
                    // Do something with the data value...

                }
            }

//            // Retrieve the values of the keys in the JSONObject
//            int compressionLevel = json.getInt("compressionlevel");
//            int height = json.getInt("height");
//            boolean infinite = json.getBoolean("infinite");
//            JSONArray layersArray = json.getJSONArray("layers");

            // Close the FileReader object
            fileReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
