package org.michiganhackers.michiganhackers.profile;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.michiganhackers.michiganhackers.R;

/**
 * Decorator Adapter to allow a Spinner to show a 'Nothing Selected...' initially
 * displayed instead of the first choice in the Adapter.
 * Taken from: https://stackoverflow.com/questions/867518/how-to-make-an-android-spinner-with-initial-text-select-one
 * edited by Vincent Nagel
 */
public class NothingSelectedSpinnerAdapter implements SpinnerAdapter, ListAdapter {

    private static final int EXTRA = 1;
    // NOTE: I changed adapter from SpinnerAdapter to ArrayAdapter<CharSequence>, but I do not fully know the implications of this.
    // This was done to implement getPostition(). If this breaks NothingSelectedSpinnerAdapter, a
    // workaround could be to just do arraylist.getPosition(item) + EXTRA outside of this class wherever getPosition is needed (and account for -1)
    private final ArrayAdapter<CharSequence> adapter;
    private final Context context;
    private final int nothingSelectedLayout;
    private final int nothingSelectedDropdownLayout;
    private final LayoutInflater layoutInflater;
    private final String hint;

    // Use this constructor to not have the hint in the list of items
    public NothingSelectedSpinnerAdapter(ArrayAdapter<CharSequence> spinnerAdapter,
            int nothingSelectedLayout, String hint, Context context) {

        this(spinnerAdapter, nothingSelectedLayout, -1, hint, context);
    }

    // Use this constructor to have the hint as the first row in the list of items
    private NothingSelectedSpinnerAdapter(ArrayAdapter<CharSequence> spinnerAdapter, int nothingSelectedLayout,
                                         int nothingSelectedDropdownLayout, String hint, Context context) {
        this.adapter = spinnerAdapter;
        this.context = context;
        this.nothingSelectedLayout = nothingSelectedLayout;
        this.nothingSelectedDropdownLayout = nothingSelectedDropdownLayout;
        layoutInflater = LayoutInflater.from(context);
        this.hint = hint;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        // This provides the View for the Selected Item in the Spinner, not
        // the dropdown (unless dropdownView is not set).
        if (position == 0) {
            return getNothingSelectedView(parent);
        }
        return adapter.getView(position - EXTRA, null, parent); // Could re-use
        // the convertView if possible.
    }

    /**
     * View to show in Spinner with Nothing Selected
     * Override this to do something dynamic... e.g. "37 Options Found"
     */
    private View getNothingSelectedView(ViewGroup parent) {
        View layout =  layoutInflater.inflate(nothingSelectedLayout, parent, false);
        TextView textView = layout.findViewById(R.id.profile_spinner_row_nothing_selected_hint);
        textView.setText(hint);
        return layout;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Android BUG! http://code.google.com/p/android/issues/detail?id=17128 -
        // Spinner does not support multiple view types
        if (position == 0) {
            return nothingSelectedDropdownLayout == -1 ?
                    new View(context) :
                    getNothingSelectedDropdownView(parent);
        }

        // Could re-use the convertView if possible, use setTag...
        return adapter.getDropDownView(position - EXTRA, null, parent);
    }

    /**
     * Override this to do something dynamic... For example, "Pick your favorite
     * of these 37".
     */
    private View getNothingSelectedDropdownView(ViewGroup parent) {
        return layoutInflater.inflate(nothingSelectedDropdownLayout, parent, false);
    }

    @Override
    public int getCount() {
        return adapter.getCount() + EXTRA;
    }

    @Override
    public Object getItem(int position) {
        return position == 0 ? null : adapter.getItem(position - EXTRA);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position >= EXTRA ? adapter.getItemId(position - EXTRA) : position - EXTRA;
    }

    @Override
    public boolean hasStableIds() {
        return adapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return adapter.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        adapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0; // Don't allow the 'nothing selected' item to be picked.
    }

    public int getPosition(CharSequence item){
        int position = adapter.getPosition(item);
        return position == -1 ? -1 : position + EXTRA;
    }
}