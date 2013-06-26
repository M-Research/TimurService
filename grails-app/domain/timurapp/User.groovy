package timurapp

class User {
    String name = null
    String email
    String phone = null
    String skype = null
    String photoUrl = null
    //double rating = 3.0

    //List<Job> jobs
    //SortedSet joboffers
    static hasMany = [jobs: Job, jobOffers: JobRequest]
    static mappedBy = [jobs: "user"]



    static constraints = {
        email blank: false, unique: true
        name nullable: true
        phone nullable: true
        skype nullable: true
        photoUrl nullable: true

    }

    def getContact() {
        def ret = ""
        if (email) {
            ret += "User: " + email
        }
        if (name) {
            ret += " Name: " + name
        }
        if (phone) {
            ret += " Phone: " + phone
        }
        if (skype) {
            ret += " Skype: " + skype
        }
        return ret
    }

    def getRating() {
        double sum;
        int count;
        Job.findAllByStatus(Job.STATUS_DONE).collect {
            Job jb ->
                sum += jb.finishRate;
                count++;
        }
        if (count != 0) {
            return sum / (double) count;
        }
        return 0;
    }

    static transients = ['contact']


}

