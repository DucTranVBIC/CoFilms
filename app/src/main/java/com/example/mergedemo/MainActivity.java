package com.example.mergedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;

import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appendTwoVideos();
    }

    private String appendTwoVideos(){
        try {
            Movie[] inMovies = new Movie[2];

            inMovies[0] = MovieCreator.build("android.resource://" + getPackageName()+"/"+R.raw.video1); // Video 1: Question
            inMovies[1] = MovieCreator.build("android.resource://" + getPackageName()+"/"+R.raw.video2); // Video 2: Reply (Answer)

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("sound")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("video")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {

                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(result);

            @SuppressWarnings("resource")
            FileChannel fc = new RandomAccessFile(Environment.getExternalStorageDirectory() + "/video3.mp4", "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/video3.mp4";
        return mFileName;
    }
}