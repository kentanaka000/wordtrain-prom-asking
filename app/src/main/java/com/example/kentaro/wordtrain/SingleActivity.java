package com.example.kentaro.wordtrain;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import java.util.ArrayList;
import java.util.List;


public class SingleActivity extends AppCompatActivity {

    TextView remainingHintsText;
    TextView wordListText;
    TextView errorText;
    TextView winText;
    TextView countText;
    EditText inputEdit;
    Button startButton;
    Button hintButton;
    ScrollView scrollView;
    TextView timerText;
    TextView instructionsText;
    RelativeLayout relativeLayout;

    String wordList;
    String hintWord;
    List<String> visited = new ArrayList();
    List<String> truevisited = new ArrayList();
    String current;

    int remainingHints = 3;
    int counter = 0;
    float winAlpha = 0;
    
    public final static String COLOR = "1E90FF";


    boolean ask = false;
    boolean end = false;
    boolean shouldHint = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        remainingHintsText = (TextView) findViewById(R.id.single_remaining_num);
        wordListText = (TextView) findViewById(R.id.single_word_list);
        inputEdit = (EditText) findViewById(R.id.single_input);
        errorText = (TextView) findViewById(R.id.warning_text);
        hintButton = (Button) findViewById(R.id.single_button_hint);
        winText = (TextView) findViewById(R.id.single_win);
        scrollView = (ScrollView) findViewById(R.id.single_scroll);
        timerText = (TextView) findViewById(R.id.single_timer);
        instructionsText = (TextView) findViewById(R.id.single_instructions);
        startButton = (Button) findViewById(R.id.single_start_button);
        relativeLayout = (RelativeLayout) findViewById(R.id.single_relative_layout);
        countText = (TextView) findViewById(R.id.single_count);

        DatabaseAccess randomWord = DatabaseAccess.getInstance(this);
        randomWord.open();
        wordList = randomWord.getRandomWord();
        visited.add(wordList);
        truevisited.add(wordList);
        hintWord = randomWord.nextWord(wordList, visited);
        randomWord.close();
        current = wordList;
        countText.setText("0");

        remainingHintsText.setText(Integer.toString(remainingHints));
    }

    public void onStartClick(View v) {
        inputEdit.setFocusable(true);
        inputEdit.setClickable(true);
        inputEdit.setCursorVisible(true);
        inputEdit.setFocusableInTouchMode(true);
        inputEdit.requestFocus();
        hintButton.setClickable(true);
        hintButton.setEnabled(true);
        startButton.setVisibility(View.INVISIBLE);
        startButton.setEnabled(false);
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(relativeLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);



        wordListText.setText(wordList);
        counter++;
        countText.setText(Integer.toString(counter));
        wordListText.setMovementMethod(new ScrollingMovementMethod());


        final long startTime = System.currentTimeMillis();
        final Handler timer = new Handler();
        timer.postDelayed(new Runnable() {
            @Override
            public void run() {
                int time = Math.round(System.currentTimeMillis() - startTime);
                String text = Double.toString(time);
                if (time < 100) {
                    timerText.setText("0.0" + text.substring(0,text.length()-2)+"\"");
                }
                else if (time < 1000) {
                    timerText.setText("0." + text.substring(0, text.length() - 2)+"\"");
                }
                else {
                    timerText.setText(text.substring(0, text.length() - 5) + "." + text.substring(text.length() - 5, text.length() - 2) + "\"");
                }
                if(!end) {
                    timer.postDelayed(this,10);
                }
            }
        }, 10);


        inputEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onInputSubmitted();
                    return true;
                }
                return false;
            }
        });
    }

    public void onInputSubmitted() {
        String input = inputEdit.getText().toString();
        input = input.toLowerCase();
        if (!ask) {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            Log.e("current", current + "a");
            boolean check = databaseAccess.check(current, input, visited);
            databaseAccess.close();
            if (input.length() != 4) {
                errorText.setText("Please enter a four letter word");
            }
            else if (findDif(input, current) == -1) {
                errorText.setText("Please change only one letter");
            }
            else if (find(input,truevisited)) {
                errorText.setText("That word has already been used");
            }
            else if (check) {
                inputEdit.setText("");
                errorText.setText("");
                hintButton.setText("HINT");
                if (remainingHints>0) hintButton.setClickable(true);

                int changed = findDif(current, input);
                wordList = input + "<br>" +  current.substring(0,changed) +
                        "<font color='#"+COLOR+"'>" + current.substring(changed,changed + 1) +
                        "</font>" + current.substring(changed+1) + wordList.substring(4);
                current = input;
                visited.add(input);
                truevisited.add(input);
                wordListText.setText(Html.fromHtml(wordList));
                counter++;
                countText.setText(Integer.toString(counter));
                if (current.equals("prom")) {
                    wordList =  "prom?" + "<br>" + wordList;
                    ask = true;
                    shouldHint = true;
                }
                else {
                    DatabaseAccess access = DatabaseAccess.getInstance(SingleActivity.this);
                    access.open();
                    String next = access.nextWord(current,visited);
                    visited.add(next);
                    truevisited.add(next);
                    hintWord = access.nextWord(next, visited);
                    access.close();
                    int redIndex = findDif(next, current);
                    wordList = next +"<br>"+ current.substring(0,redIndex) +
                            "<font color='#"+COLOR+"'>" + current.substring(redIndex,redIndex + 1) +
                            "</font>" + current.substring(redIndex+1) + wordList.substring(4);
                    if (next.equals("prom")) {
                        ask = true;
                    }
                    current = next;
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        wordListText.setText(Html.fromHtml(wordList));
                        if (shouldHint) inputEdit.setHint("yes or no");
                        counter++;
                        countText.setText(Integer.toString(counter));
                    }
                }, 1000);
            }
            else {
                errorText.setText("Sorry, that doesn't seem to be a real word");
            }
        }
        else if (ask == true) {
            if (input.equals("yes")) {
                end = true;
                errorText.setText("");
                hintButton.setEnabled(false);
                inputEdit.setKeyListener(null);
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        winAlpha+=.01;
                        winText.setAlpha(winAlpha);
                        if(winAlpha<.99) {
                            handler.postDelayed(this,10);
                        }
                    }
                }, 10);
                final Handler score = new Handler();
                score.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        winAlpha+=.01;
                        winText.setAlpha(winAlpha);
                        if(winAlpha<.99) {
                            handler.postDelayed(this,10);
                        }
                    }
                }, 1000);
            }
            else if (input.equals("no")){
                errorText.setText("Please say yes");
            }
            else {
                errorText.setText("yes or no?");
            }
        }
    }

    public void onSingleHintClick(View v) {
        if (remainingHints > 0) {
            remainingHints--;
        }
        if (remainingHints==0) {
            remainingHintsText.setTextColor(Color.RED);
            hintButton.setEnabled(false);
        }
        if (ask) {
            hintButton.setText("Just say yes");
        }
        else {
            int index = findDif(hintWord, current);
            hintButton.setText("Use \"" + hintWord.charAt(index) + "\"");
        }
        remainingHintsText.setText(Integer.toString(remainingHints));
        hintButton.setClickable(false);
    }

    @Override
    protected  void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputEdit.getWindowToken(), 0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        inputEdit.requestFocus();
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(relativeLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    int findDif (String a, String b) {
        int count = 0;
        int temp = 0;
        for (int i = 0; i < 4; i++) {
            if (a.charAt(i) != b.charAt(i)){
                count ++;
                temp = i;
            }
        }
        if (count == 1) return temp;
        else if (count == 0) return -2;
        else return -1;
    }

    boolean find(String search, List<String> list) {
        for(String str: list) {
            if(str.trim().contains(search))
                return true;
        }
        return false;
    }

}
