package timurapp

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.web.json.JSONObject

class JobController {
    AuthenticationService authenticationService

    def create(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = [error:"Not enough params"]
                if(request.JSON!=null ){
                    JSONObject obj = request.JSON
                    if(obj.containsKey("title")){
                        def title = obj.get("title")
                        def desc = obj.get("description")
                        def reward = obj.get("reward")
                        def address = obj.get("address")
                        def lon = obj.get("lon")
                        def lat = obj.get("lat")

                        Job jb =  new Job(title:title, description: desc, reward: reward,address: address,
                                            validUntil: new Date(),longitude: lon,latitude: lat);
                        user.jobs.add(jb);
                        jb.user = user;
                        user.save(flush:true);
                        toRender = [job:"Created successfully"]
                     }
                }

                render(contentType: "text/json", encoding: "UTF-8") {
                    toRender
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    def createRequest(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = [error:"Not enough params"]
                if(request.JSON!=null ){
                    JSONObject obj = request.JSON
                    if(obj.containsKey("id")){
                        int id = obj.get("id")
                        JobRequest jbrq = new JobRequest();
                        jbrq.candidate = user;
                        jbrq.job = Job.findById(id);
                        try {
                            if(JobRequest.findByJobAndCandidate(jbrq.job,jbrq.candidate)==null)     {
                                jbrq.save(flush:true);
                                toRender = [jobrequest:"Registered"];
                            } else{
                                toRender = [jobrequest:"Duplicates not allowed"]
                            }
                        }
                        catch(Exception e) {
                            toRender = [jobrequest:e.message]
                        }
                    }
                }

                render(contentType: "text/json", encoding: "UTF-8") {
                   toRender
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }
    def approveRequest(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = [error:"Not enough params"]
                if(request.JSON!=null ){
                    JSONObject obj = request.JSON
                    if(obj.containsKey("candidate_id")&&obj.containsKey("job_id")){
                        int candidate_id = obj.get("candidate_id")
                        int job_id = obj.get("job_id")

                        Job job = Job.findById(job_id);
                        User candidate = User.findById(candidate_id);

                        JobRequest jbrq = JobRequest.findByCandidateAndJob(candidate,job);
                        jbrq.status = JobRequest.STATUS_ACCEPTED;
                        jbrq.save(flush:true);

                        JobRequest.findAllByJobAndCandidateNotEqual(job,candidate).collect{
                            JobRequest req ->
                                req.status = JobRequest.STATUS_DISMISSED;
                                req.save(flush:true)
                        }
                        job.emplyee = candidate;
                        job.status = Job.STATUS_APPROVED;
                        job.save(flush:true);

                        toRender = [job:"Approved"];
                    }
                }

                render(contentType: "text/json", encoding: "UTF-8") {
                    toRender
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    def finish(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = [error:"Not enough params"]
                if(request.JSON!=null ){
                    JSONObject obj = request.JSON
                    if(obj.containsKey("job_id")&&obj.containsKey("rating")){
                        int job_id = obj.get("job_id")
                        double rate = obj.get("rating")
                        Job job = Job.findById(job_id);
                        job.status = Job.STATUS_DONE;
                        job.finishRate = rate;
                        job.save(flush:true);
                        toRender = [job:"Done"];
                    }
                }

                render(contentType: "text/json", encoding: "UTF-8") {
                    toRender
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    def cancel(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = [error:"Not enough params"]
                if(request.JSON!=null ){
                    JSONObject obj = request.JSON
                    if(obj.containsKey("job_id")){
                        int job_id = obj.get("job_id")
                        Job job = Job.findById(job_id);
                        job.status = Job.STATUS_CANCELED;
                        job.save(flush:true);

                        JobRequest.findAllByJob(job).collect{
                            JobRequest rq ->
                                rq.status = JobRequest.STATUS_DISMISSED;
                                rq.save(flush:true);
                        };
                        toRender = [job:"Canceled"];
                    }
                }

                render(contentType: "text/json", encoding: "UTF-8") {
                    toRender
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }


    def list(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {
                    /*profile(email: user.email, rating: user.rating,
                            name: user.name, phone: user.phone,
                            skype: user.skype, photo: user.photoUrl)*/

                    //JobRequest jbrq = new JobRequest();
                    //jbrq.user = user;
                    //JSONObject obj = request.JSON

                    //int id = obj.get("id")
                    //jbrq.job = Job.findById(id)

                    //request.JSON
                    //idsss(id:id)
                    Job.findAll(sort:"dateCreated", order: "desc").collect{
                        Job a -> [id:a.id,name: a.title, reward:a.reward, longitude:a.longitude,latitude:a.latitude,
                                details:a.description,
                                status:a.status,
                                contact:a.user.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }
    def listOffers(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {

                    JobRequest.findAllByJobInList(Job.findAllByUser(user)).collect{
                        JobRequest a -> [job_id:a.job.id,candidate_id:a.candidate.id,job_title: a.job.title,status:a.status,contact:a.candidate.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }
    def listAcceptedJobs(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {

                    JobRequest.findAllByCandidateAndStatusInList(user,[JobRequest.STATUS_PENDING,JobRequest.STATUS_DISMISSED]).collect{
                        JobRequest a -> [job_id:a.job.id,candidate_id:a.candidate.id,job_title: a.job.title,status:a.status,contact:a.candidate.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }
    def listApprovedJobs(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {

                    JobRequest.findAllByCandidateAndStatus(user,JobRequest.STATUS_ACCEPTED).collect{
                        JobRequest a -> [job_id:a.job.id,candidate_id:a.candidate.id,job_title: a.job.title,status:a.status,contact:a.candidate.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }
    def listFinishedJobsByUser(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {

                    Job.findAllByEmplyeeAndStatus(user,Job.STATUS_DONE).collect{
                        Job a -> [id:a.id,name: a.title, reward:a.reward, longitude:a.longitude,latitude:a.latitude,details:a.description,contact:a.user.getContact(),emploee:a.emplyee.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }
    def listFinishedJobsForUser(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {

                    Job.findAllByUserAndStatus(user,Job.STATUS_DONE).collect{
                        Job a -> [id:a.id,name: a.title, reward:a.reward, longitude:a.longitude,latitude:a.latitude,details:a.description,contact:a.user.getContact(),emploee:a.emplyee.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }

    private def withAuthentication(Closure f) {
        if (authenticationService.isAuthenticated()) {
            f.call()
        } else {
            // TODO show error page 'access forbidden'
            redirect(url: "/")
        }
    }
}
