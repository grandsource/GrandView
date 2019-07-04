package grandsource.grandview;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private GrandView mEditor;
    private TextView mPreview;
    private Button image, video, undo, redu, file, css, todo, textColor, bgColor, input, enable, align;
    private Button h1, h2, h3, h4, h5, link;
    private Button bold, italic, subscript, superscript, strike, underline, bullete, blockquote, indent, outdent, left, right, center;
    private Button get_html;
    private EditText html;
    String Path = Environment.getExternalStorageDirectory().getPath().toString()+ "/GrandView/StoreTemp/Note1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditor = (GrandView) findViewById(R.id.editor);
        image = (Button) findViewById(R.id.image);
        video = (Button) findViewById(R.id.video);
        undo = (Button) findViewById(R.id.undo);
        redu = (Button) findViewById(R.id.redu);
        file = (Button) findViewById(R.id.files);
        css = (Button) findViewById(R.id.css_add);
        todo = (Button) findViewById(R.id.todo);
        textColor = (Button) findViewById(R.id.text_color);
        bgColor = (Button) findViewById(R.id.bg_color);
        input = (Button) findViewById(R.id.disableinput);
        enable = (Button) findViewById(R.id.enable);
        align = (Button) findViewById(R.id.align);
        link = (Button) findViewById(R.id.link);
        html = (EditText) findViewById(R.id.html);
        get_html = (Button) findViewById(R.id.get_html);

        // Font button

        h1 = (Button) findViewById(R.id.h1);
        h2 = (Button) findViewById(R.id.h2);
        h3 = (Button) findViewById(R.id.h3);
        h4 = (Button) findViewById(R.id.h4);
        h5 = (Button) findViewById(R.id.h5);

        // Simple feature

        bold = (Button) findViewById(R.id.bold);
        italic = (Button) findViewById(R.id.italic);
        subscript = (Button) findViewById(R.id.subscript);
        superscript = (Button) findViewById(R.id.superscript);
        strike = (Button) findViewById(R.id.strike);
        underline = (Button) findViewById(R.id.underline);
        bullete = (Button) findViewById(R.id.bullete);
        blockquote = (Button) findViewById(R.id.blockquote);
        indent = (Button) findViewById(R.id.indent);
        outdent = (Button) findViewById(R.id.outdent);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        center = (Button) findViewById(R.id.center);


        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        outdent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        indent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        blockquote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        bullete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        strike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        superscript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        subscript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });


        h1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setFontSize(7);
            }
        });

        h2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setFontSize(5);
            }
        });

        h3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setFontSize(4);
            }
        });

        h4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setFontSize(3);
            }
        });

        h5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setFontSize(2);
            }
        });

        get_html.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertHtml(html.getText().toString());
            }
        });


        align.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextAlign("center");
            }
        });

        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setInputEnabled(true);
                mEditor.focusEditor();
            }
        });

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setInputEnabled(false);
            }
        });

        bgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBackground("https://image.freepik.com/free-psd/abstract-background-design_1297-87.jpg");
                mEditor.setTextColor(Color.WHITE);
            }
        });

        textColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(Color.BLACK);
            }
        });

        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHtml(mEditor.getHtml()+"<br/>");
                mEditor.insertTodo();

//                mEditor.savePure(mEditor.getHtml(), Path);
            }
        });

        css.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("Not Present But Working");
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor.insertImage(MainActivity.this);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor.insertVideos(MainActivity.this);
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor.undo();
            }
        });

        redu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor.redo();
            }
        });

        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mEditor.setOnInitialLoadListener(new GrandView.AfterInitialLoadListener() {
            @Override
            public void onAfterInitialLoad(boolean isReady) {
                mEditor.setFontSize(20);
                mEditor.setFontColor(Color.parseColor("#313131"));
                mEditor.setPadding(5, 10, 5, 10);
                mEditor.setHint("Insert text here...");

                mPreview = (TextView) findViewById(R.id.preview);
                mEditor.setOnTextChangeListener(new GrandView.OnTextChangeListener() {
                    @Override
                    public void onTextChange(String text) {
                        mPreview.setText(text);
                    }
                });

                mEditor.focusEditor();

            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertLink(THEME.DEFAULT);
            }
        });

        // mEditor.setEditorHeight(200);

        // mEditor.insertHtml("<b> Sabbir Ahmed Sagor </b>");

//        toast(mEditor.junkSize()+"");

    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }


    public void delete(View view) {
        try {
            mEditor.clearJunk(Path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {

        if (mEditor != null) {
            mEditor.getResult(requestCode, resultCode, result, Path);
        }

    }
}


