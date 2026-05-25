package com.quarkdown.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String STARTER_DOCUMENT = "# Quarkdown Android\n\nWrite Quarkdown or Markdown here.\n\n- Edit on top\n- Preview below\n\n**Hello from Android.**";

    private EditText editor;
    private WebView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFFAFAFA);

        TextView title = new TextView(this);
        title.setText("Quarkdown Android");
        title.setTextColor(0xFF102A43);
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setPadding(dp(16), dp(12), dp(16), dp(10));
        root.addView(title, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        editor = new EditText(this);
        editor.setText(STARTER_DOCUMENT);
        editor.setGravity(Gravity.TOP | Gravity.START);
        editor.setMinLines(8);
        editor.setTextSize(15);
        editor.setPadding(dp(14), dp(12), dp(14), dp(12));
        editor.setBackgroundColor(0xFFFFFFFF);
        root.addView(editor, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1.0f
        ));

        View divider = new View(this);
        divider.setBackgroundColor(0xFFE1E7EF);
        root.addView(divider, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(1)
        ));

        preview = new WebView(this);
        WebSettings settings = preview.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        root.addView(preview, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1.2f
        ));

        setContentView(root);
        renderPreview(editor.getText().toString());

        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderPreview(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void renderPreview(String source) {
        preview.loadDataWithBaseURL(
            "https://quarkdown.local/",
            buildHtml(source),
            "text/html",
            "UTF-8",
            null
        );
    }

    private String buildHtml(String source) {
        StringBuilder body = new StringBuilder();
        boolean inList = false;

        for (String rawLine : source.split("\\r?\\n", -1)) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                if (inList) {
                    body.append("</ul>");
                    inList = false;
                }
                continue;
            }

            if (line.startsWith("- ") || line.startsWith("* ")) {
                if (!inList) {
                    body.append("<ul>");
                    inList = true;
                }
                body.append("<li>").append(formatInline(line.substring(2))).append("</li>");
                continue;
            }

            if (inList) {
                body.append("</ul>");
                inList = false;
            }

            if (line.startsWith("### ")) {
                body.append("<h3>").append(formatInline(line.substring(4))).append("</h3>");
            } else if (line.startsWith("## ")) {
                body.append("<h2>").append(formatInline(line.substring(3))).append("</h2>");
            } else if (line.startsWith("# ")) {
                body.append("<h1>").append(formatInline(line.substring(2))).append("</h1>");
            } else {
                body.append("<p>").append(formatInline(line)).append("</p>");
            }
        }

        if (inList) {
            body.append("</ul>");
        }

        return "<!doctype html><html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
            "<style>body{font-family:sans-serif;color:#102a43;margin:18px;line-height:1.55;background:#fff}" +
            "h1,h2,h3{line-height:1.2;margin:0 0 12px}p{margin:0 0 12px}ul{padding-left:22px}code{background:#eef2f7;padding:2px 5px;border-radius:4px}</style>" +
            "</head><body>" + body + "</body></html>";
    }

    private String formatInline(String source) {
        String escaped = escape(source);
        escaped = escaped.replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>");
        escaped = escaped.replaceAll("`(.+?)`", "<code>$1</code>");
        return escaped;
    }

    private String escape(String text) {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
