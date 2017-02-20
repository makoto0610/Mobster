package Objects;

import android.location.Location;

/**
 * Created by anireddy on 2/19/17.
 */

/**
 * Wrapper around android.location.Location which adds a no-args constructor
 * (required for Firebase deserialization)
 */
public class LocationWrapper extends Location {
    public LocationWrapper() {super("");}
}
