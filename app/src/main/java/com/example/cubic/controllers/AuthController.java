package com.example.cubic.controllers;


import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthController {
    private static AuthController authInstance;
    private FirebaseAuth firebaseAuth;

    private AuthController(){
        if (authInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public synchronized static AuthController getInstance(){
        if (authInstance == null) {
            authInstance = new AuthController();
        }
        return authInstance;
    }

    public FirebaseUser getUser() {
        return isLoggedIn() ? firebaseAuth.getCurrentUser() : null;
    }

    public String getUid(){
        return firebaseAuth.getUid();
    }


    public boolean isLoggedIn(){
        return firebaseAuth.getUid()!=null;
    }

    public void logOut(){
        firebaseAuth.signOut();
    }

    public void authenticate(String email, String pass, final AuthenticationListener listener) {
        logOut();
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    listener.onAuthenticated(task.getResult().getUser());
                } else {
                    try {
                        throw task.getException();
                    }  catch(Exception e) {
                        listener.onFailure(e.getMessage());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(e.getMessage());
            }
        });

    }


    public interface AuthenticationListener {
        void onAuthenticated(FirebaseUser user);
        void onFailure(String error);
    }



}
