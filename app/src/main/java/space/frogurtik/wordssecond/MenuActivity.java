package space.frogurtik.wordssecond;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MenuActivity extends AppCompatActivity {

    static final String address = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup";
    static final String api_key = "dict.1.1.20220520T100055Z.eed6bdaf36a87b24.007587d4d27ffd2edf07df063c7d3bd559be2b8b";
    OkHttpClient httpClient;

    Button   BTN_to_classic, BTN_to_hard, BTN_to_danger, BTN_to_fast, BTN_time_is_ready, BTN_ready_on_danger;
    EditText ED_names_of_players, ED_count_seconds, ED_danger_word;

    TextView TV_warning, TV_warning_dg_danger, BTN_mainRules, BTN_dopRules;

    Dialog DG_rulesForEach, dg_to_fast, dg_to_danger;
    String typeOfGame = "null";

    String danger_word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        httpClient = new OkHttpClient();

        BTN_dopRules = findViewById(R.id.btn_dopRules);
        BTN_mainRules = findViewById(R.id.btn_mainRules);
        BTN_to_classic = findViewById(R.id.btn_to_classic);
        BTN_to_danger = findViewById(R.id.btn_to_danger);
        BTN_to_fast = findViewById(R.id.btn_to_fast);
        BTN_to_hard = findViewById(R.id.btn_to_hard);
        ED_names_of_players = findViewById(R.id.names_of_players);
        TV_warning = findViewById(R.id.warning);

        dg_to_fast = new Dialog(MenuActivity.this);
        dg_to_fast.setContentView(R.layout.dg_to_fast);                        //fast
        ED_count_seconds = dg_to_fast.findViewById(R.id.count_seconds);
        BTN_time_is_ready = dg_to_fast.findViewById(R.id.btn_time_is_ready);

        dg_to_danger = new Dialog(MenuActivity.this);
        dg_to_danger.setContentView(R.layout.dg_danger);                        //danger
        ED_danger_word = dg_to_danger.findViewById(R.id.et_word_bomb);
        BTN_ready_on_danger = dg_to_danger.findViewById(R.id.btn_ready_on_danger);
        TV_warning_dg_danger = dg_to_danger.findViewById(R.id.warning_dg_danger);

        BTN_dopRules.setOnClickListener(view -> rulesForEach());
        BTN_mainRules.setOnClickListener(view -> rulesMain());

        BTN_to_classic.setOnClickListener(view -> to_classic());
        BTN_to_hard.setOnClickListener(view -> to_hard());
        BTN_to_fast.setOnClickListener(view -> to_fast());
        BTN_to_danger.setOnClickListener(view -> to_danger());

    }

    void rulesForEach(){
        DG_rulesForEach = new Dialog(MenuActivity.this);
        DG_rulesForEach.setContentView(R.layout.dg_rules_for_each_layout);
        DG_rulesForEach.show();
    }
    void rulesMain(){
        Dialog DG_mainRules = new Dialog(MenuActivity.this);
        DG_mainRules.setContentView(R.layout.dg_mainrules);
        DG_mainRules.show();
    }

    void to_classic(){
        String s = ED_names_of_players.getText().toString();
        if (TextUtils.isEmpty(s)) TV_warning.setText("Не все необходимые данные заполнены");
        else{
            String[] arr = s.split(" ");
            TreeSet<String> arr_new = new TreeSet<>(Arrays.asList(arr));
            if (arr.length != arr_new.size()) TV_warning.setText("Имена совпадают у кого-то! Поменяйте пожалуйста");
            else{
                if (arr.length == 1) TV_warning.setText("К сожалению, нельзя играть одному :( ");
                else {
                    TV_warning.setText("");
                    typeOfGame = "classic";
                    int count_seconds = 120;
                    Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                    intent.putExtra("spisok_names", arr);
                    intent.putExtra("typeOfGame", typeOfGame);
                    intent.putExtra("count_seconds", count_seconds);
                    intent.putExtra("danger_word", "null");
                    startActivity(intent);
                }
            }
        }
    }
    void to_hard(){
        String s = ED_names_of_players.getText().toString();
        if (TextUtils.isEmpty(s)) TV_warning.setText("Не все необходимые данные заполнены");
        else{
            String[] arr = s.split(" ");
            TreeSet<String> arr_new = new TreeSet<>(Arrays.asList(arr));
            if (arr.length != arr_new.size()) TV_warning.setText("Имена совпадают у кого-то! Поменяйте пожалуйста");
            else{
                if (arr.length == 1) TV_warning.setText("К сожалению, нельзя играть одному :( ");
                else {
                    TV_warning.setText("");
                    typeOfGame = "hard";
                    int count_seconds = 120;
                    Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                    intent.putExtra("spisok_names", arr);
                    intent.putExtra("typeOfGame", typeOfGame);
                    intent.putExtra("count_seconds", count_seconds);
                    intent.putExtra("danger_word", "null");
                    startActivity(intent);
                }
            }
        }
    }
    void to_fast() {
        String s = ED_names_of_players.getText().toString();
        if (TextUtils.isEmpty(s)) TV_warning.setText("Не все необходимые данные заполнены");
        else {
            String[] arr = s.split(" ");
            TreeSet<String> arr_new = new TreeSet<>(Arrays.asList(arr));
            if (arr.length != arr_new.size()) TV_warning.setText("Имена совпадают у кого-то! Поменяйте пожалуйста");
            else {
                if (arr.length == 1) TV_warning.setText("К сожалению, нельзя играть одному :( ");
                else {
                    TV_warning.setText("");
                    dg_to_fast.show();
                    BTN_time_is_ready.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int count_seconds = Integer.parseInt(ED_count_seconds.getText().toString());
                            if (count_seconds >= 5 && count_seconds <= 60) {
                                dg_to_fast.dismiss();
                                typeOfGame = "fast";
                                Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                                intent.putExtra("spisok_names", arr);
                                intent.putExtra("typeOfGame", typeOfGame);
                                intent.putExtra("count_seconds", count_seconds);
                                intent.putExtra("danger_word", "null");
                                startActivity(intent);
                            } else
                                Toast.makeText(getApplicationContext(), "Время ответа должно быть от 5 до 60 секунд", Toast.LENGTH_SHORT).show(); //TODO можно сделать как диалоговое окно, чтобы все увидели
                        }
                    });
                }
            }
        }
    }
    void to_danger(){
        String s = ED_names_of_players.getText().toString();
        if (TextUtils.isEmpty(s)) TV_warning.setText("Не все необходимые данные заполнены");
        else{
            String[] arr = s.split(" ");
            TreeSet<String> arr_new = new TreeSet<>(Arrays.asList(arr));
            if (arr.length != arr_new.size()) TV_warning.setText("Имена совпадают у кого-то! Поменяйте пожалуйста");
            else{
                if (arr.length == 1) TV_warning.setText("К сожалению, нельзя играть одному :( ");
                else {
                    TV_warning.setText("");
                    dg_to_danger.show();
                    BTN_ready_on_danger.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            danger_word = ED_danger_word.getText().toString();
                            boolean bad_chars_in_word = danger_word.contains(",") || danger_word.contains("`") || danger_word.contains("~")
                                    || danger_word.contains("/") || danger_word.contains("!") || danger_word.contains("@") || danger_word.contains("#")
                                    || danger_word.contains("№") || danger_word.contains("$") || danger_word.contains("%") || danger_word.contains("^")
                                    || danger_word.contains("&") || danger_word.contains("*") || danger_word.contains("(") || danger_word.contains(")")
                                    || danger_word.contains("\\") || danger_word.contains("|") || danger_word.contains(" ") || danger_word.contains("1")
                                    || danger_word.contains("2") || danger_word.contains("3") || danger_word.contains("4") || danger_word.contains("5")
                                    || danger_word.contains("6") || danger_word.contains("7") || danger_word.contains("8") || danger_word.contains("9")
                                    || danger_word.contains("0") || danger_word.contains("+") || danger_word.contains("-") || danger_word.contains("{")
                                    || danger_word.contains("}") || danger_word.contains("[") || danger_word.contains("]") || danger_word.contains("\"");
                            if (TextUtils.isEmpty(danger_word)){//TODO можно сделать как textview, чтобы все увидели
                                Toast.makeText(getApplicationContext(), "Никакое слово вы не ввели! \n никто не подсматривает?", Toast.LENGTH_SHORT).show();
                            }else if (bad_chars_in_word){
                                Toast.makeText(getApplicationContext(), "В слове есть запрещенные символы. Нельзя использовать следующие символы: " +
                                        "\", ~ ` @ ! # № $ % ^ & * ; : ( ) { } [ ] \\ / | + - пробел и цифры", Toast.LENGTH_SHORT).show();
                            }else{
                                MenuActivity.AsyncWordDanger asyncWord = new MenuActivity.AsyncWordDanger();
                                asyncWord.execute(address);
                            }
                        }
                    });
                }
            }
        }
    }

    class AsyncWordDanger extends AsyncTask<String, Void, Response>{

        @Override
        protected Response doInBackground(String... strings) {
            HttpUrl.Builder urlbilder = HttpUrl.parse(strings[0]).newBuilder();
            urlbilder.addQueryParameter("key", api_key);
            urlbilder.addQueryParameter("lang", "ru-ru");
            urlbilder.addQueryParameter("text", danger_word);
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
            if (response != null){
                Gson gson = new Gson();
                if (response.code() == 200){
                    String s = null;
                    try {
                        s = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Word word = gson.fromJson(s, Word.class);
                    if (word.def.length == 0){
                        TV_warning_dg_danger.setText("Такого слова не существует!");
                    }else{
                        String[] arr = ED_names_of_players.getText().toString().split(" ");
                        dg_to_fast.dismiss();
                        typeOfGame = "danger";
                        int count_seconds = 120;
                        Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                        intent.putExtra("spisok_names", arr);
                        intent.putExtra("typeOfGame", typeOfGame);
                        intent.putExtra("danger_word", danger_word);
                        intent.putExtra("count_seconds", count_seconds);
                        startActivity(intent);
                    }
                }else TV_warning_dg_danger.setText("Не удалось проверить слово. Придумайте другое слово");
            }
        }
    }
}