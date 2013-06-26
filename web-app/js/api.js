if (typeof jQuery !== 'undefined') {

    /**
     * ------------------------------ API ------------------------------
     */

    /**
     * Calls the given function on the received user's profile (object with
     * fields email, rating, name, phone, skype, photo).
     */
    function getProfile(f) {
        apiCall("GET", "user/profile", f);
    }

    /**
     * Retrieves detailed information about the job with given ID.
     */
    function getJob(id, callback) {
        apiCall("GET", "job/list/" + id, callback);
    }

    /**
     * Retrieves the list of all jobs that current user accepted (but not yet
     * was approved or finished).
     */
    function getAcceptedJobs(callback) {
        apiCall("GET", "job/listAcceptedJobs", callback);
    }

    /**
     * Retrieves the list of all jobs that current user was approved for.
     */
    function getApprovedJobs(callback) {
        apiCall("GET", "job/listApprovedJobs", callback);
    }

    /**
     * Retrieves the list of all jobs that current user was approved for.
     */
    function getFinishedJobs(callback) {
        apiCall("GET", "job/listFinishedJobsByUser", callback);
    }

    /**
     * Retrieves the list of all jobs that current user created
     * with basic information.
     */
    function getJobOffers(callback) {
        apiCall("GET", "job/listJobsForUserLight", callback);
    }

    /**
     * gets all the requests for the job
     */
    function listRequestsForJob(data, callback) {
        apiCall("POST", "job/listRequestsForJob", callback, data);
    }

    /**
     * gets all approved requests for the job
     */
    function listApprovedRequestsForJob(data, callback) {
        apiCall("POST", "job/listApprovedRequestsForJob", callback, data);
    }

    /**
     * finishes the job
     */
    function finish(data, callback) {
        apiCall("POST", "job/finish", callback, data);
    }

    /**
     * approves the request
     */
    function approveRequest(data, callback) {
        apiCall("POST", "job/approveRequest", callback, data);
    }

    /**
     * Creates a new job offer described in given offerData and calls the
     * given callback on received result. Result is either empty or failure
     * status code:
     *     401 - if authorization is failed
     *     422 - if arguments are incorrect or absent
     */
    function createJobOffer(offerData, callback) {
        apiCall("POST", "job/create", callback, offerData);
    }

    /**
     * Accepts Job offer
     */
    function onCreateJobRequest(createRequestData, callback) {
        apiCall("POST", "job/createRequest", callback, createRequestData);
    }

    /**
     * Cancels job offer.
     */
    function onCancelJobRequest(data, callback) {
        apiCall("POST", "job/cancel", callback, data);
    }

    /**
     * Cancels accepted job offer.
     */
    function cancelOwnJobRequest(data, callback) {
        apiCall("POST", "job/cancelOwnRequest", callback, data);
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
        } else if (status == STATUS_APPROVED) {
            suff = 3;
        } else if (status == STATUS_DONE) {
            suff = 15;
        } else if (status == STATUS_CANCELED) {
            suff = 46;
        }

        return baseURL + suff.toString();
    }

    var locationMarker = null;
    var errorLocationShown = false;

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
            if (!errorLocationShown) {
                alert("Location is not accurate");
                errorLocationShown = true;
            }
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
        $('#map_det_job_id').val(job.id);
        $('#detTitle').text("Title: " + job.title);
        $('#det_desc').text("Description: " + job.description);
        $('#det_address').text("Address: " + job.address);
        $('#det_rew').text("Reward: " + job.reward);
        // TODO render date
        $('#det_valid_until').text("Valid until: " + job.validUntil);
        $('#det_contacts').text("Contacts: " + job.contact);
        return html;
    }

    /**
     * puts markers with all jobs on the map
     */
    function populateMarkersWithJobs() {
        $.getJSON('job/list', function (data) {
            $.each(data, function (index, element) {
                console.log(element);
                job = element;
                var tmpPos = new google.maps.LatLng(job.latitude, job.longitude);
                $('#map_canvas').gmap('addMarker', { 'position': tmpPos, 'icon': getIconForStatus(job.status)}
                ).click(function () {
                        $('#map_canvas').gmap('openInfoWindow', {'content': getInfoWindowContent(element)}, this);
                    });
            });
        });
    }

    /**
     * gets address (readable) for a given coordinates
     * @param location (Latitude + Longitude)
     */
    function findLocation(location) {
        $('#map_canvas').gmap('search', {'location': location}, function (results, status) {
            if (status === 'OK') {
                var address = results[0].formatted_address;
                $('<div>').simpledialog2({
                    mode: 'button',
                    headerText: false,
                    headerClose: false,
                    buttonPrompt: 'Do you want to add new task here?',
                    buttons: {
                        'OK': {
                            click: function () {
                                $.mobile.changePage('#addwork');
                                $("#new_task_location").val(address);
                            }
                        },
                        'Cancel': {
                            click: function () {
                            },
                            icon: "delete",
                            theme: "c"
                        }
                    }
                })
            }
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
        $('#map_canvas').gmap({'center': yourStartLatLng, 'zoom': 16, 'mapTypeControl': false}).
            bind('init', function (event, map) {
                $('#map_canvas').gmap('addMarker', {
                    'position': yourStartLatLng,
                    'icon': 'http://cdn.edit.g.imapbuilder.net/images/markers/54'
                });

                $(map).click(function (event) {
                    findLocation(event.latLng)
                })
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
        if (!errorLocationShown) {
            alert("Location is not accurate");
            errorLocationShown = true;
        }
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
     *  -----------------------Accept Job -------------------------------
     */

    function createJobRequest() {
        var request = {}
        request["id"] = $("#map_det_job_id").val();
        onCreateJobRequest(request, function (res) {
            if (res.error) {
                alert(res.error)
            } else if (res.jobrequest) {
                alert(res.jobrequest)
            } else {
                alert("Authorization failed");
            }
        });
    }

    /**
     * ----------------------Job Offers ----------------------------------
     */

    function getTextForTask(req) {
        var stat = req.status;
        var message = req.title + " (";
        switch (stat) {
            case STATUS_APPROVED:
                message += req.employee_info + " Approved"
                break;
            case STATUS_CANCELED:
                message += "Job is cancelled"
                break;
            case STATUS_OPEN:
                message += req.numOfWorkers + " Users Accepted"
                break;
            case STATUS_DONE:
                message += "Job is finished"
                break;
            default:
                break;
        }
        message += ")";
        return message;
    }

    function getTheme(status) {
        var theme = "e"
        switch (status) {
            case STATUS_APPROVED:
                theme = "b"
                break
            case STATUS_CANCELED:
                theme = "c"
                break;
            case STATUS_OPEN:
                theme = "e"
                break;
            case STATUS_DONE:
                theme = "a"
                break;
            default:
                break;
        }
        return theme
    }

    function cancelJobRequest() {
        var id = $("#offers_det_job_id").val();
        onCancelJobRequest({job_id: id}, function (data) {
            alert("Job cancelled");
            initAdded();
        })
    }


    function populate(offer, el) {

        if (offer.status == STATUS_OPEN) {
            listRequestsForJob({id: offer.job_id}, function (data) {
                var lst = $("<ul/>").attr("data-role", "listview").attr("data-split-icon", "check").attr("data-mini", "true");

                $.each(data, function (i, req) {
                    var eli = $("<li/>").append(
                        $("<a/>").append($("<h4/>").text("User: " + req.info + " with rating " + req.rating)));

                    eli.append($("<a/>").attr("href", "#approveRequest").
                        click(function () {
                            approveRequest({job_id: offer.job_id, candidate_id: req.candidate_id}, function () {
                                alert("Job approved");
                                initAdded();
                            })
                        })
                    );

                    lst.append(eli);
                });

                el.append(lst);
                el.trigger("create")
                lst.listview("refresh");
            });
        } else if (offer.status == STATUS_APPROVED) {
            listApprovedRequestsForJob({id: offer.job_id}, function (data) {
                var lst = $("<ul/>").attr("data-role", "listview").attr("data-split-icon", "star").attr("data-mini", "true");

                $.each(data, function (i, req) {
                    var eli = $("<li/>").append(
                        $("<a/>").append($("<h4/>").text("User: " + req.info + " with rating " + req.rating)));

                    eli.append($("<a/>").attr("href", "#").
                        click(function () {
                            $("#evaluate_job_id").val(offer.job_id);
                            $("#evaluate_job").popup("open");
                        })
                    );

                    lst.append(eli);
                });

                el.append(lst);
                el.trigger("create")
                lst.listview("refresh");
            });
        }
    }

    function evaluateJob() {
        var rating = $('#star-job').raty('score');
        if (rating == undefined) {
            rating = 0;
        }
        var job_id = parseInt($("#evaluate_job_id").val());

        finish({rating: rating, job_id: job_id}, function () {
            $("#evaluate_job").popup("close");
            initAdded();
        });
    }

    function initAdded() {
        $('#star-job').raty({ score: 4 });
        var addedEl = $("#added-coll")
        addedEl.children().remove();
        var tmpEl;

        getJobOffers(function (data) {
            $.each(data, function (i, offer) {
                tmpEl = $("<div/>").attr("data-role", "collapsible").attr("id", "offer_" + offer.job_id).attr("data-theme", getTheme(offer.status)).
                    append($("<h3/>").attr("data-position", "inline").text(getTextForTask(offer)).
                        append($("<span/>").addClass("button-span").
                            append($("<a/>").addClass("details").attr("href", "#added").attr("data-role", "button").
                                attr("data-inline", "true").attr("data-icon", "gear").
                                attr("data-icon", "gear").attr("data-icon", "gear").
                                attr("id", "show-content").attr("data-iconpos", "notext")
                                .click(function (e) {
                                    getJob(offer.job_id, function (r) {
                                        job = r.job;

                                        $('#offers_det_job_id').val(job.id);
                                        $('#offers_det_title').text("Title: " + job.title);
                                        $('#offers_det_desc').text("Description: " +
                                            job.description);
                                        $('#offers_det_status').text("Status: " +
                                            offer.status);
                                        $('#offers_det_address').text("Address: " +
                                            job.address);
                                        $('#offers_det_rew').text("Reward: " + job.reward);
                                        $('#offers_det_valid_until').text("Valid until: " +
                                            job.validUntil);

                                        $("#offers_details").popup("open");
                                    });
                                    e.stopPropagation();
                                    e.stopImmediatePropagation();
                                })

                            )));

                var contentDiv = $("<div/>");
                tmpEl.append(contentDiv);
                populate(offer, contentDiv);
                addedEl.append(tmpEl);
            });
            addedEl.trigger("create");
        });
    }

    /**
     * ------------------------ New job creation -----------------------
     */

        // var autocomplete;

    function initPlacesAutocomplete() {
        var input = document.getElementById('new_task_location');
        //var input = $('#new_task_location');
        var options = {
            types: ['geocode']
        };

        var autocomplete = new google.maps.places.Autocomplete(input, options);
    }

    function addWork() {
        var request = {};
        request['title'] = $('#new_task_title').val();
        request['description'] = $('#new_task_description').val();
        request['reward'] = $('#new_task_reward').val();
        var address = $('#new_task_location').val();
        request['address'] = address;
        request['date'] = $('#new_task_valid_date').val();
        request['time'] = $('#new_task_valid_time').val();
        //var place = autocomplete.getPlace();
        console.log(address);
        codeAddress(address, function (location) {
            var latitude = location.lat();
            var longitude = location.lng();
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

    /**
     * -------------------------- User's Tasks -------------------------
     */

    var STATUS_PENDING = "PENDING";
    var STATUS_ACCEPTED = "ACCEPTED";
    var STATUS_DISMISSED = "DISMISSED";

    function getThemeForTask(status) {
        var theme;
        switch (status) {
        case STATUS_ACCEPTED:
            theme = getTheme(STATUS_APPROVED);
            break;
        case STATUS_DISMISSED:
            theme = getTheme(STATUS_CANCELED);
            break;
        case STATUS_PENDING:
            theme = getTheme(STATUS_OPEN);
            break;
        case STATUS_DONE:
            theme = getTheme(STATUS_DONE);
            break;
        default:
            theme = getTheme(status);
            break;
        }
        return theme;
    }

    /**
     * Renders user's tasks on page.
     */
    function initTasks() {
        var taskList = $("#accepted_tasks_list");
        taskList.html("");
        var renderer = function (data) { renderUserTasks(taskList, data); };
        getAcceptedJobs(renderer);
        getApprovedJobs(renderer);
        getFinishedJobs(function (data) {
            renderFinishedJobs(taskList, data);
        });
    }

    /**
     * Adds all tasks from data to given taskList DOM element.
     */
    function renderUserTasks(taskList, data) {
        $.each(data, function (i, task) {
            var el = createTaskListElement(task);
            taskList.append(el);
            // add delete request button
            el.append($("<a/>").attr("href", "#deleteRequest").
                      click(function () {
                          cancelOwnJobRequest(
                              {job_id: task.job_id},
                              function (res) {
                                  el.remove();
                              });
                      }));

        });
        taskList.listview("refresh");
    }

    /**
     * Adds all finished jobs from data to given taskList DOM element.
     */
    function renderFinishedJobs(taskList, data) {
        var status = STATUS_DONE;
        $.each(data, function (i, job) {
            job.job_title = job.name;
            job.status = status;
            job.job_id = job.id;
            var el = createTaskListElement(job);
            taskList.append(el);
        });
        taskList.listview("refresh");
    }

    function createTaskListElement(data) {
        return $("<li/>").attr("data-icon", "false").
            attr("data-theme", getThemeForTask(data.status)).append(
                $("<a/>").append(
                    $("<h3/>").text(data.job_title),
                    $("<p/>").text("Status: " + data.status),
                    $("<p/>").text("Contact: " + data.contact)
                ).click(function () {
                    getJob(data.job_id, function (r) {
                        var job = r.job;
                        renderDetailsPopup(data.status, data.contact, job);
                    });
                }));
    }

    function renderDetailsPopup(status, contact, data) {
        $('#tasks_det_job_id').val(data.id);
        $('#tasks_det_title').text("Title: " + data.title);
        $('#tasks_det_desc').text("Description: " + data.description);
        $('#tasks_det_status').text("Status: " + status);
        $('#tasks_det_address').text("Address: " + data.address);
        $('#tasks_det_rew').text("Reward: " + data.reward);
        $('#tasks_det_valid_until').text("Valid until: " + data.validUntil);
        $('#tasks_det_contacts').text("Contacts: " + contact);
        $("#task_list_details").popup("open");
    }
}
