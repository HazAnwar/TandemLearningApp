package com.alpha.tandemexchange;

import android.graphics.Bitmap;

/**
 * This class contains the details of the user
 */

public class User {
    String username, forename, surname, password, email;
    String aboutme, languageKnow, languageLearn;
    int userid, score;
    Bitmap bitmap;


    /**
     * Used to create a user object with the following parameters
     * @param username is the user's unique username
     * @param forename is the user's forename
     * @param surname is the user's surname
     * @param password is the user's password
     * @param email is the user's email address
     */
    public User(String username, String forename, String surname, String password, String email){
        this.username = username;
        this.forename = forename;
        this.surname = surname;
        this.password = password;
        this.email = email;
    }

    /**
     * Used to create a user object with the following parameters
     * @param email is the user's email address
     * @param password is the user's password
     */
    public User(String email, String password){
        this.email = email;
        this.password = password;
        this.forename = "";
        this.surname = "";
    }

    /**
     * Used to create a user object with the following parameters
     * @param forename is the user's forename
     * @param surname is the user's surname
     * @param score is the user's score
     * @param aboutme is the information about the user
     * @param languageKnow is the language that the user already knows
     * @param languageLearn is the language that the user would like to learn
     */
    public User(String forename, String surname, int score, String aboutme, String languageKnow, String languageLearn){
        this.forename = forename;
        this.surname = surname;
        this.score = score;
        this.aboutme = aboutme;
        this.languageKnow = languageKnow;
        this.languageLearn = languageLearn;
    }

    /**
     * Used to create a user object with the following parameters
     * @param bitmap is the user's profile picture
     * @param username is the user's unique username
     * @param userid is the user's id number
     * @param forename is the user's forename
     * @param surname is the user's surname
     * @param password is the user's password
     * @param email is the user's email address
     * @param score is the user's score
     * @param aboutme is the information about the user
     * @param languageKnow is the language that the user already knows
     * @param languageLearn is the language that the user would like to learn
     */
    public User(Bitmap bitmap, String username, int userid, String forename, String surname, String password, String email, int score, String aboutme, String languageKnow, String languageLearn){
        this.bitmap = bitmap;
        this.username = username;
        this.userid = userid;
        this.forename = forename;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.score = score;
        this.aboutme = aboutme;
        this.languageKnow = languageKnow;
        this.languageLearn = languageLearn;
    }

    /**
     * Used to create a user object with the following parameters
     * @param userid is the user's id number
     * @param forename is the user's forename
     * @param surname is the user's surname
     * @param password is the user's password
     * @param email is the user's email address
     */
    public User(int userid, String forename, String surname, String password, String email){
        this.userid = userid;
        this.forename = forename;
        this.surname = surname;
        this.password = password;
        this.email = email;
    }

    /**
     * Used to create a user object with the following parameters
     * @param forename is the user's forename
     * @param surname is the user's surname
     * @param email is the user's email address
     * @param aboutme is the information about the user
     * @param languageKnow is the language that the user already knows
     * @param languageLearn is the language that the user would like to learn
     */
    public User(String forename, String surname, String email, String aboutme, String languageKnow, String languageLearn){
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.aboutme = aboutme;
        this.languageKnow = languageKnow;
        this.languageLearn = languageLearn;
    }

    /**
     * Used to create a user object with the following parameters
     * @param userid is the user's id number
     * @param forename is the user's forename
     * @param surname is the user's surname
     * @param password is the user's password
     * @param email is the user's email address
     * @param aboutme is the information about the user
     */
    public User(int userid, String forename, String surname, String password, String email, String aboutme){
        this.userid = userid;
        this.forename = forename;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.aboutme = aboutme;
    }

    /**
     * Used to create a user object with the following parameters
     * @param userid is the user's id number
     * @param forename is the user's forename
     * @param surname is the user's surname
     * @param email is the user's email address
     * @param aboutme is the information about the user
     * @param languageKnow is the language that the user already knows
     * @param languageLearn is the language that the user would like to learn
     */
    public User(int userid, String forename, String surname, String email, int score, String aboutme, String languageKnow, String languageLearn){
        this.userid = userid;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.score = score;
        this.aboutme = aboutme;
        this.languageKnow = languageKnow;
        this.languageLearn = languageLearn;
    }

    /**
     * Used to create a user object with the following parameters
     * @param bitmap is the user's profile picture
     * @param username is the user's unique username
     * @param userid is the user's id number
     * @param forename is the user's forename
     * @param surname is the user's surname
     * @param email is the user's email address
     * @param score is the user's score
     * @param aboutme is the information about the user
     * @param languageKnow is the language that the user already knows
     * @param languageLearn is the language that the user would like to learn
     */
    public User(Bitmap bitmap, String username, int userid, String forename, String surname, String email, int score, String aboutme, String languageKnow, String languageLearn){
        this.bitmap = bitmap;
        this.username = username;
        this.userid = userid;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.score = score;
        this.aboutme = aboutme;
        this.languageKnow = languageKnow;
        this.languageLearn = languageLearn;
    }

    /**
     * Used to create a user object with the following parameters
     * @param bitmap is the user's profile picture
     * @param userid is the user's id number
     * @param forename is the user's forename
     * @param surname is the user's surname
     * @param email is the user's email address
     * @param score is the user's score
     * @param aboutme is the information about the user
     * @param languageKnow is the language that the user already knows
     * @param languageLearn is the language that the user would like to learn
     */
    public User(Bitmap bitmap, int userid, String forename, String surname, String email, int score, String aboutme, String languageKnow, String languageLearn){
        this.bitmap = bitmap;
        this.userid = userid;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.score = score;
        this.aboutme = aboutme;
        this.languageKnow = languageKnow;
        this.languageLearn = languageLearn;
    }

    /**
     *Used to create a user object with the following parameters
     * @param userid is the user's id number
     * @param username is the user's username
     * @param email is the user's email address
     * @param score is the user's score
     * @param bitmap is the user's profile picture
     */
    public User(String username, int userid, String email, int score, Bitmap bitmap){
        this.username = username;
        this.userid = userid;
        this.email = email;
        this.score = score;
        this.bitmap = bitmap;
    }

    /**
     * An accessor (getter) method for accessing the user's score
     * @return returns the user's score
     */
    // method to return the score of a user

    public int getScore() {
        return score;
    }

    /**
     * An accessor (getter) method for accessing the user's forename
     * @return returns the user's forename
     */
    public String getForename() {
        return forename;
    }
}
