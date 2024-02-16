# RealmDBGuide

This repository serves as a guide for Mongo DB's Realm database for Android, in an app that stores information about courses at a college, which teachers conduct these courses and which students are enrolled in which courses.

Realm is a lightweight and much faster alternative to many popular ORMs like Room Database. The biggest difference between Realm and something like Room is that Realm is not SQL abstraction, the DB can be accessed directly without SQL injections. Instead of the relational model of Room, it is based on object store.

![Main Board](https://github.com/sateeshjhambani/RealmDBGuide/assets/60574717/22d2faaa-0ed7-479a-9dd4-9227cb68b42f)

## Usage

Realm Instance initialization, which takes the set of our objects (equivalent to entities in Room) 

```kotlin
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
```

The definition of the Teacher realm object, contains the auto-generated primary key identifier, address object (which is an EmbeddedRealmObject), and a realm list of courses they hold.

```kotlin
class Teacher: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var address: Address? = null
    var courses: RealmList<Course> = realmListOf()
}
```

Adding sample data to the realm DB

```kotlin
viewModelScope.launch {
    realm.write {
        val address1 = Address().apply {
            fullName = "John Doe"
            street = "Avenue Boulevard"
            houseNumber = 30
            zip = 11212
            city = "Chicago"
        }

        val course1 = Course().apply {
            name = "Kotlin Programming for Beginners"
        }
        val course2 = Course().apply {
            name = "Compose Basics"
        }

        val teacher1 = Teacher().apply {
            address = address1
            courses = realmListOf(course1, course2)
        }

        course1.teacher = teacher1
        course2.teacher = teacher1

        address1.teacher = teacher1

        val student1 = Student().apply {
            name = "John Junior"
        }
        val student2 = Student().apply {
            name = "Jane Junior"
        }

        course1.enrolledStudents.add(student1)
        course2.enrolledStudents.add(student2)

        copyToRealm(teacher1, updatePolicy = UpdatePolicy.ALL)

        copyToRealm(course1, updatePolicy = UpdatePolicy.ALL)
        copyToRealm(course2, updatePolicy = UpdatePolicy.ALL)

        copyToRealm(student1, updatePolicy = UpdatePolicy.ALL)
        copyToRealm(student2, updatePolicy = UpdatePolicy.ALL)
    }
}
```

A few different use cases of how the course list can be queried and returned as a flow

```kotlin
val courses = realm
    .query<Course>(
        // get courses where one of the enrolled student's name is John Junior
//            "enrolledStudents.name == $0",
//            "John Junior"

        // get courses with 2 or more students
//            "enrolledStudents.@count >= 2"

        // get courses where teacher's name contains John
//            "teacher.address.fullName CONTAINS $0",
//            "John"
    )
    .asFlow()
    .map { results ->
        results.list.toList()
    }
```

## References

[Realm Docs](https://realm.io/realm-kotlin/)

[Integration of Realm Database in Android](https://medium.com/@mr.appbuilder/integration-of-realm-database-in-android-5ad2f83afa7b)
