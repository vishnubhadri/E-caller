package com.vishnu.ecaller.ECaller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Settings extends Activity {
    public static CheckBox poweron, lockscreen, open_notificaiton;
    public static SeekBar seekBar;
    public static ImageButton imageButton;
    public static TextView textView;
    public static Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        textView = (TextView) findViewById(R.id.numbertext);
        try {
            String temp3 = ReadfromFile("seekbar");
            int level = Integer.parseInt(temp3);
            set_seekbar_level(level);
        } catch (RuntimeException e) {

            Log.e(Utils.LogTag, "onStart: ", e);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        poweron = (CheckBox) findViewById(R.id.power_on);
        lockscreen = (CheckBox) findViewById(R.id.lockscreen);
        open_notificaiton = (CheckBox) findViewById(R.id.notification);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        save = (Button) findViewById(R.id.save);

        String temp = ReadfromFile("poweron");
        if (temp.equals("true")) {
            poweron.setChecked(true);
        }
        String temp1 = ReadfromFile("lockscreen");
        if (temp1.equals("true")) {
            lockscreen.setChecked(true);
        }
        String temp2 = ReadfromFile("notification");
        if (temp2.equals("true")) {
            open_notificaiton.setChecked(true);
        }
        String temp3 = ReadfromFile("seekbar");
        try {
            int level = Integer.parseInt(temp3);
            set_seekbar_level(level);
        } catch (RuntimeException e) {
            Log.e(Utils.LogTag, "onStart: ", e);
        }

        power_on_events();
        lockscreen_events();
        open_via_notification();
        on_change_process();
        poweron.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup("By clicking this ECaller service run when power on");
                return false;
            }
        });
        open_notificaiton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup("By clicking notification the ECaller service is open. Note:It may insecure");
                return false;
            }
        });
        lockscreen.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup("By clicking this ECaller service run when power on note:It use more memory and device may slow");
                return false;
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup("The result may different from the preview");
            }
        });
        save_fun();

    }

    public void WriteOnFile(String filename, String status) {
        // add-write text into file
        Log.i(Utils.LogTag, "writing on file ");
        try {
            FileOutputStream filenam = openFileOutput(filename + "status.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter1 = new OutputStreamWriter(filenam);
            outputWriter1.write(status);
            outputWriter1.close();
            //display file saved message

            Log.i(Utils.LogTag, "File saved in " + outputWriter1.toString());
            Log.i(Utils.LogTag, "File content " + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String ReadfromFile(String filename) {
        //reading text from file
        Log.i(Utils.LogTag, "reading files   ");
        String s = "";
        int charRead;
        try {
            FileInputStream fileIn = openFileInput(filename + "status.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer = new char[Utils.READ_BLOCK_SIZE];
            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();

        } catch (Exception e) {
            WriteOnFile(filename, "false");
            s = "false";
            e.printStackTrace();
        }
        return s;
    }

    public void onBackPressed() {
        this.finish();
    }

    public void power_on_events() {
        poweron.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    popup("This function is used to run on power on.");
                    PackageManager pm = Settings.this.getPackageManager();
                    ComponentName componentName = new ComponentName(Settings.this, ECallerService.class);
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                    WriteOnFile("poweron", "true");
                    Toast.makeText(getApplicationContext(), "Actived Service on power on", Toast.LENGTH_LONG).show();
                    Log.i(Utils.LogTag, "Power on activated");
                } else {
                    PackageManager pm = Settings.this.getPackageManager();
                    ComponentName componentName = new ComponentName(Settings.this, ECallerService.class);
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                    WriteOnFile("poweron", "false");
                    Log.i(Utils.LogTag, "Power on deactivated");
                    Toast.makeText(getApplicationContext(), "Deactived Service on power on", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void lockscreen_events() {
        lockscreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    popup("Note:It use more memory and device may slow");
                    PackageManager pm = Settings.this.getPackageManager();
                    ComponentName componentName = new ComponentName(Settings.this, Lockscreenstatus.class);
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                    Toast.makeText(getApplicationContext(), "Set run only on lockscreen", Toast.LENGTH_LONG).show();
                    WriteOnFile("lockscreen", "true");
                    Log.i(Utils.LogTag, "Set run only on lockscreen");

                } else {
                    Toast.makeText(getApplicationContext(), "Set run over all apps", Toast.LENGTH_LONG).show();
                    WriteOnFile("lockscreen", "false");
                    Log.i(Utils.LogTag, "Set run over all apps");
                }
            }
        });
    }

    public void open_via_notification() {
        open_notificaiton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    popup("Note:It may insecure");
                    Toast.makeText(getApplicationContext(), "Enable access via notification", Toast.LENGTH_LONG).show();
                    WriteOnFile("notification", "true");
                    Log.i(Utils.LogTag, "Enable access via notification");
                } else {
                    Toast.makeText(getApplicationContext(), "Disable access via notification", Toast.LENGTH_LONG).show();
                    WriteOnFile("notification", "false");
                    Log.i(Utils.LogTag, "Disable access via notification");
                }

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Help3:
                Dialog dialog = new Dialog(Settings.this);
                dialog.setContentView(R.layout.activity_settings_help);
                dialog.setTitle("About");
                dialog.show();
                return true;
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void popup(String s) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder
                .setTitle("Ecaller")
                .setMessage(s)
                .setIcon(R.drawable.ic_launcher_new)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes button clicked, do something
                    }
                })
                .show();
    }

    public void set_seekbar_level(int arg1) {
        Log.i(Utils.LogTag, "set_seekbar_level");
        seekBar.setProgress(arg1);
        image_view_size(arg1);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void image_view_size(int arg1) {
        Log.i(Utils.LogTag, "image_view_size " + arg1);
        textView.setText(arg1 + "");
        android.view.ViewGroup.LayoutParams params = imageButton.getLayoutParams();
        params.height = arg1;
        params.width = arg1;
        imageButton.setLayoutParams(params);
        ECallerService ecs = new ECallerService();
        ecs.set_icon_size();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void on_change_process() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                WriteOnFile("seekbar", progress + "");
                image_view_size((progress));
                Log.i(Utils.LogTag, "Seekbar changing to " + (progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
    }

    public void refresh() {
        Log.i(Utils.LogTag, "refreshing activity");
        Intent i = getIntent();
        finish();
        startActivity(i);
    }

    public void save_fun() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteOnFile("seekbar", textView + "");
                Toast.makeText(Settings.this, "Saved to size: " + textView.getText().toString() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
