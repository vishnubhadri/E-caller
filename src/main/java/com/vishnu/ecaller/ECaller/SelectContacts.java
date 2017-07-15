package com.vishnu.ecaller.ECaller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SelectContacts extends Activity {
    private static final String TAG = SelectContacts.class.getSimpleName();
    Button b, save, del;
    EditText name, number;
    String s;
    private Uri uriContact;
    private String contactID;     // contacts unique ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.i(Utils.LogTag, "Select contact starting ");
        s = getIntent().getExtras().getString("key");
        b = (Button) findViewById(R.id.SelectButton1);
        del = (Button) findViewById(R.id.del_contact);
        name = (EditText) findViewById(R.id.cname);
        number = (EditText) findViewById(R.id.cnumber);
        save = (Button) findViewById(R.id.savecontact);
        if (!ReadfromFile(s + "number").isEmpty()) {
            number.setHint(ReadfromFile(s + "number"));
        }
        if (!ReadfromFile(s + "name").isEmpty()) {
            name.setHint(ReadfromFile(s + "name"));
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.length() < 1 && number.length() < 10) {
                    Toast.makeText(SelectContacts.this, "Enter valid name and number", Toast.LENGTH_SHORT).show();
                } else if (name.length() < 1) {
                    Toast.makeText(SelectContacts.this, "Enter valid name", Toast.LENGTH_SHORT).show();
                } else if (number.length() < 10) {
                    Toast.makeText(SelectContacts.this, "Enter valid number", Toast.LENGTH_SHORT).show();
                } else {
                    WriteOnFile(s);
                    finish();
                }
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteOnFile(s, "Set new contact");
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelectContact(b);
            }
        });
        del.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup("It can delete the current contact. This will not is use until you set again");
                return false;
            }
        });
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup("By clicking this the contacts will open. You can select from this also");
                return false;
            }
        });
        save.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup("This is used to save the contact");
                return false;
            }
        });
    }

    public void onClickSelectContact(View btnSelectContact) {
        Log.i(Utils.LogTag, "onClickSelectContact: ");
        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        name.setText(null);
        number.setText(null);
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Utils.REQUEST_CODE_PICK_CONTACTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            try {
                retrieveContactName();
            } catch (Exception e) {
                Toast.makeText(this, "Unable to set name, set manually \n code: " + e, Toast.LENGTH_SHORT).show();
            }
            try {
                retrieveContactNumber();
            } catch (SecurityException s) {
                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE};
                if(!Main.hasPermissions(this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                }

            } catch (Exception e) {
                Toast.makeText(this, "Unable to set number, set manually \n  code: " + e, Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void retrieveContactNumber() {
        String contactNumber = null;
        // getting contacts ID
        Log.i(Utils.LogTag, "Geting contact number: ");
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorID.close();
        Log.d(TAG, "Contact ID: " + contactID);
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        if (contactNumber.isEmpty() || contactNumber.length() < 1) {
            throw new NullPointerException("Unable to retrive contact number");
        }
        number.setText(contactNumber);
        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }

    private void retrieveContactName() {
        Log.i(Utils.LogTag, "Geting contact name: ");
        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();
        if (contactName.isEmpty() || contactName.length() < 1) {
            throw new NullPointerException("Unable to retrive contact name");
        }
        name.setText(contactName);
        Log.d(TAG, "Contact Name: " + contactName);

    }

    public void WriteOnFile(String filename) {
        // add-write text into file
        Log.i(Utils.LogTag, "wrinting on file ");
        try {
            FileOutputStream filenam = openFileOutput(filename + "name.txt", MODE_PRIVATE);
            FileOutputStream filenum = openFileOutput(filename + "number.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter1 = new OutputStreamWriter(filenam);
            OutputStreamWriter outputWriter2 = new OutputStreamWriter(filenum);
            outputWriter1.write(name.getText().toString());
            outputWriter2.write(number.getText().toString());
            outputWriter1.close();
            outputWriter2.close();

            //display file saved message
            Toast.makeText(getBaseContext(), "File saved successfully in name " + filename, Toast.LENGTH_SHORT).show();
            Log.i(Utils.LogTag, "File saved in " + outputWriter1.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void WriteOnFile(String filename, String value) {
        // add-write text into file
        Log.i(Utils.LogTag, "wrinting on file ");
        try {
            FileOutputStream filenam = openFileOutput(filename + "name.txt", MODE_PRIVATE);
            FileOutputStream filenum = openFileOutput(filename + "number.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter1 = new OutputStreamWriter(filenam);
            OutputStreamWriter outputWriter2 = new OutputStreamWriter(filenum);
            outputWriter1.write(value);
            outputWriter2.write(value);
            outputWriter1.close();
            outputWriter2.close();

            //display file saved message
            Toast.makeText(getBaseContext(), "Contact deleted " + filename, Toast.LENGTH_SHORT).show();
            Log.i(Utils.LogTag, "Contact deleted " + outputWriter1.toString());
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String ReadfromFile(String filename) {
        //reading text from file
        Log.i(Utils.LogTag, "reading contact ");
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
        }
        return s;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.selectcontact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Help2:
                Dialog dialog = new Dialog(SelectContacts.this);
                dialog.setContentView(R.layout.activity_selectcontact_help);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(SelectContacts.this);
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


