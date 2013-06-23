if (typeof jQuery !== 'undefined') {
    /**
     * Calls the given function on the received user's profile (object with
     * fields email, rating, name, phone, skype, photo).
     */
    function getProfile(f) {
        apiCall("GET", "api/user", function (result) {f(result.profile);});
    }

    /**
     * Generic call to the API. Executes given function on the result of given
     * method that was invoked given HTTP method.
     */
    function apiCall(httpMethod, apiMethod, f) {
        $.ajax({
            type: httpMethod,
            url: apiMethod,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function callFunction(result) {
                f(result);
            },
            error: function (xhr, status, error) {
                // TODO handle error
                alert(error.toString());
            }
        });
    }

    /**
     * Return the default location for the case when browser supports
     * the Geolocation API or GPS is not available
     */
    function getDefaultCoordinates() {

        var coords = {
            latitude: 50.454007,
            longitude:30.526199
        }

        var position = {
            coords : coords
        }

        return position;
    }

}
