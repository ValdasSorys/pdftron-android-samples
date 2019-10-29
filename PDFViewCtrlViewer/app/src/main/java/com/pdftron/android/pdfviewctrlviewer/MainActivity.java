package com.pdftron.android.pdfviewctrlviewer;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.controls.AnnotationToolbar;
import com.pdftron.pdf.controls.AnnotationToolbarButtonId;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AppUtils;
import com.pdftron.pdf.utils.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private PDFViewCtrl mPdfViewCtrl;
    private PDFDoc mPdfDoc;
    private ToolManager mToolManager;
    private AnnotationToolbar mAnnotationToolbar;
    private Button mButton;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPdfViewCtrl = findViewById(R.id.pdfviewctrl);
        setupToolManager();
        setupAnnotationToolbar();
        try {
            AppUtils.setupPDFViewCtrl(mPdfViewCtrl);
            viewFromResource(R.raw.sample, "sample_file");
        } catch (PDFNetException e) {
            Log.e(TAG, "Error setting up PDFViewCtrl");
        }

        mPdfViewCtrl.addThumbAsyncListener(new PDFViewCtrl.ThumbAsyncListener() {
            @Override
            public void onThumbReceived(int page, int[] buf, int width, int height) {
                // buf if filled with "-1"
                Log.d("Listener Called: ", "Thumb received");

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(buf, 0, width, 0, 0, width, height);
                mImageView.setImageBitmap(bitmap);
            }
        });

        mButton = findViewById(R.id.get_thumb);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mPdfViewCtrl.getThumbAsync(mPdfViewCtrl.getCurrentPage());
                } catch (PDFNetException e) {
                    e.printStackTrace();
                }
            }
        });
        mImageView = findViewById(R.id.thumbnail);
    }

    /**
     * Helper method to set up and initialize the ToolManager.
     */
    public void setupToolManager() {
        mToolManager = ToolManagerBuilder.from()
                .build(this, mPdfViewCtrl);
    }

    /**
     * Helper method to set up and initialize the AnnotationToolbar.
     */
    public void setupAnnotationToolbar() {
        mAnnotationToolbar = findViewById(R.id.annotationToolbar);
        // Remember to initialize your ToolManager before calling setup
        mAnnotationToolbar.setup(mToolManager);
        mAnnotationToolbar.hideButton(AnnotationToolbarButtonId.CLOSE);
        mAnnotationToolbar.show();
    }

    /**
     * Helper method to view a PDF document from resource
     *
     * @param resourceId of the sample PDF file
     * @param fileName   of the temporary PDF file copy
     * @throws PDFNetException if invalid document path is supplied to PDFDoc
     */
    public void viewFromResource(int resourceId, String fileName) throws PDFNetException {
        File file = Utils.copyResourceToLocal(this, resourceId, fileName, ".pdf");
        mPdfDoc = new PDFDoc(file.getAbsolutePath());
        mPdfViewCtrl.setDoc(mPdfDoc);
        // Alternatively, you can open the document using Uri:
        // Uri fileUri = Uri.fromFile(file);
        // mPdfDoc = mPdfViewCtrl.openPDFUri(fileUri, null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Handle configuration changes from the toolbar here
        mAnnotationToolbar.onConfigurationChanged(newConfig);
    }

    /**
     * We need to clean up and handle PDFViewCtrl based on Android lifecycle callback.
     */

    @Override
    protected void onPause() {
        super.onPause();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.pause();
            mPdfViewCtrl.purgeMemory();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.destroy();
            mPdfViewCtrl = null;
        }

        if (mPdfDoc != null) {
            try {
                mPdfDoc.close();
            } catch (Exception e) {
                // handle exception
            } finally {
                mPdfDoc = null;
            }
        }
    }
}
