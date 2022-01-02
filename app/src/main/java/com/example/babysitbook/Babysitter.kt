package com.example.babysitbook

class Babysitter(t_first : String,
                 t_last : String,
                 t_email : String,
                 t_age : Int,
                 t_country : String,
                 t_city : String,
                 t_exp: Int) : User {

    override var firstName = t_first
    override var lastName = t_last
    override val email = t_email
    override var age = t_age
    override var country = t_country
    override var city = t_city
    var exp = t_exp
}