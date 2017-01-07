package com.example.madey.easynotes;

import com.example.madey.easynotes.models.AudioClipModel;
import com.example.madey.easynotes.models.CoarseAddress;
import com.example.madey.easynotes.models.Coordinates;
import com.example.madey.easynotes.models.ImageModel;
import com.example.madey.easynotes.models.ListItemModel;
import com.example.madey.easynotes.models.SimpleNoteModel;
import com.google.gson.Gson;

import java.util.Arrays;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest {
    public ApplicationTest() {
    }

    public static void main(String[] args) {

        SimpleNoteModel simpleNoteModel = new SimpleNoteModel();

        simpleNoteModel.setId(1);
        simpleNoteModel.setTitle("Hello World");
        simpleNoteModel.setContent("Great Content Fuck You Bro!!!!!");
        simpleNoteModel.setCreationDate(System.currentTimeMillis());
        simpleNoteModel.setLastModifiedDate(System.currentTimeMillis() + 2598741);
        AudioClipModel audioClipModel = new AudioClipModel("Hello World Audio", "Hello World Audi Description Fuck the police!! ");
        audioClipModel.toString();
        simpleNoteModel.setHasAudioRecording(true);
        simpleNoteModel.setAudioClipModels(Arrays.asList(audioClipModel));
        simpleNoteModel.setLocationEnabled(true);
        simpleNoteModel.setCoarseAddress(new CoarseAddress("Lucknow", "India"));
        simpleNoteModel.setCoordinates(new Coordinates("2, 5"));
        simpleNoteModel.setHasImages(true);
        ImageModel imageModel = new ImageModel();
        imageModel.setFileName("IMG_2134.PNG");
        imageModel.setCaption("Hello Fucking Awesome Image!!!");
        imageModel.setOriginalBitmap(null);
        simpleNoteModel.setImageModels(Arrays.asList(imageModel));

        simpleNoteModel.setHasList(true);
        ListItemModel listItemModel = new ListItemModel("Fuckin A");
        ListItemModel listItemModel1 = new ListItemModel("Fucking B");
        listItemModel1.setChecked(true);

        simpleNoteModel.setListItems(Arrays.asList(listItemModel, listItemModel1));

        Gson gson = new Gson();

        System.out.println(gson.toJson(simpleNoteModel));
        String jsonString = gson.toJson(simpleNoteModel);

        gson.fromJson(jsonString, SimpleNoteModel.class);

        /*
        HeterogeneousArrayList<Object> list = new HeterogeneousArrayList<>();

        list.add(new StringBuilder());
        list.add(new StringBuilder());
        list.add(new StringBuilder());
        list.add(new StringBuilder());
        list.add(new StringBuilder());

        System.out.println(list.getCount(StringBuilder.class));
        */
    }
}