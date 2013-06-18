class UrlMappings {

	static mappings = {
        "/users"(controller: "userCard", parseRequest: true) {
            action = [GET: "list", POST: "addNewUser"]
        }
        "/users/$id"(controller: "userCard", parseRequest: true) {
            action = [GET: "details", DELETE: "delete"]
        }
        "/users/$id/offers"(controller: "userCard", parseRequest: true) {
            action = [GET: "offersList", POST: "addNewOffer"]
        }
        "/offers"(controller: "jobOffer", parseRequest: true) {
            action = [GET: "list"]
        }
        "/offers/$id"(controller: "jobOffer", parseRequest: true) {
            action = [GET: "details", PUT: "changeStatus"]
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
