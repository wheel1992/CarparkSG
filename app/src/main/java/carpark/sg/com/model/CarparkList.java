package carpark.sg.com.model;

import java.util.ArrayList;

/**
 * Created by joseph on 3/5/2015.
 */
public class CarparkList {

    private static CarparkList mCarparkList = new CarparkList();
    private ArrayList<Carpark> mList = new ArrayList<Carpark>();
    private CarparkList(){} // prevent creating new instance


    public static CarparkList getInstance(){

        return mCarparkList;
    }

    public Carpark getCarpark(int pos){
        if(mList != null){
            return mList.get(pos);
        }

        return null;
    }

    public void removeAll(){
        if(mList.size() > 0){
            mList = new ArrayList<Carpark>();
        }
    }

    public boolean addNewCarpark(Carpark mCp){
        if(mList != null){
            mList.add(mCp);
            return true;
        }
        return false;
    }

    public Carpark searchCarpark(String id){
        for(Carpark cp : mList){
            if(cp.getID().equalsIgnoreCase(id)){
                return cp;
            }
        }
        return null;
    }

    public void updateCarpark(Carpark newCp){
        int index = 0;
        for(Carpark cp : mList){
            if(cp.getID().equalsIgnoreCase(newCp.getID())){
                index = mList.indexOf(cp);
                mList.set(index, newCp);
                return;
            }
        }
    }

    public void updateCarparkAvailableLot(Carpark newCp){
        int index = 0;
        for(Carpark cp : mList){
            if(cp.getID().equalsIgnoreCase(newCp.getID())){
                index = mList.indexOf(cp);
                mList.get(index).setAvailableLot(newCp.getAvailableLot());
                return;
            }
        }
    }

    public int getSize(){
        return this.mList.size();
    }

    public ArrayList<Carpark> getList(){
        return this.mList;
    }







}
