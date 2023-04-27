package com.traceme.uploadrcitdata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Intent intent;

    CardView branchSyllabus, branchNotes,branchPyq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        branchSyllabus = findViewById(R.id.branchSyllabus);
        branchNotes = findViewById(R.id.branchNotes);
        branchPyq = findViewById(R.id.branchPyq);



        branchSyllabus.setOnClickListener(this);
        branchNotes.setOnClickListener(this);
        branchPyq.setOnClickListener(this);

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.branchSyllabus:
                intent = new Intent(MainActivity.this,SyllabusActivity.class);
                break;
            case R.id.branchNotes:
                intent = new Intent(MainActivity.this, NotesActivity.class);
                break;
            case R.id.branchPyq:
                intent = new Intent(MainActivity.this, PyqActivity.class);
                break;
        }
        this.startActivity(intent);
    }

}