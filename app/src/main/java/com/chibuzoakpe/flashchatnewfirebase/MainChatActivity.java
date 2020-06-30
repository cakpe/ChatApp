package com.chibuzoakpe.flashchatnewfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // TODO: Set up the display name and get the Firebase reference
        setupDisplayName();

        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        // TODO: Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    sendMessage();
                }
                return false;
            }
        });

        // TODO: Add an OnClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    // TODO: Retrieve the display name from the Shared Preferences
    private void setupDisplayName() {
        SharedPreferences preferences = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);
        mDisplayName = preferences.getString(RegisterActivity.DISPLAY_NAME_KEY, null);

        if(mDisplayName == null) {
            mDisplayName = "Anonymous";
        }
    }

    private void sendMessage() {
        // TODO: Grab the text the user typed in and push the message to Firebase
        Log.d("FlashChat", "Message sent");

        String message = mInputText.getText().toString();
        InstantMessage iMessage = new InstantMessage(message, mDisplayName);

        /*save message in firebase service
            the below format is not in the developer docs, so you really need to come back to this every now and then
            the "messages" is arbitrary and is simply the name of the structure the messages will be stored in
         */
        mDatabaseReference.child("messages").push().setValue(iMessage);
        mInputText.setText("");
    }

    // TODO: Override the onStart() lifecycle method. Setup the adapter here.


    @Override
    public void onStop() {
        super.onStop();

        // TODO: Remove the Firebase event listener on the adapter.

    }

}
