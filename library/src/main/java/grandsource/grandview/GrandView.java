package grandsource.grandview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("AppCompatCustomView")
public class GrandView  extends WebView {

    Bitmap bitmap;

    String videoPath = "";

    Activity activity;

    public enum Type {
        BOLD,
        ITALIC,
        SUBSCRIPT,
        SUPERSCRIPT,
        STRIKETHROUGH,
        UNDERLINE,
        H1,
        H2,
        H3,
        H4,
        H5,
        H6,
        ORDESADLIST,
        UNORDESADLIST,
        JUSTIFYCENTER,
        JUSTIFYFULL,
        JUSTUFYLEFT,
        JUSTIFYRIGHT
    }

    public interface OnTextChangeListener {

        void onTextChange(String text);
    }

    public interface OnDecorationStateListener {

        void onStateChangeListener(String text, List<GrandView.Type> types);
    }

    public interface AfterInitialLoadListener {

        void onAfterInitialLoad(boolean isReady);
    }

    private static final String SETUP_HTML = "file:///android_asset/main.html";
    private static final String CALLBACK_SCHEME = "sa-callback://";
    private static final String STATE_SCHEME = "sa-state://";
    private boolean isReady = false;
    private String mContents;
    private GrandView.OnTextChangeListener mTextChangeListener;
    private GrandView.OnDecorationStateListener mDecorationStateListener;
    private GrandView.AfterInitialLoadListener mLoadListener;

    public GrandView(Context context) {
        this(context, null);
    }

    public GrandView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public GrandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setAppCacheEnabled(true);
        getSettings().setAppCachePath(context.getFilesDir().getAbsolutePath() + "/cache");
        getSettings().setDatabaseEnabled(true);
        getSettings().setDatabasePath(context.getFilesDir().getAbsolutePath() + "/databases");
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(createWebviewClient());
        getSettings().setDomStorageEnabled(true);
        loadUrl(SETUP_HTML);

        applyAttributes(context, attrs);
    }

    boolean val = true;

    protected GrandView.EditorWebViewClient createWebviewClient() {
        return new GrandView.EditorWebViewClient();
    }

    public void setOnTextChangeListener(GrandView.OnTextChangeListener listener) {
        mTextChangeListener = listener;
    }

    public void setOnInitialLoadListener(GrandView.AfterInitialLoadListener listener) {
        mLoadListener = listener;
    }

    private void callback(String text) {
        mContents = text.replaceFirst(CALLBACK_SCHEME, "");
        if (mTextChangeListener != null) {
            mTextChangeListener.onTextChange(mContents);
        }
    }

    private void stateCheck(String text) {
        String state = text.replaceFirst(STATE_SCHEME, "").toUpperCase(Locale.ENGLISH);
        List<GrandView.Type> types = new ArrayList<>();
        for (GrandView.Type type : GrandView.Type.values()) {
            if (TextUtils.indexOf(state, type.name()) != -1) {
                types.add(type);
            }
        }

        if (mDecorationStateListener != null) {
            mDecorationStateListener.onStateChangeListener(state, types);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean dispatchFirst = super.dispatchKeyEvent(event);
        // Listening here for whatever key events you need
        if (event.getAction() == KeyEvent.ACTION_UP)
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_SPACE:
                case KeyEvent.KEYCODE_ENTER:
                    // e.g. get space and enter events here
                    break;
            }
        return dispatchFirst;
    }

    private void applyAttributes(Context context, AttributeSet attrs) {
        final int[] attrsArray = new int[]{
                android.R.attr.gravity
        };
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);

        int gravity = ta.getInt(0, NO_ID);
        switch (gravity) {
            case Gravity.LEFT:
                exec("javascript:SA.setTextAlign(\"left\")");
                break;
            case Gravity.RIGHT:
                exec("javascript:SA.setTextAlign(\"right\")");
                break;
            case Gravity.TOP:
                exec("javascript:SA.setVerticalAlign(\"top\")");
                break;
            case Gravity.BOTTOM:
                exec("javascript:SA.setVerticalAlign(\"bottom\")");
                break;
            case Gravity.CENTER_VERTICAL:
                exec("javascript:SA.setVerticalAlign(\"middle\")");
                break;
            case Gravity.CENTER_HORIZONTAL:
                exec("javascript:SA.setTextAlign(\"center\")");
                break;
            case Gravity.CENTER:
                exec("javascript:SA.setVerticalAlign(\"middle\")");
                exec("javascript:SA.setTextAlign(\"center\")");
                break;
        }

        ta.recycle();
    }

    public void setHtml(String contents) {
        if (contents == null) {
            contents = "";
        }
        try {
            exec("javascript:SA.setHtml('" + URLEncoder.encode(contents, "UTF-8") + "');");
        } catch (UnsupportedEncodingException e) {
            // No handling
        }
        mContents = contents;
    }

    public String getHtml() {
        return mContents;
    }

    public void setFontColor(int color) {
        String hex = convertHexColorString(color);
        exec("javascript:SA.setBaseTextColor('" + hex + "');");
    }

    public void setFontSize(int fontSize) {
        if (fontSize <= 7) {
            exec("javascript:SA.setFontSize('" + fontSize + "');");
        } else {
            exec("javascript:SA.setBaseFontSize('" + fontSize + "px');");
        }

    }


    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        exec("javascript:SA.setPadding('" + left + "px', '" + top + "px', '" + right + "px', '" + bottom
                + "px');");
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        // still not support RTL.
        setPadding(start, top, end, bottom);
    }

    public void setEditorBackgroundColor(int color) {
        setBackgroundColor(color);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(int resid) {
        Bitmap bitmap = Utils.decodeResource(getContext(), resid);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();

        exec("javascript:SA.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    @Override
    public void setBackground(Drawable background) {
        Bitmap bitmap = Utils.toBitmap(background);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();

        exec("javascript:SA.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    public void setBackground(String url) {
        exec("javascript:SA.setBackgroundImage('url(" + url + ")');");
    }

    public void setWidth(int px) {
        exec("javascript:SA.setWidth('" + px + "px');");
    }

    public void setHeight(int px) {
        exec("javascript:SA.setHeight('" + px + "px');");
    }

    public void setHint(String hint) {
        exec("javascript:SA.setPlaceholder('" + hint + "');");
    }

    public void setInputEnabled(Boolean inputEnabled) {
        exec("javascript:SA.setInputEnabled(" + inputEnabled + ")");
    }

    public void loadCSS(String cssFile) {
        String jsCSSImport = "(function() {" +
                "    var head  = document.getElementsByTagName(\"head\")[0];" +
                "    var link  = document.createElement(\"link\");" +
                "    link.rel  = \"stylesheet\";" +
                "    link.type = \"text/css\";" +
                "    link.href = \"" + cssFile + "\";" +
                "    link.media = \"all\";" +
                "    head.appendChild(link);" +
                "}) ();";
        exec("javascript:" + jsCSSImport + "");
    }

    public void undo() {
        exec("javascript:SA.undo();");
    }

    public void redo() {
        exec("javascript:SA.redo();");
    }

    public void setTextColor(int color) {
        exec("javascript:SA.prepareInsert();");


        String hex = convertHexColorString(color);
        exec("javascript:SA.setTextColor('" + hex + "');");

    }

    public void setTextColor(String color) {
        exec("javascript:SA.prepareInsert();");


        exec("javascript:SA.setTextColor('" + color + "');");

    }

    public void setTextBackgroundColor(int color) {
        exec("javascript:SA.prepareInsert();");

        String hex = convertHexColorString(color);
        exec("javascript:SA.setTextBackgroundColor('" + hex + "');");
    }

//    public void setFontSize(int fontSize) {
//        if (fontSize > 7 || fontSize < 1) {
//            Log.e("GrandView", "Font size should have a value between 1-7");
//        }
//        exec("javascript:SA.setFontSize('" + fontSize + "');");
//    }

    public void removeFormat() {
        exec("javascript:SA.removeFormat();");
    }


    public void loadImage(String url, String alt) {
        exec("javascript:SA.prepareInsert();");
        exec("javascript:SA.insertImage('" + url + "', '" + alt + "');");
        requestFocus(View.FOCUS_DOWN);
    }

    public void insertImage(Activity context) {

        insertImageDialog(context);
        activity = context;
    }


    public void insertVideos(String url) {
        exec("javascript:SA.prepareInsert();");
        exec("javascript:SA.insertVideos('" + url + "');");
    }

    public void insertVideos(Activity context){
        activity = context;
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        context.startActivityForResult(Intent.createChooser(intent,"Select Video"),3303);
    }

    public void insertLink(String href, String title) {
        exec("javascript:SA.prepareInsert();");
        exec("javascript:SA.insertLink('" + href + "', '" + title + "');");
    }

    public void insertLink(int DEFAULT_THEME) {
        if (DEFAULT_THEME == 2205) {
            loadDialog();
        }
    }

    public void insertTodo() {
        exec("javascript:SA.prepareInsert();");
        exec("javascript:SA.setTodo('" + Utils.getCurrentTime() + "');");
    }

    public void focusEditor() {
        requestFocus();
        exec("javascript:SA.focus();");
        ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(this, InputMethodManager.SHOW_FORCED);
    }

    public void setTextAlign(String alignment) {
        exec("javascript:SA.prepareInsert();");
        exec("javascript:SA.setVerticalAlign('" + alignment + "');");
    }

    public void clearFocusEditor() {
        exec("javascript:SA.blurFocus();");
    }

    private String convertHexColorString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    protected void exec(final String trigger) {
        if (isReady) {
            load(trigger);
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    exec(trigger);
                }
            }, 100);
        }
    }

    private void load(String trigger) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null);
        } else {
            loadUrl(trigger);
        }
    }

    protected class EditorWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {

            isReady = url.equalsIgnoreCase(SETUP_HTML);
            if (mLoadListener != null) {
                mLoadListener.onAfterInitialLoad(isReady);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            } else {
                String decode;
                try {
                    decode = URLDecoder.decode(url, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // No handling
                    return false;
                }

                if (TextUtils.indexOf(url, CALLBACK_SCHEME) == 0) {
                    callback(decode);
                    return true;
                } else if (TextUtils.indexOf(url, STATE_SCHEME) == 0) {
                    stateCheck(decode);
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        }
    }

    public void setHeading(int heading) {
        exec("javascript:SA.setHeading('" + heading + "');");
    }

    public void setIndent() {
        exec("javascript:SA.setIndent();");
    }

    public void setOutdent() {
        exec("javascript:SA.setOutdent();");
    }

    public void setAlignLeft() {
        exec("javascript:SA.setJustifyLeft();");
    }

    public void setAlignCenter() {
        exec("javascript:SA.setJustifyCenter();");
    }

    public void setAlignRight() {
        exec("javascript:SA.setJustifyRight();");
    }

    public void setBlockquote() {
        exec("javascript:SA.setBlockquote();");
    }

    public void setBullets() {
        exec("javascript:SA.setBullets();");
    }

    public void setNumbers() {
        exec("javascript:SA.setNumbers();");
    }

    public void setBold() {
        exec("javascript:SA.setBold();");
    }

    public void setItalic() {
        exec("javascript:SA.setItalic();");
    }

    public void setSubscript() {
        exec("javascript:SA.setSubscript();");
    }

    public void setSuperscript() {
        exec("javascript:SA.setSuperscript();");
    }

    public void setStrikeThrough() {
        exec("javascript:SA.setStrikeThrough();");
    }

    public void setUnderline() {
        exec("javascript:SA.setUnderline();");
    }

    public void insertHtml(String html) {
        String pureHtml = html.replaceAll(System.getProperty("line.separator"), "");
        exec("javascript:SA.insertHTML('" + pureHtml + "');");
    }


    //other work

    // OnActivity Result

    private void toast(String s){
        Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(int dp, Context context){
        return dp * ((int) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(int px, Context context){
        return px / ((int) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private String saveToInternalStorage(Bitmap bitmapImage,String fileDataName,String Path){
        ContextWrapper cw = new ContextWrapper(getContext().getApplicationContext());
        // Path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File FPath = new File(Path);

        if (!FPath.exists()) {
            FPath.mkdirs();
        }

        int i= 0;

        File childfile[] = FPath.listFiles();
        for (File file2 : childfile) {
            i++;
        }

        // Create imageDir
        //File myPath=new File(FPath,"Image"+i+".jpg");
        File myPath=new File(FPath,fileDataName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return FPath.getAbsolutePath()+"/"+fileDataName;
    }

    public boolean clearJunk(String Path) throws IOException {

        File dir = new File(Path);
        org.apache.commons.io.FileUtils.deleteDirectory(dir);
        return true;
    }

    public long junkSize(String Path)
    {
        String size = "";

        File dir = new File(Path);

        long kb = folderSize(dir) / 1024;

        return kb;
    }

    public static long folderSize(File directory) {
        long length = 0;
        try {
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    length += file.length();
                else
                    length += folderSize(file);
            }
        }catch (Exception e)
        {
            return length;
        }
        return length;
    }

    private void loadDialog() {

        final Dialog editdialog = new Dialog(getContext());
        editdialog.setTitle("LINK");
        editdialog.setContentView(R.layout.default_link_getter);
        editdialog.show();

        final EditText title = (EditText) editdialog.findViewById(R.id.title);
        final EditText link = (EditText) editdialog.findViewById(R.id.link);
        Button btn_save = (Button) editdialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) editdialog.findViewById(R.id.btn_cancel);

        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String mTitle = title.getText().toString();
                String mLink = link.getText().toString().trim().toLowerCase();

                if (mTitle.length() > 0 && mLink.length() > 0)
                {
                    try {

                        if (Patterns.WEB_URL.matcher(mLink).matches()) {

                            try {

                                String[] check = mLink.split("//");
                                if (check[0].equals("http:") || check[0].equals("https:")) {
                                    insertLink(mLink, mTitle);
                                    editdialog.dismiss();
                                } else {
                                    insertLink("http://" + mLink, mTitle);
                                    editdialog.dismiss();
                                }

                            } catch (Exception e) {
                                insertLink("http://" + mLink, mTitle);
                                editdialog.dismiss();
                            }
                        } else {
                            toast("Invalid Link");
                        }
                    } catch (Exception e){
                        toast("Invalid Link");
                    }

                } else {
                    toast("Please Fill Both Box");
                }
            }
        });

        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editdialog.dismiss();
            }
        });
    }


    private void insertImageDialog(final Activity activity) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent,1101 );

    }

    private static String r16 = "(?s)(<img.*?)(src\\s*?=\\s*?(?:\"|').*?(?:\"|'))";
    private static String r14 = "(?s)\\/|\\=";

    ArrayList<String> unMatchingList = new ArrayList<String>();
    ArrayList<String> matchingList = new ArrayList<String>();

    public void savePure(String str,String Path) {
        if (!str.isEmpty()) {

            Pattern p = Pattern.compile(r16);

            Matcher m = p.matcher(str);

            String name="";

            StringBuffer sb = new StringBuffer();
            int i=1;
            while(m.find())
            {
                String g2 = m.group(2);

                String[] names=g2.split(r14);

                String fullPath = g2.replace("src=","").replace("\"","");

                if(names.length>=1)
                {
                    name = names[names.length-1].replace("\"","");

                    unMatchingList.add(name);


                    File directory = new File(Path);
                    File[] files = directory.listFiles();

                    for (int j = 0; j < files.length; j++)
                    {
                        String fileName = files[j].getName();

                        matchingList.add(fileName);
                    }
                }
                else
                {
                    name = "";
                }
                //Name might be empty string.
                name = name.replaceAll("\"|'","");
            }

            for (int l = 0; l < matchingList.size(); l++) {
                Log.d("Filesnames",matchingList.get(l));
            }

            Log.d("Filesnames",matchingList.size()+"");

            List<String> finalList = new ArrayList<String>(matchingList);
            finalList.removeAll(unMatchingList);

            try {
                for (int k = 0; k < finalList.size(); k++) {

                    File dir = new File(Path);
                    dir.delete();
                }
            }catch (Exception e) {
                Log.d("Filesnames",e+"");

            }

            unMatchingList.clear();
            matchingList.clear();
            finalList.clear();

        }
    }


    // Get All Result

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getResult(int requestCode, int resultCode, Intent data, String Path) {

        if(requestCode == 1101 && data != null){
            try {
                Uri galleryPath = data.getData();

//                toast(FileUtils.getPathFromUri(activity,galleryPath));

                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),galleryPath);
                String save = saveToInternalStorage(bitmap,getFileName(galleryPath,activity),Path);
                loadImage(save,"Image");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        else if (requestCode == 3302 && data != null) {
            Uri selectedImageUri = data.getData();

            /* OI FILE Manager
            videoPath = selectedImageUri.getPath();

*/

//            toast(FileUtils.getPathFromUri(activity,selectedImageUri));

            File FPath = new File(Path);

            if (!FPath.exists()) {
                FPath.mkdirs();
            }

            String FILE_NAME = Path+getFileName(selectedImageUri,activity);

            try {
                InputStream in = activity.getContentResolver().openInputStream(selectedImageUri);
                createFileFromInputStream(in,FILE_NAME);
                toast(""+Path);
            } catch (FileNotFoundException e) {
                toast("File Can't be Writable");
                e.printStackTrace();
            }

            insertVideos(FILE_NAME);

        }
    }

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private static File createFileFromInputStream(InputStream inputStream, String fileName) {

        try{
            File f = new File(fileName);

            f.setWritable(true, false);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            System.out.println("error in creating a file");
            e.printStackTrace();
        }
        return null;
    }
}