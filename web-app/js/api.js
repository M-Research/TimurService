if (typeof jQuery !== 'undefined') {

    $.ajaxSetup({
        contentType: "application/json; charset=utf-8"
    });

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

    /**
     * gets the icon for a job
     * @param status of the job
     * @returns {url pointing to the icon}
     */
    function getIconForStatus(status) {
        var baseURL = 'http://cdn.edit.g.imapbuilder.net/images/markers/'
        var suff = ''
        if (status == STATUS_OPEN) {
            suff = 5
        } else if (status == STATUS_APPROVED) {
            suff = 3
        } else if (status == STATUS_DONE) {
            suff = 15
        } else if (status == STATUS_CANCELED) {
            suff = 46
        }

        return baseURL + suff.toString()
    }

    /**
     * location of the user
     */
    var locationMarker = null;

    /**
     * initializes the map
     */
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

    /**
     * buids the content of the marker's baloon
     * @param job
     * @returns {string}
     */
    function getInfoWindowContent(job) {
        var html = "Details: " + job.name + " </br> Reward: " + job.reward + "</br>" +
            "<a href=\"#details\" data-rel=\"popup\" data-transition=\"pop\" data-position-to=\"window\" data-inline=\"true\">Show details</a>";
        $('#detTitle').text("Title: " + job.name)
        $('#det_desc').text("Description: " + job.details)
        $('#det_address').text("Address: " + job.address)
        $('#det_rew').text("Reward: " + job.reward)
        $('#det_contacts').text("Contacts: " + job.contact)
        return html;
    }

    /**
     * puts markers with all jobs on the map
     */
    function populateMarkersWithJobs() {
        $.getJSON('job/list', function (data) {
            $.each(data, function (index, element) {
                console.log(element)
                job = element
                var tmpPos = new google.maps.LatLng(job.latitude, job.longitude);
                $('#map_canvas').gmap('addMarker', { 'position': tmpPos, 'icon': getIconForStatus(job.status)}
                ).click(function () {
                        $('#map_canvas').gmap('openInfoWindow', {'content': getInfoWindowContent(element)}, this);
                    });
            });
        });
    }

    /**
     * callback when position is retrieved either from GeoLocation API
     * or through the GPS (available on the supported devices)
     * @param position
     */
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

    /**
     * error handling, initialize the map with default coordinates
     * @param error
     */
    function positionRetrievalError(error) {
        console.log("Something went wrong: ", error);
        positionRetrievalSuccess(getDefaultCoordinates())
        alert("Location is not accurate");
    }

    /**
     * gets the content of Profile for a current user
     */
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
     * submits the Add Job form
     */

    /*
     def title = obj.get("title")
     def desc = obj.get("description")
     def reward = obj.get("reward")
     def address = obj.get("address")
     def lon = obj.get("lon")
     def lat = obj.get("lat")
     def validUntil;
     if (obj.containsKey("validUntil")) {
     validUntil = new Date(obj.get("validUntil"));
     } else {
     validUntil = new Date()
     }

     */
    function addWork() {
        var stringDate = $("#task_due_date").val() + " " + $("#task_due_time").val();
        var date = Date.parse(stringDate);
        var name = $("#task_name").val();
        var description = $("#task_details").val();
        var reward = $("#task_reward").val();
        var address = $("#task_address").val();

        console.log(address);

        codeAddress(address, function (position) {
            $.post("job/create",
                JSON.stringify( {title: name, description: description, reward: reward,
                    address: address, lon: position.lng(), lat: position.lat(),
                    validUntil: date }),  { contentType: 'application/json' }).
                done(function (data) {
                    console.log(data);
                    alert(data)
                });
        });
    }

    /**
     * submits the Profile form
     */
    function submitProfileForm() {
        var name = $("#profile_name").val()
        var email = $("#profile_email").val()
        var phone = $("#profile_phone").val()
        var skype = $("#profile_skype").val()

        $.post("user/changeProfile",
            {name: name, email: email, phone: phone, skype: skype })
            .done(function (data) {
                alert("Profile Updated");
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

    var geocoder = new google.maps.Geocoder();

    function codeAddress(address, callback) {
        geocoder.geocode({ 'address': address}, function (results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                callback(results[0].geometry.location)
            } else {
                alert('Geocode was not successful for the following reason: ' + status);
            }
        });
    }
}
