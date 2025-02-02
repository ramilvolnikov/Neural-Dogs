package static_classification;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.soundcloud.android.crop.Crop;

import org.tensorflow.lite.examples.classification.R;

import java.io.File;

import realtime_classification.RealtimeActivity;

public class MainMenuActivity extends AppCompatActivity {

    private Button cameraButton;
    private Button galleryButton;
    private Button realtimeButton;

    // for permission requests
    public static final int REQUEST_PERMISSION = 300;

    // request code for permission requests to the os for image
    public static final int REQUEST_IMAGE = 100;

    // will hold uri of image obtained from camera
    private Uri imageUri;

    // string to send to next activity that describes the chosen classifier
    private String chosen;

    //boolean value dictating if chosen model is quantized version or not.
    private boolean quant;

    static final int GALLERY_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //From splash to main menu
        setTheme(R.style.AppTheme_ImageClassification);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Full screen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        // request permission to use the camera on the user's phone
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }

        // request permission to write data (images) to the user's external storage of their phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        // request permission to read data (images) from the user's external storage of their phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
        //Image from gallery
        galleryButton = findViewById(R.id.gallery_button);
        galleryButton.setOnClickListener(view -> {
            // filename in assets
            chosen = "model_unquant.tflite";
            // model in not quantized
            quant = false;
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        });
        //Image from camera
        cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(view -> {
            // filename in assets
            chosen = "model_unquant.tflite";
            // model in not quantized
            quant = false;
            // open camera
            openCameraIntent();
        });
        //Start realtime mode
        realtimeButton = findViewById(R.id.realtime_button);
        realtimeButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainMenuActivity.this, RealtimeActivity.class);
            startActivity(intent);
        });
    }

    // opens camera for user
    private void openCameraIntent(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        // tell camera where to store the resulting picture
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // start camera, and wait for it to finish
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    // checks that the user has allowed all the required permission of read and write and camera. If not, notify the user and close the application
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(),"This application needs read, write, and camera permissions to run. Application now closing.",Toast.LENGTH_LONG).show();
                System.exit(0);
            }
        }
    }

    // dictates what to do after the user takes an image, selects and image, or crops an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        ImageView imageView = findViewById(R.id.selected_image);

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    imageUri = data.getData();
                }
        }
        // if the camera activity is finished, obtained the uri, crop it to make it square, and send it to ClassifyActivity
        if((requestCode == REQUEST_IMAGE && resultCode == RESULT_OK)||(requestCode == GALLERY_REQUEST)) {
            try {
                Uri source_uri = imageUri;
                Uri dest_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                // need to crop it to square image
                Crop.of(source_uri, dest_uri).asSquare().start(MainMenuActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if cropping activity is finished, get the resulting cropped image uri and send it to ClassifyActivity
        else if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK){
            imageUri = Crop.getOutput(data);
            Intent classifyIntent = new Intent(MainMenuActivity.this, ClassifyActivity.class);
            // put image data in extras to send
            classifyIntent.putExtra("resID_uri", imageUri);
            // put filename in extras
            classifyIntent.putExtra("chosen", chosen);
            // put model type in extras
            classifyIntent.putExtra("quant", quant);
            // send other required data
            startActivity(classifyIntent);
        }
    }
}
