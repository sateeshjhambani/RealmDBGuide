package com.sateeshjh.realmdbguide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sateeshjh.realmdbguide.models.Address
import com.sateeshjh.realmdbguide.models.Course
import com.sateeshjh.realmdbguide.models.Student
import com.sateeshjh.realmdbguide.models.Teacher
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val realm = MyApp.realm

    val courses = realm
        .query<Course>()
        .asFlow()
        .map { results ->
            results.list.toList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    init {
        createSampleEntries()
    }

    private fun createSampleEntries() {
        viewModelScope.launch {
            realm.write {
                val address1 = Address().apply {
                    fullName = "John Doe"
                    street = "Avenue Boulevard"
                    houseNumber = 30
                    zip = 11212
                    city = "Chicago"
                }
                val address2 = Address().apply {
                    fullName = "Jane Doe"
                    street = "Parker's Street"
                    houseNumber = 31
                    zip = 15432
                    city = "Texas"
                }

                val course1 = Course().apply {
                    name = "Kotlin Programming for Beginners"
                }
                val course2 = Course().apply {
                    name = "Compose Basics"
                }
                val course3 = Course().apply {
                    name = "Graphs in Mobile Development"
                }

                val teacher1 = Teacher().apply {
                    address = address1
                    courses = realmListOf(course1, course2)
                }

                val teacher2 = Teacher().apply {
                    address = address2
                    courses = realmListOf(course3)
                }

                course1.teacher = teacher1
                course2.teacher = teacher1
                course3.teacher = teacher2

                address1.teacher = teacher1
                address2.teacher = teacher2

                val student1 = Student().apply {
                    name = "John Junior"
                }
                val student2 = Student().apply {
                    name = "Jane Junior"
                }

                course1.enrolledStudents.add(student1)
                course2.enrolledStudents.add(student2)
                course3.enrolledStudents.addAll(listOf(student1, student2))

                copyToRealm(teacher1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(teacher2, updatePolicy = UpdatePolicy.ALL)

                copyToRealm(course1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(course2, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(course3, updatePolicy = UpdatePolicy.ALL)

                copyToRealm(student1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(student2, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }
}