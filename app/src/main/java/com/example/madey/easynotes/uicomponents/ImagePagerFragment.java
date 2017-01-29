package com.example.madey.easynotes.uicomponents;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.madey.easynotes.AsyncTasks.UpdateImageListTask;
import com.example.madey.easynotes.MainActivity;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.models.ImageModel;
import com.example.madey.easynotes.models.SimpleNoteModel;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImagePagerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImagePagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagePagerFragment extends Fragment {

    private static final String LOG_TAG = "ImagePagerFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ViewPager viewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private SimpleNoteModel simpleNoteModel;
    private int position;
    private OnFragmentInteractionListener mListener;

    public ImagePagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param simpleNoteModel Parameter 1. The note model from which need to show the images
     * @param position        Parameter 1.
     * @return A new instance of fragment ImagePagerFragment.
     */
    public static ImagePagerFragment newInstance(SimpleNoteModel simpleNoteModel, int position) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, simpleNoteModel);
        args.putInt(ARG_PARAM2, position);
        fragment.setArguments(args);
        return fragment;
    }

    public SimpleNoteModel getSimpleNoteModel() {
        return simpleNoteModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            simpleNoteModel = getArguments().getParcelable(ARG_PARAM1);
            position = getArguments().getInt(ARG_PARAM2);
        }
        MainActivity.CURRENT_FRAGMENT = MainActivity.FRAGMENTS.PAGER;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image_pager, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        imagePagerAdapter = new ImagePagerAdapter(getChildFragmentManager(), simpleNoteModel.getImageModels());
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(position, true);

        //Configure tool bar
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_image:
                int position = viewPager.getCurrentItem();
                ImageModel imageModel = simpleNoteModel.getImageModels().get(position);
                File file = new File(Utils.getStoragePath(getActivity()) + "/" + imageModel.getImageFileName());
                if (file.exists() && file.delete()) {
                    imagePagerAdapter.removeImageModel(position);
                    //also update the note, by running an AsyncTask
                    UpdateImageListTask updateImageListTask = new UpdateImageListTask(getActivity()) {
                        @Override
                        protected void onPostExecute(Integer affected) {
                            if (affected > 0) {
                                Log.d(LOG_TAG, "Updated imageModels for note with id " + simpleNoteModel.getId());
                                if (0 == imagePagerAdapter.getCount()) {
                                    Toast.makeText(getActivity(), "Image Deleted", Toast.LENGTH_SHORT).show();
                                    getFragmentManager().popBackStack();
                                    getFragmentManager().beginTransaction().remove(ImagePagerFragment.this).commit();
                                }
                            } else {
                                Log.w(LOG_TAG, "There was an error updating imageModels for note with id " + simpleNoteModel.getId() + ". The note may not have been persisted yet.");
                                getFragmentManager().popBackStack();
                                getFragmentManager().beginTransaction().remove(ImagePagerFragment.this).commit();
                            }
                        }
                    };
                    updateImageListTask.execute(simpleNoteModel);
                }
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu != null) {
            menu.setGroupVisible(R.id.pager_menu_group, true);
            menu.setGroupVisible(R.id.create_note_menu_group, false);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
