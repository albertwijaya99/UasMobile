package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.ui.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.HomeActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.MainActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.R;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.RegisterActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.Session;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.SplashActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.User;
import static android.app.Activity.RESULT_OK;

public class NotificationsFragment extends Fragment {
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

    private User user;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private StorageTask uploadTask;
    private Uri filePath=Uri.parse("");

    private static final int PICK_IMAGE = 1;
    private Context mContext;
    Session sharedpref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        sharedpref = new Session(getContext());
        if(sharedpref.loadNightModeState()) {
            getContext().setTheme(R.style.DarkTheme);
            getActivity().getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }else{
            getContext().setTheme(R.style.AppTheme);
            getActivity().getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }

        coordinatorLayout = root.findViewById(R.id.coordinatorLayout);
        tvName = root.findViewById(R.id.tvName);
        tvPosition = root.findViewById(R.id.tvPosition);
        tvSubject = root.findViewById(R.id.tvSubject);
        tvBio = root.findViewById(R.id.tvBio);
        tvOblTo = root.findViewById(R.id.tvOblTo);
        tvClasses = root.findViewById(R.id.tvClasses);
        tvSalary = root.findViewById(R.id.tvSalary);
        tvEmail = root.findViewById(R.id.tvEmail);
        tvPhone = root.findViewById(R.id.tvPhone);
        image_view = root.findViewById(R.id.image_view);
        image_edit = root.findViewById(R.id.image_edit);

        etName = root.findViewById(R.id.etName);
        spPosition = root.findViewById(R.id.spPosition);
        etSubject = root.findViewById(R.id.etSubject);
        etBio = root.findViewById(R.id.etBio);
        etSalary = root.findViewById(R.id.etSalary);
        etPhone = root.findViewById(R.id.etPhone);
        check = root.findViewById(R.id.check);
        AL = root.findViewById(R.id.AL);
        BL = root.findViewById(R.id.BL);
        CL = root.findViewById(R.id.CL);
        DL = root.findViewById(R.id.DL);

        btnEdit = root.findViewById(R.id.btnEdit);
        btnLogout = root.findViewById(R.id.btnLogout);
        btnDone = root.findViewById(R.id.btnDone);
        btnBack = root.findViewById(R.id.btnBack);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profilePicture/" + mAuth.getUid());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser mUser = mAuth.getCurrentUser();
                final String keyId = mUser.getUid();

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
                        final StorageReference ref = mStorageRef.child(keyId);
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
                        mDatabase.child(keyId).setValue(user);
                    }
                }, 5000);
                viewMode();
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
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
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

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            // Get a reference to our posts
            loadUserInfo();
        }
        return root;
    }

    private void loadUserInfo(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser mUser = mAuth.getCurrentUser();
                String currentUserId = mUser.getUid().toString();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds != null) {
                        String key = ds.getKey().toString();
                        if (currentUserId.equals(key)) {

                            String name = ds.child("name").getValue().toString();
                            String position = ds.child("position").getValue().toString();
                            String subject = ds.child("subject").getValue().toString();
                            String bio = ds.child("bio").getValue().toString();
                            String oblTo = ds.child("oblTo").getValue().toString();
                            String classes = ds.child("classes").getValue().toString();
                            int salary = Integer.parseInt(ds.child("salary").getValue().toString());
                            String email = ds.child("email").getValue().toString();
                            String phone = ds.child("phone").getValue().toString();
                            String uri = ds.child("uri").getValue().toString();
                            user = new User(name, position, subject, bio, oblTo, classes, salary, email, phone, uri);
                            seedData(user);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Connection Error", Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(Color.parseColor("#FDA89F"));
                snackbar.show();
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
            Glide.with(mContext).load(newUri).into(image_view);
            Glide.with(mContext).load(newUri).into(image_edit);
        }else{
            Glide.with(mContext).load(R.drawable.a).into(image_view);
            Glide.with(mContext).load(R.drawable.a).into(image_edit);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}