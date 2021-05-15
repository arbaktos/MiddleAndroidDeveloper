package ru.skillbranch.skillarticles.data.adapters

import org.json.JSONObject
import ru.skillbranch.skillarticles.data.local.User
import ru.skillbranch.skillarticles.extensions.asMap

class UserJsonAdapter() : JsonAdapter<User> {
    override fun fromJson(json: String): User? {
        //TODO implement me
        return User(json, json, json)
    }

    override fun toJson(obj: User?): String {
        //TODO implement me
        return ""
    }
}