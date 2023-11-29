package com.example.moneyminder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenseFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;


    private TextView totalExpenseTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_expense, container, false);

        // Initialize Firebase
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance("https://moneyminder-252a9-default-rtdb.firebaseio.com/").getReference().child("ExpenseData").child(uid);
        totalExpenseTextView = myView.findViewById(R.id.expense_text_result); //totalExpenseTextView for displaying the total expense
        recyclerView = myView.findViewById(R.id.recycler_id_expense);
        recyclerView.setHasFixedSize(true);

        // Set up the LinearLayoutManager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        // Calculate and display the total expense
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalExpense = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    totalExpense += data.getAmount();

                }
                totalExpenseTextView.setText(String.valueOf(totalExpense));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myView;
    }

    @Override

    //display a list of expenses in a RecyclerView
    public void onStart() {
        super.onStart();
//sets up a FirebaseRecyclerAdapter to populate the recyclerView with data from the databaseReference
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(databaseReference,Data.class).build();

        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data,MyViewHolder>(options) {

            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                // Bind data to the ViewHolder
                holder.setDate(model.getDate());
                holder.setAmount(model.getAmount());
                holder.setDescription(model.getDescription());
            }
        };

        // Set the adapter to the RecyclerView and start listening for data changes
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
//
        TextView date;
        TextView amount;
        TextView description;

        public MyViewHolder( View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_txt_expense);
            amount = itemView.findViewById(R.id.amount_txt_expense);
            description = itemView.findViewById(R.id.description_txt_expense);

        }

        public void setDate(String date) {

            this.date.setText(date);
        }

        public void setAmount(int amount) {
            this.amount.setText(String.valueOf(amount));
        }

        public void setDescription(String description) {
            this.description.setText(description);
        }
    }
}
