package com.reissgrvs.spotifyplaylisttool.Dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.reissgrvs.spotifyplaylisttool.Activities.MultiplaylistActivity;
import com.reissgrvs.spotifyplaylisttool.Activities.PlaylistActivity;
import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import com.reissgrvs.spotifyplaylisttool.Util.MultiplaylistStore;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Playlist;
import retrofit.client.Response;


public class PlaylistAddDialog extends Dialog implements View.OnClickListener {
    public PlaylistAddDialog(@NonNull Context context) {

        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_playlist);

        Button createButton = (Button) findViewById(R.id.button_add_playlist);
        final EditText playlistEditText = (EditText) findViewById(R.id.edit_playlist_name);
        final EditText descriptionEditText = (EditText) findViewById(R.id.edit_playlist_description);
        final CheckBox publicCheck = (CheckBox) findViewById(R.id.check_public);
        final CheckBox multiCheck = (CheckBox) findViewById(R.id.check_multi);

        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createPlaylist(multiCheck.isChecked(), playlistEditText.getText().toString(), descriptionEditText.getText().toString(), publicCheck.isChecked());
            }
        });

    }

    private void createPlaylist(final Boolean multi, String playlistName, String playlistDescription, Boolean publicPlaylist){
        //Set up parameters
        Map<String, Object> options = new HashMap<>();
        options.put("name", playlistName);
        options.put("public", publicPlaylist);
        options.put("description", playlistDescription);
        String userID = TokenStore.getUserId(getContext());

        final Class intentActivity = (multi) ? MultiplaylistActivity.class : PlaylistActivity.class;

        SpotifyAPIManager.getService().createPlaylist(userID, options, new SpotifyCallback<Playlist>() {
            @Override
            public void success(Playlist item, Response response) {

                if (multi) {
                    MultiplaylistStore.addMulti(item.id);}

                Intent startMultiplaylistActivity = new Intent(getContext(), intentActivity);
                startMultiplaylistActivity.putExtra(Intent.EXTRA_SUBJECT, item.id)
                                            .putExtra(Intent.EXTRA_TEXT, item.name)
                                            .putExtra(Intent.EXTRA_USER, TokenStore.getUserId(getContext()));
                getContext().startActivity(startMultiplaylistActivity);
            }

            @Override
            public void failure(SpotifyError error) {
            }
        });

        dismiss();
    }
    @Override
    public void onClick(View v) {
        dismiss();
    }
}
