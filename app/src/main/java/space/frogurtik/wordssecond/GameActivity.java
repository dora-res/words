package space.frogurtik.wordssecond;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GameActivity extends AppCompatActivity {

    public class AudioGame {
        MediaPlayer music_clock;

        public void stop() {
            if (music_clock != null) {
                music_clock.release();
                music_clock = null;
            }
        }

        public void play(Context c) {
            stop();
            music_clock = MediaPlayer.create(c, R.raw.zvuk_chasov);
            music_clock.setLooping(true);
            music_clock.setPlaybackParams(music.music_clock.getPlaybackParams().setSpeed(0.8f));
            music_clock.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });
            music_clock.start();
        }

        public void replay() {
            music.stop();
            music.play(getApplicationContext());
        }

    }

    static final String address = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup";
    static final String api_key = "dict.1.1.20220520T100055Z.eed6bdaf36a87b24.007587d4d27ffd2edf07df063c7d3bd559be2b8b";
    OkHttpClient httpClient;

    String new_word;
    String[] toChar_new_word;
    String flag = "";

    TextView TV_names_of_players, TV_Player_smb_you_hod, TV_spisok_slov, TV_warning, TV_slovo_na_bukvu, TV_timer, TW_typesGame;
    Button BTN_exit, BTN_hod_ready;
    EditText ET_new_word_from_player;
    String all_words_for_TV = "";
    int count_seconds = 120;
    int count_all_words = 0;
    String na_kakuyi_bukvu_slovo = "";
    ArrayList<String> list_of_all_words = new ArrayList<>();
    CountDownTimer countDownTimer;

    String[] spisok_of_gamers;
    String typeOfGame;
    String danger_word = "defulter_wordik";

    AudioGame music = new AudioGame();
    SoundPool musicLoos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        music.play(this);

        musicLoos = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        musicLoos.load(this, R.raw.fail, 1);
        httpClient = new OkHttpClient();

        TW_typesGame = findViewById(R.id.TW_typeGame);
        TV_names_of_players = findViewById(R.id.TW_spisok_people);
        TV_Player_smb_you_hod = findViewById(R.id.Player_smb_you_hod);
        TV_spisok_slov = findViewById(R.id.spisok_slov);
        BTN_exit = findViewById(R.id.exit);
        BTN_hod_ready = findViewById(R.id.hod_ready);
        TV_slovo_na_bukvu = findViewById(R.id.slovo_na_bukvu);
        ET_new_word_from_player = findViewById(R.id.new_word_from_player);
        TV_timer = findViewById(R.id.timer);
        TV_warning = findViewById(R.id.warning);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            spisok_of_gamers = extras.getStringArray("spisok_names");
            typeOfGame = extras.getString("typeOfGame");
            count_seconds = extras.getInt("count_seconds");
            danger_word = extras.getString("danger_word").toUpperCase();
        }

        switch (typeOfGame){
            case "fast": TW_typesGame.setText("Режим игры: Быстрый"); break;
            case "classic": TW_typesGame.setText("Режим игры: Классика"); break;
            case "hard": TW_typesGame.setText("Режим игры: Хард"); break;
            case "danger": TW_typesGame.setText("Режим игры: Опасный"); break;
        }

        countDownTimer = new CountDownTimer(1000L * count_seconds, 1000) {

            @Override
            public void onTick(long l) {
                long seconds = (1000L * count_seconds - l) / 1000;
                TV_timer.setText(seconds / 60 + ":" + seconds % 60);
                if (seconds == Integer.parseInt(String.valueOf(count_seconds * 2 / 3))) {
                    Toast.makeText(getApplicationContext(), "Осталось мало времени!", Toast.LENGTH_SHORT).show();
                    music.music_clock.setPlaybackParams(music.music_clock.getPlaybackParams().setSpeed(1.1f));
                }
            }

            @Override
            public void onFinish() {
                TV_timer.setText(count_seconds / 60 + ":" + count_seconds % 60);
                music.stop();
                musicLoos.play(1, 1, 1, 1, 0, 1f);
                Dialog dg_looser = new Dialog(GameActivity.this);
                dg_looser.setContentView(R.layout.ly_dg_looser);
                TextView maintxt = dg_looser.findViewById(R.id.maintxt);
                maintxt.setText("Игрок " + spisok_of_gamers[count_all_words % spisok_of_gamers.length] + " проиграл!");
                TextView bigtxt = dg_looser.findViewById(R.id.bigtxt);
                bigtxt.setText("К сожалению, вы, игрок " + spisok_of_gamers[count_all_words % spisok_of_gamers.length] + ", проиграли сейчас.\nЗапасайтесь новыми словами и снова в бой!");
                Button from_dg_looser_to_menu = dg_looser.findViewById(R.id.from_dg_looser_to_menu);
                from_dg_looser_to_menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GameActivity.this, MenuActivity.class);
                        startActivity(intent);
                    }
                });
                dg_looser.show();
            }
        };

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < spisok_of_gamers.length - 1; i++) {
            s.append(spisok_of_gamers[i]).append(", ");
        }
        s.append(spisok_of_gamers[spisok_of_gamers.length - 1]);
        TV_names_of_players.setText("Игроки: " + s);

        TV_Player_smb_you_hod.setText("Игрок " + spisok_of_gamers[0] + ", ваш ход");

        countDownTimer.start();
        list_of_all_words.add(" . ");

        int i_alphabet =  new Random().nextInt(30);
        String[] alphabet = {"A", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И", "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Э", "Ю", "Я"};
        na_kakuyi_bukvu_slovo = alphabet[i_alphabet];
        TV_slovo_na_bukvu.setText("Введите слово на букву " + na_kakuyi_bukvu_slovo);

        switch (typeOfGame) {
            case "danger":
                BTN_hod_ready.setOnClickListener(view -> hod_danger());
                break;
            case "hard":
                BTN_hod_ready.setOnClickListener(view -> hod_hard());
                break;
            default:
                BTN_hod_ready.setOnClickListener(view -> hod_classic_or_fast());//classic or fast
        }

        BTN_exit.setOnClickListener(view -> exit_ot_game());

    }

    private void hod_classic_or_fast() {
        new_word = ET_new_word_from_player.getText().toString();
        boolean bad_chars_in_word = new_word.contains(",") || new_word.contains("`") || new_word.contains("~")
                || new_word.contains("/") || new_word.contains("!") || new_word.contains("@") || new_word.contains("#")
                || new_word.contains("№") || new_word.contains("$") || new_word.contains("%") || new_word.contains("^")
                || new_word.contains("&") || new_word.contains("*") || new_word.contains("(") || new_word.contains(")")
                || new_word.contains("\\") || new_word.contains("|") || new_word.contains(" ") || new_word.contains("1")
                || new_word.contains("2") || new_word.contains("3") || new_word.contains("4") || new_word.contains("5")
                || new_word.contains("6") || new_word.contains("7") || new_word.contains("8") || new_word.contains("9")
                || new_word.contains("0") || new_word.contains("+") || new_word.contains("-") || new_word.contains("{")
                || new_word.contains("}") || new_word.contains("[") || new_word.contains("]") || new_word.contains("\"");
        if (TextUtils.isEmpty(new_word)) TV_warning.setText("Вы ничего не написали");
        else if (bad_chars_in_word) TV_warning.setText("В слове есть запрещенные символы. Нельзя использовать следующие символы: \", ~ ` @ ! # № $ % ^ & * ; : ( ) { } [ ] \\ / | пробел и цифры");
        else if (list_of_all_words.contains(new_word.toUpperCase())) TV_warning.setText("Это слово уже было");
        else {
            toChar_new_word = new_word.split("");
            if (toChar_new_word[0].toUpperCase().equals(na_kakuyi_bukvu_slovo)) {
                AsyncWord asyncWord = new AsyncWord();
                asyncWord.execute(address);
            } else TV_warning.setText("Слово не на ту букву");
        }
    }
    private void hod_hard() {
        new_word = ET_new_word_from_player.getText().toString();
        boolean bad_chars_in_word = new_word.contains(",") || new_word.contains("`") || new_word.contains("~") || new_word.contains(".")
                || new_word.contains("/") || new_word.contains("!") || new_word.contains("@") || new_word.contains("#")
                || new_word.contains("№") || new_word.contains("$") || new_word.contains("%") || new_word.contains("^")
                || new_word.contains("&") || new_word.contains("*") || new_word.contains("(") || new_word.contains(")")
                || new_word.contains("\\") || new_word.contains("|") || new_word.contains(" ") || new_word.contains("1")
                || new_word.contains("2") || new_word.contains("3") || new_word.contains("4") || new_word.contains("5")
                || new_word.contains("6") || new_word.contains("7") || new_word.contains("8") || new_word.contains("9")
                || new_word.contains("0") || new_word.contains("+") || new_word.contains("-") || new_word.contains("{")
                || new_word.contains("}") || new_word.contains("[") || new_word.contains("]") || new_word.contains("\"");
        if (TextUtils.isEmpty(new_word)) TV_warning.setText("Вы ничего не написали");
        else if (bad_chars_in_word) TV_warning.setText("В слове есть запрещенные символы. Нельзя использовать следующие символы: \", ~ ` @ ! # № $ % ^ & * ; : ( ) { } [ ] \\ / | пробел и цифры");
        else if (list_of_all_words.contains(new_word.toUpperCase())) TV_warning.setText("Это слово уже было");
        else {
            String[] toChar_new_word = new_word.split("");
            if (toChar_new_word[toChar_new_word.length - 1].toUpperCase().equals(na_kakuyi_bukvu_slovo)) {
                AsyncWord asyncWord = new AsyncWord();
                asyncWord.execute(address);
            } else TV_warning.setText("Слово не на ту букву");
        }
    }
    private void hod_danger() {
        new_word = ET_new_word_from_player.getText().toString();
        if (new_word.toUpperCase().equals(danger_word)) {
            countDownTimer.cancel();
            music.stop();
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int mStreamId = musicLoos.play(1, 1, 1, 1, 0, 1f);
            Dialog dg_looser = new Dialog(GameActivity.this);
            dg_looser.setContentView(R.layout.ly_dg_looser);
            TextView maintxt = dg_looser.findViewById(R.id.maintxt);
            maintxt.setText("Игрок " + spisok_of_gamers[count_all_words % spisok_of_gamers.length] + " проиграл!");
            TextView bigtxt = dg_looser.findViewById(R.id.bigtxt);
            bigtxt.setText("К сожалению, вы, игрок " + spisok_of_gamers[count_all_words % spisok_of_gamers.length] + ", проиграли сейчас.\nЗапасайтесь новыми словами и снова в бой!\n\nСлово-бомба: " + danger_word);
            Button from_dg_looser_to_menu = dg_looser.findViewById(R.id.from_dg_looser_to_menu);
            from_dg_looser_to_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(GameActivity.this, MenuActivity.class);
                    startActivity(intent);
                }
            });
            dg_looser.show();
        } else {
            boolean bad_chars_in_word = new_word.contains(",") || new_word.contains("`") || new_word.contains("~")
                    || new_word.contains("/") || new_word.contains("!") || new_word.contains("@") || new_word.contains("#")
                    || new_word.contains("№") || new_word.contains("$") || new_word.contains("%") || new_word.contains("^")
                    || new_word.contains("&") || new_word.contains("*") || new_word.contains("(") || new_word.contains(")")
                    || new_word.contains("\\") || new_word.contains("|") || new_word.contains(" ") || new_word.contains("1")
                    || new_word.contains("2") || new_word.contains("3") || new_word.contains("4") || new_word.contains("5")
                    || new_word.contains("6") || new_word.contains("7") || new_word.contains("8") || new_word.contains("9")
                    || new_word.contains("0") || new_word.contains("+") || new_word.contains("-") || new_word.contains("{")
                    || new_word.contains("}") || new_word.contains("[") || new_word.contains("]") || new_word.contains("\"");
            if (TextUtils.isEmpty(new_word)) TV_warning.setText("Вы ничего не написали");
            else if (bad_chars_in_word) TV_warning.setText("В слове есть запрещенные символы. Нельзя использовать следующие символы: \", ~ ` @ ! # № $ % ^ & * ; : ( ) { } [ ] \\ / | пробел и цифры");
            else if (list_of_all_words.contains(new_word.toUpperCase())) TV_warning.setText("Это слово уже было");
            else {
                String[] toChar_new_word = new_word.split("");
                if (toChar_new_word[0].toUpperCase().equals(na_kakuyi_bukvu_slovo)) {
                    AsyncWord asyncWord = new AsyncWord();
                    asyncWord.execute(address);
                } else {
                    TV_warning.setText("Слово не на ту букву");
                }
            }
        }
    }

    private void exit_ot_game() {
        music.stop();
        countDownTimer.cancel();
        if (typeOfGame.equals("danger")) {
            Dialog dg_exit_gameDANGER = new Dialog(GameActivity.this);
            dg_exit_gameDANGER.setContentView(R.layout.dg_lay_exitdanger);
            TextView tv_word_bomb = dg_exit_gameDANGER.findViewById(R.id.word_bomb_dg);
            Button btn_from_dg_danger_to_menu = dg_exit_gameDANGER.findViewById(R.id.from_dg_danger_to_menu);
            tv_word_bomb.setText("Слово-бомба: " + danger_word);
            btn_from_dg_danger_to_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(GameActivity.this, MenuActivity.class);
                    startActivity(intent);
                }
            });
            dg_exit_gameDANGER.show();
        } else {
            Intent intent = new Intent(GameActivity.this, MenuActivity.class);
            startActivity(intent);
        }
    }

    class AsyncWord extends AsyncTask<String, Void, Response> {

        @Override
        protected Response doInBackground(String... strings) {
            HttpUrl.Builder urlbilder = HttpUrl.parse(strings[0]).newBuilder();
            urlbilder.addQueryParameter("key", api_key);
            urlbilder.addQueryParameter("lang", "ru-ru");
            urlbilder.addQueryParameter("text", new_word);
            String addr = urlbilder.build().toString();
            Request request = new Request.Builder().url(addr).build();
            Response response = null;
            try {
                response = httpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            if (response != null) {
                Gson gson = new Gson();
                try {
                    if (response.code() == 200) {
                        String s = response.body().string();
                        Word word = gson.fromJson(s, Word.class);
                        if (word.def.length == 0) {
                            TV_warning.setText("Такого слова не существует!");
                        } else {
                            flag = "есть слово";
                            doFlag();//вызов функции обработки результата
                        }
                    } else {
                        TV_warning.setText("Не удалось проверить слово. Придумайте другое слово");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else TV_warning.setText("Не удалось проверить слово. Придумайте другое слово");
        }

        public void doFlag() {
            count_all_words++;
            all_words_for_TV += new_word.toUpperCase() + " - ";
            TV_spisok_slov.setText(all_words_for_TV);
            TV_Player_smb_you_hod.setText("Игрок " + spisok_of_gamers[count_all_words % spisok_of_gamers.length] + ", ваш ход");
            toChar_new_word = new_word.split("");
            if (typeOfGame.equals("classic") || typeOfGame.equals("fast") || typeOfGame.equals("danger")) {
                if (toChar_new_word[toChar_new_word.length - 1].toUpperCase().equals("Ь")
                        || toChar_new_word[toChar_new_word.length - 1].toUpperCase().equals("Ъ")
                        || toChar_new_word[toChar_new_word.length - 1].toUpperCase().equals("Ы"))
                    na_kakuyi_bukvu_slovo = toChar_new_word[toChar_new_word.length - 2].toUpperCase();
                else na_kakuyi_bukvu_slovo = toChar_new_word[toChar_new_word.length - 1].toUpperCase();
                TV_slovo_na_bukvu.setText("Введите слово на букву " + na_kakuyi_bukvu_slovo);
            } else if (typeOfGame.equals("hard")) {
                na_kakuyi_bukvu_slovo = toChar_new_word[0].toUpperCase();
                TV_slovo_na_bukvu.setText("Введите слово с буквой " + na_kakuyi_bukvu_slovo + " на конце");
            }
            ET_new_word_from_player.setText("");
            list_of_all_words.add(new_word.toUpperCase());
            countDownTimer.cancel();
            countDownTimer.start();
            TV_warning.setText("");
            music.replay();
        }

    }


}