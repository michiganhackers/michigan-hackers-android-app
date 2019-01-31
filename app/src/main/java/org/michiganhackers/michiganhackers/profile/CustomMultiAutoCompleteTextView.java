package org.michiganhackers.michiganhackers.profile;

import android.app.Activity;
import android.widget.ActionMenuView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;

import org.michiganhackers.michiganhackers.SemicolonTokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomMultiAutoCompleteTextView {

        private ArrayAdapter<CharSequence> arrayAdapter;
        // List is used to check input against because ArrayAdapter.getPosition() only looks at mObjects
        // and not mOriginalValues
        private List<CharSequence> originalList;
        private MultiAutoCompleteTextView multiAutoCompleteTextView;
        private TextInputLayout textInputLayout;
        private int emptyErrorResource, invalidErrorSuffixResource;
        private Activity activity;

        public CustomMultiAutoCompleteTextView(MultiAutoCompleteTextView multiAutoCompleteTextView,
                                               TextInputLayout textInputLayout, int emptyErrorResource,
                                               int invalidErrorSuffixResource, Integer initialListResource,
                                               Activity activity) {
            this.activity = activity;
            this.originalList = new ArrayList<>();
            if (initialListResource != null) {
                originalList.addAll(Arrays.asList(this.activity.getResources().getStringArray(initialListResource)));
            }

            this.multiAutoCompleteTextView = multiAutoCompleteTextView;
            this.textInputLayout = textInputLayout;
            this.emptyErrorResource = emptyErrorResource;
            this.invalidErrorSuffixResource = invalidErrorSuffixResource;

            this.arrayAdapter = new ArrayAdapter<>(this.activity, android.R.layout.simple_dropdown_item_1line, this.originalList);
            this.multiAutoCompleteTextView.setAdapter(this.arrayAdapter);
            this.multiAutoCompleteTextView.setTokenizer(new SemicolonTokenizer());

        }

        public boolean contains(CharSequence item) {
            return originalList.contains(item);
        }

        public void add(CharSequence item) {
            originalList.add(item);

            arrayAdapter.add(item);
            arrayAdapter.notifyDataSetChanged();
        }

        public void addAll(List<String> items) {
            originalList.clear();
            originalList.addAll(items);

            arrayAdapter.clear();
            arrayAdapter.addAll(items);
            arrayAdapter.notifyDataSetChanged();
        }

        // Adds all items from itemList that are in originalList
        public void fill(List<String> itemList) {
            StringBuilder sb = new StringBuilder();
            for (String item : itemList) {
                if (originalList.contains(item)) {
                    sb.append(item).append("; ");
                }
            }
            multiAutoCompleteTextView.setText(sb.toString());
        }

        public boolean checkInput(List<String> inputList) {
            boolean warningShown = false;
            if (inputList.size() == 0) {
                textInputLayout.setError(this.activity.getString(emptyErrorResource));
                warningShown = true;
            } else {
                for (String input : inputList) {
                    if (!originalList.contains(input)) {
                        textInputLayout.setError("\"" + input + "\" " + this.activity.getString(invalidErrorSuffixResource));
                        warningShown = true;
                        break;
                    }
                }
            }
            if (!warningShown) {
                textInputLayout.setError(null);
            }
            return warningShown;
        }

        public List<String> getInput() {
            List<String> list = new ArrayList<>(Arrays.asList(multiAutoCompleteTextView.getText().toString().trim().split("\\s*;\\s*")));
            // remove duplicates
            Set<String> set = new HashSet<>(list);
            list.clear();
            list.addAll(set);
            // if input is empty, list will have 1 empty string
            if (list.size() == 1 && list.get(0).isEmpty()) {
                list.remove(0);
            }
            return list;
        }

    }

