package com.reissgrvs.spotifyplaylisttool.Util;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MultiplaylistStore {

    //TODO: Make files user specific
    static private String FILENAME = "multistore.ser";
    static private HashMap<String, ArrayList<String>> multiplaylistStore;


    static public void loadMultiPlaylistFile(Context mContext){
        try {
            FileInputStream fileInputStream = new FileInputStream(mContext.getFilesDir()+"/"+ FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            multiplaylistStore = (HashMap)objectInputStream.readObject();
        }
        catch(ClassNotFoundException | IOException | ClassCastException e) {
            multiplaylistStore = new HashMap<>();
            e.printStackTrace();
        }

    }

    static public Set<String> getPlaylistIDs(){
        return multiplaylistStore.keySet();
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

    static public void addToMulti(String playlistID, ArrayList<String> childPlaylists, Context context)
    {
        loadMultiPlaylistFile(context);
        multiplaylistStore.put(playlistID,childPlaylists);
        saveMultiPlaylistFile(context);
    }

    static public  void removeFromMulti(String playlistID, String childPlaylist, Context context){
        loadMultiPlaylistFile(context);
        multiplaylistStore.get(playlistID).remove(childPlaylist);
        saveMultiPlaylistFile(context);
    }

    static public ArrayList<String> getMulti(String playlistID){
        return multiplaylistStore.get(playlistID);
    }

    static public boolean isMulti(String playlistID){
        return multiplaylistStore.containsKey(playlistID);
    }
}
