package com.sateeshjh.realmdbguide

import android.app.Application
import com.sateeshjh.realmdbguide.models.Address
import com.sateeshjh.realmdbguide.models.Course
import com.sateeshjh.realmdbguide.models.Student
import com.sateeshjh.realmdbguide.models.Teacher
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApp: Application() {

    companion object {
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    Address::class,
                    Teacher::class,
                    Course::class,
                    Student::class,
                )
            )
        )
    }
}