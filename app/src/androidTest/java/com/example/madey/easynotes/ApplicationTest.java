package com.example.madey.easynotes;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.example.madey.easynotes.models.HeterogeneousArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public static void main(String[] args) {
        HeterogeneousArrayList<Object> list = new HeterogeneousArrayList<>();

        list.add(new StringBuilder());
        list.add(new StringBuilder());
        list.add(new StringBuilder());
        list.add(new StringBuilder());
        list.add(new StringBuilder());

        System.out.println(list.getCount(StringBuilder.class));
    }
}