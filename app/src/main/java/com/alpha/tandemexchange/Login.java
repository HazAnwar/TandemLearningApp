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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.alpha.tandemexchange.util.AuthenticationProvider;

import java.util.Random;

/**
 * This activity allows the user to login onto his existing account
 */
public class Login extends AppCompatActivity implements View.OnClickListener {

    /**
     * Field to show/hide the password characters
     */
    CheckBox checkBox;

    /**
     * Attemps to login when clicked
     */
    Button loginButton;

    /**
     * Fields for logging in
     */
    EditText loginEmail, loginPassword, emailEntry, verifEntry, passwordEntry;

    /**
     * Opens dialog when the user does not know password
     */
    TextView forgotPassword;

    /**
     * Field with the users data
     */
    StoreLocalUserData storeLocalUserData;

    Toolbar loginToolbar;
    Boolean passwordIsValid = false;
    public String editTextCode, password, verifCode = randomString(6);
    private String email;
    static final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static Random r = new Random();

    /**
     * Creates an instance of the Login activity, setting up the GUI and data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        loginToolbar = (Toolbar) findViewById(R.id.toolbar);
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        forgotPassword = (TextView)findViewById(R.id.forgotPassword);
        checkBox = (CheckBox) findViewById(R.id.togglePassword);
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

        //Sets the correct color for older API versions
        TextView textView = (TextView) findViewById(R.id.title);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            loginEmail.setTextColor(Color.BLACK);
            loginPassword.setTextColor(Color.BLACK);
            loginButton.setTextColor(Color.BLACK);
            forgotPassword.setTextColor(Color.GRAY);
            textView.setTextColor(Color.GRAY);
        }

        setSupportActionBar(loginToolbar);
        loginToolbar.setTitleTextColor(0xFFFFFFFF);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        storeLocalUserData = new StoreLocalUserData(this);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

    }

    /**
     * Inflates the menu; this adds items to the action bar if it is present.
     * @param menu
     * @return returns the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Sets up the menu behaviour when clicked
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.registerAccount) {
            finish();
            startActivity(new Intent(this, Register.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the behaviour for the login Button and forgotPassword TextView. The Login button authenticates
     * the user and the forgotPassword TextView shows the forgotPasswordDialog
     * @param v is the View that has been clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                User user = new User(email, password);

                authenticate(user);
                break;

            case R.id.forgotPassword:
                forgotPasswordDialog();

        }
    }

    /**
     * Generates a random verification code as a String for when the user has forgotten his/her password
     * @param len is the length of the verification code to be generated
     * @return returns the random String
     */
    String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for( int i=0; i<len; i++ )
            sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        return sb.toString();
    }

    /**
     * Method to create and show a dialog to allow the user to recover his account by sending a verification
     * code to his/her KCL sign up email. It will then launch the verificationDialog
     */
    private void forgotPasswordDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View changePasswordDialogView = factory.inflate(R.layout.layout_changepass_dialog, null);
        final AlertDialog.Builder forgotPassDialog = new AlertDialog.Builder(this);
        forgotPassDialog.setView(changePasswordDialogView);
        forgotPassDialog.setTitle("Enter your email address");
        forgotPassDialog.setMessage("Please enter your email address below: ");
        forgotPassDialog.setIcon(R.drawable.ic_error_black_48dp);

        forgotPassDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                emailEntry = (EditText) changePasswordDialogView.findViewById(R.id.emailInput);
                email = emailEntry.getText().toString();
                sendChangePasswordEmail(email);
                verificationDialog();

            }
        });
        forgotPassDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        forgotPassDialog.show();
    }

    /**
     * Method to create and show the dialog to allow the user to enter the verification code sent to his/her
     * email. If the entered code matches, the changePasswordDialog will launch
     */
    private void verificationDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View verificationDialogView = factory.inflate(R.layout.layout_dialog, null);
        final AlertDialog.Builder verifDialog = new AlertDialog.Builder(this);
        verifDialog.setView(verificationDialogView);
        verifDialog.setTitle("Account Verification");
        verifDialog.setMessage("We have sent a verification code to your email address, please type it in below...");
        verifDialog.setIcon(R.drawable.ic_error_black_48dp);

        verifDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                verifEntry = (EditText) verificationDialogView.findViewById(R.id.verificationInput);
                editTextCode = verifEntry.getText().toString();
                if (verifCode.equals(editTextCode)) {
                    changePasswordDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR! The code you entered does not match, please try again.", Toast.LENGTH_LONG).show();
                    verificationDialog();
                }
            }
        });
        verifDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        verifDialog.show();
    }

    /**
     * Method to create and show the dialog to allow the user to change his/her password if the verification code
     * was correct. The password must be valid (at least six characters)
     */
    private void changePasswordDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View changePasswordDialogView = factory.inflate(R.layout.layout_enternewpass_dialog, null);
        final AlertDialog.Builder changePassDialog = new AlertDialog.Builder(this);
        changePassDialog.setView(changePasswordDialogView);
        changePassDialog.setTitle("Change password");
        changePassDialog.setMessage("Enter a new password below");
        changePassDialog.setIcon(R.drawable.ic_error_black_48dp);
        changePassDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //new user password entered here, could someone make it at least 6 characters long
                passwordEntry = (EditText) changePasswordDialogView.findViewById(R.id.newPasswordInput);
                passwordEntry.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // do nothing
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // do nothing
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        password = passwordEntry.getText().toString();
                        if (password.length() < 6) {
                            passwordEntry.setError("Must be at least 6 characters!");
                            passwordIsValid = false;
                        } else {
                            passwordEntry.setError(null);
                            passwordIsValid = true;
                        }
                    }
                });
                if (passwordIsValid) {
                    User user = new User(email, password);
                    updateUser(user);
                    finish();
                }
            }
        });
        changePassDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        changePassDialog.show();
    }

    /**
     * If the password has been changed, this method updates the data to the server and database
     * @param user is the user of which the information is being updated
     */
    private void updateUser(User user){
        ServerRequest request = new ServerRequest(this);
        request.changePasswordInBackground(user);
        storeLocalUserData = new StoreLocalUserData(this);
        storeLocalUserData.storeUserData(user);
    }

    /**
     * Method to send an email with the verification code to change the password
     * @param email is the email of the user to which the verification email is sent
     */
    private void sendChangePasswordEmail(String email){
        try {
            GMailSender sender = new GMailSender("projectrunalpha@gmail.com", "alpharuntest");
            sender.sendMail("KCL Tandem Language Learning Account Activation",
                    "Please enter the code below into the application to change your password" +
                            "\n\nYour verification code is: " + verifCode +
                            "\n\nThanks," +
                            "\nThe Tandem Team",
                    "projectrunalpha@gmail.com",
                    email);
        } catch (Exception e) {
            android.util.Log.e("SendMail", e.getMessage(), e);
        }
    }

    /**
     * Method to authenticate the user attempting to log in
     * @param user is the user that is being authenticated
     */
    private void authenticate(User user){
        ServerRequest request = new ServerRequest(this);
        request.fetchUserDataInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    /**
     * Method to show an error message if the user attempts to log in and the login details are wrong or
     * the server is down
     */
    private void showErrorMessage(){
        AlertDialog.Builder loginDialog = new AlertDialog.Builder(this);
        loginDialog.setTitle("Incorrect Login Details");
        loginDialog.setMessage("Unable to log in due to incorrect login details or server issue, please check details and try again...");
        loginDialog.setIcon(R.drawable.ic_error_black_48dp);
        loginDialog.setPositiveButton("OK", null);
        loginDialog.show();
    }

    /**
     * Method to log in user and launch the MainActivity with his data
     * @param returnedUser is data of the user that has logged in
     */
    private void logUserIn(User returnedUser){
        storeLocalUserData.storeUserData(returnedUser);
        storeLocalUserData.setLoggedIn(true);
        final String name = returnedUser.username;
        login(name);
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Method that attempts to authenticate the user logging in
     * @param name is the name of user attempting to authenticate
     */
    private void login(final String name) {
        MainActivity.authenticate(new MyAuthenticationProvider.Credentials(MainActivity.getLayerAppId(), name),

                new AuthenticationProvider.Callback() {
                    /**
                     * Message informing user that authentication has been successful
                     * @param provider
                     * @param userId
                     */
                    @Override
                    public void onSuccess(AuthenticationProvider provider, String userId) {
                        //do nothing
                    }

                    /**
                     * Produces an error message when there is an authentication error
                     * @param provider
                     * @param error
                     */
                    @Override
                    public void onError(AuthenticationProvider provider, final String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login.this, error, Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });
    }
}
