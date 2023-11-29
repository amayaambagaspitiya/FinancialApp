package com.example.moneyminder;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DashboardFragment extends Fragment {

    private FloatingActionButton main_btn;
    private boolean isOpen;
    private Animation fadeOpen, fadeClose;

    private TextView totalIncomeTextView;
    private TextView totalExpenseTextView;
    private TextView lastIncomeAmountTextView;
    private TextView lastExpenseAmountTextView;
    private TextView lastIncomeDateTextView;
    private TextView lastExpenseDateTextView;

    private PieChart pieChart;

    private int sumIncome, sumExpense;
    private int lastIncomeAmount, lastExpenseAmount;
    private String lastIncomeDate, lastExpenseDate;
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initializing Firebase authentication and database references
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance("https://moneyminder-252a9-default-rtdb.firebaseio.com/").getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance("https://moneyminder-252a9-default-rtdb.firebaseio.com/").getReference().child("ExpenseData").child(uid);

        // Finding UI elements by their IDs
        main_btn = myView.findViewById(R.id.dashboard_fragment_main_plus_button);

        totalIncomeTextView = myView.findViewById(R.id.income_set_result);
        totalExpenseTextView = myView.findViewById(R.id.expense_set_result);

        lastIncomeAmountTextView = myView.findViewById(R.id.amount_last_income);
        lastExpenseAmountTextView = myView.findViewById(R.id.amount_last_expense);
        lastIncomeDateTextView = myView.findViewById(R.id.date_last_income);
        lastExpenseDateTextView = myView.findViewById(R.id.date_last_expense);

        pieChart = myView.findViewById(R.id.pieChart);

        // Loading animations
        fadeOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        fadeClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        // Setting onClickListener for the main button
        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });

        sumIncome = 0;
        sumExpense = 0;
        lastIncomeAmount = 0;
        lastExpenseAmount = 0;
        lastExpenseDate = null;
        lastIncomeDate = null;

        // Setting up ValueEventListener for income data
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    sumIncome += data.getAmount();
                    lastIncomeAmount = data.getAmount();
                    lastIncomeDate = data.getDate();
                }

                // Updating the UI and PieChart

                totalIncomeTextView.setText(String.valueOf(sumIncome));
                lastIncomeAmountTextView.setText(String.valueOf(lastIncomeAmount));
                lastIncomeDateTextView.setText(lastIncomeDate);
                updatePieChart(myView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updatePieChart(myView);
            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    sumExpense += data.getAmount();
                    lastExpenseAmount = data.getAmount();
                    lastExpenseDate = data.getDate();
                }
                totalExpenseTextView.setText(String.valueOf(sumExpense));
                lastExpenseAmountTextView.setText(String.valueOf(lastExpenseAmount));
                lastExpenseDateTextView.setText(lastExpenseDate);
                updatePieChart(myView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updatePieChart(myView);
            }
        });

        return myView;
    }

    private void updatePieChart(View myView) {
        pieChart = myView.findViewById(R.id.pieChart);

       // Creates a list to hold entries for the PieChart.
        ArrayList<PieEntry> entries = new ArrayList<>();
        float fSumIncome = sumIncome;
        float fSumExpense = sumExpense;

        entries.add(new PieEntry(fSumIncome, "Income"));//Adds a PieEntry representing income to the list.
        entries.add(new PieEntry(fSumExpense, "Expense"));

        // Create a dataset with the entries
        PieDataSet dataSet = new PieDataSet(entries, "Income vs Expense");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(16f);

        // Create pie data with the dataset
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Budget");
        pieChart.invalidate();
    }

    private void addData() {
        // AlertDialog setup
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.insert_layout,null);
        myDialog.setView(myView);
        AlertDialog dialog = myDialog.create();

        // Finding UI elements in the AlertDialog
        RadioGroup radioGroupType = myView.findViewById(R.id.radio_group_type);

        EditText editAmount = myView.findViewById(R.id.amount);
        EditText editDescription = myView.findViewById(R.id.description);

        Button btnSave = myView.findViewById(R.id.btn_save);
        Button btnCancel = myView.findViewById(R.id.btn_cancel);

        // Set OnClickListener for the "Save" button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = radioGroupType.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(getActivity(), "Please select a type (Income or Expense)", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = myView.findViewById(selectedId);
                String type = selectedRadioButton.getText().toString().trim();

                // Get amount and description from EditText fields

                String amount = editAmount.getText().toString().trim();
                String description = editDescription.getText().toString().trim();

                if (TextUtils.isEmpty(amount)) {
                    editAmount.setError("Amount is Required...");
                    return;
                }
                int converted_amount = Integer.parseInt(amount);

                if (TextUtils.isEmpty(description)) {
                    editDescription.setError("Description is Required...");
                    return;
                }

                // Get the current date

                String mDate = DateFormat.getDateInstance().format(new Date());

                // Add data to Firebase based on type (Income or Expense)

                if (type.equals("Income")) {
                    String id = mIncomeDatabase.push().getKey();
                    Data data = new Data(converted_amount,type,description,id,mDate);
                    mIncomeDatabase.child(id).setValue(data);
                    Toast.makeText(getActivity(),"Data added.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                } else if (type.equals("Expense")) {
                    String id = mExpenseDatabase.push().getKey();
                    Data data = new Data(converted_amount,type,description,id,mDate);
                    mExpenseDatabase.child(id).setValue(data);
                    Toast.makeText(getActivity(),"Data added.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}