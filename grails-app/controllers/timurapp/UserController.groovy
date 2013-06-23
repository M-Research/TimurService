package timurapp

import org.codehaus.groovy.grails.web.json.JSONObject

class UserController {
    AuthenticationService authenticationService

    def profile() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {
                    [email: user.email, rating: user.getRating(),
                            name: user.name, phone: user.phone,
                            skype: user.skype, photo: user.photoUrl
                    ]
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    //FIXME: test version
    def changeProfile(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                user.name = params.name
                user.phone = params.phone
                user.skype = params.skype
                user.photoUrl = params.photoUrl
                user.save(flush: true)
                render(contentType: "text/json", encoding: "UTF-8", status: 200)
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    def candidateProfile() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {

                def toRender = [error:"Not enough params"]
                if(request.JSON!=null ){
                    JSONObject obj = request.JSON
                    if(obj.containsKey("candidate_id")){
                        int candidate_id = obj.get("candidate_id")

                        User candidate = User.findById(candidate_id);
                        if(candidate!=null){
                            toRender = [email: candidate.email, rating: candidate.getRating(),
                                    name: candidate.name, phone: candidate.phone,
                                    skype: candidate.skype, photo: candidate.photoUrl]
                        }else{
                            toRender = [error:"No such user"]
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

    def ownedJobs()
    {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                //user.refresh();
                render(contentType: "text/json", encoding: "UTF-8") {
                    Job.findAllByUser(user,[sort:"dateCreated",order:"desc"]).collect{
                            Job a -> [id:a.id,name: a.title, reward:a.reward, longitude:a.longitude,
                                     latitude:a.latitude,details:a.description,status:a.status,contact:a.user.getContact()]
                    }
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }
    def openedJobs()
    {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                //user.jobs.add(new Job(title:"Новая работа", description: "Тратата", reward: 3,address: "Подол, у фонтана"));
                //Job jb = new Job(title:"Новая работа", description: "Тратата", validUntil: new Date(), reward: 3,address: "Подол, у фонтана", status: Job.STATUS_OPEN);
                //jb.save();
                //def res = Job.findByTitle("Новая работа");
                render(contentType: "text/json", encoding: "UTF-8") {
                            Job.findAllByStatus(Job.STATUS_OPEN,[sort:"dateCreated",order:"desc"]).collect{
                                Job a -> [id:a.id,name: a.title, reward:a.reward, longitude:a.longitude,
                                          latitude:a.latitude,details:a.description,status:a.status,
                                          contact:a.user.getContact(),
                                          user_rating:a.user.getRating()]
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
