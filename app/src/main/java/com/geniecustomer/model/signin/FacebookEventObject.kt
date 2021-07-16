package com.dash.model.login

class FacebookEventObject(
    firstName: String,
    lastName: String,
    fbId: String,
    email: String,
    countrycode: String,
    phonenumber: String
) {


    val firstName: String = firstName
    val lastName: String = lastName
    val fbId: String = fbId
    val email: String = email
    var countrycode: String = countrycode
    var phonenumber: String = phonenumber



    fun setCountryCode(countryCode: String) {
        this.countrycode = countrycode
    }

    fun setPhone(phonenumber: String) {
        this.phonenumber = phonenumber
    }


}
