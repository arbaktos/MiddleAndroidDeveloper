package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException

object UserHolder {

    private val map = mutableMapOf<String, User>()
    private val phoneFormat = Regex("""^[+][\d]{11}""")

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ) : User = User.makeUser(fullName, email = email, password = password)
        .also { user ->
            if(map.containsKey(user.login)) throw IllegalArgumentException("A user with this email already exists")
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
                map.containsKey(user.login) -> throw IllegalArgumentException("A user with this phone already exists")
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

    fun importUsers(list: List<String>): List<User> {
        val userList = mutableListOf<User>()
        list.forEach { line ->

            val userData = line.split(";")
            val _fullName = userData[0]
            val _email = if (userData[1].isNotBlank()) userData[1] else null
            val _phone = if (userData[3].isNotBlank()) userData[3] else null
            val _passwordHash = userData[2].substringAfter(":")

            val userCsv =
                User.makeUser(_fullName, email = _email, phone = _phone, passwordHash = _passwordHash)

            userList.add(userCsv
                    .also { user ->
                        if(userData[2].isNotEmpty()) {
                            user.phone = _phone
                            user.salt = userData[2].substringBefore(":")
                            if(map.containsKey(user.login)) throw IllegalArgumentException("User already exist")
                            else map[user.login] = user
                        }
            })
        }
        return userList
    }
}