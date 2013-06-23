if (typeof jQuery !== 'undefined') {
    /**
     * Calls the given function on the received user's profile (object with
     * fields email, rating, name, phone, skype, photo).
     */
    function getProfile(f) {
        apiCall("GET", "api/user", function (result) {
            f(result.profile);
        });
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

    var STATUS_OPEN = "OPENED";
    var STATUS_APPROVED = "APPROVED";
    var STATUS_DONE = "DONE";
    var STATUS_CANCELED = "CANCELED";

    function getIconForStatus(status) {
        var baseURL = 'http://cdn.edit.g.imapbuilder.net/images/markers/'
        var suff = ''
        if (status == STATUS_OPEN) {
            suff = 5
        } else if (status == STATUS_APPROVED){
            suff = 3
        } else if (status == STATUS_DONE){
            suff = 15
        } else if (status == STATUS_CANCELED){
            suff = 46
        }

        return baseURL + suff.toString()
    }
    //http://cdn.edit.g.imapbuilder.net/images/markers/54
    //3-gr, 5-blue, 15-red, 46-sign
    var locationMarker = null;

    function initMapPage() {
        var job = null;

        if (geo_position_js.init()) {
            geo_position_js.getCurrentPosition(positionRetrievalSuccess, positionRetrievalError);
        } else if (navigator.geolocation) {
            if (!locationMarker) {
                console.log("Initial Position Found");
                navigator.geolocation.getCurrentPosition(
                    positionRetrievalSuccess(position),
                    positionRetrievalError(error),
                    {
                        timeout: (1000),
                        maximumAge: (1000 * 60 * 15),
                        enableHighAccuracy: true
                    }
                );
            }
        } else {
            positionRetrievalSuccess(getDefaultCoordinates())
            alert("Location is not accurate");
        }
    }

    function getInfoWindowContent(job){
        var html = "Details: " + job.name + " </br> Reward: " + job.reward + "</br>" +
            "<a href=\"#details\" data-rel=\"popup\" data-transition=\"pop\" data-position-to=\"window\" data-inline=\"true\">Show details</a>";
        $('#detTitle').text("Title: " + job.name)
        $('#det_desc').text("Description: " + job.desc)
        $('#det_address').text("Address: " + job.address)
        $('#det_rew').text("Reward: " + job.reward)
        return html;
    }

    function populateMarkersWithJobs(){
        $.getJSON('job/list', function (data) {
            $.each(data, function (index, element) {
                console.log(element)
                job = element
                var tmpPos = new google.maps.LatLng(job.latitude, job.longitude);
                $('#map_canvas').gmap('addMarker', { 'position': tmpPos, 'icon':  getIconForStatus(job.status)}
                ).click(function () {
                        $('#map_canvas').gmap('openInfoWindow', {'content': getInfoWindowContent(element)}, this);
                    });
            });
        });
    }

    function positionRetrievalSuccess(position) {
        if (locationMarker) {
            return;
        }
        console.log("Initial Position Found");
        var yourStartLatLng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);

        $('#map_canvas').gmap({'center': yourStartLatLng, 'zoom': 16, 'mapTypeControl': false});
        $('#map_canvas').gmap('addMarker', {
            'position': yourStartLatLng,
            'icon': 'http://cdn.edit.g.imapbuilder.net/images/markers/54'
        });

        populateMarkersWithJobs();
    }

    function positionRetrievalError(error) {
        console.log("Something went wrong: ", error);
        positionRetrievalSuccess(getDefaultCoordinates())
        alert("Location is not accurate");
    }

    function initProfileForm() {
        $.getJSON('user/profile', function (res) {
            console.log(res)
            if (res.name) {
                $('#profile_name').val(res.name);
            }

            if (res.skype) {
                $('#profile_skype').val(res.skype);
            }

            if (res.phone) {
                $('#profile_phone').val(res.phone);
            }

            $('#profile_email').val(res.email);
        });
    }

    /**
     * Return the default location for the case when browser supports
     * the Geolocation API or GPS is not available
     */
    function getDefaultCoordinates() {

        var coords = {
            latitude: 50.454007,
            longitude: 30.526199
        }

        var position = {
            coords: coords
        }

        return position;
    }


}
