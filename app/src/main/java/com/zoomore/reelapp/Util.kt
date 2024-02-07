package com.zoomore.reelapp

import com.google.firebase.auth.PhoneAuthProvider

var userVerificationId: String? = null
var userToken: PhoneAuthProvider.ForceResendingToken? = null

const val GOOGLE_RC_SIGN_IN = 8223