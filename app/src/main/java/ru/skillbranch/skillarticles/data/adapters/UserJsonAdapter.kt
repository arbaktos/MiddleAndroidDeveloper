package ru.skillbranch.skillarticles.data.adapters

import org.json.JSONObject
import ru.skillbranch.skillarticles.data.local.User
import ru.skillbranch.skillarticles.extensions.asMap

class UserJsonAdapter() : JsonAdapter<User> {
    override fun fromJson(json: String): User? {
        val jsonObj = JSONObject(json)

        val id: String? = jsonObj.getString("id")
        val name: String? = jsonObj.getString("name")
        val avatar: String? = jsonObj.getString("avatar")
        val rating: Int = jsonObj.getString("rating").toInt() ?: 0
        val respect: Int = jsonObj.getString("respect").toInt() ?: 0
        val about: String? = jsonObj.getString("about")
        return if (id != null && name != null) User(id, name, avatar, rating, respect, about)
        else null
    }

    override fun toJson(obj: User?): String {
        return obj?.asMap().toString()
    }
}