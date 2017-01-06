package com.example.madey.easynotes;

import com.example.madey.easynotes.models.AudioClipModel;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest {
    public ApplicationTest() {
    }

    public static void main(String[] args) {

        AudioClipModel audioClipModel = new AudioClipModel("Hello World", "Hello World, I am testing you!! ");
        audioClipModel.toString();
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