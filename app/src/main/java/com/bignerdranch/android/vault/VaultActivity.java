package com.bignerdranch.android.vault;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
// import android.provider.ContactsContract;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

// import android.view.MenuItem; Adds back button fin action tool bar

import android.provider.ContactsContract;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
//import android.content.res.Configuration;

public class VaultActivity extends AppCompatActivity {

    int counter = 50; // Max login attempts to prevent brute force attacks
    EditText cellPhoneNumber;
    ListView listView;
    ArrayList<String> StoreContacts;
    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    String name, phonenumber;
    String PhoneNumber;
    public static final int RequestPermissionCode = 1;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("doit");
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.account_creation_page);
        File file = getBaseContext().getFileStreamPath("mytextfile3.txt");  // Checks if account already exist
        if (file.exists()) {
            setContentView(R.layout.login_page_v3);
        } else {
            setContentView(R.layout.account_creation_page);
        }

        // else:
        //set set content view to account creation
        //     getSupportActionBar().setHomeButtonEnabled(true);   Adds back button in toolbar
        //     getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //  @Override
    //   public boolean onOptionsItemSelected(MenuItem item) { Adds back button in toolbar
    //       onBackPressed();
    //       return true;
    //   }

    public void loginClicked(View view) {
        EditText password = (EditText) findViewById(R.id.editText_enter_password);
        Button loginButton = (Button) findViewById(R.id.login_button);
        if (password.getText().toString().equals("toor")) {
            //   Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_vault);
        } else {
            Toast.makeText(getApplicationContext(), "FOR NOW PASSWORD IS: toor", Toast.LENGTH_SHORT).show();
            --counter;
            if (counter == 0) {
                loginButton.setEnabled(false);
            }
        }
    }

    public void CreateVaultAccountClicked(View view) {
        // add-write text into file
        File file = getBaseContext().getFileStreamPath("mytextfile3.txt");  // Checks if account already exist
        if (!file.exists()) {
            setContentView(R.layout.save_cellphone_data);  // Does not exist
        }
        if (file.exists()) {
            Toast.makeText(getBaseContext(), "You already have a Vault account.",  // Does exist
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void MakeAccountClicked(View view) {
        try {
            cellPhoneNumber = (EditText) findViewById(R.id.editText_enter_cellPhoneNumber);
            FileOutputStream fileout = openFileOutput("mytextfile3.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(cellPhoneNumber.getText().toString());
            outputWriter.close();

            //display file saved message
            Toast.makeText(getBaseContext(), "File saved successfully!",
                    Toast.LENGTH_SHORT).show();

            setContentView(R.layout.login_page_v3);  // Login enter pass word you created before this point.
        } catch (Exception e) {                      // Have not added create password functionality yet must do that at some point!
            e.printStackTrace();
        }
    }

    public void goToLoginPageClicked(View view) {
        setContentView(R.layout.login_page_v3);
    }

    public void goToTextCreationInterface(View view) {
        setContentView(R.layout.craft_textmessage);

        StoreContacts = new ArrayList<String>();
        EnableRuntimePermission();

        GetContactsIntoArrayList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, StoreContacts);

        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.searchContacts);
        actv.setThreshold(1);
        actv.setAdapter(adapter);
        actv.setTextColor(Color.WHITE);
    }

    public void craftAndSendTextMessage(View view) {

    }


    // Function SendText:
    // This function converts cell contact index from name plus number to just number
    // cell contact index is now in cell phone number format and can now be used to send
    // a text message.
    public void SendText(View view) {
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.searchContacts);
        String cellContactString = actv.getText().toString();
        for(int x = 0; x < cellContactString.length()-1; ++x){
            char indexValue = cellContactString.charAt(x);
            if(indexValue == ':') {
                PhoneNumber = cellContactString.substring(x + 1, cellContactString.length() - 1);
                x = cellContactString.length(); // break for loop without buggy break statement
            }
        }
        Toast.makeText(VaultActivity.this, PhoneNumber, Toast.LENGTH_LONG).show();


    }

        // Function B 
        // Erase this function from here and the craft_text_messages_XML.
        // The user chooses the target contact, of which the contact index still has the
        // letters associated with the name of the contact. I want to erase the letters and
        // keep only the contact number. When the text message button is clicked, set a onCLick that
        // automatically erases all the letters from the search contact button, this way,
        // I am left with only the contact cell phone number and not cell phone number plus the name.
        // Now the retrieved index is in a format that the system can read and send text to and from.


    public void viewVaultTextMessages(View view){
        // Handle view test functionality
    }



    public void viewContacts(View view){
        //Handle view contacts functionality
        setContentView(R.layout.contact_items_listview);
        listView = (ListView)findViewById(R.id.listview1);
        button = (Button)findViewById(R.id.button1);

        StoreContacts = new ArrayList<String>();
        EnableRuntimePermission();

        GetContactsIntoArrayList();
        arrayAdapter = new ArrayAdapter<String>(
                VaultActivity.this,
                R.layout.contact_items_listview,
                R.id.textView, StoreContacts
        );

        listView.setAdapter(arrayAdapter);
    }

    public void GetContactsIntoArrayList(){

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null, null, null);

        while (cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            StoreContacts.add(name + " "  + ":" + " " + phonenumber);
        }

        cursor.close();

    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                VaultActivity.this,
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(VaultActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(VaultActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(VaultActivity.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(VaultActivity.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }



}

/*
    String data = "test test test";
    File test0Dir = new File("test0");
        if(!test0Dir.exists()){
                test0Dir.createNewFile();
                test0Dir.mkdir();
                }

                FileOutputStream fos0 = openFileOutput("test0",Context.MODE_PRIVATE);
                fos0.write(data.getBytes());
                fos0.close();
*/

/*   public void internalStorageWorksorNot(View view) {

            try {
                FileInputStream fileIn=openFileInput("mytextfile.txt");
                InputStreamReader InputRead= new InputStreamReader(fileIn);

                int c;
                String temp="";
                while( (c = fileIn.read()) != -1){
                    temp = temp + Character.toString((char)c);
                }
                fileIn.read();
                Toast.makeText(getBaseContext(), temp,Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
    }


        public String eraseLettersLeaveOnlyCellNumber(AutoCompleteTextView cellContactIndex){
        String cellContactString = cellContactIndex.getText().toString();
        for(int x = 0; x < cellContactString.length()-1; ++x){
            if(cellContactString.charAt(x).)
        }

    }

    */