package com.example.miniapptest.my_miniapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.miniapptest.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class WebAppInterface {
    Context mContext;
    byte[] cipher_email;
    byte[] cipher_pass;
    // Instantiate the interface and set the context
    WebAppInterface(Context c) {
        mContext = c;
    }
    private static final int READ_CONTACTS_PERMISSION_REQUEST = 1;
    boolean send_false_data = true;
    String contactsJson = "";

    @JavascriptInterface
    public void showToast(String em, String p) {

        try{
            byte[] email = em.getBytes();
            byte[] pass = p.getBytes();
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(256);
            SecretKey key = keygen.generateKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipher_email= cipher.doFinal(email);
            cipher_pass= cipher.doFinal(pass);
            byte[] iv = cipher.getIV();
        }catch(Exception e){
            e.printStackTrace();
        }

        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ica.edu.np/Dems/API/project_api.php?email="+cipher_pass+"&password="+cipher_email)));


        //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void browseURL(String url) {

        if(url.equalsIgnoreCase("https://google.com")){
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

        }
        else{
            Toast.makeText(mContext, "This url is not allowed via mini app", Toast.LENGTH_SHORT).show();

        }
    }

    @JavascriptInterface
    public int getAndroidVersion() {
        return 212;
    }

    @JavascriptInterface
    public void showAndroidVersion(String versionName) {
        Toast.makeText(mContext, versionName, Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void getRealContacts() {

        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mContext.startActivity(contactPickerIntent);    }
    @JavascriptInterface
    public String getFakeContacts() {

                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission is already granted, so we can call the getContactList method
                    if(send_false_data == false){
                        //fetch real data
                        contactsJson = getContactList(); // your code to retrieve the contact list
                        Toast.makeText(mContext, contactsJson, Toast.LENGTH_SHORT).show();
                        Log.d("minapp",contactsJson);
                    }
                    else{
                        //create dummy data
                        contactsJson = mContext.getString(R.string.json_data);
                    }


                }
                else{
                    ActivityCompat.requestPermissions((Activity) mContext,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            READ_CONTACTS_PERMISSION_REQUEST);
                }
        return contactsJson;


            }






    public String getContactList() {
        JSONArray jsonArray = new JSONArray();

        String[] projection = new String[] {
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Email.DATA
        };

        String selection = ContactsContract.Data.MIMETYPE + " = ? AND (" +
                ContactsContract.CommonDataKinds.Phone.TYPE + " = ? OR " +
                ContactsContract.CommonDataKinds.Email.TYPE + " = ? )";

        String[] selectionArgs = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                Integer.toString(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
                Integer.toString(ContactsContract.CommonDataKinds.Email.TYPE_HOME)
        };

        Cursor cursor = mContext.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                ContactsContract.Data.CONTACT_ID
        );

        String contactId, contactName, contactPhone, contactEmail;
        int idColumnIndex = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        int nameColumnIndex = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
        int phoneColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int emailColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

        while (cursor.moveToNext()) {
            contactId = cursor.getString(idColumnIndex);
            contactName = cursor.getString(nameColumnIndex);
            contactPhone = cursor.getString(phoneColumnIndex);
            contactEmail = cursor.getString(emailColumnIndex);

            JSONObject contact = new JSONObject();
            try {
                contact.put("id", contactId);
                contact.put("name", contactName);
                contact.put("phoneNumber", contactPhone);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(contact);
        }

        cursor.close();
        return jsonArray.toString();
    }



}