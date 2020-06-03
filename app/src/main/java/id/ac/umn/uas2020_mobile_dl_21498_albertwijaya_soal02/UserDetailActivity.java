package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity {
    User user;
    private CoordinatorLayout coordinatorLayout;
    private TextView tvName, tvPosition, tvSubject, tvBio, tvOblTo;
    private TextView tvClasses, tvSalary, tvEmail, tvPhone;
    private CircleImageView image_view, image_edit;

    private EditText etName, etSubject, etBio;
    private EditText etSalary, etPhone;
    private LinearLayout check;
    private CheckBox AL, BL, CL, DL;
    private Spinner spPosition;

    private Button btnEdit, btnLogout, btnDone, btnBack;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private StorageTask uploadTask;
    private Uri filePath=Uri.parse("");

    private static final int PICK_IMAGE = 1;
    Session sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }else{
            setTheme(R.style.AppTheme);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        tvName = findViewById(R.id.tvName);
        tvPosition = findViewById(R.id.tvPosition);
        tvSubject = findViewById(R.id.tvSubject);
        tvBio = findViewById(R.id.tvBio);
        tvOblTo = findViewById(R.id.tvOblTo);
        tvClasses =findViewById(R.id.tvClasses);
        tvSalary = findViewById(R.id.tvSalary);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        image_view = findViewById(R.id.image_view);
        image_edit = findViewById(R.id.image_edit);

        etName = findViewById(R.id.etName);
        spPosition = findViewById(R.id.spPosition);
        etSubject = findViewById(R.id.etSubject);
        etBio = findViewById(R.id.etBio);
        etSalary = findViewById(R.id.etSalary);
        etPhone = findViewById(R.id.etPhone);
        check = findViewById(R.id.check);
        AL = findViewById(R.id.AL);
        BL = findViewById(R.id.BL);
        CL = findViewById(R.id.CL);
        DL = findViewById(R.id.DL);

        btnEdit = findViewById(R.id.btnEdit);
        btnLogout = findViewById(R.id.btnLogout);
        btnDone = findViewById(R.id.btnDone);
        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        seedData(user);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("employees");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profilePicture/" + user.getName().toString());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seedData(user);
                viewMode();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDetailActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        image_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Profile Picture"), PICK_IMAGE);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                user.setName(name);

                String position = spPosition.getSelectedItem().toString();
                user.setPosition(position);

                String subject = etSubject.getText().toString();
                user.setSubject(subject);

                String bio = etBio.getText().toString();
                user.setBio(bio);

                String oblTo = "";
                if (position.equals("Coordinator & Lecturer")) {
                    oblTo = "UMN";
                } else if (position.equals("Lecturer")) {
                    oblTo = "Coordinator & Lecturer";
                } else if (position.equals("Head of Lab")) {
                    oblTo = "Lecturer";
                } else if (position.equals("Lab Coordinator")) {
                    oblTo = "Head of Lab";
                } else if (position.equals("Lab Assistant")) {
                    oblTo = "Lab Coordinator";
                }
                user.setOblTo(oblTo);

                String classes = checkTheBox();
                user.setClasses(classes);

                int salary = Integer.parseInt(etSalary.getText().toString());
                user.setSalary(salary);

                String phone = etPhone.getText().toString();
                user.setPhone(phone);

                if(!(uploadTask!=null && uploadTask.isInProgress())){
                    if(!filePath.toString().equals("")){
                        final StorageReference ref = mStorageRef.child(user.getEmail());
                        uploadTask = ref.putFile(filePath);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        user.setUri(uri.toString());
                                        seedData(user);
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
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatabase.child(user.getName()).setValue(user);
                    }
                }, 5000);
                viewMode();
            }
        });
    }
    private void editMode() {
        image_view.setVisibility(View.GONE);
        image_edit.setVisibility(View.VISIBLE);

        tvName.setVisibility(View.GONE);
        etName.setVisibility(View.VISIBLE);

        tvPosition.setVisibility(View.GONE);
        spPosition.setVisibility(View.VISIBLE);

        tvSubject.setVisibility(View.GONE);
        etSubject.setVisibility(View.VISIBLE);

        tvBio.setVisibility(View.GONE);
        etBio.setVisibility(View.VISIBLE);

        tvClasses.setVisibility(View.GONE);
        check.setVisibility(View.VISIBLE);

        tvSalary.setVisibility(View.GONE);
        etSalary.setVisibility(View.VISIBLE);

        tvPhone.setVisibility(View.GONE);
        etPhone.setVisibility(View.VISIBLE);

        btnEdit.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);
        btnDone.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
    }
    private void viewMode() {
        image_edit.setVisibility(View.GONE);
        image_view.setVisibility(View.VISIBLE);

        etName.setVisibility(View.GONE);
        tvName.setVisibility(View.VISIBLE);

        spPosition.setVisibility(View.GONE);
        tvPosition.setVisibility(View.VISIBLE);

        etSubject.setVisibility(View.GONE);
        tvSubject.setVisibility(View.VISIBLE);

        etBio.setVisibility(View.GONE);
        tvBio.setVisibility(View.VISIBLE);

        tvClasses.setVisibility(View.VISIBLE);
        check.setVisibility(View.GONE);

        etSalary.setVisibility(View.GONE);
        tvSalary.setVisibility(View.VISIBLE);

        etPhone.setVisibility(View.GONE);
        tvPhone.setVisibility(View.VISIBLE);

        btnDone.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.VISIBLE);
    }

    private void seedData(User user) {

        if(!user.getUri().equals("")){
            Uri newUri = Uri.parse(user.getUri());
            Glide.with(this).load(newUri).into(image_view);
            Glide.with(this).load(newUri).into(image_edit);
        }else{
            Glide.with(this).load(R.drawable.a).into(image_view);
            Glide.with(this).load(R.drawable.a).into(image_edit);
        }

        tvName.setText(user.getName());
        etName.setText(user.getName());

        String position = user.getPosition();
        tvPosition.setText(position);
        if (position.equals("Coordinator & Lecturer")) {
            spPosition.setSelection(0);
        } else if (position.equals("Lecturer")) {
            spPosition.setSelection(1);
        } else if (position.equals("Head of Lab")) {
            spPosition.setSelection(2);
        } else if (position.equals("Lab Coordinator")) {
            spPosition.setSelection(3);
        } else if (position.equals("Lab Assistant")) {
            spPosition.setSelection(4);
        }

        tvSubject.setText(user.getSubject());
        etSubject.setText(user.getSubject());

        tvBio.setText(user.getBio());
        etSubject.setText(user.getSubject());

        tvOblTo.setText(user.getOblTo());

        String classes = user.getClasses();
        tvClasses.setText(classes);
        if (classes.contains("AL")) {
            AL.setChecked(true);
        } else {
            AL.setChecked(false);
        }
        if (classes.contains("BL")) {
            BL.setChecked(true);
        } else {
            BL.setChecked(false);
        }
        if (classes.contains("CL")) {
            CL.setChecked(true);
        } else {
            CL.setChecked(false);
        }
        if (classes.contains("DL")) {
            DL.setChecked(true);
        } else {
            DL.setChecked(false);
        }



        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        tvSalary.setText(String.valueOf(kursIndonesia.format(user.getSalary())));
        etSalary.setText(String.valueOf(user.getSalary()));

        tvEmail.setText(user.getEmail());

        tvPhone.setText(user.getPhone());
        etPhone.setText(user.getPhone());


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
            image_edit.setImageURI(filePath);
            image_view.setImageURI(filePath);
        }
    }
}