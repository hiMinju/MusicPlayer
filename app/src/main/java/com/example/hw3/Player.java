package com.example.hw3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class Player extends AppCompatActivity {
    private MediaPlayer mPlayer;
    private ArrayList<Music> musicList;
    private int position = 0; // the now position in list
    private TextView title;
    private ImageView albumArt;
    private ProgressBar progressBar;
    private Button btn_play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        title = (TextView)findViewById(R.id.title);
        btn_play = (Button)findViewById(R.id.btn_play);

        Intent intent = getIntent();
        musicList = (ArrayList<Music>)intent.getSerializableExtra("music");

        mPlayer = new MediaPlayer();

        // start the music
        play(musicList.get(position));
    }

    // the method of playing music
    public void play(Music music) {
        try {
            title.setText(music.getTitle());
            mPlayer.reset();
            mPlayer.setDataSource(music.getDataPath());
            mPlayer.prepare();
            mPlayer.start(); // start the song

            if(mPlayer.isPlaying()) {
                btn_play.setBackground(ContextCompat.getDrawable(this, R.drawable.pause));
            }
            else {
                btn_play.setBackground(ContextCompat.getDrawable(this, R.drawable.play));
            }

            // set the album art
            final Uri artwork = Uri.parse("content://media/external/audio/albumart");
            albumArt = (ImageView)findViewById(R.id.imageView);
            Uri albumUri = ContentUris.withAppendedId(artwork, music.getAlbumId());
            Picasso.get().load(albumUri).into(albumArt);

            // Set the progress bar values
            progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setProgress(0);
            progressBar.setMax(100);

            // Update the progress bar

        } catch (IOException e) {
            e.getMessage();
            Toast.makeText(Player.this, "Error in playing", Toast.LENGTH_LONG).show();
        }

    }
}
