package timurapp

class User {
    String name = null
    String email
    String phone = null
    String skype = null
    String photoUrl = null
    double rating = 3.0

    static hasMany = [jobs:Job, jobOffers:JobRequest]
    static mappedBy = [jobs: "user"]


    static constraints = {
        email blank: false, unique: true
        name nullable: true
        phone nullable: true
        skype nullable: true
        photoUrl nullable: true

    }

    def getContact(){
        return "User: "+email
                name==null?:" Name: "+name
                phone==null?:" Phone: "+phone
                skype==null?:" Skype: "+skype

    }
    static transients = ['contact']


}
