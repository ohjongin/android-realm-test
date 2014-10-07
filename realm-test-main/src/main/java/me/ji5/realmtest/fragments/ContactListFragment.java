package me.ji5.realmtest.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Iterator;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import me.ji5.realmtest.GlobalConstants;
import me.ji5.realmtest.R;
import me.ji5.realmtest.fragments.dummy.DummyContent;
import me.ji5.realmtest.model.Contact;
import me.ji5.realmtest.model.Phone;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ContactListFragment extends ListFragment implements GlobalConstants {
    protected static final String TAG = ContactListFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    public static ContactListFragment newInstance() {
        ContactListFragment fragment = new ContactListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if (getArguments() != null) {
        }

        Realm realm = Realm.getInstance(getActivity());
        RealmQuery<Contact> query = realm.where(Contact.class);
        RealmResults<Contact> result = query.findAll();

        for (Contact contact : result) {
            Log.e(TAG, contact.getDisplayName());
        }

        // TODO: Change Adapter to display your content
        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.contact, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean consume = false;

        switch (item.getItemId()) {
            case R.id.action_add_new:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
                consume = true;
                break;
            default:
                break;
        }

        return consume;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (data != null && data.getExtras() != null) {
            Bundle extras = data.getExtras();
            Set keys = extras.keySet();
            Iterator iterate = keys.iterator();
            while (iterate.hasNext()) {
                String key = (String) iterate.next();
                Log.v(TAG, key + "[" + extras.get(key) + "]");
            }
            Uri result = data.getData();
            Log.v(TAG, "Got a result: " + result.toString());
        }

        switch (requestCode) {
            case PICK_CONTACT:
                // content://com.android.contacts/contacts/lookup/0r7-2C46324E483C324A3A484634/7
                Uri contactData = data.getData();
                long contactId = -1;
                Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                if (!c.moveToFirst()) {
                    c.close();
                    break;
                }

                contactId = c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID));

                // get the contact id from the Uri
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String id = contactData.getLastPathSegment();
                String selection = ContactsContract.Data.CONTACT_ID + "=?";
                String[] selectionArgs = new String[] { String.valueOf(contactId) };
                c =  getActivity().getContentResolver().query(uri, null, selection, selectionArgs, null);

                // Log.e(TAG, uri.toString());
                // printCursorColumns(c);

                if (!c.moveToFirst()) {
                    c.close();
                    break;
                }

                //long contactId = c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID));
                long dbId = c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                String displayName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int phoneType = c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                int photoId = c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));

                Realm realm = Realm.getInstance(getActivity());
                realm.beginTransaction();

                Contact contact = realm.createObject(Contact.class);
                contact.setConactId(contactId);
                contact.setDbId(dbId);
                contact.setDisplayName(displayName);
                contact.setPhotoId(photoId);

                Phone phone = realm.createObject(Phone.class);
                phone.setPhoneNumber(phoneNumber);
                phone.setPhoneType(phoneType);
                contact.getPhones().add(phone);

                realm.commitTransaction();

                c.close();
                break;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(Uri.parse("fragment://" + getClass().getSimpleName() + "/" + position));
        }
    }

    protected void printCursorColumns(Cursor c) {
        for (int i = 0; i < c.getColumnCount(); i++) {
            Log.e(TAG, "[" + i + "] " + c.getColumnName(i));
        }
    }
}
