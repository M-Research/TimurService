package timurapp

class StartAppController {
    AuthenticationService authenticationService

    def index() {
        if (authenticationService.isAuthenticated()) {
            render(view: "/_index")
        } else {
            render(view: "/index")
        }
    }
}
