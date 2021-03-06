package com.chibuzoakpe.flashchatnewfirebase;

/*
You need to create this class if you're going to work with
custom adapters instead if the ArrayAdapter
The class must extend BaseAdapter and the methods with "override"
needed to be implemented because BaseAdapter is an abstract class.

This adapter class also does the listening for data changes from firebase (reading data) essentially
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;

    //A data snapshot is the data type of data objects sent back from firebase
    private ArrayList<DataSnapshot> mSnapshotList;

    /*
    The childEventListener tracks when there's a change in the data in firebase and reports
    back to the application.
    All the methods were auto-generated for us when we made an object of the
    ChildEventListener class.
    But the only method we really care about is the onChildAdded(). This is due to the nature
    of the functionality of the app. If you could delete and edit messages, then the other
    methods would be valuable.
     */
    private ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            /*
            remember that a data snapshot is what firebase returns to you. So we add the
            snapshot to our list
             */
            mSnapshotList.add(snapshot);

            //we then have to notify the list view adapter that data has been added
            notifyDataSetChanged();
        }


        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    /*
    The adapter constructor basically initializes everything that would be
    needed by each item in the list view. It also takes the activity that
    would be using it as a parameter
     */
    public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {
        this.mActivity = activity;
        this.mDisplayName = name;
        this.mDatabaseReference = ref.child("messages");

        //We need to attach our listener to the database reference
        this.mDatabaseReference.addChildEventListener(mChildEventListener);

        mSnapshotList = new ArrayList<>();
    }

    /*
    We then create a helper class to help us model the view that each individual
    row in the list view would be holding.
    This is not necessary syntax and the "traditional" way of doing it is in the link in your
    notes
     */
    static class ViewHolder {
        TextView authorname;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    /*
    This is the method the list view uses to ask the adapter how many messages
    there are int the database.
     */
    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    /*
    We changed the return value of this method to InstantMessage because it is objects of the
    InstantMessage class that we are working with. The original signature was "Object"

    This method is a two-phase process. We get the data snapshot at the relevant position
    Then we convert the data snapshot to the desired object type we want to return (InstantMessage)
     */
    @Override
    public InstantMessage getItem(int position) {
        DataSnapshot snapshot = mSnapshotList.get(position);

        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*
    This method is called the number of times that getCount() returns
    Also, we implement it efficiently so as to re-use views when we can
    That's why the flow and syntax may seem a bit foreign compared to what's online
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /*
        convertView represents the view of a list item
        what we are simply doing is checking if there is an existing row on the screen
        that can be re-used (when you scroll past it on the screen)
        If there is no existing view, we create a new view from scratch from the layout file
         */
        if(convertView == null) {
            //You inflate() when you want to progammatically add a view to an activity or fragment
            LayoutInflater inflater = (LayoutInflater)mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_msg_row, parent, false);

            /*
            now linkup the views in the ViewHolder class with the chat_msg-_row layout
             */
            ViewHolder holder = new ViewHolder();
            holder.authorname = (TextView) convertView.findViewById(R.id.author);
            holder.body = (TextView) convertView.findViewById(R.id.message);

            /*
            As I suspected, you could call getLayoutParams() on holder.body or holder.authorname
            You just need to get the original layout params you styled in chat_msg_row
            And you can get the layout params from any id in the chat_msg_row layout file
             */
            holder.params = (LinearLayout.LayoutParams) holder.body.getLayoutParams();

            /*
            Now let the adapter store the view holder for a short period of time so that it
            can be re-used
             */
            convertView.setTag(holder);
        }

        InstantMessage message = getItem(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();

        String author = message.getAuthor();
         /*
        compare the author of the message to the logged in user
        so we can set the appearance of the text bubble
         */
        boolean isMe = author.equals(mDisplayName);
        setRowAppearance(isMe, holder);

        holder.authorname.setText(author);

        String msg = message.getMessage();
        holder.body.setText(msg);

        return convertView;
    }

    /*
    This method sets a visual difference between an individual's message bubble and another person's
     */
    public void setRowAppearance(boolean isMe, ViewHolder viewHolder) {
        if(isMe) {
            viewHolder.params.gravity = Gravity.END;
            viewHolder.authorname.setTextColor(Color.GREEN);
            viewHolder.body.setBackgroundResource(R.drawable.bubble2);
        }
        else {
            viewHolder.params.gravity = Gravity.START;
            viewHolder.authorname.setTextColor(Color.BLUE);
            viewHolder.body.setBackgroundResource(R.drawable.bubble1);
        }

        /*
        Try commenting either of these lines out to understand its effect
        But my understanding so far is you set the params characteristics before you then apply to
        params to each feature of the new inflated view that needs it
         */
        viewHolder.authorname.setLayoutParams(viewHolder.params);
        viewHolder.body.setLayoutParams(viewHolder.params);

    }

    /*
    This method detaches the childEventListener from the database reference when it's no longer
    needed. Helps to free resources.
     */
    public void cleanUp() {
        this.mDatabaseReference.removeEventListener(this.mChildEventListener);
    }
}
