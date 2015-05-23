package carpark.sg.com.carparksg.logic;

import android.content.Context;
import android.location.Address;

import java.io.IOException;
import java.util.List;

/**
 * Created by joseph on 4/5/2015.
 */
public class Logic {



    // default constructor
    public Logic() {}


    public static void searchNearbyCarparkAddress(Context context, String address) throws IOException{
        //List<Address> mResultList = GoogleLocation.getListOfAddresses(context, address);

        //printAddresses(mResultList);


    }

    private static void printAddresses(List<Address> list){
        System.out.print("Latitude\t");
        System.out.print("Longitude\t");
        System.out.print("Address");
        System.out.println("");
        System.out.println("====================================================================================");
        for(Address a : list){
            System.out.print(GoogleLocation.getLatitudeFromAddress(a) + "\t");
            System.out.print(GoogleLocation.getLongitudeFromAddress(a) + "\t");
            System.out.print(GoogleLocation.getFullStreetFromAddress(a) );
            System.out.println("");
        }
    }



}
