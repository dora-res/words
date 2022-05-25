package space.frogurtik.wordssecond;

import static android.view.View.GONE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button btn_to_start_game;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_to_start_game = findViewById(R.id.btn_to_start_game);
        progress_bar = findViewById(R.id.progress_bar);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    //TimeUnit.SECONDS.sleep(new Random().nextInt(7)+3);
                    int i = new Random().nextInt(5)+2;
                    btn_to_start_game.setVisibility(GONE);
                    btn_to_start_game.postDelayed(new Runnable() {
                        public void run() {
                            btn_to_start_game.setVisibility(View.VISIBLE);
                        }
                    }, i*1000);
                    TimeUnit.MILLISECONDS.sleep(i*1000-500);
                    progress_bar.setVisibility(View.INVISIBLE);
                    btn_to_start_game.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MainActivity.this, MenuActivity.class));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }
}