package com.alpha.tandemexchange;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * The class allows a new user to register so that they can use the application
 */
public class Register extends AppCompatActivity implements View.OnClickListener {
    CheckBox checkBox;
    Button registerButton;
    TextView loginAccount;
    Toolbar registerToolbar;
    static Random r = new Random();
    String verifCode = randomString(6);
    Boolean[] regValues = new Boolean[5];
    String username, forename, surname, password, email, editTextCode;
    EditText registerUsername, registerForename, registerSurname, registerPassword, registerEmail, codeText;
    static final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * Creates an instance of the Register activity, setting up the GUI and the data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        registerToolbar = (Toolbar) findViewById(R.id.toolbar);
        checkBox = (CheckBox) findViewById(R.id.toggleRPass);
        registerForename = (EditText) findViewById(R.id.registerForename);
        registerSurname = (EditText) findViewById(R.id.registerSurname);
        registerPassword = (EditText) findViewById(R.id.registerPassword);
        registerUsername = (EditText) findViewById(R.id.registerUsername);
        registerEmail = (EditText) findViewById(R.id.registerEmail);
        loginAccount = (TextView) findViewById(R.id.loginAccount);
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        loginAccount.setOnClickListener(this);

        TextView textView = (TextView) findViewById(R.id.title);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            registerForename.setTextColor(Color.BLACK);
            registerSurname.setTextColor(Color.BLACK);
            registerUsername.setTextColor(Color.BLACK);
            registerEmail.setTextColor(Color.BLACK);
            registerPassword.setTextColor(Color.BLACK);
            registerButton.setTextColor(Color.BLACK);
            loginAccount.setTextColor(Color.GRAY);
            textView.setTextColor(Color.GRAY);
        }

        setSupportActionBar(registerToolbar);
        registerToolbar.setTitleTextColor(0xFFFFFFFF);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        for (int i = 0; i < 5; i++){
            regValues[i] = false;
        }


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            /**
             * Displays the password if the buttonView is clicked
             * @param buttonView is the button used to show the password the user has entered
             * @param isChecked is true if the user has clicked on buttonView
             *                  and false if the user hasn't clicked on buttonView
             */
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    registerPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    registerPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        registerForename.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            /**
             * Checks if the forename entered is longer than 1 character, and lets the user know if it isn't
             */
            @Override
            public void afterTextChanged(Editable s) {
                forename = registerForename.getText().toString();
                if (forename.length() < 2) {
                    registerForename.setError("Must be longer than one character!");
                    regValues[0] = false;
                } else {
                    registerForename.setError(null);
                    regValues[0] = true;
                }
            }
        });

        registerSurname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            /**
             * Checks if the surname entered is longer than 1 character, and lets the user know if it isn't
             */
            @Override
            public void afterTextChanged(Editable s) {
                surname = registerSurname.getText().toString();
                if(surname.length() < 2) {
                    registerSurname.setError("Must be longer than one character!");
                    regValues[1] = false;
                } else {
                    registerSurname.setError(null);
                    regValues[1] = true;
                }
            }
        });

        registerUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            // checks if the username is at most 15 characters long,
            // and lets the user know if it isn't

            @Override
            public void afterTextChanged(Editable s) {
                username = registerUsername.getText().toString();
                if(username.length() > 15) {
                    registerUsername.setError("Must be less than 15 characters!");
                    regValues[2] = false;
                } else {
                    registerUsername.setError(null);
                    regValues[2] = true;
                }
            }
        });


        registerEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            /**
             * Checks if the email address entered is a KCL email, and lets the user know if it isn't
             * Also checks if the email address entered their full KCL email and not the one that begins
             * with 'K1', and lets the user know if it isn't
             */
            @Override
            public void afterTextChanged(Editable s) {
                email = registerEmail.getText().toString();
                if(!email.endsWith("@kcl.ac.uk")) {
                    registerEmail.setError("Please enter a valid KCL email address!");
                    regValues[3] = false;
                } else if ((email.length() == 18 && email.startsWith("K1"))) {
                    registerEmail.setError("Please enter your full KCL email address, not your K1****** address!");
                    regValues[3] = false;
                } else {
                    registerEmail.setError(null);
                    regValues[3] = true;
                }
            }
        });

        registerPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            /**
             * Checks if the password is at least 6 characters long, and lets the user know if it isn't
             */
            public void afterTextChanged(Editable s) {
                password = registerPassword.getText().toString();
                if(password.length() < 6) {
                    registerPassword.setError("Must be at least 6 characters!");
                    regValues[4] = false;
                } else {
                    registerPassword.setError(null);
                    regValues[4] = true;
                }
            }
        });

    }

    @Override
    /**
     * Inflates the menu; this adds items to the action bar if it is present.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Provides an option to go straight to the login page if they already have an account
     * @param item is the item selected in the menu
     * @return the option selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.loginToAccount) {
            finish();
            startActivity(new Intent(this, Login.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /* Once the user has entered all the required information,
     * they can register successfully, otherwise it lets them know
     * that they haven't completed the register details correctly
     */
    @Override
    public void onClick(View v) {
        forename = registerForename.getText().toString();
        surname = registerSurname.getText().toString();
        password = registerPassword.getText().toString();
        email = registerEmail.getText().toString();
        username = registerUsername.getText().toString();
        switch (v.getId()) {
            case R.id.registerButton:
                int passRate = 0;
                for (int i = 0; i < 5; i++){
                    if (regValues[i]){
                        passRate++;
                    }
                }
                if(passRate == 5) {
                    Toast.makeText(getApplicationContext(), "Sending verification email...", Toast.LENGTH_LONG).show();
                    sendEmail();
                    verificationDialog();
                    break;
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter all the required information correctly!", Toast.LENGTH_LONG).show();
                    break;
                }

            case R.id.loginAccount:
                finish();
                startActivity(new Intent(this, Login.class));
                break;
        }
    }

    /**
     * Registers the user onto the server
     * @param user the user that has just registered
     */
    private void registerUser(User user){
        ServerRequest request = new ServerRequest(this);
        request.storedUserDataInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }

    String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for( int i=0; i<len; i++ )
            sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        return sb.toString();
    }

    /** This method sends the user a verification code
     * to their KCL email address which they must enter to
     * activate their account.
     */
    private void sendEmail(){
        try {
            GMailSender sender = new GMailSender("projectrunalpha@gmail.com", "alpharuntest");
            sender.sendMail("KCL Tandem Language Learning Account Activation",
                    "Welcome to the KCL Tandem Language Learning application," +
                            "\n\nPlease enter the code below into the application to activate your account!" +
                            "\n\nYour verification code is: " + verifCode +
                            "\n\nThanks," +
                            "\nThe Tandem Team",
                    "projectrunalpha@gmail.com",
                    registerEmail.getText().toString());
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    /** This is the dialog box where the verification code
     * needs to be entered to complete the verification process
     * It also has a resend button to send the code again
     * and notifies the user of what they need to do
     */
    private void verificationDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View verificationDialogView = factory.inflate(R.layout.layout_dialog, null);
        final AlertDialog.Builder verifDialog = new AlertDialog.Builder(this);
        verifDialog.setView(verificationDialogView);
        verifDialog.setTitle("Account Verification");
        verifDialog.setMessage("We have sent a verification code to your email address, please type it in below...");
        verifDialog.setIcon(R.drawable.ic_error_black_48dp);
        verifDialog.setNeutralButton("Resend", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendEmail();
                Toast.makeText(getApplicationContext(), "Sending verification email...", Toast.LENGTH_LONG).show();
                verificationDialog();
            }
        });

        verifDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            /**
             * This verifies if the code entered is correct or not and informs the user if they need to re-enter
             * their details
             * @param dialog
             * @param which
             */
            public void onClick(DialogInterface dialog, int which) {
                codeText = (EditText) verificationDialogView.findViewById(R.id.verificationInput);
                editTextCode = codeText.getText().toString();
                if (verifCode.equals(editTextCode)) {
                    Toast.makeText(getApplicationContext(), "Your account has been successfully created!", Toast.LENGTH_LONG).show();
                    User user = new User(username, forename, surname, password, email);
                    registerUser(user);
                    finish();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR! The code you entered does not match, please try again.", Toast.LENGTH_LONG).show();
                    verificationDialog();
                }
            }
        });
        verifDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            /**
             * Closes the dialog when the Cancel button is clicked
             * @param dialog
             * @param which
             */
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        verifDialog.show();
    }

}
