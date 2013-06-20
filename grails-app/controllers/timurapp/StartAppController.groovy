package timurapp

class StartAppController {
    AuthenticationService authenticationService


    def index() {
        if (authenticationService.isAuthenticated()) {
            // save user to DB if not yet present
            def userEmail = authenticationService.getMail()
            if (userEmail != null) {
                def user = User.findByEmail(userEmail)
                if (!user) {
                    user = new User(email: userEmail)
                    user.save(flush: true)
                }
            }
            render(view: "/_index")
        } else {
            render(view: "/index")
        }
    }
}
