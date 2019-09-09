package com.example.cubic.controllers;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.cubic.constants.Beacon.BEACON_EXPIRATION_TIME;

public class BeaconController {
        private static BeaconController beaconInstance;
        public CubicBeaconInterface beaconInterface;
        public List<BeaconTemp> beaconCollection;


        private BeaconController(){
            if (beaconInstance != null){
                throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
            }
            beaconCollection = new ArrayList<>();
            beaconInterface = new CubicBeaconInterface() {
                @Override
                public void onExit(String beaconId) {

                }

                @Override
                public void onEnter(String beaconId) {

                }
            };
        }

        public void setBeaconInterface(CubicBeaconInterface beaconInterface) {
            this.beaconInterface = beaconInterface;
        }

        public void addBeaconChange(Beacon beacon){
            String beaconId = beacon.getId1().toString();

            double distance1 = beacon.getDistance();
            String distance = String.valueOf(Math.round(distance1*100.0)/100.0);

            boolean isExist = isExistRegion(beaconId);

            if(!isExist){
                beaconInterface.onEnter(beacon.getId1().toString());
                beaconCollection.add(new BeaconTemp(beaconId));
            } else {
                int beaconIndex  = indexOfBeacon(beaconId);
                if(beaconIndex != -1){
                    beaconCollection.set(beaconIndex, new BeaconTemp(beaconId));
                }
            }


        }

        public synchronized static BeaconController getInstance(){
            if (beaconInstance == null) {
                beaconInstance = new BeaconController();
            }
            return beaconInstance;
        }


        public boolean isExistRegion(String beaconId){
            for(int x = 0; x< beaconCollection.size(); x++){
                if(beaconCollection.get(x).beaconId.equals(beaconId)){
                    return true;
                } else if(x == beaconCollection.size()-1){
                    return false;
                }
            }

            return false;
        }

        public int indexOfBeacon(String id){
            for(int x = 0; x< beaconCollection.size(); x++){
                BeaconTemp beaconTemp = beaconCollection.get(x);
                if(beaconTemp.beaconId.equals(id)){
                    return x;
                }
            }

            return -1;
        };

        public void clearOutOfRegionBeacons() {
            Calendar today = Calendar.getInstance();
            for(int x = 0; x< beaconCollection.size(); x++){
                BeaconTemp beaconTemp = beaconCollection.get(x);
                long lastUpdate = beaconTemp.logged;
                long timeDiff = today.getTimeInMillis() - lastUpdate;
                long timeDiffSec = timeDiff / (1000);

                if(timeDiffSec> BEACON_EXPIRATION_TIME){
                    beaconInterface.onExit(beaconTemp.beaconId);
                    beaconCollection.remove(x);
                }
            }
        }

        public interface CubicBeaconInterface {
            void onExit(String beaconId);
            void onEnter(String beaconId);
        }

        class BeaconTemp {
            String beaconId;
            long logged;

            public BeaconTemp(String beaconId) {
                this.beaconId =  beaconId;
                this.logged = new Date().getTime();
            }


        }

}

