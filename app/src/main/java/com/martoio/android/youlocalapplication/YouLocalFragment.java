package com.martoio.android.youlocalapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * Created by Martin on 7/8/2016 for YouLocalApplication.
 */
public class YouLocalFragment extends Fragment {

    //TAG variable to be used for Log statements
    private static final String TAG = "YouLocalFragment";
    private static final String STARTED_KEY = "STARTED";
    private static final String YOULOCAL_DIALOG = "Dialog";
    private boolean mStarted = false;
    private boolean validEmail, validPassword;
    private int mTextIndex = 0;
    private String[] mForgottenText = {"Forgotten Password?", "Back to Login"};
    private int mPassHeight;

    private EditText mEmail;
    private EditText mPassword;
    private TextInputLayout mPassLayout;
    private FrameLayout mLoginContainer;
    private LinearLayout mLoginBody;
    private TextSwitcher mSwitcher;
    private Button mLogin;
    private ProgressDialog mProgress;
    private ImageView mLogo;

    public static YouLocalFragment newInstance(){
        return new YouLocalFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //recall the mStarted value from savedInstanceState;
        if (savedInstanceState != null){
            mStarted = savedInstanceState.getBoolean(STARTED_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_youlocal_login, container, false);
        //Initialize all the fields in the app;
        initFields(v);
        //Create the animation on first run of app;
        introAnimation();
        //attach onFocusChange listeners to remove keyboard when focus is lost from the email/password fields;
        removeKeyboardOnFocusChange();

        //Login
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if fields are valid;
                validatePasswordLogin();
                validateEmailLogin();

                if (validEmail && validPassword && mTextIndex == 0) { //only run the Login if the fields are valid and the Login button is in the "Login" state
                    //API CALL HERE;
                    new Login().execute(mEmail.getText().toString(), mPassword.getText().toString());
                }
            }
        });

        return v;
    }

    /*
    * Method to initialize the fields
    * */
    private void initFields(View v) {
        mLogo = (ImageView) v.findViewById(R.id.youLocalLogo);
        mEmail = (EditText) v.findViewById(R.id.youLocalLogin_email);
        mSwitcher = (TextSwitcher) v.findViewById(R.id.textSwitcher_login_password);
        mPassword = (EditText) v.findViewById(R.id.youLocalLogin_password);
        mPassLayout = (TextInputLayout) v.findViewById(R.id.input_layout_password);
        mLoginContainer = (FrameLayout) v.findViewById(R.id.login_button_container);
        mLoginBody = (LinearLayout) v.findViewById(R.id.login_body);
        mLogin = (Button) v.findViewById(R.id.youLocalLogin_login);

        mPassLayout.measure(0, 0);
        mPassHeight = mPassword.getMeasuredHeight();

        //Create the textSwitcher
        createTextSwitcher();
    }

    /*
    * Convenience method for removing the Input keyboard when focus is lost
    * */
    private void removeKeyboardOnFocusChange() {
        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    /*
    * Setup method for the text switcher
    * */
    private void createTextSwitcher() {
        //Create the text field inside the text switcher
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView forgottenPass = new TextView(getActivity());
                forgottenPass.setAllCaps(true); //set text to be all capitals;
                forgottenPass.setGravity(Gravity.CENTER); //center text in parent;
                forgottenPass.setTextAppearance(getActivity(), R.style.ForgottenPassText);
                return forgottenPass;
            }
        });

        mSwitcher.setText(mForgottenText[mTextIndex]); //set text to be "Forgotten Password?"
        //set animations;
        Animation in = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        Animation out = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_bottom);

        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);
        //attach onClick event to switch the two texts
        mSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle between the two texts;
                mTextIndex++;
                if (mTextIndex == mForgottenText.length) {
                    mTextIndex = 0;
                }
                //set new text
                mSwitcher.setText(mForgottenText[mTextIndex]);
                showHidePassword(mTextIndex); //run the animation for switching the fields;
            }
        });
    }


    /*
    * Method to animate the logo and fields on app startup
    * */
    private void introAnimation() {
        if (!mStarted){
            mLoginBody.setTranslationY(900); //push fields down
            mLogo.setTranslationY(-900); //push logo up
            mLogo.animate()
                    .translationY(0) //move logo to initial position
                    .setDuration(300) //0.3 s long
                    .setListener(new AnimatorListenerAdapter() {
                        //when logo anim ends, run field anims
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mLoginBody.animate()
                                    .translationY(0) //move fields to initial position;
                                    .setDuration(300); //0.3 s long;
                        }
                    });
            mStarted = true; //set started to true to prevent running animation on orientation changes;
        }
    }

    /*
    * Method for animating the password field and login button
    *
    * */
    private void showHidePassword(int showHide){
        switch (showHide){
            case 0: //Show
                //mPassLayout.setVisibility(View.VISIBLE);
                mPassLayout.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        mLoginContainer.animate().translationY(0);
                        mLogin.setText("LOGIN");
                    }
                });
                break;
            case 1: //Hide
                mPassLayout.animate()
                        .translationY(mPassHeight)
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mPassword.setText("");
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                mLoginContainer.animate().translationY(-mPassHeight);
                                mLogin.setText("RESET");
                            }
                        });
                break;
        }
    }

    /*
    * Validate the Password field
    * Checks for: empty password, password length between (4; 256) characters;
    * @params void;
    * @return boolean;
    * */
    private boolean validateEmailLogin(){
        String email = mEmail.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            validEmail = false;
            mEmail.setError("Please enter a valid (non-empty) email");
        } else {
            mEmail.setError(null);
            validEmail = true;
        }

        return validEmail;

    }

    /*
    * Validate the Password field
    * Checks for: empty password, password length between (4; 256) characters;
    * @params void;
    * @return boolean;
    * */
    private boolean validatePasswordLogin(){
        String password = mPassword.getText().toString();

        if (password.isEmpty()) {
            validPassword = false;
            mPassword.setError("Password field can't be blank");
        } else if (password.length() < 4 ){
            validPassword = false;
            mPassword.setError("Must be more than 4 characters. Current: "+String.valueOf(password.length()));
        } else if (password.length() > 256){
            validPassword = false;
            mPassword.setError("Must be less than 256 characters. Current: "+String.valueOf(password.length()));
        } else {
            validPassword = true;
            mPassword.setError(null);
        }

        return validPassword;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //create savedInstanceState var to prevent opening animation from running on view recall;
        super.onSaveInstanceState(outState);
        outState.putBoolean(STARTED_KEY, mStarted);
    }


    /*
    * Async API call
    * */

    private class Login extends AsyncTask<String, Void, Boolean>{

        YouLocalUser user = null;

        @Override
        protected void onPreExecute() {
            //display a simple Authenticating dialog while fetching info;
            mProgress = new ProgressDialog(getActivity());
            mProgress.setMessage("Authenticating...");
            mProgress.show();

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgress.dismiss(); //remove Authenticating dialog;

            //check if user was actually fetched
            if (user == null){
                if (!isOnline()){
                    //display message if not online
                    Toast.makeText(getActivity(), "Not connected to the Internet. Try again later", Toast.LENGTH_LONG).show();
                    return;
                }
                //display message on other error
                Toast.makeText(getActivity(), "Could not authenticate. Please try again.", Toast.LENGTH_LONG).show();
                return;
            }

            Bundle args = new Bundle(); //create bundle for AlertDialog arguments;

            FragmentManager manager = getFragmentManager(); //get the fragment manager
            YouLocalUserDialog dialog = new YouLocalUserDialog(); //create new user dialog
            //set arguments...fix this before submission
            args.putString("about", user.getAbout());
            args.putString("avatar", user.getAvatarURL());
            args.putString("name", user.getFullName());
            dialog.setArguments(args);
            //show the argument
            dialog.show(manager, YOULOCAL_DIALOG);

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (isOnline()){
                //if online, make the API call;
                user = new YouLocalLogin().fetchUser(strings[0], strings[1]);
                return true;
            } else {
                //else do nothing;
                return false;
            }
        }

        //Check if user is connected to the Internet
        private boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
    }
}
