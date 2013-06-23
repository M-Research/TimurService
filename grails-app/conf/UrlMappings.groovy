class UrlMappings {

	static mappings = {
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
