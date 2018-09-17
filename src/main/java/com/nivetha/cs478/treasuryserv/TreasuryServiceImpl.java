package com.nivetha.cs478.treasuryserv;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.JsonReader;
import android.util.Log;

import com.nivetha.cs478.treasuryServCommon.TreasuryService;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nivetha on 12/4/17.
 */

public class TreasuryServiceImpl extends Service {

    // Service API url
    private String apiURL = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=";

    // Flags to determine the Service status
    public static boolean isBound = false;
    public static boolean isIdle = true;
    public static boolean isDestroyed = false;

    // Implement the Stub for this Object
    private final TreasuryService.Stub mBinder = new TreasuryService.Stub() {
        @Override
        public int[] monthlyCash(int year) throws RemoteException {

            // Setting status to running API method
            isIdle = false;
            int[] monthlyCash = new int[12];

            // SQL query to fetch monthyCash from server
            String sqlQuery = "SELECT sum(\"open_today\") as \"cash\" FROM t1 WHERE \"year\" = "+year+" group by \"month\"";
            try{
                // Fetching data from server
                URL url = new URL(apiURL+sqlQuery);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Parse the response from server
                monthlyCash = parseInputStream(in, 12);
            }
            catch (Exception e){ e.printStackTrace();}
            return monthlyCash;
        }

        @Override
        public int[] dailyCash(int day, int month, int year, int workingDays) throws RemoteException {

            // Set status to running API method
            isIdle = false;
            ++workingDays;
            int[] dailyCash = new int[workingDays];

            // SQL query to fetch dailyCash
            String sqlQuery = "SELECT sum(\"open_today\") as \"cash\" FROM t1 WHERE \"date\" >= '"+year+"-"+month+"-"+day+"' and \"month\" >= "+month+" group by \"day\" limit "+workingDays;
            try{
                // Fetching data from server
                URL url = new URL(apiURL+sqlQuery);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Parse the response from server
                dailyCash = parseInputStream(in, workingDays);
            }
            catch (Exception e){e.printStackTrace();}
            return dailyCash;
        }

        @Override
        public int yearlyAvg(int year) throws RemoteException {

            // Set status to running API method
            isIdle = false;
            int yearlyAvg = 0;

            // SQL query to fetch yearlyAvg
            String sqlQuery = "SELECT avg(\"open_today\") as \"average\" FROM t1 WHERE \"year\" = "+year;
            try{
                // Fetching data from server
                URL url = new URL(apiURL+sqlQuery);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Parse response from server
                yearlyAvg = parseInputStream(in, 1)[0];
            }
            catch (Exception e){e.printStackTrace();}
            return yearlyAvg;
        }
    };

    // Parse InputStream from server API
    private int[] parseInputStream(InputStream in, int resSize){
        int[] response = new int[resSize];
        int i=0;
        try{
            JsonReader reader = new JsonReader(new InputStreamReader(in,"UTF-8"));
            reader.beginArray();
            while (reader.hasNext()){
                reader.beginObject();
                while (reader.hasNext()){
                    String key = reader.nextName();
                    if(key.equals("cash")){
                        response[i] = reader.nextInt();
                        Log.i("response[i]",""+response[i]);
                    }

                    else if(key.equals("average"))
                        response[i] = (int)reader.nextDouble();
                }
                ++i;
                reader.endObject();
            }
        }
        catch (IOException e) {}

        Log.i("parsedResult",""+response[0]);
        return response;
    }

    // Return the Stub defined above
    @Override
    public IBinder onBind(Intent intent) {
        isBound = true;
        isDestroyed = false;
        isIdle = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;
        isIdle = true;
        return  super.onUnbind(intent);
    }

    @Override
    public void onDestroy(){
        isDestroyed = true;
        super.onDestroy();
    }

}
