package timurapp

// TODO pagination
// TODO security
// TODO validation
// TODO actual implementation
class UserCardController {
    def list() {
        render(contentType: "text/json") {
            result = "Ok"
            action = "listUsers"
        }
    }

    def addNewUser() {
        // Extract parameters that describe the user

        def nick = params.nick
        def eMail = params.eMail
        def phone = params.phone

        render(contentType: "text/json") {
            result = "Ok"
            action = "addNewUser"
            nickParam = nick
            eMailParam = eMail
            phoneParam = phone
        }
    }

    def details() {
        def id = params.id
        render(contentType: "text/json") {
            result = "Ok"
            action = "userDetails"
            userId = id
        }
    }

    def delete() {
        def id = params.id
        render(contentType: "text/json") {
            result = "Ok"
            action = "deleteUser"
            userId = id
        }
    }

    def offersList() {
        def id = params.id

        // Extract query params

        // "accepted" or "offered" (i. e., not yet accepted) or "all"
        def status = params.status
        // "asc" or "desc"
        def sort = params.sort
        // TODO some geographic data

        render(contentType: "text/json") {
            result = "Ok"
            action = "usersJobs"
            statusParam = status
            sortParam = sort
        }
    }

    def addNewOffer() {
        // id of user that wants to add the offer
        def userId = params.id

        // Extract parameters that describe the offer

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
}
