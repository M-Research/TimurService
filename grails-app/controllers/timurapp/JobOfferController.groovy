package timurapp

// TODO pagination
// TODO security
// TODO validation
// TODO actual implementation
class JobOfferController {
    def list() {
        // Extract query params

        // "accepted" or "offered" (i. e., not yet accepted) or "all"
        def status = params.status
        // "asc" or "desc"
        def sort = params.sort
        // TODO some geographic data

        render(contentType: "text/json") {
            result = "Ok"
            action = "listOffers"
            statusParam = status
            sortParam = sort
        }
    }

    def details() {
        // Extract ID of particular offer (part of path)
        def id = params.id

        render(contentType: "text/json") {
            result = "Ok"
            action = "getOfferDetails"
            offerId = id
        }
    }

    def changeStatus() {
        // Extract ID of particular offer (part of path)
        def id = params.id
        // Extract ID of user who wants to accept/reject offer (query param)
        def userId = params.userId

        render(contentType: "text/json") {
            result = "Ok"
            action = "updateOfferState"
            offerId = id
            userIdParam = userId
        }
    }

    /* TODO don't know how to restrict deletion to only offers created by
       the invoking user (parameters doesn't get passed)
    def delete() {
        // Extract ID of particular offer (part of path)
        def id = params.id
        // Extract ID of user who wants to accept/reject offer (query param)
        def userId = params.userId

        render(contentType: "text/json") {
            result = "Ok"
            action = "deleteOffer"
            offerId = id
            userIdParam = userId
        }
    }
    */
}
