package timurapp

class Job {

    static String STATUS_OPEN = "OPENED"
    static String STATUS_APPROVED = "APPROVED"
    static String STATUS_DONE = "DONE"



    String title
    String description
    double reward
    String address
    Date validUntil
    Date dateCreated
    Date lastUpdated



    String status = STATUS_OPEN

    double longitude,latitude
    int geozoom
    static hasMany = [voluntures:User]

    static belongsTo = User

    static constraints = {
         geozoom nullable:true
    }
}
