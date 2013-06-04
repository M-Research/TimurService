package timurapp

class JobOfferController {
    def list() {
        // Extract query params

        // "accepted" or "offered" (i. e., not yet accepted)
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

    def addNewOffer() {
        // Extract parameters that describe the offer

        // id of user that wants to add the offer
        def userId = params.userId
        // when the work should be started
        def startDate = params.startDate
        // when the work should be finished
        def completeDate = params.completeDate
        // details of work
        def details = params.details
        // what is the reward for work
        def reward = params.reward

        render(contentType: "text/json") {
            result = "Ok"
            action = "addNewOffer"
            userIdParam = userId
            startDateParam = startDate
            completeDateParam = completeDate
            detailsParam = details
            rewardParam = reward
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
