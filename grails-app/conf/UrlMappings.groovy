class UrlMappings {

	static mappings = {
        "/api/user"(controller: "jobOffer", parseRequest: true) {
            action = [GET: "profile"]
        }
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
        "/"(controller: "startApp") {
            action = [GET: "index"]
        }
		"/admin"(view:"/admin")
		"500"(view:'/error')
	}
}
