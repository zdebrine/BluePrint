package com.example.cubic.constants;

public class Beacon {
    public static String [] BEACONS_IDS = {
            "0x222f22e44f1aae5be1fb",
            "0x2516958df47fec53be2e"
    };
    public static long BEACON_SCANNER_INTERVAL = 1000; // Scan interval to get update from surrounding beacons
    public static int BEACON_EXPIRATION_TIME = 10; // If no update from beacon within X seconds, Exit will fire

    public static String BEACON_REGION_ENTER_EVENT = "BEACON_REGION_ENTER_EVENT";
    public static String BEACON_REGION_EXIT_EVENT = "BEACON_REGION_EXIT_EVENT";
}
