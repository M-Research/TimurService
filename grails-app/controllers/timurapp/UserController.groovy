package timurapp

class UserController {
    AuthenticationService authenticationService

    def profile() {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                render(contentType: "text/json", encoding: "UTF-8") {
                    profile(email: user.email, rating: user.rating,
                            name: user.name, phone: user.phone,
                            skype: user.skype, photo: user.photoUrl)
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }

    def fillTestUser(User user){
        Job jb = new Job(title:"Новая работа", description: "Тратата", reward: 3,address: "address1")
        jb.latitude = 50.445781
        jb.longitude = 30.534439
        user.jobs.add(jb)

        Job jb2 = new Job(title:"Новая работа2", description: "Тратата2", reward: 6 ,address: "address2")
        jb2.latitude = 50.44846
        jb2.longitude = 30.513539
        user.jobs.add(jb2)

        jb2.user = user
        jb.user = user
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
                user.save()
                render(contentType: "text/json", encoding: "UTF-8") { [ok] }
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
                fillTestUser(user)
                user.save(flush:true)

                render(contentType: "text/json", encoding: "UTF-8") {
                    profile(jobs:
                        user.jobs.collect{
                            Job a -> [name: a.title, reward:a.reward, latitude:a.latitude, longitude:a.longitude,
                            desc: a.description, address: a.address]
                        }
                    )
                }
            } else {
                render(status: 404, contentType: "text/json", encoding: "UTF-8")
            }
        }
    }
    def openJobs()
    {
        withAuthentication {
            def userEmail = authenticationService.getMail()
            User user = User.findByEmail(userEmail)
            if (user) {
                fillTestUser(user);
                //user.jobs.add(new Job(title:"Новая работа", description: "Тратата", reward: 3,address: "Подол, у фонтана"));
                //Job jb = new Job(title:"Новая работа", description: "Тратата", validUntil: new Date(), reward: 3,address: "Подол, у фонтана", status: Job.STATUS_OPEN);
                //jb.save();
                //def res = Job.findByTitle("Новая работа");
                render(contentType: "text/json", encoding: "UTF-8") {
                    availableJobs(jobs:
                            Job.findAllByStatus(Job.STATUS_OPEN).collect{
                                Job a -> [id:a.id,name: a.title, reward:a.reward, latitude:a.latitude, longitude:a.longitude,
                                        desc: a.description, address: a.address]
                            },
                    )
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
