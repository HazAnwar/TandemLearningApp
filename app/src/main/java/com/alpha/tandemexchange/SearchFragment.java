package com.alpha.tandemexchange;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class is the fragment in the Main Activity that shows a list of all the users. It includes
 * sorting and filtering options
 */

public class SearchFragment extends Fragment {

    /**
     * The list of users is displayed using a RecyclerView, for which the following fields are necessary
     */
    RecyclerView.LayoutManager layoutManager;
    static RecyclerView.Adapter recyclerViewAdapter;
    static RecyclerView recyclerView;

    /**
     * These RadioGroups and RadioButtons are for choosing the sorting options: By score and alphabetically
     *  and descending and ascending
     */
    static RadioGroup orderRadioGroup, sortRadioGroup;
    static RadioButton radioButtonAscending, radioButtonDecending, radioButtonScore, radioButtonAlphabet;

    /**
     * Filters the users to show only those with languages in common with the signed in user
     */
    static CheckBox checkboxFilter;

    /**
     * Button to show the dialog with sorting and filtering options
     */
    MenuItem sortUsers;

    /**
     * List of all the users in the database
     */
    static List<User> arrayListUsers;

    /**
     * Language known, Language to learn and ID of the signed in user
     */
    private String userLanguageKnown, userLanguageLearn;
    static int userID;

    /**
     * Booleans necessary to implement sorting and filtering
     */
    static boolean ascending, score, filterLangauge;

    /**
     * Creates the view of the fragment and adds the RecyclerView with all users to it
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return returns the View of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View searchView = inflater.inflate(R.layout.fragment_search, null);
        setHasOptionsMenu(true);

        ServerRequest request = new ServerRequest(getContext());
        request.getAllUserDataAsyncTask();

        //populates the search fragment with all the other users in the database
        recyclerView = (RecyclerView) searchView.findViewById(R.id.recyclerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        ascending = true;

        getUserDetails();

        return searchView;
    }

    @Override
    /**
     *Inflates the search menu
     */
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        sortUsers = menu.findItem(R.id.sortUsers);
    }

    /**
     * Adds the sorting and filtering button, which creates the dialog when clicked
     * @param item is the selected MenuItem
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortUsers:
                sortDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to pass the data to the RecyclerView. It doesn't show the signed in user in the list
     * @param arrayList is an ArrayList with all the users in the database
     */

    public static void refreshRecyclerView(ArrayList arrayList){
        List<User> userList = arrayList;
        ArrayList arrayListUser = new ArrayList();
        for (int i = 0; i < userList.size(); i++) {
            if (userID != userList.get(i).userid) {
                arrayListUser.add(userList.get(i));
            }
        }
        recyclerViewAdapter = new RecyclerViewAdapter(arrayListUser);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public static void setUsersList(ArrayList arrayList) {
        arrayListUsers = arrayList;
    }

    /**
     * Method to get some data about the signed in user
     */
    private void getUserDetails() {
        StoreLocalUserData storeLocalUserData = new StoreLocalUserData(getActivity());
        User user = storeLocalUserData.getLoggedInUser();
        userLanguageKnown = user.languageKnow;
        userLanguageLearn = user.languageLearn;
        userID = user.userid;
    }

    /**
     * Method to create and show a Dialog with options for sorting and filtering the RecyclerView list of users
     */
    private void sortDialog() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View sortDialogView = factory.inflate(R.layout.sort_dialog, null);

        orderRadioGroup = (RadioGroup) sortDialogView.findViewById(R.id.orderType);
        radioButtonAscending = (RadioButton) sortDialogView.findViewById(R.id.radioButtonAscending);
        radioButtonDecending = (RadioButton) sortDialogView.findViewById(R.id.radioButtonDecending);
        sortRadioGroup = (RadioGroup) sortDialogView.findViewById(R.id.sortType);
        radioButtonScore = (RadioButton) sortDialogView.findViewById(R.id.radioButtonScore);
        radioButtonAlphabet = (RadioButton) sortDialogView.findViewById(R.id.radioButtonAlphabet);
        checkboxFilter = (CheckBox) sortDialogView.findViewById(R.id.checkboxFilter);

        checkboxFilter.setChecked(filterLangauge);
        if (ascending) {
            orderRadioGroup.check(radioButtonAscending.getId());
        } else {
            orderRadioGroup.check(radioButtonDecending.getId());
        } if (score) {
            sortRadioGroup.check(radioButtonScore.getId());
        } else {
            sortRadioGroup.check(radioButtonAlphabet.getId());
        }


        final android.app.AlertDialog.Builder sortUsersDialog = new android.app.AlertDialog.Builder(getActivity());
        sortUsersDialog.setTitle("Sort By")
                .setView(sortDialogView)
                .setIcon(R.drawable.ic_tune_black_48dp)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ArrayList arrayList = new ArrayList();
                                if (checkboxFilter.isChecked()) {
                                    for (int i = 0; i < arrayListUsers.size(); i++) {
                                        if (userLanguageLearn.equals(arrayListUsers.get(i).languageKnow) &&
                                                userLanguageKnown.equals(arrayListUsers.get(i).languageLearn)) {
                                            arrayList.add(arrayListUsers.get(i));
                                        }
                                    }
                                    filterLangauge = true;
                                } else {
                                    for (int i = 0; i < arrayListUsers.size(); i++) {
                                        if (userID != arrayListUsers.get(i).userid) {
                                            arrayList.add(arrayListUsers.get(i));
                                        }
                                    }
                                    filterLangauge = false;
                                }

                                int buttonID = sortRadioGroup.getCheckedRadioButtonId();
                                int buttonId = orderRadioGroup.getCheckedRadioButtonId();
                                if (buttonID == radioButtonScore.getId()) {
                                    if (buttonId == radioButtonAscending.getId()) {
//                                        Toast.makeText(getContext(), "Sorting by score in ascending order...", Toast.LENGTH_SHORT).show();
                                        Collections.sort(arrayList, new Comparator<User>() {
                                            public int compare(User user1, User user2) {
                                                return user1.getScore() - user2.getScore();
                                            }
                                        });
                                        ascending = true;
                                        score = true;
                                    } else if (buttonId == radioButtonDecending.getId()) {
//                                        Toast.makeText(getContext(), "Sorting by score in descending order...", Toast.LENGTH_SHORT).show();
                                        Collections.sort(arrayList, new Comparator<User>() {
                                            public int compare(User user1, User user2) {
                                                return user2.getScore() - user1.getScore();
                                            }
                                        });
                                        ascending = false;
                                        score = true;
                                    }
                                } else if (buttonID == radioButtonAlphabet.getId()) {
                                    if (buttonId == radioButtonAscending.getId()) {
//                                        Toast.makeText(getContext(), "Sorting alphabetically in ascending order...", Toast.LENGTH_SHORT).show();
                                        Collections.sort(arrayList, new Comparator<User>() {
                                            public int compare(User user1, User user2) {
                                                return user1.getForename().compareTo(user2.getForename());
                                            }
                                        });
                                        ascending = true;
                                        score = false;
                                    } else if (buttonId == radioButtonDecending.getId()) {
//                                        Toast.makeText(getContext(), "Sorting alphabetically in descending order...", Toast.LENGTH_SHORT).show();
                                        Collections.sort(arrayList, new Comparator<User>() {
                                            public int compare(User user1, User user2) {
                                                return user2.getForename().compareTo(user1.getForename());
                                            }
                                        });
                                        ascending = false;
                                        score = false;
                                    }
                                }

                                refreshRecyclerView(arrayList);
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
        sortUsersDialog.create();
        sortUsersDialog.show();
    }
}