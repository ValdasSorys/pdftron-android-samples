package com.pdftron.android.tutorial.customui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.pdftron.android.tutorial.customui.custom.CustomAnnotationToolbar;
import com.pdftron.android.tutorial.customui.custom.CustomLinkClick;
import com.pdftron.android.tutorial.customui.custom.CustomQuickMenu;
import com.pdftron.collab.ui.viewer.CollabViewerBuilder2;
import com.pdftron.collab.ui.viewer.CollabViewerTabHostFragment2;
import com.pdftron.common.PDFNetException;
import com.pdftron.fdf.FDFDoc;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.annots.Markup;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements PdfViewCtrlTabHostFragment2.TabHostListener {

    private CollabViewerTabHostFragment2 mPdfViewCtrlTabHostFragment;

    public static final String NOTES_TOOLBAR_TAG = "notes_toolbar";
    public static final String SHAPES_TOOLBAR_TAG = "shapes_toolbar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a PdfViewCtrlTabHostFragment with a document Uri
        File f = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
        Uri uri = Uri.fromFile(f);
        ViewerConfig viewerConfig = new ViewerConfig.Builder()
                .fullscreenModeEnabled(false)
                .multiTabEnabled(false)
                .build();
        mPdfViewCtrlTabHostFragment = CollabViewerBuilder2.withUri(uri)
                .usingNavIcon(R.drawable.ic_star_white_24dp)
                .usingConfig(viewerConfig)
                .build(this);
        mPdfViewCtrlTabHostFragment.addHostListener(this);

        // Apply customizations to tab host fragment
        new CustomQuickMenu(MainActivity.this, mPdfViewCtrlTabHostFragment);
        new CustomLinkClick(MainActivity.this, mPdfViewCtrlTabHostFragment);
        new CustomAnnotationToolbar(MainActivity.this, mPdfViewCtrlTabHostFragment);

        // Add the fragment to our activity
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, mPdfViewCtrlTabHostFragment);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPdfViewCtrlTabHostFragment != null) {
            mPdfViewCtrlTabHostFragment.removeHostListener(this);
        }
    }

    private AnnotationToolbarBuilder buildNotesToolbar() {
        return AnnotationToolbarBuilder.withTag(NOTES_TOOLBAR_TAG) // Identifier for toolbar
                .setToolbarName("Notes Toolbar") // Name used when displaying toolbar
                .addToolButton(ToolbarButtonType.INK, 1)
                .addToolButton(ToolbarButtonType.STICKY_NOTE, 2)
                .addToolButton(ToolbarButtonType.TEXT_HIGHLIGHT, 3)
                .addToolButton(ToolbarButtonType.TEXT_UNDERLINE, 4)
                .addToolButton(ToolbarButtonType.TEXT_STRIKEOUT, 5)
                .addToolStickyButton(ToolbarButtonType.UNDO, DefaultToolbars.ButtonId.UNDO.value())
                .addToolStickyButton(ToolbarButtonType.REDO, DefaultToolbars.ButtonId.REDO.value());
    }

    private AnnotationToolbarBuilder buildShapesToolbar() {
        return AnnotationToolbarBuilder.withTag(SHAPES_TOOLBAR_TAG) // Identifier for toolbar
                .setToolbarName("Shapes Toolbar") // Name used when displaying toolbar
                .addToolButton(ToolbarButtonType.SQUARE, DefaultToolbars.ButtonId.SQUARE.value())
                .addToolButton(ToolbarButtonType.CIRCLE, DefaultToolbars.ButtonId.CIRCLE.value())
                .addToolButton(ToolbarButtonType.LINE, DefaultToolbars.ButtonId.LINE.value())
                .addToolButton(ToolbarButtonType.POLYGON, DefaultToolbars.ButtonId.POLYGON.value())
                .addToolButton(ToolbarButtonType.POLYLINE, DefaultToolbars.ButtonId.POLYLINE.value())
                .addToolStickyButton(ToolbarButtonType.UNDO, DefaultToolbars.ButtonId.UNDO.value())
                .addToolStickyButton(ToolbarButtonType.REDO, DefaultToolbars.ButtonId.REDO.value());
    }

    @Override
    public void onTabDocumentLoaded(String s) {
        if (mPdfViewCtrlTabHostFragment != null && mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment() != null) {
            // xfdf that contains one `Underline` text annotation
            String myXfdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<xfdf xmlns=\"http://ns.adobe.com/xfdf/\" xml:space=\"preserve\">\n" +
                    "\t<annots>\n" +
                    "\t\t<underline style=\"solid\" width=\"1.5\" color=\"#E44234\" opacity=\"1\" creationdate=\"D:20240110131152Z\" flags=\"print\" date=\"D:20240110131156Z\" name=\"e5d4e1fb-4b45-4451-a8c9-b74ca363a8b8\" page=\"0\" coords=\"64.881,747.417,234.414,747.417,64.881,716.205,234.414,716.205\" rect=\"59.7095,715.455,239.5855,747.417\" subject=\"New Subject\" title=\"someId\">\n" +
                    "\t\t\t<trn-custom-data bytes=\"{&quot;contactId&quot;:&quot;70b8cf72-3109-4803-aabe-3ff72f955594&quot;}\" />\n" +
                    "\t\t\t<popup date=\"D:20240110131152Z\" page=\"0\" rect=\"58.199,716.205,292.099,747.417\" />\n" +
                    "\t\t\t<contents>A Simple PDF</contents>\n" +
                    "\t\t</underline>\n" +
                    "\t</annots>\n" +
                    "\t<pages>\n" +
                    "\t\t<defmtx matrix=\"1.333333,0.000000,0.000000,-1.333333,0.000000,1056.000000\" />\n" +
                    "\t</pages>\n" +
                    "\t<pdf-info import-version=\"4\" version=\"2\" xmlns=\"http://www.pdftron.com/pdfinfo\" />\n" +
                    "</xfdf>";

            try {
                FDFDoc myFDFDoc = FDFDoc.createFromXFDF(myXfdf);
                String idk = myFDFDoc.saveAsXFDF();
                mPdfViewCtrlTabHostFragment.getCollabManager().setCurrentUser("someId", "Name");
                mPdfViewCtrlTabHostFragment.getCollabManager().setCurrentDocument("1");
                mPdfViewCtrlTabHostFragment.getCollabManager().importAnnotationCommand(idk, true);
            } catch (PDFNetException e) {
                throw new RuntimeException(e);
            }

            ToolManager tm = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getToolManager();
            tm.addAnnotationModificationListener(new ToolManager.AnnotationModificationListener() {
                @Override
                public void onAnnotationsAdded(Map<Annot, Integer> annots) {
                    demoExtraAnnotData("onAnnotationsAdded", annots);
                }

                @Override
                public void onAnnotationsPreModify(Map<Annot, Integer> annots) {

                }

                @Override
                public void onAnnotationsModified(Map<Annot, Integer> annots, Bundle extra) {
                    demoExtraAnnotData("onAnnotationsModified", annots);

                    Annot myAnnot = annots.keySet().iterator().next();
                    try {
                        System.out.println(myAnnot.getContents());
                    } catch (PDFNetException e) {
                        throw new RuntimeException(e);
                    }

                    // This is how I got the xfdf that I pasted above
                    try {
                        PDFViewCtrl pdfviewctrl = mPdfViewCtrlTabHostFragment.getPdfViewCtrl();
                        pdfviewctrl.docLockRead();
                        FDFDoc fdf = pdfviewctrl.getDoc().fdfExtract(PDFDoc.e_annots_only);
                        pdfviewctrl.docUnlockRead();
                        String result = fdf.saveAsXFDF();
                        // System.out.println(result);
                    } catch (PDFNetException e) {
                        throw new RuntimeException(e);
                    }


                }

                @Override
                public void onAnnotationsPreRemove(Map<Annot, Integer> annots) {
                    demoExtraAnnotData("onAnnotationsPreRemove", annots);
                }

                @Override
                public void onAnnotationsRemoved(Map<Annot, Integer> annots) {

                }

                @Override
                public void onAnnotationsRemovedOnPage(int pageNum) {

                }

                @Override
                public void annotationsCouldNotBeAdded(String errorMessage) {

                }
            });
        }
    }

    @Override
    public boolean onToolbarOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_show_toast) {
            Toast.makeText(this, "Show toast is clicked!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onTabHostShown() {

    }

    @Override
    public void onTabHostHidden() {

    }

    @Override
    public void onLastTabClosed() {

    }

    @Override
    public void onTabChanged(String s) {

    }

    @Override
    public boolean onOpenDocError() {
        return false;
    }

    @Override
    public void onNavButtonPressed() {

    }

    @Override
    public void onShowFileInFolder(String s, String s1, int i) {

    }

    @Override
    public boolean canShowFileInFolder() {
        return false;
    }

    @Override
    public boolean canShowFileCloseSnackbar() {
        return false;
    }

    @Override
    public boolean onToolbarCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        return false;
    }

    @Override
    public boolean onToolbarPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onStartSearchMode() {

    }

    @Override
    public void onExitSearchMode() {

    }

    @Override
    public boolean canRecreateActivity() {
        return true;
    }

    @Override
    public void onTabPaused(FileInfo fileInfo, boolean b) {

    }

    @Override
    public void onJumpToSdCardFolder() {

    }

    private void demoExtraAnnotData(String event, Map<Annot, Integer> annots) {
        try {
            for (Annot a : annots.keySet()) {
                if (a.isMarkup()) {
                    Markup mu = new Markup(a);
                    mu.setSubject("New Subject");
                }
                a.setCustomData("contactId", UUID.randomUUID().toString());
            }
            String xfdf = getXfdf(annots);
            Log.d("PDFTron", event + ": " + xfdf);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    private String getXfdf(Map<Annot, Integer> annots) {
        if (mPdfViewCtrlTabHostFragment != null && mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment() != null) {
            try {
                PDFDoc pdfDoc = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getPdfDoc();
                FDFDoc fdfDoc = pdfDoc.fdfExtract(new ArrayList<>(annots.keySet()));
                return fdfDoc.saveAsXFDF();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
