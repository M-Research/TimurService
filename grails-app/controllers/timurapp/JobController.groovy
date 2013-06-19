package timurapp
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.web.json.JSONObject

class JobController {
    AuthenticationService authenticationService
    def createRequest(){
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {
                    /*profile(email: user.email, rating: user.rating,
                            name: user.name, phone: user.phone,
                            skype: user.skype, photo: user.photoUrl)*/

                    JobRequest jbrq = new JobRequest();
                    jbrq.user = user;
                    JSONObject obj = request.JSON

                    int id = obj.get("id")
                    //jbrq.job = Job.findById(id)

                    //request.JSON
                    idsss(id:id)
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
