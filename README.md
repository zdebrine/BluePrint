# BluePrint

This application monitors for Enter/exit events of your Eddystone Bluetooth beacons.

# How to add beacons

In ..constants/Beacon.java under BEACONS_IDS = {, add the namespace ID of your Eddystone beacon. (**The beacon needs to be in the UID format. You can change the format of your beacon with the NRF for Eddystone app**)

# Save your project and run on your device

From here, enter and exit events are monitored and added you your Firebase Database.
