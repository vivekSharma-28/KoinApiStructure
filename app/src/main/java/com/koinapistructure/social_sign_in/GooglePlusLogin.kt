package com.koinapistructure.social_sign_in

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.Auth

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.plus.Plus


class GoogleLogin(
    private val context: Context,
    private val fragment: Fragment?,
    private val listener: OnClientConnectedListener
) : GoogleApiClient.OnConnectionFailedListener {

    val TAG = "Google Plus"
    lateinit var mGoogleApiClient: GoogleApiClient

    init {
        inIt()
    }

    private fun inIt() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().requestProfile()
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(context)
            .addApi(Plus.API)
            /*.enableAutoManage(context *//* FragmentActivity *//*, this *//* OnConnectionFailedListener *//*)*/
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    fun onStart() {
        mGoogleApiClient.connect()
        val opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
        if (opr.isDone) {
            val result = opr.get()
            handleSignInResult(result)
        } else {
            opr.setResultCallback { googleSignInResult -> handleSignInResult(googleSignInResult) }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == RC_SIGN_IN && mGoogleApiClient.isConnected && resultCode == RESULT_OK) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result!!)
        } else {
//             signIn();
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount
            var gender = "-1"
            try {
//                val person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient)
                //val p_gender = person.gender

                //Log.e("gender", "" + person.gender)

                /*if (p_gender == 0)
                //male
                {
                    gender = "1"//male
                } else if (p_gender == 1) {
                    gender = "0"//female
                } else if (p_gender == 2) {
                    gender = "2"//other
                }*/

            } catch (ne: NullPointerException) {
                ne.printStackTrace()
            }

            var imageUrl = ""
            var email: String? = ""
            if (acct!!.photoUrl != null) {
                imageUrl = acct.photoUrl!!.toString()
                Log.e("fp url", "" + imageUrl)
            }
            if (acct.email != null) {
                email = acct.email
            }

            if (acct.displayName != null) {


                val fullname = acct.displayName
                if (fullname!!.contains(" ")) {
                    val n = fullname.split((" ").toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    //                    userName = n[0];
                    //                    lastName = n[1];
                } else {
                    //.userName = fullname;
                }
            }
            //uniqueId = acct.getId();
            //            UserBean.getObect().profilePicUrl = imageUrl;
            //            UserBean.getObect().picUrl = imageUrl;
            //emailId = email;
            //accountType = "2";
            listener.onGoogleProfileFetchComplete(
                acct.id,
                acct.displayName,
                acct.email,
                imageUrl,
                gender,
                acct.givenName.toString(),
                acct.familyName.toString()
            )
        } else {
            listener.onClientFailed(result.status.statusMessage)
            signOut()
        }
    }

    fun signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            /*if(status.isSuccess()){
                                Log.e("Logout","success");
                            }else{
                                Log.e("Logout","Failed");
                            }*/
        }
    }

    private fun revokeAccess() {
        if (mGoogleApiClient.isConnected) {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback {
                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)

                if (fragment == null) {
                    (context as Activity).startActivityForResult(signInIntent, RC_SIGN_IN)
                } else {
                    fragment.startActivityForResult(signInIntent, RC_SIGN_IN)
                }
            }
        } else {
            mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)
        }
    }

    fun signIn() {
        revokeAccess()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:$connectionResult")

        (context as OnClientConnectedListener).onClientFailed(connectionResult.errorMessage)
    }

    interface OnClientConnectedListener {
        fun onGoogleProfileFetchComplete(
            id: String?,
            name: String?,
            email: String?,
            picURL: String,
            gender: String,
            firstname : String,
            lastname : String
        )

        fun onClientFailed(msg: String?)
    }

    companion object {
        val RESULT_OK = -1
        val RC_SIGN_IN = 9001
    }
}