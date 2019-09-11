package com.example.cubic.constants;

public class Beacon {
    public static String [] BEACONS_IDS = {
            "0xd841eb0113842bfeb63e",
            "0xd9600577451c54cb70b2"
    };
    public static long BEACON_SCANNER_INTERVAL = 1000; // Scan interval to get update from surrounding beacons
    public static int BEACON_EXPIRATION_TIME = 10; // If no update from beacon within X seconds, Exit will fire

    public static String BEACON_REGION_ENTER_EVENT = "BEACON_REGION_ENTER_EVENT";
    public static String BEACON_REGION_EXIT_EVENT = "BEACON_REGION_EXIT_EVENT";
}
