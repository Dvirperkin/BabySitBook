package com.example.babysitbook

class Parent(
    override val firstName: String,
    override val lastName: String,
    override val email: String,
    override val age: Int,
    override val country: String,
    override val city: String
) : User {

}