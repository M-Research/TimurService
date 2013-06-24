if (typeof jQuery !== 'undefined') {
    /**
     * ------------------------------ API ------------------------------
     */

    /**
     * Calls the given function on the received user's profile (object with
     * fields email, rating, name, phone, skype, photo).
     */
    function getProfile(f) {
        apiCall("GET", "user/profile", function (result) {
            f(result);
        });
    }

    /**
     * Creates a new job offer described in given offerData and calls the
     * given callback on received result. Result is either empty or failure
     * status code:
     *     401 - if authorization is failed
     *     422 - if arguments are incorrect or absent
     */
    function createJobOffer(offerData, callback) {
        apiCall("POST", "job/create", function (result) {
            callback(result);
        }, offerData);
    }

    /**
     * Generic call to the API. Executes given function on the result of given
     * method that was invoked with given HTTP method OR on the status code of
     * failed request.
     */
    function apiCall(httpMethod, apiMethod, f, data) {
        $.ajax({
            type: httpMethod,
            url: apiMethod,
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function callFunction(result) {
                f(result);
            },
            error: function (xhr) {
                f(xhr.status);
            }
        });
    }

    /**
     * ------------------------------ Map ------------------------------
     */

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
        var baseURL = 'http://cdn.edit.g.imapbuilder.net/images/markers/';
        var suff = '';
        if (status == STATUS_OPEN) {
            suff = 5;
        } else if (status == STATUS_APPROVED){
            suff = 3;
        } else if (status == STATUS_DONE){
            suff = 15;
        } else if (status == STATUS_CANCELED){
            suff = 46;
        }

        return baseURL + suff.toString();
    }

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
            positionRetrievalSuccess(getDefaultCoordinates());
            alert("Location is not accurate");
        }
    }

    /**
     * buids the content of the marker's baloon
     * @param job
     * @returns {string}
     */
    function getInfoWindowContent(job) {
        var html = "Title: " + job.title + " </br> Reward: " + job.reward + "</br>" +
            "<a href=\"#details\" data-rel=\"popup\" data-transition=\"pop\" data-position-to=\"window\" data-inline=\"true\">Show details</a>";
        $('#detTitle').text("Title: " + job.title);
        $('#det_desc').text("Description: " + job.details);
        $('#det_address').text("Address: " + job.address);
        $('#det_rew').text("Reward: " + job.reward);
        // TODO render date
        $('#det_valid_until').text("Valid until: " + job.validUntil);
        $('#det_contacts').text("Contacts: " + job.contact)
        return html;
    }

    /**
    * puts markers with all jobs on the map
    */
    function populateMarkersWithJobs(){
        $.getJSON('job/list', function (data) {
            $.each(data, function (index, element) {
                console.log(element);
                job = element;
                var tmpPos = new google.maps.LatLng(job.latitude, job.longitude);
                $('#map_canvas').gmap('addMarker', { 'position': tmpPos, 'icon':  getIconForStatus(job.status)}
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
        positionRetrievalSuccess(getDefaultCoordinates());
        alert("Location is not accurate");
    }

    /**
     * Return the default location for the case when browser supports
     * the Geolocation API or GPS is not available
     */
    function getDefaultCoordinates() {

        var coords = {
            latitude: 50.454007,
            longitude: 30.526199
        };

        var position = {
            coords: coords
        };

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

    /**
     * ------------------------ New job creation -----------------------
     */
    // TODO remove global state
    var autocomplete;

    function initPlacesAutocomplete() {
        var input = document.getElementById('new_task_location');
        //var input = $('#new_task_location');
        var options = {};
        autocomplete = new google.maps.places.Autocomplete(input, options);
    }

    function addWork() {
        var request = {};
        request['title'] = $('#new_task_title').val();
        request['description'] = $('#new_task_description').val();
        request['reward'] = $('#new_task_reward').val();
        request['address'] = $('#new_task_location').val();
        request['date'] = $('#new_task_valid_date').val();
        request['time'] = $('#new_task_valid_time').val();
        var place = autocomplete.getPlace();
        var latitude = place.geometry.location.lat();
        var longitude = place.geometry.location.lng();
        request['lon'] = longitude;
        request['lat'] = latitude;
        createJobOffer(request, function (res) {
            switch (res) {
                case 422:
                    alert("Provided data is incorrect.");
                    break;
                default:
                    alert("Task added successfully.");
                    break;
            }
        });
    }

    /**
     * ---------------------------- Profile ----------------------------
     */

    function initProfileForm() {
        getProfile(function (res) {
            console.log(res);
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
}
