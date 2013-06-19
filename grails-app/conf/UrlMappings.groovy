class UrlMappings {

	static mappings = {
        "/api/user"(controller: "user", parseRequest: true) {
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
