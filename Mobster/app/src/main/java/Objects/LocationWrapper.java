package Objects;

import android.location.Location;

/**
 * Wrapper around android.location.Location which adds a no-args constructor
 * (required for Firebase deserialization)
 * 
 * @author Ani
 * @version 1.0
 */
public class LocationWrapper extends Location {
    /**
     * Constructor for Location Wrapper class
     */
    public LocationWrapper() {super("");}
}
