package com.vishnu.ecaller.ECaller;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ECallerService extends Service {
    Handler myHandler = new Handler();
    private static WindowManager windowManager;
    private static RelativeLayout chatheadView, removeView;
    private static LinearLayout btnlist, txt_linearlayout;
    Runnable myRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (btnlist != null) {
                btnlist.setVisibility(View.GONE);
            }
        }
    };
    private static ImageView chatheadImg, removeImg;
    private static Button cb1, cb2, cb3, cb4, cb5;
    private static int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private static Point szWindow = new Point();
    private static boolean isLeft;
    private static String sMsg = "";

    @SuppressWarnings("deprecation")

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.d(Utils.LogTag, "ECallerService.onCreate()");
    }

    private void handleStart() {
        Log.i(Utils.LogTag, "handleStart");
        try {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            removeView = (RelativeLayout) inflater.inflate(R.layout.remove, null);
            WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

            removeView.setVisibility(View.GONE);
            removeImg = (ImageView) removeView.findViewById(R.id.remove_img);
            windowManager.addView(removeView, paramRemove);


            chatheadView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);
            chatheadImg = (ImageView) chatheadView.findViewById(R.id.chathead_img);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                windowManager.getDefaultDisplay().getSize(szWindow);
            } else {
                int w = windowManager.getDefaultDisplay().getWidth();
                int h = windowManager.getDefaultDisplay().getHeight();
                szWindow.set(w, h);
            }

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = 0;
            params.y = 100;

            windowManager.addView(chatheadView, params);
            chatheadView.setVisibility(View.VISIBLE);
            set_icon_size();
            chatheadView.setOnTouchListener(new View.OnTouchListener() {
                long time_start = 0, time_end = 0;
                boolean isLongclick = false, inBounded = false;
                int remove_img_width = 0, remove_img_height = 0;
                Handler handler_longClick = new Handler();
                Runnable runnable_longClick = new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Log.d(Utils.LogTag, "Into runnable_longClick");

                        isLongclick = true;
                        removeView.setVisibility(View.VISIBLE);
                        chathead_longclick();

                    }
                };

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

                    int x_cord = (int) event.getRawX();
                    int y_cord = (int) event.getRawY();
                    int x_cord_Destination, y_cord_Destination;

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            time_start = System.currentTimeMillis();
                            handler_longClick.postDelayed(runnable_longClick, 600);

                            remove_img_width = removeImg.getLayoutParams().width;
                            remove_img_height = removeImg.getLayoutParams().height;

                            x_init_cord = x_cord;
                            y_init_cord = y_cord;

                            x_init_margin = layoutParams.x;
                            y_init_margin = layoutParams.y;

                            if (btnlist != null) {
                                btnlist.setVisibility(View.GONE);
                                myHandler.removeCallbacks(myRunnable);
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int x_diff_move = x_cord - x_init_cord;
                            int y_diff_move = y_cord - y_init_cord;

                            x_cord_Destination = x_init_margin + x_diff_move;
                            y_cord_Destination = y_init_margin + y_diff_move;

                            if (isLongclick) {
                                int x_bound_left = szWindow.x / 2 - (int) (remove_img_width * 1.5);
                                int x_bound_right = szWindow.x / 2 + (int) (remove_img_width * 1.5);
                                int y_bound_top = szWindow.y - (int) (remove_img_height * 1.5);

                                if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                                    inBounded = true;

                                    int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                    int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight()));

                                    if (removeImg.getLayoutParams().height == remove_img_height) {
                                        removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                        removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                        param_remove.x = x_cord_remove;
                                        param_remove.y = y_cord_remove;

                                        windowManager.updateViewLayout(removeView, param_remove);
                                    }

                                    layoutParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - chatheadView.getWidth())) / 2;
                                    layoutParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - chatheadView.getHeight())) / 2;

                                    windowManager.updateViewLayout(chatheadView, layoutParams);
                                    break;
                                } else {
                                    inBounded = false;
                                    removeImg.getLayoutParams().height = remove_img_height;
                                    removeImg.getLayoutParams().width = remove_img_width;

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                    int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
                                    int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

                                    param_remove.x = x_cord_remove;
                                    param_remove.y = y_cord_remove;

                                    windowManager.updateViewLayout(removeView, param_remove);
                                }

                            }


                            layoutParams.x = x_cord_Destination;
                            layoutParams.y = y_cord_Destination;

                            windowManager.updateViewLayout(chatheadView, layoutParams);
                            break;
                        case MotionEvent.ACTION_UP:
                            isLongclick = false;
                            removeView.setVisibility(View.GONE);
                            removeImg.getLayoutParams().height = remove_img_height;
                            removeImg.getLayoutParams().width = remove_img_width;
                            handler_longClick.removeCallbacks(runnable_longClick);

                            if (inBounded) {
                                try {
                                    Main.btnStopService.performClick();
                                } catch (NullPointerException e) {
                                    chatheadView.setVisibility(View.GONE);
                                }

                                inBounded = false;
                                break;
                            }

                            int x_diff = x_cord - x_init_cord;
                            int y_diff = y_cord - y_init_cord;

                            if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                                time_end = System.currentTimeMillis();
                                if ((time_end - time_start) < 300) {
                                    chathead_click();
                                }
                            }

                            y_cord_Destination = y_init_margin + y_diff;

                            int BarHeight = getStatusBarHeight();
                            if (y_cord_Destination < 0) {
                                y_cord_Destination = 0;
                            } else if (y_cord_Destination + (chatheadView.getHeight() + BarHeight) > szWindow.y) {
                                y_cord_Destination = szWindow.y - (chatheadView.getHeight() + BarHeight);
                            }
                            layoutParams.y = y_cord_Destination;

                            inBounded = false;
                            resetPosition(x_cord);

                            break;
                        default:
                            Log.d(Utils.LogTag, "chatheadView.setOnTouchListener  -> event.getAction() : default");
                            break;
                    }
                    return true;
                }
            });
            btnlist = (LinearLayout) inflater.inflate(R.layout.dialog, null);
            cb1 = (Button) btnlist.findViewById(R.id.calbtn1);
            cb2 = (Button) btnlist.findViewById(R.id.calbtn2);
            cb3 = (Button) btnlist.findViewById(R.id.calbtn3);
            cb4 = (Button) btnlist.findViewById(R.id.calbtn4);
            cb5 = (Button) btnlist.findViewById(R.id.calbtn5);
            cb1.setText(ReadfromFile("File1name", cb1));
            cb2.setText(ReadfromFile("File2name", cb2));
            cb3.setText(ReadfromFile("File3name", cb3));
            cb4.setText(ReadfromFile("File4name", cb4));
            cb5.setText(ReadfromFile("File5name", cb5));
            txt_linearlayout = (LinearLayout) btnlist.findViewById(R.id.linearlayout);


            WindowManager.LayoutParams paramsTxt = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            paramsTxt.gravity = Gravity.CENTER;

            btnlist.setVisibility(View.GONE);
            windowManager.addView(btnlist, paramsTxt);
            click_to_call_from_button();
        } catch (Exception e) {
            Log.e(Utils.LogTag, "Exception found", e);
            Main.btnStopService.performClick();
            Main.btnStartService.performClick();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        Log.i(Utils.LogTag, "onConfigurationChanged");

        super.onConfigurationChanged(newConfig);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

    }

    private void resetPosition(int x_cord_now) {
        if (x_cord_now <= szWindow.x / 2) {
            isLeft = true;
            moveToLeft(x_cord_now);

        } else {
            isLeft = false;
            moveToRight(x_cord_now);

        }

    }

    private void moveToLeft(final int x_cord_now) {
        final int x = szWindow.x - x_cord_now;

        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = 0 - (int) (double) bounceValue(step, x);
                windowManager.updateViewLayout(chatheadView, mParams);
            }

            public void onFinish() {
                mParams.x = 0;
                windowManager.updateViewLayout(chatheadView, mParams);
            }
        }.start();
    }

    private void moveToRight(final int x_cord_now) {
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = szWindow.x + (int) (double) bounceValue(step, x_cord_now) - chatheadView.getWidth();
                windowManager.updateViewLayout(chatheadView, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - chatheadView.getWidth();
                windowManager.updateViewLayout(chatheadView, mParams);
            }
        }.start();
    }

    private double bounceValue(long step, long scale) {
        double value = scale * java.lang.Math.exp(-0.055 * step) * java.lang.Math.cos(0.08 * step);
        return value;
    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    private void chathead_click() {

        if (btnlist.getVisibility() == View.INVISIBLE | btnlist.getVisibility() == View.GONE) {
            btnlist.setVisibility(View.VISIBLE);
        } else {
            btnlist.setVisibility(View.INVISIBLE);
        }
    }

    private void chathead_longclick() {
        Log.d(Utils.LogTag, "Into ECallerService.chathead_longclick() ");
        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeView, param_remove);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d(Utils.LogTag, "ECallerService.onStartCommand() -> startId=" + startId);
        if (intent != null) {
            Bundle bd = intent.getExtras();

            if (bd != null)
                sMsg = bd.getString(Utils.EXTRA_MSG);

            if (sMsg != null && sMsg.length() > 0) {
                if (startId == Service.START_STICKY) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                        }
                    }, 300);

                }
            }

        }

        if (startId == Service.START_STICKY) {
            handleStart();
            return super.onStartCommand(intent, flags, startId);
        } else {
            return Service.START_NOT_STICKY;
        }

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (chatheadView != null) {
            windowManager.removeView(chatheadView);
        }

        if (btnlist != null) {
            windowManager.removeView(btnlist);
        }

        if (removeView != null) {
            windowManager.removeView(removeView);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(Utils.LogTag, "onBind()");
        return null;
    }

    public void click_to_call_from_button() {
        Log.i(Utils.LogTag, "click_to_call_from_button: ");
        cb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(Utils.LogTag, "calling contact 1: ");
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ReadfromFile("File1number"))).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(ECallerService.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                btnlist.setVisibility(View.INVISIBLE);
                startActivity(i);
            }
        });

        cb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(Utils.LogTag, "calling contact 2: ");
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ReadfromFile("File2number"))).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(ECallerService.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                btnlist.setVisibility(View.INVISIBLE);
                startActivity(i);
            }
        });

        cb3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(Utils.LogTag, "calling contact 3: ");
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ReadfromFile("File3number"))).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(ECallerService.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                btnlist.setVisibility(View.INVISIBLE);
                startActivity(i);
            }
        });

        cb4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(Utils.LogTag, "calling contact 4: ");
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ReadfromFile("File4number"))).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                if (ActivityCompat.checkSelfPermission(ECallerService.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                btnlist.setVisibility(View.INVISIBLE);
                startActivity(i);
            }
        });

        cb5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(Utils.LogTag, "calling contact 5: ");
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ReadfromFile("File5number"))).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(ECallerService.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                btnlist.setVisibility(View.INVISIBLE);
                startActivity(i);
            }
        });

    }

    public String ReadfromFile(String filename) {
        //reading text from file
        Log.i(Utils.LogTag, "ReadfromFile(filename)");
        String s = "";
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
            Log.i(Utils.LogTag, "No Contact in name " + filename);
            s = "No Contact";
        }
        return s;
    }

    public String ReadfromFile(String filename, Button b) {
        String result = ReadfromFile(filename);
        Log.i(Utils.LogTag, "ReadfromFile(filename,button)");
        if (result.isEmpty() || result.equals("No Contact") || result.equals("Set new contact")) {
            b.setVisibility(View.INVISIBLE);
        }
        return result;
    }

    public int ReadfromFile_def(String filename, int sovl) {
        String result = ReadfromFile(filename);
        Log.i(Utils.LogTag, "ReadfromFile(filename,button)");
        if (result.isEmpty() || result.equals("No Contact") || result.equals("Set new contact")) {
            result = sovl + "";
        }
        return Integer.parseInt(result);
    }

    public void set_icon_size() {
        try {
            int arg1 = ReadfromFile_def("seekbarstatus", 112);
            Log.i("teasd", arg1+"");
            android.view.ViewGroup.LayoutParams params1 = chatheadView.getLayoutParams();
            params1.height = arg1;
            params1.height = arg1;
            params1.width = arg1;
            chatheadView.setLayoutParams(params1);
        } catch (Exception e) {
            Log.e(Utils.LogTag, "handleStart: ", e);
        }
    }
}

