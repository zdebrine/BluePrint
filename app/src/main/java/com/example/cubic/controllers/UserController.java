package com.example.cubic.controllers;

import android.support.annotation.NonNull;

import com.example.cubic.constants.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UserController {
    private static UserController userInstance;
    private AuthController authController;
    private DatabaseReference userRef;


    private UserController() {
        if (userInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        userRef = FirebaseDatabase.getInstance().getReference(Database.USERS_REFERENCE);
        authController = AuthController.getInstance();
    }

    public synchronized static UserController getInstance() {
        if (userInstance == null) {
            userInstance = new UserController();
        }
        return userInstance;
    }

    public void writeNewUsername(final String name) {
        userRef.child(authController.getUid()).child("name").setValue(name);
    }

    public void getUserName(final UserDataCallBack userDataCallBack) {
        userRef.child(authController.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name = dataSnapshot.getValue().toString();
                    userDataCallBack.onSuccess(name);
                } else {
                    userDataCallBack.onSuccess("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userDataCallBack.onFailure(databaseError.getMessage());
            }
        });
    }

    public void writeUserEvent(String event, String beaconId){
        Date eventDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

        HashMap<String, String> eventMap = new HashMap<>();
        eventMap.put("event", event);
        eventMap.put("beacon", beaconId);
        eventMap.put("moment", dateFormat.format(eventDate));
        userRef.child(authController.getUid()).child("events").push().setValue(eventMap);

    }


    //Interfaces
    public interface UserDataCallBack {
        void onSuccess(String name);
        void onFailure(String err);

    }

}
