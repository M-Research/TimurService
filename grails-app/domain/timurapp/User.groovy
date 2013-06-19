package timurapp

class User {
    String name = null
    String email
    String phone = null
    String skype = null
    String photoUrl = null
    double rating = 3.0

    static constraints = {
        email blank: false, unique: true
        name nullable: true
        phone nullable: true
        skype nullable: true
        photoUrl nullable: true
    }
}
