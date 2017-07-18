package com.reissgrvs.spotifyplaylisttool.Util;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Reiss on 26/06/2017.
 */

public class MultiPlaylistStore {

    //TODO: Make files user specific
    static String FILENAME = "multistore.ser";
    static private HashMap<String, ArrayList<String>> multiplaylistStore;


    static public void loadMultiPlaylistFile(Context mContext){
        try {
            FileInputStream fileInputStream = new FileInputStream(mContext.getFilesDir()+"/"+ FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            multiplaylistStore = (HashMap)objectInputStream.readObject();
        }
        catch(ClassNotFoundException | IOException | ClassCastException e) {
            //TODO: Maybe a more elegant way of doing this
            multiplaylistStore = new HashMap<>();
            e.printStackTrace();
        }

    }

    static public void saveMultiPlaylistFile(Context mContext){
        try {
            FileOutputStream fos = mContext.openFileOutput( FILENAME , Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(multiplaylistStore);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void addMulti(String playlistID){
        multiplaylistStore.put(playlistID, new ArrayList<String>());
    }

    static public void addToMulti(String playlistID, ArrayList<String> childPlaylists, Context mContext)
    {
        loadMultiPlaylistFile(mContext);
        multiplaylistStore.put(playlistID,childPlaylists);
        saveMultiPlaylistFile(mContext);
    }

    static public ArrayList<String> getMulti(String playlistID){
        Log.d("getMulti", "fetched multi with children: " + multiplaylistStore.get(playlistID).toString() );
        return multiplaylistStore.get(playlistID);
    }

    static public boolean isMulti(String playlistID){
        Log.d("isMulti", playlistID);
        return multiplaylistStore.containsKey(playlistID);
    }
}
