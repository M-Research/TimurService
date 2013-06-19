package timurapp

class JobRequest {

    Job job
    User user
    Date dateCreated
    static constraints = {
        job(unique: true)
        user(unique: true)
    }
}
