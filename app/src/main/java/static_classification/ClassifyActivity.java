package static_classification;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.retrofitaplication.InfoActivity;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.examples.classification.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import adapter.MyAdapter;

public class ClassifyActivity extends AppCompatActivity implements MyAdapter.OnLearnListener {

    // presets for rgb conversion
    private static final int RESULTS_TO_SHOW = 3;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    public static final String BREED = "breed";
    // options for model interpreter
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
    // tflite graph
    private Interpreter tflite;
    // All the possible labels for model
    private List<String> labelList;
    // Selected image as bytes
    private ByteBuffer imgData = null;
    // Probabilities of each label for non-quantized graphs
    private float[][] labelProbArray = null;
    // Probabilities of each label for quantized graphs
    private byte[][] labelProbArrayB = null;
    // Labels with top probabilities
    private String[] topLables = null;
    // Top probabilities
    private String[] topConfidence = null;


    // Selected classifier
    private String chosen;
    private boolean quant;

    // Input image dimensions for our model
    private int DIM_IMG_SIZE_X = 224;
    private int DIM_IMG_SIZE_Y = 224;
    private int DIM_PIXEL_SIZE = 3;

    // Image data
    private int[] intValues;

    // Activity elements
    private ImageView selected_image;
    private Button classify_button;
    private Button back_button;

    // Priority queue with top results
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        // Ordering by value
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Checking type of classifier
        chosen = (String) getIntent().getStringExtra("chosen");
        quant = (boolean) getIntent().getBooleanExtra("quant", false);

        // Initialize array
        intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

        super.onCreate(savedInstanceState);

        // Initialize interpreter and labels
        try{
            tflite = new Interpreter(loadModelFile(), tfliteOptions);
            labelList = loadLabelList();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        // Initialize byte array
        imgData = ByteBuffer.allocateDirect(
                            4 * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);

        imgData.order(ByteOrder.nativeOrder());

        // Initialize probabilities array
        labelProbArray = new float[1][labelList.size()];

        setContentView(R.layout.activity_classify);

        // Full screen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        // Initialize imageView
        selected_image = (ImageView) findViewById(R.id.selected_image);

        // Initialize array with top labels
        topLables = new String[RESULTS_TO_SHOW];
        // Initialize array with top probabilities
        topConfidence = new String[RESULTS_TO_SHOW];

        // Back to main menu
        back_button = (Button)findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> finish());

        // Classify!
        classify_button = (Button)findViewById(R.id.classify_image);
        classify_button.setOnClickListener(view -> {
            // Get bitmap from imageView
            Bitmap bitmap_orig = ((BitmapDrawable)selected_image.getDrawable()).getBitmap();
            // Resize bitmap
            Bitmap bitmap = getResizedBitmap(bitmap_orig, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y);
            // Convert bitmap to byte array
            convertBitmapToByteBuffer(bitmap);
            // Run interpreter
            tflite.run(imgData, labelProbArray);
            // Show results
            printTopKLabels();
        });

        // Get image from camera or gallery (MainMenuActivity)
        Uri uri = (Uri)getIntent().getParcelableExtra("resID_uri");
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            selected_image.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loading model
    private MappedByteBuffer loadModelFile() throws IOException {
        //Open an uncompressed asset
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd(chosen);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Converting bitmap to byte array
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Loop through all pixels
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                // Get rgb values from intValues where each int holds the rgb values for a pixel
                imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
            }
        }
    }

    // Loading labels from txt-file into string array
    private List<String> loadLabelList() throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    // Print top labels with confidences
    private void printTopKLabels() {
        // Add all results to priority queue
        for (int i = 0; i < labelList.size(); ++i) {
                sortedLabels.add(
                        new AbstractMap.SimpleEntry<>(labelList.get(i), labelProbArray[0][i]));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                // poll - returns the item with remove from the head of the queue
                sortedLabels.poll();
            }
        }

        // Get top results
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            topLables[i] = label.getKey();
            topConfidence[i] = String.format("%.0f%%",label.getValue()*100);
        }

        //  Initialize data for adapter
        ArrayList<String> topBreed = new ArrayList<String>();
        ArrayList<String> topBrConf = new ArrayList<String>();
        ArrayList<Integer> iconIds = new ArrayList<Integer>();

        // Results are ranked ascending
        for(int i = RESULTS_TO_SHOW - 1; i >= 0; i--)
        {
            topBreed.add(topLables[i]);
            topBrConf.add(topConfidence[i]);
        }
        iconIds.add(R.drawable.gold);
        iconIds.add(R.drawable.silver);
        iconIds.add(R.drawable.bronz);

        // create RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // create adapter
        MyAdapter adapter = new MyAdapter(topBreed, topBrConf, iconIds, this);
        // set adapter
        recyclerView.setAdapter(adapter);

    }


    // Resizing bitmap
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    // Item click listener in RecyclerView
    @Override
    public void onLearnClick(int position) {
        // Check element that was clicked
        String breed;
        switch (position) {
            case 0:
                breed = topLables[2];
                break;
            case 1:
                breed = topLables[1];
                break;
            case 2:
                breed = topLables[0];
                break;
            default:
                breed = null;
        }
        // Start InfoActivity and put breed-name to intent
        Intent startInfoIntent = new Intent(ClassifyActivity.this, InfoActivity.class);
        startInfoIntent.putExtra(BREED,breed);
        startActivity(startInfoIntent);
    }
}
