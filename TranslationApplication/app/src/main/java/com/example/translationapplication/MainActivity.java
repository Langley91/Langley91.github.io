package com.example.translationapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity {

    /***************************************************************************
     * Our UI component references. These references will
     * be assigned to the UI components defined in res/layout/activity_main.xml.
     * Those assignments are made in onCreate()
     ***************************************************************************/

    /* the text box (called an EditText) where we type what we want to translate */
    private EditText translateEditText;

    /* the text box (called TextView) where our translation appears */
    private TextView translatedTextView;

    /* the title for the EditText (the language we are translating from) */
    private TextView fromLanguageTextView;

    /* the title for the translation TextView (the language we are translating to) */
    private TextView toLanguageTextView;

    /* the drop-down box (called a spinner) for selecting a language to translate from */
    private Spinner fromSpinner;

    /* the spinner for selecting a language to translate to */
    private Spinner toSpinner;

    /* the open file button */
    private FloatingActionButton openFileButton;

    /* the save file button */
    private FloatingActionButton saveFileButton;

    /* the reverse languages button */
    private ImageButton reverseLanguagesButton;


    /* Activity request codes for use in startActivityForResult and onActivityResult
    *  These codes are specified in the call to startActivityForResult.
    *  When the activity finishes (e.g. you choose a file to open or you choose a file
    *  to create) it passes the same code to onActivityResult, so that onActivity result
    *  can decide what to do. */
    private static final int PICK_TEXT_FILE = 2;
    private static final int SAVE_TEXT_FILE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Android has already created all of our components from activity_main.xml.
         * Here we assign references to all of those components so that we can use them
         * in our program without having to call findViewById every time
         */
        translateEditText = findViewById(R.id.translateEditText);
        translatedTextView = findViewById(R.id.translatedTextView);
        fromLanguageTextView = findViewById(R.id.fromLanguageTextView);
        toLanguageTextView = findViewById(R.id.toLanguageTextView);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        openFileButton = findViewById(R.id.openFileButton);
        saveFileButton = findViewById(R.id.saveFileButton);
        reverseLanguagesButton = findViewById(R.id.reverseLanguagesButton);

        /* set the toSpinner to 'select language' */
        toSpinner.setSelection(getResources().getStringArray(R.array.language_array).length - 1);

        /*
         The rest of onCreate is adding listeners to our UI components. A listener
         is an object that contains the code that executes when we interact
         with the component. For example, this first listener calls 'translate()'
         when the text in translateEditText is changed.
         */
        translateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                /* translate is a member of MainActivity that is  defined later in the class */
                translate();
            }
            /* These functions is required for a TextWatcher, but we don't need them for this
             * app, so they're empty */
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        /* When the reverse button is pressed, swap the text and swap the languages in the spinners */
        reverseLanguagesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int swapPosition = fromSpinner.getSelectedItemPosition();
                translateEditText.setText(translatedTextView.getText());
                fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
                toSpinner.setSelection(swapPosition);
            }
        });
        /* when the open file button is pressed, use the Storage Access Framework to open a file.
         * I used  this tutorial:
         * https://developer.android.com/training/data-storage/shared/documents-files
         */
        openFileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final int PICK_TEXT_FILE = 2;
                /* Uri pickerInitialUri */
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

                startActivityForResult(intent, PICK_TEXT_FILE);
            }
        });

        /* when the save file button is pressed, use the Storage Access Framework to create a file.
         * I used this tutorial:
         * https://developer.android.com/training/data-storage/shared/documents-files
         */
        saveFileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, "translation.txt");

                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker when your app creates the document.
                //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

                startActivityForResult(intent, SAVE_TEXT_FILE);
            }
        });

        /* This listener is used for both spinners */
        final OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageTextView.setText((String)fromSpinner.getSelectedItem());
                toLanguageTextView.setText((String)toSpinner.getSelectedItem());
                translate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        fromSpinner.setOnItemSelectedListener(spinnerListener);
        toSpinner.setOnItemSelectedListener(spinnerListener);
    }
    /*
        This is where the magic happens.
     */
    private void translate() {
        /* get the text to translate from the UI component */
        String toTranslate = translateEditText.getText().toString();



        /* A map of all the text that has not been translated yet and their original starting
         * index in the original string. We initialize this map with the original string at index 0
         */
        SortedMap<Integer, String> notTranslated = new TreeMap<>();

        /* A map of all the  translated strings and their ORIGINAL index in the starting string,
         * not their new index. This will help us keep track of where the go when we put the
         * result together.
         */
        SortedMap<Integer, String> translated = new TreeMap<>();

        /* intialize the notTranslated map with the  starting string */
        notTranslated.put(0, toTranslate);

        /* get the languages from the UI components */
        String fromLanguage = (String) fromSpinner.getSelectedItem();
        String toLanguage = (String) toSpinner.getSelectedItem();
        /* If the language is the same, just copy the text */
        if(fromLanguage.equals(toLanguage)) {
            translatedTextView.setText(translateEditText.getText());
        }
        /* make sure a language is selected */
        else if(!(fromLanguage.equals("Select Language") || toLanguage.equals("Select Language"))) {

            /* find the dictionary IDs from the languages. */
            int fromArrayID = getResources().getIdentifier(fromLanguage, "array", getPackageName());
            int toArrayID = getResources().getIdentifier(toLanguage, "array", getPackageName());

            /* get the dictionaries from the dictionary IDs */
            String[] fromDictionary = getResources().getStringArray(fromArrayID);
            String[] toDictionary = getResources().getStringArray(toArrayID);

            /* Look for anything in quotes and add it to the top of the dictionaries, so that
             * they don't get translated. Look up 'regex' to see how it works.
             */
            Pattern regex = Pattern.compile("((?<![\\\\])['\"])((?:.(?!(?<![\\\\])\\1))*.?)\\1");
            Matcher matcher = regex.matcher(toTranslate);
            List<String> quotedStrings = new ArrayList<>();
            while(matcher.find()) {
                String result = matcher.group(0);
                quotedStrings.add(result);
            }
            /* create new dictionaries with the quoted strings at the top */
            List<String> fromDictionaryList = new ArrayList<>(Arrays.asList(fromDictionary));
            List<String> toDictionaryList = new ArrayList<>(Arrays.asList(toDictionary));
            fromDictionaryList.addAll(0, quotedStrings);
            toDictionaryList.addAll(0, quotedStrings);
            fromDictionary = fromDictionaryList.toArray(new String[0]);
            toDictionary = toDictionaryList.toArray(new String[0]);

            /*  For each dictionary entry, create a local versions of our maps to be added in later.

             */
            for (int i = 0; i < fromDictionary.length; i++) {
                    /* These are our words/phrases from each dictionary. They correspond exactly;
                       e.g. the third entry in our fromDictionary will translate to the third entry
                       in our toDictionary.
                     */
                    String nextFrom = fromDictionary[i];
                    String nextTo = toDictionary[i];
                    SortedMap<Integer, String> nextTranslatedMap = new TreeMap<>();
                    SortedMap<Integer, String> nextToTranslateMap = new TreeMap<>();
                    for(Entry<Integer, String> entry: notTranslated.entrySet()) {
                        Integer index = entry.getKey();
                        String nextToTranslate = entry.getValue();
                        Integer nextIndex;
                        int begin = 0;
                        /* fill in our translated map with our phrase and the original index
                         * of the phrase that was translated. There's some math here with indexes,
                         * but the code is keeping track of where each original phrase was with the
                         * original index and the begin variable.
                         */
                        while((nextIndex = nextToTranslate.indexOf(nextFrom, begin)) != -1) {
                            nextTranslatedMap.put(index + nextIndex, nextTo);
                            begin += nextFrom.length();
                        }
                        /* Do the same thing but all lower case this time. helps with single words that
                        *  aren't at the beginning of a sentence.
                        */
                        while((nextIndex = nextToTranslate.indexOf(nextFrom.toLowerCase(), begin)) != -1) {
                            nextTranslatedMap.put(index + nextIndex, nextTo.toLowerCase());
                            begin += nextFrom.length();
                        }
                    }
                    /* Now that we have translated the string, we need to remove the pieces that were
                     * translated. We break up the original string based on the indexes and lengths
                     * of the translated strings. For example, if our original string was 'Oh hello there'
                     * and we just translated 'hello' then we add new strings 'Oh' at index 0 and
                     * 'there' at index 9. Our translation of 'hello' is at index 4 in the translated map.
                     * Since 'Oh' and 'Oh hello there' are both at index 0, then 'Oh' will overwrite
                     * 'Oh hello there' because a map can only have one entry for every key.
                     */
                    for(Entry<Integer, String> entry: notTranslated.entrySet()) {
                        Integer index = entry.getKey();
                        String nextToBreakUp = entry.getValue();
                        SortedMap<Integer, String> subset = nextTranslatedMap.subMap(index, index + nextToBreakUp.length());
                        Integer last = 0;
                        for(Integer subIndex: subset.keySet()) {
                            nextToTranslateMap.put(last + index, nextToBreakUp.substring(last, subIndex - index));
                            last = subIndex - index + nextFrom.length();
                        }
                        nextToTranslateMap.put(last + index, nextToBreakUp.substring(last));
                    }

                    /* add our local maps for this dictionary entry to the big maps outside the loop */
                    notTranslated.putAll(nextToTranslateMap);
                    translated.putAll(nextTranslatedMap);
            }
            /* add the untranslated strings to the translated map, because couldn't find
             * entries for them in the dictionary. They will get printed with the translation.
             */
            for(Entry<Integer, String> entry : notTranslated.entrySet()) {
                if(!translated.containsKey(entry.getKey())) {
                    translated.put(entry.getKey(), entry.getValue());
                }
            }
            /* put all the translated strings together for our result */
            StringBuilder result = new StringBuilder();
            for(String value: translated.values()) {
                result.append(value);
            }
            /* Added new line 23/03/2020 */
            if(toLanguage.equals("PHP")) {result.append(";");}
               else {result.append("");}

            translatedTextView.setText(result.toString());
        }

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        /* These are the handlers for when we open a file and save a file. I used this documentation
         * as my guide:
         * https://developer.android.com/training/data-storage/shared/documents-files
         */
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == Activity.RESULT_OK && resultData != null) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri;
            switch (requestCode) {
                case PICK_TEXT_FILE: {
                    uri = resultData.getData();
                    StringBuilder stringBuilder = new StringBuilder();
                    try (
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            BufferedReader reader =
                                    new BufferedReader(
                                            new InputStreamReader(Objects.requireNonNull(inputStream))
                                    )
                    ) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                    } catch (IOException ioe) {
                        Log.e("MyExceptions", "Open Text IO exception", ioe);
                    }

                    translateEditText.setText(stringBuilder.toString());
                    break;
                }

                case SAVE_TEXT_FILE: {
                    // The result data contains a URI for the document or directory that
                    // the user selected.
                    uri = resultData.getData();
                    Log.d("MyDebug", uri.getPath());
                    try {
                        ParcelFileDescriptor pfd = getContentResolver().
                                openFileDescriptor(uri, "w");
                        FileOutputStream fileOutputStream =
                                new FileOutputStream(pfd.getFileDescriptor());
                        fileOutputStream.write(translatedTextView.getText().toString().getBytes());
                        // Let the document provider know you're done by closing the stream.
                        fileOutputStream.close();
                        pfd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}