package com.traceme.uploadrcitdata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class SyllabusActivity extends AppCompatActivity {

    StorageReference db;
    DatabaseReference dataPdf;
    Spinner semSpinner;
    ArrayList<String> semList = new ArrayList<>();
   private RadioGroup radioGroup;
    private RadioButton radioBranch;
    private ImageButton uploadImgBtn;
    private TextInputEditText uploadEditText;
    private static final int REQ_CODE_PDF = 15;
    
    AppCompatButton uploadBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        
        semSpinner = findViewById(R.id.semSpinner);
        uploadBtn = findViewById(R.id.uploadBtn);
        uploadImgBtn = findViewById(R.id.uploadImgBtn);
        uploadEditText = findViewById(R.id.uploadEditText);

        //finding Id of Radio group
        radioGroup = findViewById(R.id.radioGrp);


        //Loading semester
        semList.add("Sem 3");
        semList.add("Sem 4");
        semList.add("Sem 5");
        semList.add("Sem 6");
        semList.add("Sem 7");
        semList.add("Sem 8");
        ArrayAdapter<String> semListAdatpter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,semList);
        semSpinner.setAdapter(semListAdatpter);

        //Storage and Database Reference
        db = FirebaseStorage.getInstance().getReference();
        dataPdf = FirebaseDatabase.getInstance().getReference("syllabus");

        uploadBtn.setEnabled(false);

        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPdf();
            }
        });

    }

    private void selectPdf() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent,"Select Syllabus"),REQ_CODE_PDF);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_PDF && resultCode == RESULT_OK && data!=null && data.getData()!=null){

            uploadBtn.setEnabled(true);
            uploadEditText.setText(data.getDataString());

            uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //getting value of branch
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radioBranch = findViewById(selectedId);
                    String branch = radioBranch.getText().toString();
                    System.out.println("Radio Branch :"+branch);
                    //getting value of semester
                    String semester = semSpinner.getSelectedItem().toString();

                    //uploading pdfFile to firebase
                    final ProgressDialog progressDialog = new ProgressDialog(SyllabusActivity.this);
                    progressDialog.setTitle("Uploading PDF...");
                    progressDialog.show();

                    StorageReference storageReference = db.child("Syllabus").child(uploadEditText.getText().toString());
                    storageReference.putFile(data.getData())
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    //Generating link
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while(!uriTask.isComplete());
                                    Uri uri = uriTask.getResult();

                                    //uploading syllabus under semester and branch name
                                    SyllabusDetails syllabusDetails = new SyllabusDetails(uploadEditText.getText().toString(),uri.toString());
                                    String Key = dataPdf.push().getKey();
                                    dataPdf.child(semester).child(branch).child(Key).setValue(syllabusDetails);
                                    Toast.makeText(SyllabusActivity.this, "File Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    uploadBtn.setEnabled(false);

                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                    double progress = (100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                                    progressDialog.setMessage("File Uploaded "+(int)progress+"%");

                                }
                            });

                }
            });

        }
    }
}