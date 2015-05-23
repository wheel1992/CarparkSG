package carpark.sg.com.model;

/**
 * Created by joseph on 4/5/2015.
 */
public class Constant {

    public static final int SEARCH_HDB_NEARBY_CARPARK_USING_ADDRESS = 1;
    public static final int SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE = 2;
    public static final int SEARCH_HDB_SPECIFIC_CARPARK_DETAIL = 3;

    // URL
    public static final String DUMMY_ADDRESS = "[address]";
    public static final String DUMMY_LATITUDE = "[lat]";
    public static final String DUMMY_LONGITUDE = "[long]";
    public static final String DUMMY_RADIUS = "[radius]";
    public static final String DUMMY_NUMBER = "[number]";
    public static final String RADIUS = "500";
    public static final String URL_GEOCODE_HTTP_ADDRESS_QUERY = "http://52.74.180.246:8080/carparksg/logic.php?a=[address]";
    public static final String URL_HDB_HTTP_CARPARK_AVAILABILITY_QUERY = "http://52.74.180.246:8080/carparksg/logic.php?long=[long]&lat=[lat]&r=[radius]";
    public static final String URL_HDB_HTTP_CARPARK_DETAIL_QUERY = "http://52.74.180.246:8080/carparksg/logic.php?long=[long]&lat=[lat]&n=[number]";

    // Google Street View URL
    public static final String URL_GOOGLE_STREET_VIEW_QUERY = "https://maps.googleapis.com/maps/api/streetview?size=640x640&location=[lat],[long]&fov=90&heading=270&pitch=5";
    // Google set direction
    public static final String URL_GOOGLE_MAP_DIRECTION_QUERY = "https://maps.google.com/maps?saddr=[srcLat],[srcLong]&daddr=[destLat],[destLong]";
    public static final String URL_GOOGLE_MAP_STREET_VIEW_QUERY = "google.streetview:cbll=[lat],[long]";
    public static final String URI_GOOGLE_MAP = "com.google.android.apps.maps";

    //Toast messages
    public static final String TOAST_FAVOURITED_MESSAGE = "Marked as favourite";
    public static final String TOAST_UNFAVOURITED_MESSAGE = "Marked as unfavourite";

    // AlertDialog
    public static final String ALERT_DIALOG_GPS_DSIABLED_MESSAGE = "Your GPS seems to be disabled, would you like to enable it?";


    // Fragment names to add back stack
    public static final String FRAGMENT_SEARCH_NAME = "FragmentSearch";
    public static final String FRAGMENT_FAVOURITE = "FragmentFavourite";
    public static final String FRAGMENT_RECENT = "FragmentRecent";
    public static final String FRAGMENT_CARPARK_DETAIL_NAME = "FragmentCarparkDetail";


    //Fragment Carpark Detail
    public static final String LOT_NOT_AVAILABLE = "Not Available";
    public static final String LOT_ZERO_AVAILABLE = "0 Lot Left";
    public static final String LOT_AVAILABLE = " Lots Available";
    public static final String LOT_LOADING = "Getting Latest Lots...";
    public static final String LOT_ERROR_LOADING = "Error. Please Refresh Again";
    public static final int INDEX_LOT_LOADING = -2;
    public static final int INDEX_LOT_ERROR_LOADING = -3;

    //Coordinate value
    public static final double NO_LOCATION_LATITUDE = -1.0;
    public static final double NO_LOCATION_LONGITUDE = -1.0;

    //Save history in a file
    public static final String HISTORY_FILE_NAME = "history.txt";
    public static final String FAVOURITE_FILE_NAME = "favourite.txt";


    //Error messages
    public static final String ERROR_NO_FAVOURITE = "No favourite found.";
    public static final String ERROR_NO_RECENT = "No recent search found.";

}
