class UrlMappings {

	static mappings = {
        "/offers"(controller: "jobOffer", parseRequest: true) {
            action = [GET: "list", POST: "addNewOffer"]
        }
        "/offers/$id"(controller: "jobOffer", parseRequest: true) {
            action = [GET: "details", PUT: "changeStatus"]
        }
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
