package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


public class AddActivity extends AppCompatActivity {

    private Button btnCancel, btnSignUp, btnProfile;
    private EditText etName, etEmail, etPhone, etSalary, etBio, etSubject;
    private CheckBox AL, BL, CL, DL;
    private Spinner spPosition;
    private TextView tvProfile;
    User user;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private StorageTask uploadTask;
    private Uri filePath=Uri.parse("");
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;

    private String name;
    private String position;
    private String subject;
    private String bio;
    private String oblTo;
    private String classes;
    private int salary;
    private String email;
    private String phone;
    private String uri;
    Session sharedpref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }else{
            setTheme(R.style.AppTheme);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        btnCancel = findViewById(R.id.btnCancel);
        btnSignUp = findViewById(R.id.btnRegister);
        btnProfile = findViewById(R.id.btnProfile);
        tvProfile = findViewById(R.id.tvProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etSalary = findViewById(R.id.etSalary);
        etBio = findViewById(R.id.etBio);
        etSubject = findViewById(R.id.etSubject);
        AL = findViewById(R.id.AL);
        BL = findViewById(R.id.BL);
        CL = findViewById(R.id.CL);
        DL = findViewById(R.id.DL);
        spPosition = findViewById(R.id.spPosition);

        mDatabase = mDatabase = FirebaseDatabase.getInstance().getReference("employees");

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Profile Picture"), PICK_IMAGE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valid = true;
                name = etName.getText().toString();
                if(name.isEmpty()){
                    name="";
                }

                email = etEmail.getText().toString();
                if(email.isEmpty()){
                    etEmail.setError("Email cannot be empty");
                    valid = false;
                }

                String sal = etSalary.getText().toString();
                if(sal.isEmpty()){
                    salary = 0;
                }else{
                    salary = Integer.parseInt(sal);
                }

                bio = etBio.getText().toString();
                if(bio.isEmpty()){
                    bio="";
                }

                subject = etSubject.getText().toString();
                if(subject.isEmpty()){
                    subject="";
                }
                oblTo = "";
                if (spPosition.equals("Coordinator & Lecturer")) {
                    oblTo = "UMN";
                } else if (spPosition.equals("Lecturer")) {
                    oblTo = "Coordinator & Lecturer";
                } else if (spPosition.equals("Head of Lab")) {
                    oblTo = "Lecturer";
                } else if (spPosition.equals("Lab Coordinator")) {
                    oblTo = "Head of Lab";
                } else if (spPosition.equals("Lab Assistant")) {
                    oblTo = "Lab Coordinator";
                }

                position = spPosition.getSelectedItem().toString();
                if(position.isEmpty()){
                    position="";
                }

                classes = checkTheBox();

                phone = etPhone.getText().toString();
                if(phone.isEmpty()){
                    phone="";
                    valid = false;
                }

                if(valid){
                    mStorageRef = FirebaseStorage.getInstance().getReference().child("profilePicture/" + email);
                    user = new User(name, position, subject, bio, oblTo, classes, salary, email, phone,"");
                    if(!(uploadTask!=null && uploadTask.isInProgress())){
                        if(!filePath.toString().equals("")){
                            final StorageReference ref = mStorageRef.child(email);
                            uploadTask = ref.putFile(filePath);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            user.setUri(uri.toString());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            });
                        }
                    }
                    mDatabase.child(email).setValue(user);
                    Intent intent = new Intent(AddActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private String checkTheBox() {
        String classes = "";
        if (AL.isChecked()) {
            classes = classes.concat("AL");
        }
        if (BL.isChecked()) {
            if (classes.equals("")) {
                classes = classes.concat("BL");
            } else {
                classes = classes.concat(", BL");
            }
        }
        if (CL.isChecked()) {
            if (classes.equals("")) {
                classes = classes.concat("CL");
            } else {
                classes = classes.concat(", CL");
            }
        }
        if (DL.isChecked()) {
            if (classes.equals("")) {
                classes = classes.concat("DL");
            } else {
                classes = classes.concat(", DL");
            }
        }
        return classes;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            filePath = data.getData();
            String filename = filePath.toString().substring(filePath.toString().lastIndexOf("/")+1);
            tvProfile.setText(filename);
        }
    }
}
