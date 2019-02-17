package com.alpha.tandemexchange;

import android.test.AndroidTestCase;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RecyclerViewAdapterTest extends AndroidTestCase {

    private List<User> userList;
    User user1;
    User user2;

    public void setUp() throws Exception {
        user1 = new User(1, "Name1", "Surname1", "name1.surname1@kcl.ac.uk", 10, "Things about me 1", "English", "Spanish");
        user2 = new User(2, "Name2", "Surname2", "name2.surname2@kcl.ac.uk", 20, "Things about me 2", "Spanish", "English");
        userList.add(user1);
        userList.add(user2);
    }

    @Test
    public void testGetItemCount() throws Exception {
        assertEquals(userList.size(), 2);
    }

    @Test
    public void testOnBindViewHolder() throws Exception {
        RecyclerViewAdapter.ViewHolder viewHolder = null;
        int i = 0;
        User user = userList.get(i);
        assertEquals(user, user1);
        viewHolder.name.setText(user.forename + " " + user.surname);
        viewHolder.score.setText(String.valueOf(user.score));
        viewHolder.aboutme.setText(user.aboutme);
        viewHolder.languageKnow.setText(" " + user.languageKnow);
        viewHolder.languageLearn.setText(" " + user.languageLearn);

        assertEquals("Name1 Surname1", viewHolder.name.getText());
        assertEquals(10, Integer.parseInt(viewHolder.score.getText().toString()));
        assertEquals("Things about me 1", viewHolder.aboutme.getText());
        assertEquals(" English", viewHolder.languageKnow.getText());
        assertEquals(" Spanish", viewHolder.languageLearn.getText());
    }
}