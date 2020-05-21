package com.smartjaegers.checkfuel.managers;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartjaegers.checkfuel.adapters.HistoryAdapter;
import com.smartjaegers.checkfuel.adapters.WarningAdapter;
import com.smartjaegers.checkfuel.models.DayOfUse;
import com.smartjaegers.checkfuel.models.Problem;
import com.smartjaegers.checkfuel.models.Refill;

import java.util.List;

public class WarningManager {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setConfig(RecyclerView recyclerView, Context context, List<Problem> problems, String sortBy) {

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new WarningAdapter(problems, context));
    }

}