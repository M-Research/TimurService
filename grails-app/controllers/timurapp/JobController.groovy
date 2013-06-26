package timurapp

import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.web.json.JSONObject

class JobController {
    AuthenticationService authenticationService

    def create() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = []
                def status = 422
                if (request.JSON != null) {
                    JSONObject obj = request.JSON
                    def title = obj?.title
                    def desc = obj?.description
                    def reward = obj?.reward
                    def address = obj?.address
                    def date = obj?.date
                    def time = obj?.time
                    def lon = obj?.lon
                    def lat = obj?.lat

                    if (title && desc && reward && address && lon &&
                            lat && date && time) {
                        // TODO timezone
                        def validUntil = Date.parse(
                                "MM/dd/yyyy K:m a", "${date} ${time}")
                        Job jb = new Job(title: title, description: desc,
                                reward: reward,
                                address: address,
                                validUntil: validUntil,
                                longitude: lon, latitude: lat);
                        user.jobs.add(jb);
                        jb.user = user;
                        jb.save();
                        user.save(flush: true);
                        status = 200
                    }
                }

                render(contentType: "text/json", encoding: "UTF-8",
                        status: status) {
                    toRender
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    def createRequest() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = [error: "Not enough params"]
                if (request.JSON != null) {
                    JSONObject obj = request.JSON
                    if (obj.containsKey("id")) {
                        def id = obj.get("id")
                        JobRequest jbrq = new JobRequest();
                        jbrq.candidate = user;
                        jbrq.job = Job.findById(id);
                        jbrq.dateCreated = new Date();
                        try {
                            if (JobRequest.findByJobAndCandidate(jbrq.job, jbrq.candidate) == null) {
                                jbrq.save(flush: true);
                                toRender = [jobrequest: "Registered"];
                            } else {
                                toRender = [jobrequest: "Duplicates not allowed"]
                            }
                        }
                        catch (Exception e) {
                            toRender = [jobrequest: e.message]
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

    def approveRequest() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = [error: "Not enough params"]
                if (request.JSON != null) {
                    JSONObject obj = request.JSON
                    if (obj.containsKey("candidate_id") && obj.containsKey("job_id")) {
                        def candidate_id = obj.get("candidate_id")
                        def job_id = obj.get("job_id")

                        Job job = Job.findById(job_id);
                        User candidate = User.findById(candidate_id);

                        JobRequest jbrq = JobRequest.findByCandidateAndJob(candidate, job);
                        jbrq.status = JobRequest.STATUS_ACCEPTED;
                        jbrq.save(flush: true);

                        JobRequest.findAllByJobAndCandidateNotEqual(job, candidate).collect {
                            JobRequest req ->
                                req.status = JobRequest.STATUS_DISMISSED;
                                req.save(flush: true)
                        }
                        job.emplyee = candidate;
                        job.status = Job.STATUS_APPROVED;
                        job.save(flush: true);

                        toRender = [job: "Approved"];
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

    def finish() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = [error: "Not enough params"]
                if (request.JSON != null) {
                    JSONObject obj = request.JSON
                    if (obj.containsKey("job_id") && obj.containsKey("rating")) {
                        def job_id = obj.get("job_id")
                        def rate = obj.get("rating")
                        Job job = Job.findById(job_id);
                        job.status = Job.STATUS_DONE;
                        job.finishRate = rate;
                        job.save(flush: true);
                        // TODO delete or move to next state?
                        JobRequest.findAllByJob(job).collect {
                            JobRequest rq ->
                                rq.delete(flush: true)
                        };
                        toRender = [job: "Done"];
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

    def cancel() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = [error: "Not enough params"]
                if (request.JSON != null) {
                    JSONObject obj = request.JSON
                    if (obj.containsKey("job_id")) {
                        def job_id = obj.get("job_id")
                        Job job = Job.findById(job_id);
                        job.status = Job.STATUS_CANCELED;
                        job.save(flush: true);

                        JobRequest.findAllByJob(job).collect {
                            JobRequest rq ->
                                rq.status = JobRequest.STATUS_DISMISSED;
                                rq.save(flush: true);
                        };
                        toRender = [job: "Canceled"];
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

    def cancelOwnRequest() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                def toRender = []
                def status = 422
                if (request.JSON) {
                    JSONObject obj = request.JSON
                    Job job = Job.findById(obj?.job_id);
                    if (job) {
                        JobRequest req = JobRequest.findByCandidateAndJob(user, job)
                        if (req) {
                            req.delete(flush: true)
                            toRender = [job: "Request deleted"]
                            status = 200
                        }
                    }
                }

                render(contentType: "text/json", encoding: "UTF-8",
                        status: status) {
                    toRender
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    def list() {
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
                    if (params.id) {
                        job = Job.findById(params.id)
                        renderJob(job)
                    } else {
                        Job.findAll(sort: "dateCreated", order: "desc").collect { renderJob(it) }
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    def renderJobWithOffers(Job i) {
        def stat = i.status;
        def ret = [];
        def message = "";
        switch (stat) {
            case Job.STATUS_APPROVED:
                def candidate = JobRequest.findByJobAndStatus(i, JobRequest.STATUS_ACCEPTED).candidate;
                def info = candidate.name != null ? candidate.name : candidate.email;
                ret = [job_id: i.id, title: i.title, status: i.status, employee_info: info, employee_id: candidate.id]
                break;
            case Job.STATUS_OPEN:
                def number = JobRequest.findAllByJob(i).size()
                ret = [job_id: i.id, title: i.title, status: i.status, numOfWorkers: number]
                break;
            case Job.STATUS_DONE:
            case Job.STATUS_CANCELED:
                ret = [job_id: i.id, title: i.title, status: i.status]
                break;
            default:
                break;
        }
        ret
    }

    def listJobsForUserLight() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {
                    Job.findAllByUser(user).collect {
                        renderJobWithOffers(it)
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    def listApprovedRequestsForJob() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                if (request.JSON) {
                    JSONObject obj = request.JSON
                    if (obj.id) {
                        def job = Job.findById(obj.id)
                        if (job) {
                            render(contentType: "text/json", encoding: "UTF-8") {
                                JobRequest.findAllByJobAndStatus(job, JobRequest.STATUS_ACCEPTED).collect {
                                    JobRequest a -> [candidate_id: a.candidate.id, info: (a.candidate.name != null ? a.candidate.name : a.candidate.email), rating: a.candidate.rating]
                                }
                            }
                        }
                    } else {
                        listOffers()
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    def listRequestsForJob() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                if (request.JSON) {
                    JSONObject obj = request.JSON
                    if (obj.id) {
                        def job = Job.findById(obj.id)
                        if (job) {
                            render(contentType: "text/json", encoding: "UTF-8") {
                                JobRequest.findAllByJob(job).collect {
                                    JobRequest a -> [candidate_id: a.candidate.id, info: (a.candidate.name != null ? a.candidate.name : a.candidate.email), rating: a.candidate.rating]
                                }
                            }
                        }
                    } else {
                        listOffers()
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }

    def listOffers() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {

                    JobRequest.findAllByJobInList(Job.findAllByUser(user)).collect {
                        JobRequest a -> [job_id: a.job.id, candidate_id: a.candidate.id, job_title: a.job.title, status: a.status, contact: a.candidate.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }

    def listAcceptedJobs() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {
                    JobRequest.findAllByCandidateAndStatusInList(
                            user, [JobRequest.STATUS_PENDING, JobRequest.STATUS_DISMISSED]).collect {
                        JobRequest a ->
                            [job_id: a.job.id, candidate_id: a.candidate.id,
                                    job_title: a.job.title, status: a.status,
                                    contact: a.candidate.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }

    def listApprovedJobs() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {

                    JobRequest.findAllByCandidateAndStatus(user, JobRequest.STATUS_ACCEPTED).collect {
                        JobRequest a -> [job_id: a.job.id, candidate_id: a.candidate.id, job_title: a.job.title, status: a.status, contact: a.candidate.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }

    def listFinishedJobsByUser() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {

                    Job.findAllByEmplyeeAndStatus(user, Job.STATUS_DONE).collect {
                        Job a -> [id: a.id, name: a.title, reward: a.reward, longitude: a.longitude, latitude: a.latitude, details: a.description, contact: a.user.getContact(), emploee: a.emplyee.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }

    def listFinishedJobsForUser() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {

                    Job.findAllByUserAndStatus(user, Job.STATUS_DONE).collect {
                        Job a -> [id: a.id, name: a.title, reward: a.reward, longitude: a.longitude, latitude: a.latitude, details: a.description, contact: a.user.getContact(), emploee: a.emplyee.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }

    }

    private def renderJob(Job a) {
        [id: a.id, title: a.title, reward: a.reward,
                longitude: a.longitude, latitude: a.latitude,
                description: a.description, address: a.address,
                status: a.status, validUntil: a.validUntil,
                contact: a.user.getContact()]
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
