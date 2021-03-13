package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException

class UserHolder {

    private val map = mutableMapOf<String, User>()
    val phoneFormat = Regex("""^[+][\d]{11}""")

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ) : User = User.makeUser(fullName, email = email, password = password)
        .also { user ->
            if(map.containsKey(user.login)) throw IllegalArgumentException("User already exist")
            else map[user.login] = user
        }

    fun loginUser(login: String, password: String): String? {
        val phoneLogin = cleanPhone(login)
        return if (phoneLogin.isNotEmpty()) {
            map[phoneLogin]
        } else {
            map[login.trim()]
        }?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }
    }


    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        return User.makeUser(fullName, phone = rawPhone).also { user ->
            when {
                map.containsKey(user.login) -> throw IllegalArgumentException("User already exist")
                !user.phone!!.matches(phoneFormat) -> throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
                else -> map[user.login] = user
            }
        }
    }

    fun requestAccessCode(login: String) : Unit {
        val user = map[cleanPhone(login)]
        user?.apply {
            val code = generateAccessCode()
            this.passwordHash = encrypt(code)
            this.accessCode = code
            sendAccessCodeToUser(login, code)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    private fun cleanPhone(phone: String) : String {
        return phone.replace("""[^+\d]""".toRegex(), "")
    }

    fun importUsersCsv(list: List<String>): List<User> {
        val userList = mutableListOf<User>()
        list.forEach { line ->
            val userData = line.split(";")
            userList.add(User
                    .makeUser(fullName = userData[0], email = userData[1])
                    .also { user ->
                        if(userData[2].isNotEmpty()) {
                            user.salt = userData[2].substringBefore(":")
                            user.passwordHash = userData[2].substringAfter(":")
                            if(map.containsKey(user.login)) throw IllegalArgumentException("User already exist")
                            else map[user.login] = user
                        }
            })
        }

        return userList
    }
}