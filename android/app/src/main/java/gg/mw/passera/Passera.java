package gg.mw.passera;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Base64;
import java.util.regex.*;
import java.util.*;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;

public class Passera extends ActionBarActivity {

    EditText pwField;
    EditText pwLen;
    EditText pwRep;
    TextView showingLabel;
    CheckBox charsBox;
    ProgressBar progressBar;
    Button hideButton;
    Button buttonDo;
    Button buttonShow;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passera);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.passera, menu);

        pwField = (EditText)findViewById(R.id.pw);
        pwRep = (EditText)findViewById(R.id.pwrep);
        pwLen = (EditText)findViewById(R.id.pwlen);
        showingLabel = (TextView)findViewById(R.id.showing);
        charsBox = (CheckBox)findViewById(R.id.checkbox_chars);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        hideButton = (Button)findViewById(R.id.hide);
        buttonDo = (Button)findViewById(R.id.button_do);
        buttonShow = (Button)findViewById(R.id.button_show);
        scrollView = (ScrollView)findViewById(R.id.scrollView);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }


    private String hash(String pw, int len, boolean chars) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        String salt = passwd(pw, 64, chars);

        int runs = 0;

        for (int i = 0; i < salt.length(); i++){
            runs = runs + (int) salt.charAt(i);

        }
        runs += len;

        System.out.println(runs);

        byte[] hasher;

        int percent;

        for (int z = 0; z < runs; z++) {
            hasher = encrypt((salt + pw).getBytes("UTF-8"), "SHA-512");

            StringBuilder sb = new StringBuilder(2*hasher.length);
            for(byte b : hasher){
                sb.append(String.format("%02x", b&0xff));
            }
            pw = sb.toString();

            percent = (z*100)/runs;

            progressBar.setProgress(percent);

        }

        return pw;
    }

    private String passwd(String str, int len, boolean chars) throws UnsupportedEncodingException, NoSuchAlgorithmException {


        byte[] hasher = encrypt(str.getBytes("UTF-8"), "SHA-512");
        String hash = Base64.encodeToString(hasher, Base64.NO_WRAP);


        byte[] replacer = encrypt(hash.getBytes("UTF-8"), "SHA-512");
        String replace = Base64.encodeToString(replacer, Base64.NO_WRAP);

        String passwd = hash.substring(0, len);




        if (chars) {

            String[] replaceables = {"\\!", "\\@", "\\#", "\\$", "\\%", "\\^", "\\&", "\\*", "\\(", "\\)", "\\-", "\\_", "\\+", "\\=", "\\]", "\\[", "\\{", "\\}", "\\?", "\\<", "\\>"};



            List<Integer> nums = new LinkedList<Integer>();
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(hash);
            while (m.find()) {
                nums.add(Integer.parseInt(m.group()));
            }

            int num = 0;

            System.out.println(nums);
            System.out.println(nums.get(0).toString().substring(0,1));

            for (int i = 0; i < len/2; i++){

                num = num + Integer.parseInt(nums.get(0).toString().substring(0,1));

                if (num > 20) {
                    num = num -20;
                }

                passwd = passwd.replaceFirst( "[" + replace.charAt(i) + "]", replaceables[num]);

            }

        }

        return passwd;

    }

    private byte[] encrypt(byte[] target, String algo) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance(algo);
        md.update(target);

        return md.digest();

    }


    public void pwshow(View view) throws UnsupportedEncodingException, NoSuchAlgorithmException {


        final String phrase = pwField.getText().toString();
        int len = Integer.parseInt(pwLen.getText().toString());
        final boolean chars = charsBox.isChecked();
        String phraseRep = pwRep.getText().toString();

        if (len > 64) {
            len = 64;
            CharSequence newlen = "64";
            pwLen.setText(newlen);
        }
        if (len < 4) {
            len = 4;
            CharSequence newlen = "4";
            pwLen.setText(newlen);
        }

        final int length = len;

        if ( phraseRep.equals(phrase) || phraseRep.equals("")) {

            showingLabel.setText( "" );
            progressBar.setVisibility(View.VISIBLE);
            buttonDo.setEnabled(false);
            buttonShow.setEnabled(false);
            pwField.setText("");
            pwRep.setText("");
            scrollView.scrollTo(0, showingLabel.getTop());

            new Thread(new Runnable() {
                public void run() {
                    String phrasehash;
                    try {
                        phrasehash = hash(phrase, length, chars);
                        final String pw = passwd(phrasehash, length, chars);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                showingLabel.setText( pw );
                                scrollView.scrollTo(0, showingLabel.getBottom());
                                hideButton.setVisibility(View.VISIBLE);
                                buttonDo.setEnabled(true);
                                buttonShow.setEnabled(true);
                                progressBar.setProgress(0);
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        }
        else {

            showingLabel.setText( "Passwords did not match" );

        }



    }


    public void pw(View view) {

        final String phrase = pwField.getText().toString();
        int len = Integer.parseInt(pwLen.getText().toString());
        final boolean chars = charsBox.isChecked();
        String phraseRep = pwRep.getText().toString();

        if (len > 64) {
            len = 64;
            CharSequence newlen = "64";
            pwLen.setText(newlen);
        }
        if (len < 4) {
            len = 4;
            CharSequence newlen = "4";
            pwLen.setText(newlen);
        }

        final int length = len;

        if ( phraseRep.equals(phrase) || phraseRep.equals("")) {

            showingLabel.setText( "" );
            progressBar.setVisibility(View.VISIBLE);
            buttonDo.setEnabled(false);
            buttonShow.setEnabled(false);
            pwField.setText("");
            pwRep.setText("");
            scrollView.scrollTo(0, showingLabel.getTop());

            new Thread(new Runnable() {
                public void run() {
                    String phrasehash;
                    try {
                        phrasehash = hash(phrase, length, chars);
                        final String pw = passwd(phrasehash, length, chars);




                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                CharSequence pwl = "passera";

                                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText(pwl, pw);

                                clipboard.setPrimaryClip(clip);

                                showingLabel.setText( "Copied to clipboard" );
                                scrollView.scrollTo(0, showingLabel.getBottom());
                                buttonDo.setEnabled(true);
                                buttonShow.setEnabled(true);
                                progressBar.setProgress(0);
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        else {

            showingLabel.setText( "Passwords did not match" );

        }


    }

    public void pwhide(View view) {

        showingLabel.setText("");
        hideButton.setVisibility(View.INVISIBLE);

    }
}

