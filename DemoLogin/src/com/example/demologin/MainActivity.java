package com.example.demologin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.GraphJSONObjectCallback;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
  LoginButton login;
  boolean isFirst=true;
  private static final int RC_SIGN_IN = 0;
  CallbackManager  mCallbackMgr;
  GoogleApiClient mGoogleApiClient;
  private boolean mIntentInProgress;
  private boolean mSignInClicked=false;
  ConnectionResult mConnectionResult;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(MainActivity.this);		
		setContentView(R.layout.activity_main);
		
		
		 // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API,Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
		      
       
		if(isFirst){
			//isFirst=false;
		try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "com.example.demologin", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            //Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }
		}
		else{
			
			setFacebookLogin();
		}
		
		((SignInButton)findViewById(R.id.sign_in_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				signInWithGplus();
			}
		});
	}
	
	public void click(View v)
	{
		if(v.getId()==R.id.btn_fbsign_in)
		((LoginButton)findViewById(R.id.login_button)).performClick();
		else{
			//((SignInButton)findViewById(R.id.sign_in_button)).performClick();
			signInWithGplus();
		}
	}
	private void setFacebookLogin() {
		// TODO Auto-generated method stub
		mCallbackMgr= CallbackManager.Factory.create();
		
		login= (LoginButton) findViewById(R.id.login_button);
		login.setReadPermissions("user_friends");
		
		login.registerCallback(mCallbackMgr, new FacebookCallback<LoginResult>() {
			
			@Override
			public void onSuccess(LoginResult result) {
				// TODO Auto-generated method stub
				AccessToken accessToken= result.getAccessToken();
				Profile profile= Profile.getCurrentProfile();
				String name= profile.getName();
				Toast.makeText(MainActivity.this, "Welcome "+ name, Toast.LENGTH_SHORT).show();
				
				GraphRequest request=GraphRequest.newMeRequest(accessToken, new GraphJSONObjectCallback() {
					
					@Override
					public void onCompleted(JSONObject object, GraphResponse response) {
						// TODO Auto-generated method stub
						
						AlertDialog dialog=new AlertDialog.Builder(MainActivity.this)
						.setMessage(object.toString()).create();
						
						dialog.show();
					}
				});
				
				Bundle parameters = new Bundle();
				parameters.putString("fields", "id,name,link");
				request.setParameters(parameters);
				request.executeAsync();
			}
			
			@Override
			public void onError(FacebookException error) {
				// TODO Auto-generated method stub
			 Log.e("", error.getMessage());	
			}
			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	 protected void onStart() {
	        super.onStart();
	        mGoogleApiClient.connect();
	    }
	 
	    protected void onStop() {
	        super.onStop();
	        if (mGoogleApiClient.isConnected()) {
	            mGoogleApiClient.disconnect();
	        }
	    }
	@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			if(mCallbackMgr!=null)
			mCallbackMgr.onActivityResult(requestCode, resultCode, data);
			
			
			if (requestCode == RC_SIGN_IN) {
		        if (resultCode != RESULT_OK) {
		            mSignInClicked = false;
		        }
		 
		        mIntentInProgress = false;
		 
		        if (!mGoogleApiClient.isConnecting()) {
		            mGoogleApiClient.connect();
		        }
		    }
		}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		/* mSignInClicked = false;
		 Log.e("this", "User is connected!");
		processUserInfoAndUpdateUI();*/
		
		processUserInfoAndUpdateUI();
	
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
		 mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	
		 if (!result.hasResolution()) {
		        GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
		                0).show();
		        return;
		    }
		 
		    if (!mIntentInProgress) {
		        // Store the ConnectionResult for later usage
		        mConnectionResult = result;
		 
		        if (mSignInClicked) {
		            // The user has already clicked 'sign-in' so we attempt to
		            // resolve all
		            // errors until the user is signed in, or they cancel.
		            resolveSignInError();
		        }
		    }
	}
	
	
	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
	    if (!mGoogleApiClient.isConnecting()) {
	        mSignInClicked = true;
	        resolveSignInError();
	    }
	}
	 
	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
	    if (mConnectionResult.hasResolution()) {
	        try {
	            mIntentInProgress = true;
	            mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
	        } catch (SendIntentException e) {
	        	e.printStackTrace();
	            mIntentInProgress = false;
	            mGoogleApiClient.connect();
	        }
	    }
	}
	
	/**
	    * API to update signed in user information
	    */
	   private void processUserInfoAndUpdateUI() {
	      Person signedInUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
	      Log.e("", ""+ signedInUser.getBirthday());
	      Log.e("", ""+ signedInUser.toString());
	      setLoginSuccess();
	   }
	   
	   
	   private void setLoginSuccess()
	   {
		   SharedPreferences preferences=getSharedPreferences(Splash.MYPREF, Context.MODE_PRIVATE);
		   Editor editor=preferences.edit();
		   
		   editor.putBoolean("isLogin", true);
		   editor.commit();
	   }
}
