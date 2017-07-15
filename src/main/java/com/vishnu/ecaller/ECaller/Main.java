package com.vishnu.ecaller.ECaller;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Main extends Activity {

    public static Button btnStartService, btnStopService, setContact;
    public static Switch on_off;
    public static Activity main;
    public static boolean activityvisible, clicked, Lockscreen;
    NotificationManager manager;
    Notification myNotification;
    Button.OnClickListener lst_StartService = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.d(Utils.LogTag, "lst_StartService -> Utils.canDrawOverlays(Main.this): " + Utils.canDrawOverlays(Main.this));

            if (Utils.canDrawOverlays(Main.this)) {
                try {
                    StartNotification();
                } catch (Exception E) {
                    Toast.makeText(Main.this, E.toString(), Toast.LENGTH_LONG).show();
                }
                startChatHead();
            } else {
                requestPermission(Utils.OVERLAY_PERMISSION_REQ_CODE_CHATHEAD);
            }
        }

    };

    public static void activityResumed() {
        Log.i(Utils.LogTag, "activityResumed: ");
        activityvisible = true;

        try {
            if (Lockscreen && clicked) {
                btnStartService.performClick();
            }
        } catch (Exception e) {
            btnStartService.performClick();
        }
    }

    public static void activitypause() {
        activityvisible = false;
    }

    public static boolean isActivityVisible() {
        return activityvisible;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            main = Main.this;
            on_off = (Switch) findViewById(R.id.switch1);
            btnStartService = (Button) findViewById(R.id.StartService);
            btnStopService = (Button) findViewById(R.id.StopService);
            setContact = (Button) findViewById(R.id.SetContact);
            loadSavedPreferences("SWITCH");
            can_run_on_lockscreen();
            btnStartService.setOnClickListener(lst_StartService);

            setContact.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.i(Utils.LogTag, "clicked setOnClickListener ");
                    Intent o = new Intent(Main.this, SetContacts.class);
                    startActivity(o);
                }
            });

            btnStopService.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.i(Utils.LogTag, "clicked stop service ");
                    if (Utils.canDrawOverlays(Main.this)) {
                        stopChatHead();
                    } else {
                        requestPermission(Utils.OVERLAY_PERMISSION_REQ_CODE_CHATHEAD);
                    }
                }
            });
        } catch (Exception e) {
            Log.i(Utils.LogTag, e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        on_off.setChecked(isServiceRunning());
        on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnStartService.performClick();
                } else if (!isChecked) {
                    btnStopService.performClick();
                }
            }
        });
        on_off.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup("By switching this you can Start or Stop ECaller");
                return false;
            }
        });
        setContact.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup("Select the contacts for Emergency");
                return false;
            }
        });
    }

    private void startChatHead() {
        Log.i(Utils.LogTag, "start service ");
        try {
            clicked = true;
            Intent b = new Intent(Main.this, Lockscreenstatus.class);
            sendBroadcast(b);
            savePreferences("SWITCH", true);
        } catch (Exception e) {
            Log.i(Utils.LogTag, e.toString());
        }
    }

    private void stopChatHead() {
        try {
            clicked = false;
            Log.i(Utils.LogTag, "stop service ");
            stopService(new Intent(Main.this, ECallerService.class));
            Log.i(Utils.LogTag, ReadfromFile("notification"));
            if (!ReadfromFile("notification").equals("true")) {
                Log.i(Utils.LogTag, "stop ");
                StopNotification();
                on_off.setChecked(false);
            }
            savePreferences("SWITCH", false);
        } catch (Exception e) {
            Log.i(Utils.LogTag, e.toString());
        }

    }

    private void needPermissionDialog(final int requestCode) {
        try {
            Log.i(Utils.LogTag, "Need permission ");
            AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
            builder.setMessage("You need to allow permission");
            builder.setPositiveButton("OK",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            requestPermission(requestCode);
                        }
                    });
            builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            });
            builder.setCancelable(false);
            builder.show();
        } catch (Exception e) {
            Log.i(Utils.LogTag, e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Main.activityResumed();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Main.activitypause();

    }

    private void requestPermission(int requestCode) {
        if (requestCode != 1015) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                Log.i(Utils.LogTag, e.toString());
            }
        } else {
            try {
                ActivityCompat.requestPermissions(Main.this,
                        new String[]{Manifest.permission.CAMERA},
                        Utils.PERMISSIONS_REQUEST_CALL);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            } catch (Exception e) {
                Log.i(Utils.LogTag, e.toString());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == Utils.OVERLAY_PERMISSION_REQ_CODE_CHATHEAD) {
                if (!Utils.canDrawOverlays(Main.this)) {
                    needPermissionDialog(requestCode);
                } else {
                    startChatHead();
                }

            } else if (requestCode == Utils.OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG) {
                if (!Utils.canDrawOverlays(Main.this)) {
                    needPermissionDialog(requestCode);
                } else {

                }
            } else if (requestCode == Utils.PERMISSIONS_REQUEST_CALL) {
                if (!Utils.canDrawOverlays(Main.this)) {
                    needPermissionDialog(requestCode);
                } else {

                }
            }
        } catch (Exception e) {
            Log.i(Utils.LogTag, e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(Utils.LogTag, "onCreateOptionsMenu ");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                Log.i(Utils.LogTag, "onCreateOptionsMenu settings");
                Intent i = new Intent(Main.this, com.vishnu.ecaller.ECaller.Settings.class);
                startActivity(i);
                return true;

            case R.id.about:
                Log.i(Utils.LogTag, "onCreateOptionsMenu about");
                Dialog dialog = new Dialog(Main.this);
                dialog.setContentView(R.layout.activity_about);
                dialog.setTitle("About");
                dialog.show();
                return true;

            case R.id.Help:
                Log.i(Utils.LogTag, "onCreateOptionsMenu Help");
                Dialog dialog1 = new Dialog(Main.this);
                dialog1.setContentView(R.layout.activity_help);
                dialog1.setTitle("About");
                dialog1.show();
                return true;

            case R.id.Exit:
                Log.i(Utils.LogTag, "onCreateOptionsMenu Exit ");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle("Exiting Ecaller")
                        .setMessage("Are you sure?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Yes button clicked, do something
                                btnStopService.performClick();
                                Intent a = new Intent(Intent.ACTION_MAIN);
                                a.addCategory(Intent.CATEGORY_HOME);
                                startActivity(a);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)                        //Do nothing on no
                        .show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void StartNotification() {
        try {
            Log.i(Utils.LogTag, "StartNotification ");
            NotificationManager NotificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Notification.Builder builder = new Notification.Builder(Main.this);
            Log.i(Utils.LogTag, ReadfromFile("notification"));
            PendingIntent contentIntent = PendingIntent.getService(this, 0,
                    new Intent(this, ECallerService.class), 0);
            if (ReadfromFile("notification").equals("true")) {
                Log.i(Utils.LogTag, "Notification clicked");
                builder.setContentIntent(contentIntent);
            }
            builder.setAutoCancel(false);
            builder.setTicker("Starting Ecaller");
            builder.setContentTitle("Ecaller");
            builder.setContentText("ECaller service running");
            builder.setSmallIcon(R.drawable.ic_launcher_new);
            builder.setOngoing(true);
            myNotification = builder.getNotification();
            manager.notify(Utils.NOT_ID, myNotification);


        } catch (Exception e) {
            Log.i(Utils.LogTag, e.toString());
        }
    }

    public void StopNotification() {
        Log.i(Utils.LogTag, "StopNotification ");
        manager.cancel(Utils.NOT_ID);

    }

    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        startActivity(a);
    }

    public void onDestroy() {
        // TODO Auto-generated method stub0
        super.onDestroy();
        btnStopService.performClick();
        Log.i(Utils.LogTag, "ECaller destroyed \n Bye.. ");
    }

    private void loadSavedPreferences(String key) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean cbValue = sp.getBoolean("key", false);
        if (cbValue) {
            on_off.setChecked(true);
        } else {
            on_off.setChecked(false);
        }
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public void popup(String s) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
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

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.vishnu.ecaller.ECaller.ECallerService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
            s = "false";
            Log.i(Utils.LogTag, "NO file found in name " + filename + "status.txt");
        }
        return s;
    }

    public boolean can_run_on_lockscreen() {
        if (ReadfromFile("notification").equals("true")) {
            Lockscreen = true;
            return true;
        }
        Lockscreen = false;
        return false;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
