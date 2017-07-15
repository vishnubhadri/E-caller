package com.vishnu.ecaller.ECaller;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Locale;


public class MyDialog extends Activity implements TextToSpeech.OnInitListener {
    private static final String TAG = MyDialog.class.getSimpleName();
    WindowManager mWindowManager;
    View mView;
    public static boolean active = false;
    public static Activity myDialog;
    private WindowManager windowManager;
    private LinearLayout linearLayout1;
    private RelativeLayout chatheadView;
    public String context_menu_name, context_menu_number;
    LinearLayout linearlayout;
    static Button callbtn1, callbtn2, callbtn3, callbtn4, callbtn5;
    View top;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tts = new TextToSpeech(this, this);
        setContentView(R.layout.dialog);

        myDialog = MyDialog.this;

        linearlayout = (LinearLayout) findViewById(R.id.linearlayout);
    }

    //@Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        try {
            callbtn1 = (Button) findViewById(R.id.calbtn1);
            callbtn2 = (Button) findViewById(R.id.calbtn2);
            callbtn3 = (Button) findViewById(R.id.calbtn3);
            callbtn4 = (Button) findViewById(R.id.calbtn4);
            callbtn5 = (Button) findViewById(R.id.calbtn5);
        } catch (Exception e) {
            Log.i(Utils.LogTag, e.toString());
        }
        try {
            callbtn1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(Utils.LogTag, "callbtn1 long click: ");
                    registerForContextMenu(linearlayout);
                    context_menu_name = ReadfromFile("File1name");
                    context_menu_number = ReadfromFile("File1number");
                    return false;
                }
            });
            callbtn2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(Utils.LogTag, "callbtn2 long click: ");
                    registerForContextMenu(linearlayout);
                    context_menu_name = ReadfromFile("File2name");
                    context_menu_number = ReadfromFile("File2number");
                    return false;
                }
            });
            callbtn3.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(Utils.LogTag, "callbtn3 long click: ");
                    registerForContextMenu(linearlayout);
                    context_menu_name = ReadfromFile("File3name");
                    context_menu_number = ReadfromFile("File3number");
                    return false;
                }
            });
            callbtn4.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(Utils.LogTag, "callbtn4 long click: ");
                    registerForContextMenu(linearlayout);
                    context_menu_name = ReadfromFile("File4name");
                    context_menu_number = ReadfromFile("File4number");
                    return false;
                }
            });
            callbtn5.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(Utils.LogTag, "callbtn5 long click: ");
                    registerForContextMenu(linearlayout);
                    context_menu_name = ReadfromFile("File5name");
                    context_menu_number = ReadfromFile("File5number");
                    return false;
                }
            });
        } catch (Exception e) {
            Log.i(Utils.LogTag, e.toString());
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        active = false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        active = false;
    }

    public String ReadfromFile(String filename) {
        String s = null;

        //reading text from file
        int charRead;
        try {
            FileInputStream fileIn = openFileInput(filename + ".txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[Utils.READ_BLOCK_SIZE];

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();
        } catch (Exception e) {
            e.printStackTrace();
            s = "No Contact";
        }
        return s;

    }

    public void onInit(int status) {
        // TODO Auto-generated method stub
        try {
            if (status == TextToSpeech.SUCCESS) {

                int result = tts.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not support for TTS", Toast.LENGTH_LONG).show();
                    Log.e("TTS", "Language is not supported");
                }
            } else {
                Log.e("TTS", "Initilization Failed");
            }
        } catch (Exception e) {
            Log.i(Utils.LogTag, e.toString());
        }
    }

    private void speakOut(String text) {
        try {
            if (text.isEmpty()) {
                tts.speak("You haven't typed text", TextToSpeech.QUEUE_ADD, null);
            } else {
                try {
                    tts.speak(text, TextToSpeech.QUEUE_ADD, null);
                } catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.i(Utils.LogTag, e.toString());
        }
    }

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxvolume = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        int curvolume = am.getStreamVolume(AudioManager.STREAM_SYSTEM);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && maxvolume == curvolume) {
            String name = ReadfromFile("File1name");
            String number = ReadfromFile("File1number");
            speakOut(name);
            speakOut(number);
        }
        return false;

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Contact information");
        menu.add(0, v.getId(), 0, "Name: " + context_menu_name);//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Number: " + context_menu_number);
    }

    public void onBackPressed() {
        this.finish();
    }


}