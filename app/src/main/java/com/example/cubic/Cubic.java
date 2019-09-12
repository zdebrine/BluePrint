package com.example.cubic;

import android.app.Application;
import android.content.Context;

import com.segment.analytics.Analytics;



public class Cubic extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Context contex =this;

        // Create an analytics client with the given context and Segment write key.
        Analytics analytics = new Analytics.Builder(this, "at6RBjcntJqqcgzqjjZfoeUNY3APzI93")
                // Enable this to record certain application events automatically!
                .trackApplicationLifecycleEvents()
                // Enable this to record screen views automatically!
                .recordScreenViews()
                .build();

// Set the initialized instance as a globally accessible instance.
        Analytics.setSingletonInstance(analytics);

    }


}
