package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ramcin on 2017-04-24.
 */

final class DataManager {
    static void saveState(Context context, String filename, Serializable... objects) {
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;

        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);

            for (Serializable o : objects) {
                // OBS! Order matters for writing and reading
                objectOutputStream.writeObject(o);
            }

            // OBS! OBS! null is written to mark the end-of-file
            objectOutputStream.writeObject(null);

            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static List<Object> readState(Context context, String filename) {
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;
        List<Object> objects = null;

        try {
            fileInputStream = context.openFileInput(filename);
            objectInputStream = new ObjectInputStream(fileInputStream);
            objects = new ArrayList<>();
            Object o;

            // OBS! OBS! null is the end-of-file marker; it was written for this purpose
            while ((o = objectInputStream.readObject()) != null) {
                // OBS! Order matters for writing and reading
                objects.add(o);
            }

            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }

    // Create and save game components
    static void createUser(Context context, String filename) {
        Tree tree = new Tree();
        Float distance = new Float(0);
        // TODO: get coordinates from LocationManager
        double LONGITUDE = 17.951595;
        double LATITUDE = 59.404890;
        Environment environment = new Environment(LATITUDE, LONGITUDE);

        // IMPORTANT: ORDER MATTERS
        saveState(context, filename, tree, distance, environment);
    }
}
