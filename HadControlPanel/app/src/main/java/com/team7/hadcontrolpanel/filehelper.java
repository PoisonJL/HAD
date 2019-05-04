package com.team7.hadcontrolpanel;

import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;

public class filehelper {
    public static final String FILENAME ="listinfo.dat";


    /**
     *write data
     */
    public static void writeData(ArrayList<String> items, Context context){
        try {
            //output the file
            FileOutputStream fos = context.openFileOutput(FILENAME, context.MODE_PRIVATE);
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(items);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *read data
     */
    public static ArrayList<String> readData(Context context) {
        ArrayList<String> itemList=null;
        try {
            // open from a file
            FileInputStream fis= context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            itemList =  (ArrayList<String>) ois.readObject();
        } catch (FileNotFoundException e) {
            itemList=new ArrayList<>();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //return with list of items
        return itemList;
    }
}