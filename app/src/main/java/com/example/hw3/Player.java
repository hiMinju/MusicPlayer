package com.example.hw3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mPlayer;
    private ArrayList<Music> musicList;
    private int position = 0; // the now position in list

    private TextView title;
    private ImageView albumArt;
    private ProgressBar progressBar;
    private Button btn_play;
    private TextView now_time;
    private TextView total_time;

    private Handler mHandler = new Handler();
    private ConvertTime times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        title = (TextView)findViewById(R.id.title);
        btn_play = (Button)findViewById(R.id.btn_play);
        now_time = (TextView)findViewById(R.id.now_time);
        total_time = (TextView)findViewById(R.id.total_time);

        Intent intent = getIntent();
        musicList = (ArrayList<Music>)intent.getSerializableExtra("music");

        mPlayer = new MediaPlayer();
        times = new ConvertTime();
        position = intent.getIntExtra("index", 0);

        mPlayer.setOnCompletionListener(this);

        // start the music
        play();
    }

    // click event handler
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
            {
                if(position < (musicList.size()-1)) {
                    position += 1;
                }
                else {
                    position = 0;
                }
                play();
                break;
            }
            case R.id.btn_prev:
            {
                if(position > 0) {
                    position -= 1;
                }
                else {
                    position = musicList.size() - 1;
                }
                play();
                break;
            }
            case R.id.btn_play:
            {
                enterPlayBtn();
                break;
            }
        }
    }

    // the method of playing music
    public void play() {
        try {
            title.setText(musicList.get(position).getTitle());
            mPlayer.reset();
            mPlayer.setDataSource(musicList.get(position).getDataPath());
            mPlayer.prepare();

            if(mPlayer == null) {
                Toast.makeText(Player.this, "player is null", Toast.LENGTH_LONG).show();
                return;
            }

            // play or pause the music
            enterPlayBtn();

            // set the album art
            final Uri artwork = Uri.parse("content://media/external/audio/albumart");
            albumArt = (ImageView)findViewById(R.id.imageView);
            Uri albumUri = ContentUris.withAppendedId(artwork, musicList.get(position).getAlbumId());
            Picasso.get().load(albumUri).into(albumArt);

            // Set the progress bar values
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setProgress(0);
            progressBar.setMax(100);

            // Update the progress bar
            updateProgressBar();
        } catch (IOException e) {
            e.getMessage();
            Toast.makeText(Player.this, "Error in playing", Toast.LENGTH_LONG).show();
        }
    }

    public void enterPlayBtn() {
        if(mPlayer.isPlaying()) {
            btn_play.setBackground(ContextCompat.getDrawable(this, R.drawable.play));
            mPlayer.pause();
        }
        else {
            btn_play.setBackground(ContextCompat.getDrawable(this, R.drawable.pause));
            mPlayer.start(); // start the song
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mPlayer.getDuration();
            long currentDuration = mPlayer.getCurrentPosition();

            // Displaying Total Duration time
            total_time.setText(""+times.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            now_time.setText(""+times.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(times.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            progressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(position < (musicList.size()-1)) {
            position += 1;
        }
        else {
            position = 0;
        }
        play();
    }
}
