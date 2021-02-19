package com.example.klaf.data;

import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.klaf.R;

import java.io.IOException;

public class OnClickAudioPlayer implements View.OnClickListener {
    private static final String URL = "https://wooordhunt.ru/data/sound/sow/us/%s.mp3";
    private final MediaPlayer player;

    public OnClickAudioPlayer() {
        player = new MediaPlayer();
    }

    @Override
    public void onClick(View v) {
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.simple_click);
        v.startAnimation(animation);

        String word = ((TextView) v).getText().toString();
        player.reset();
        try {
            player.setDataSource(String.format(URL, word));
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                v.clearAnimation();
            }
        });

        player.prepareAsync();
    }
}
