package timurapp

class Job {

    static String STATUS_OPEN = "OPENED"
    static String STATUS_APPROVED = "APPROVED"
    static String STATUS_DONE = "DONE"
    static String STATUS_CANCELED = "CANCELED"




    String title
    String description
    double reward
    String address
    Date validUntil

    Date dateCreated
    Date lastUpdated



    String status = STATUS_OPEN
    User emplyee
    double longitude,latitude
    double finishRate = 0
    //int geozoom
    //static hasMany = [voluntures:User]

    static belongsTo = [user: User]


    static constraints = {
         //geozoom nullable:true
         emplyee(nullable: true)
    }
    /* @Override
 int compareTo(User t) {
     if(t.id == null) return -1;
     if(id == null) return -1;
     return id-t.id
 }*/
    static mapping = {
        sort dateCreated: "desc"
    }
}
