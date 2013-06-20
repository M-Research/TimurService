package timurapp

class JobRequest implements Serializable {

    static String STATUS_PENDING = "PENDGING"
    static String STATUS_ACCEPTED = "ACCEPTED"
    static String STATUS_DISMISSED = "DISMISSED"


    Job job
    User candidate
    String status = STATUS_PENDING
    Date dateCreated
    String comment
    static constraints = {
        comment(nullable: true)
    }
    static mapping = {
        id composite:['job', 'candidate']
    }
}
