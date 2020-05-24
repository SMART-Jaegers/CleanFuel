package com.smartjaegers.checkfuel.models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.smartjaegers.checkfuel.managers.AuthenticationManager;
import com.smartjaegers.checkfuel.managers.DatabaseManagerForQuality;

import java.util.LinkedList;
import java.util.List;

public class DataBluetooth {

    private List<Quality> listOfTop;

    private static final double MIN_SPEED_FOR_CALCULATIONS = 72.0;

    private double speedInKmPerHour;
    private double revolutionPerMinute;
    private double fuelFlowRate;
    private double currentVolumeInPercent; // З цього перетворювати у літри, знаючи машину
    private double currentVolumeInLiters;
    private double currentFuelQuality;

    private double liter; //з OBD
    private double alreadyKm; //з OBD
    private double litersPerKm; //(літриДо - літриПісля)/кілометри з OBD

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] data = intent.getByteArrayExtra("theMessage");
            try {
                speedInKmPerHour = data[0];
                revolutionPerMinute = data[1];
                fuelFlowRate = data[2];
                currentVolumeInPercent = data[3];
                currentVolumeInLiters = data[4];
                Log.d("---DataBluetooth: ", "broadcastReceiver: onReceive: "+ speedInKmPerHour);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double calculateQuality() {
        currentFuelQuality = 1 / calculateAverageOfInvertedQuality();
        return currentFuelQuality;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private double calculateAverageOfInvertedQuality() {
        LinkedList<Double> listToCalculate = addInverseQualityToList();
        if (listToCalculate.size() != 0) {
            return listToCalculate.stream().reduce(0.0, Double::sum) / listToCalculate.size();
        }
        return 1;
    }

    private LinkedList<Double> addInverseQualityToList() {
        LinkedList<Double> listOfInverseQuality = new LinkedList<>();
        do {
            // брати дані з блютузу :)
            if (speedInKmPerHour >= MIN_SPEED_FOR_CALCULATIONS) {
                double inverseQuality = calculateInverseQuality(speedInKmPerHour, revolutionPerMinute, fuelFlowRate);
                listOfInverseQuality.add(inverseQuality);
            }
        } while (false);                  // доки машина їдe
        return listOfInverseQuality;
    }

    private double calculateInverseQuality(double speedInKmPerHour, double revolutionPerMinute, double fuelFlowRate) {
        return fuelFlowRate * revolutionPerMinute / (speedInKmPerHour * speedInKmPerHour);
    }

//*********************************************

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double calculatePredictionOnKm() {
        return /*currentVolumeInLiters*/10.0 * calculateCoefficient() / 1.0/*fuelFlowRate*/;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private double calculateCoefficient() {
        return calculateQuality() / calculateAverageTopQuality();
    }

    private double calculateAverageTopQuality() {
        double allSum = 0;
        double allNumber = 0;
        AuthenticationManager authenticationManager = new AuthenticationManager();
        if (authenticationManager.entryToDatabase()) {
            DatabaseManagerForQuality.readQuality(new OnGetResult() {
                @Override
                public void onSuccess() {
                    listOfTop = DatabaseManagerForQuality.getQualities();
                    Log.i("---Quality---", "success reading quality");
                }

                @Override
                public void onStart() {
                    Log.i("---Quality---", "start reading quality");
                }

                @Override
                public void onFailure() {
                    Log.i("---Quality---", "fail reading quality");
                }
            });

            for (int count = 0; count < listOfTop.size(); count++) {
                allSum += listOfTop.get(count).getRate() * listOfTop.get(count).getNumberOfUse();
                allNumber += listOfTop.get(count).getNumberOfUse();
            }

            return allSum / allNumber;
        }
        return 1;
    }

    public double getLiter() {
        return liter;
    }

    public void setLiter(double liter) {
        this.liter = liter;
    }

    public double getAlreadyKm() {
        return alreadyKm;
    }

    public void setAlreadyKm(double alreadyKm) {
        this.alreadyKm = alreadyKm;
    }

    public double getLitersPerKm() {
        return litersPerKm;
    }

    public void setLitersPerKm(double litersPerKm) {
        this.litersPerKm = litersPerKm;
    }
}
