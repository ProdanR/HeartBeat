package com.example.heartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetUpActivity extends AppCompatActivity {
    private CircularImageView profilePicture;
    private EditText profileDescription;
    private Button doneButton;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;
    private static String currentUserId;
    private Uri imageUri;
    private static final int galleryPick= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        mAuth=FirebaseAuth.getInstance();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");

        profilePicture= (CircularImageView) findViewById(R.id.profile_picture);
        profileDescription= (EditText) findViewById(R.id.profile_description);
        doneButton= (Button) findViewById(R.id.done_button);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent= new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPick); //open gallery

            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInformations();
            }
        });


    }

    private void saveInformations() {
        final StorageReference filePath=UserProfileImageRef.child(currentUserId+".jpg");
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SetUpActivity.this,"Profile information & description added succesfully",Toast.LENGTH_SHORT).show();

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String downloadUrl= uri.toString();
                            UsersRef.child("profileImage").setValue(downloadUrl);
                            UsersRef.child("profileDescription").setValue(profileDescription.getText().toString());
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryPick && resultCode==RESULT_OK && data!=null ) { Uri resultUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {

                Uri resultUri = result.getUri();
                imageUri= resultUri;
                profilePicture.setImageURI(resultUri);
            }
        }

    }

    public static void setCurrentUserId(String userId) {
        currentUserId = userId;
    }
}