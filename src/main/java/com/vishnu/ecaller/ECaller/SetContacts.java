package com.vishnu.ecaller.ECaller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SetContacts extends Activity {
    Button b1, b2, b3, b4, b5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_contacts);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        {
            b1 = (Button) findViewById(R.id.button1);
            b2 = (Button) findViewById(R.id.button2);
            b3 = (Button) findViewById(R.id.button3);
            b4 = (Button) findViewById(R.id.button4);
            b5 = (Button) findViewById(R.id.button5);

            b1.setText(ReadfromFile("File1name"));
            b2.setText(ReadfromFile("File2name"));
            b3.setText(ReadfromFile("File3name"));
            b4.setText(ReadfromFile("File4name"));
            b5.setText(ReadfromFile("File5name"));


            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(Utils.LogTag, "select contact button 1 clicked ");
                    Intent i = new Intent(SetContacts.this, SelectContacts.class);
                    i.putExtra("key", "File1");
                    startActivity(i);
                }
            });

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(Utils.LogTag, "select contact button 2 clicked ");
                    Intent i = new Intent(SetContacts.this, SelectContacts.class);
                    i.putExtra("key", "File2");
                    startActivity(i);
                }
            });

            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(Utils.LogTag, "select contact button 3 clicked ");
                    Intent i = new Intent(SetContacts.this, SelectContacts.class);
                    i.putExtra("key", "File3");
                    startActivity(i);
                }
            });

            b4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(Utils.LogTag, "select contact button 4 clicked ");
                    Intent i = new Intent(SetContacts.this, SelectContacts.class);
                    i.putExtra("key", "File4");
                    startActivity(i);
                }
            });

            b5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(Utils.LogTag, "select contact button 5 clicked ");
                    Intent i = new Intent(SetContacts.this, SelectContacts.class);
                    i.putExtra("key", "File5");
                    startActivity(i);
                }
            });
            Button b = new Button(this);
            b.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    popup("By clicking this buttons you can select the contacts. This is visible in this order");
                    return false;
                }
            });
        }

    }

    public String ReadfromFile(String filename) {
        //reading text from file
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
            e.printStackTrace();
            s = "Set new contact";
        }
        return s;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setcontact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Help3:
                Dialog dialog = new Dialog(SetContacts.this);
                dialog.setContentView(R.layout.activity_setcontact_help);
                dialog.setTitle("Help");
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        this.finish();
    }

    public void popup(String s) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SetContacts.this);
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

}
