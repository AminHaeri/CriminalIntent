package com.example.amin.criminalintent.controller;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.amin.criminalintent.R;
import com.example.amin.criminalintent.models.Crime;
import com.example.amin.criminalintent.models.CrimeLab;
import com.example.amin.criminalintent.utils.PictureUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeDetailFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crimeId";
    private static final String DIALOG_TAG = "DialogDate";
    private static final int REQ_DATE_PICKER = 0;
    private static final int REQ_CONTACT = 1;
    private static final int REQ_PHOTOS = 2;

    private Crime mCrime;

    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;

    private ImageView mPhotoView;
    private ImageButton mPhotoButton;

    private File mPhotoFile;

    public static CrimeDetailFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeDetailFragment fragment = new CrimeDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CrimeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime_detail, container, false);

        mTitleField = view.findViewById(R.id.crime_title);
        mDateButton = view.findViewById(R.id.crime_date);
        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        mSuspectButton = view.findViewById(R.id.crime_suspect);
        mReportButton = view.findViewById(R.id.crime_report);
        mPhotoButton = view.findViewById(R.id.crime_camera);
        mPhotoView = view.findViewById(R.id.crime_photo);

        handleTitleTextField();
        handleSolvedCheckbox();
        handleDateButton();
        handleSuspectButton();
        handleReportButton();
        handlePhotoButton();

        updatePhotoView();
        return view;
    }

    private void handlePhotoButton() {
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Uri uri = getPhotoFileUri();
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                PackageManager packageManager = getActivity().getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(
                        captureIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : activities) {
                    getActivity().grantUriPermission(
                            activity.activityInfo.packageName,
                            uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureIntent, REQ_PHOTOS);
            }
        });
    }

    private void handleReportButton() {
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = new Intent(Intent.ACTION_SEND);
                reportIntent.setType("text/plain");
                reportIntent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                reportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));

                startActivity(Intent.createChooser(reportIntent, getString(R.string.send_report)));
            }
        });
    }

    private void handleSuspectButton() {
        final Intent chooseContact = new Intent(Intent.ACTION_PICK);
        chooseContact.setType(ContactsContract.Contacts.CONTENT_TYPE);

        final PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(chooseContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(chooseContact, REQ_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
    }

    private void handleDateButton() {
        mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mCrime.getDate());
                datePickerFragment.setTargetFragment(CrimeDetailFragment.this,
                        REQ_DATE_PICKER);
                datePickerFragment.show(getFragmentManager(), DIALOG_TAG);
            }
        });
    }

    private void handleSolvedCheckbox() {
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                onCrimeUpdate(mCrime);
            }
        });
    }

    private void handleTitleTextField() {
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                onCrimeUpdate(mCrime);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private Uri getPhotoFileUri() {
        return FileProvider.getUriForFile(getActivity(),
                            "com.example.amin.criminalintent.fileprovider",
                            mPhotoFile);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInstance(getActivity()).update(mCrime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQ_DATE_PICKER) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mDateButton.setText(date.toString());
            onCrimeUpdate(mCrime);
        } else if (requestCode == REQ_CONTACT) {
            Uri contactUri = data.getData();

            Cursor cursor = getActivity().getContentResolver().query(contactUri,
                    null,
                    null,
                    null,
                    null);

            try {
                if (cursor.getCount() == 0)
                    return;

                cursor.moveToFirst();
                String suspectName = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                mCrime.setSuspect(suspectName);
                mSuspectButton.setText(suspectName);
                onCrimeUpdate(mCrime);
            } finally {
                cursor.close();
            }
        } else if (requestCode == REQ_PHOTOS) {
            Uri uri = getPhotoFileUri();
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private String getCrimeReport() {
        String dateString = new SimpleDateFormat("yyyy/MM/dd").format(mCrime.getDate());

        String solvedString = mCrime.isSolved() ?
                getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        String suspectString = mCrime.getSuspect() == null ?
                getString(R.string.crime_report_no_suspect) :
                getString(R.string.crime_report_suspect, mCrime.getSuspect());

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspectString);
        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(),
                    getActivity());

            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void onCrimeUpdate(Crime crime) {
        CrimeLab.getInstance(getActivity()).update(mCrime);

        CrimeListFragment crimeListFragment = (CrimeListFragment)
                getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_list_container);
        crimeListFragment.updateUI();
    }
}












