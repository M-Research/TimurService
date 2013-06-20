<!DOCTYPE html>
<html>
<head>
    <title>Timur Service</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.1/jquery.mobile-1.3.1.min.css"/>
    <link rel="stylesheet" type="text/css" href="http://dev.jtsage.com/cdn/datebox/latest/jqm-datebox.min.css"/>
    <link rel="stylesheet" href="map.css" type="text/css"/>

    <script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
    <script src="http://code.jquery.com/mobile/1.3.1/jquery.mobile-1.3.1.min.js"></script>
    <script src="http://maps.google.com/maps/api/js?sensor=true" type="text/javascript"></script>
    <script src="ui/min/jquery.ui.map.full.min.js" type="text/javascript"></script>
    <script src="http://cdnjs.cloudflare.com/ajax/libs/geo-location-javascript/0.4.8/geo-min.js"
            type="text/javascript"></script>
    <script type="text/javascript" src="http://dev.jtsage.com/cdn/datebox/latest/jqm-datebox.core.min.js"></script>
    <script type="text/javascript"
            src="http://dev.jtsage.com/cdn/datebox/latest/jqm-datebox.mode.flipbox.min.js"></script>
    <script type="text/javascript"
            src="http://dev.jtsage.com/cdn/datebox/i18n/jquery.mobile.datebox.i18n.en_US.utf8.js"></script>

    <script src="js/api.js" type="text/javascript"></script>
</head>

<body>

<div data-role="page" id="map-page">

    <div data-role="header">
        <div data-role="navbar" data-id="navigbar">
            <ul>
                <li><a href="#map-page" class="ui-btn-active ui-state-persist">Map</a></li>
                <li><a href="#addwork">Add Task</a></li>
                <li><a href="#accepted">My Tasks</a></li>
                <li><a href="#added">My Offers</a></li>
                <li><a href="#profile">Profile</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
    <!-- /header -->

    <!--TODO: add loading-->
    <div data-role="content" id="map_canvas">


        <div data-role="popup" id="details">
            <h4 id="detTitle">
                Title:
            </h4>
            <h4 id="det_desc">
                Description:
            </h4>
            <h4 id="det_rew">
                Reward:
            </h4>
            <h4 id="det_address">
                Address:
            </h4>

            <input type="submit" data-theme="a" data-icon="check" data-iconpos="left"
                   value="Accept" onClick="location.href = '#map-page' ">
        </div>

        <script type="text/javascript">

            var job = null;

            function success(position) {
                //TODO: load and show markers
                if (locationMarker) {
                    return;
                }


                console.log("Initial Position Found");
                var yourStartLatLng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
                $('#map_canvas').gmap({'center': yourStartLatLng, 'zoom': 14, 'mapTypeControl': false});

                $.getJSON('user/ownedJobs', function (data) {
                    $.each(data.profile.jobs, function (index, element) {
                        job = element

                        var tmpPos = new google.maps.LatLng(job.latitude, job.longitude);

                        $('#map_canvas').gmap('addMarker', { 'position': tmpPos }
                        ).click(function () {
                                    job = element
                                    var html = "Details: " + job.name + " </br> Reward :" + job.reward + "</br>" +
                                            "<a href=\"#details\" data-rel=\"popup\" data-transition=\"pop\" data-position-to=\"window\" data-inline=\"true\">Show details</a>";
                                    $('#detTitle').text("Title: " +job.name)
                                    $('#det_desc').text("Description: " +job.desc)
                                    $('#det_address').text("Address: " +job.address)
                                    $('#det_rew').text("Reward: " +job.reward)
                                    $('#map_canvas').gmap('openInfoWindow', {'content': html}, this);

                                });
                    });
                });
            }

            function error(error) {
                console.log("Something went wrong: ", error);
                success(getDefaultCoordinates())
                alert("Location is not accurate");
            }

            if (geo_position_js.init()) {
                geo_position_js.getCurrentPosition(success, error);
            } else if (navigator.geolocation) {
                var locationMarker = null;
                if (!locationMarker) {

                    console.log("Initial Position Found");
                    navigator.geolocation.getCurrentPosition(
                            success(position),
                            error(error),
                            {
                                timeout: (1000),
                                maximumAge: (1000 * 60 * 15),
                                enableHighAccuracy: true
                            }
                    );
                }
            } else {
                success(getDefaultCoordinates())
                alert("Location is not accurate");
            }
        </script>

    </div>

    <!-- /content -->

    <!--<div data-role="footer" data-position="fixed">-->
    <!--</div>-->
    <!-- /footer -->
</div>

<div data-role="page" id="added">

    <div data-role="header" data-id="navigbar">
        <div data-role="navbar">
            <ul>
                <li><a href="#map-page">Map</a></li>
                <li><a href="#addwork">Add Task</a></li>
                <li><a href="#accepted">My Tasks</a></li>
                <li><a href="#added" class="ui-btn-active ui-state-persist">My Offers</a></li>
                <li><a href="#profile">Profile</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
    <!-- /header -->

    <!-- TODO: implement -->
    <div data-role="content"><h1>Empty list of added works</h1></div>

</div>

<div data-role="page" id="accepted">

    <div data-role="header" data-id="navigbar">
        <div data-role="navbar">
            <ul>
                <li><a href="#map-page">Map</a></li>
                <li><a href="#addwork">Add Task</a></li>
                <li><a href="#accepted" class="ui-btn-active ui-state-persist">My Tasks</a></li>
                <li><a href="#added">My Offers</a></li>
                <li><a href="#profile">Profile</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
    <!-- /header -->

    <!-- TODO: implement -->
    <div data-role="content"><h1>Empty list of accepted works</h1></div>

</div>

<div data-role="page" id="addwork">

    <div data-role="header" data-id="navigbar">
        <div data-role="navbar">
            <ul>
                <li><a href="#map-page">Map</a></li>
                <li><a href="#addwork" class="ui-btn-active ui-state-persist">Add Task</a></li>
                <li><a href="#accepted">My Tasks</a></li>
                <li><a href="#added">My Offers</a></li>
                <li><a href="#profile">Profile</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
    <!-- /header -->

    <!-- TODO: implement -->
    <div data-role="content">
        <div data-role="fieldcontain">
            <label for="textinput1">
                Name
            </label>
            <input name="" id="textinput1" placeholder="" value="" type="text">
        </div>

        <div data-role="fieldcontain">
            <label for="textarea5">
                Description
            </label>
            <textarea name="" id="textarea5" placeholder=""></textarea>
        </div>

        <div data-role="fieldcontain">
            <label for="textinput3">
                Salary
            </label>
            <input name="" id="textinput3" placeholder="" value="" type="text">
        </div>

        <label for="mydate">Some Date</label>
        <input name="mydate" id="mydate" type="date" data-role="datebox"
               data-options='{"mode": "timeflipbox", "overrideTimeFormat": 24}'>

        <script type="text/javascript">
            function addWork() {
                getProfile(function (res) {
                    alert(res.name)
                });

//                $.getJSON('user/ownedJobs', function (data) {
//                    $.each(data.profile.jobs, function (index, element) {
//                        alert(element.name);
//                    });
//
//                    //alert(data);
//                });
            }
        </script>

        <input type="submit" data-icon="plus" data-iconpos="left" value="Add "
               onclick="addWork()"/>
    </div>

</div>

<div data-role="page" id="profile">
    <div data-role="header" data-id="navigbar">
        <div data-role="navbar">
            <ul>
                <li><a href="#map-page">Map</a></li>
                <li><a href="#addwork">Add Task</a></li>
                <li><a href="#accepted">My Tasks</a></li>
                <li><a href="#added">My Offers</a></li>
                <li><a href="#profile" class="ui-btn-active ui-state-persist">Profile</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
    <script type="text/javascript">
        getProfile(function (res) {
            if (res.name) {
                $('#profile_name').innerHTML = res.name;
            }

            if (res.skype) {
                $('#profile_skype').text(res.skype);
            }

            if (res.phone) {
                $('#profile_phone').text(res.phone);
            }

            $('#profile_email').text(res.email);
        });
    </script>

    <div data-role="content" id="profileFillPageContent">
        <div data-role="fieldcontain">
            <label for="profile_name">Name</label>
            <input name="" id="profile_name" placeholder="" value="" type="text">
        </div>

        <div data-role="fieldcontain">
            <label for="profile_email">E-Mail</label>
            <textarea name="" id="profile_email" placeholder=""></textarea>
        </div>

        <div data-role="fieldcontain">
            <label for="profile_phone">Phone</label>
            <input name="" id="profile_phone" placeholder="" value="" type="text">
        </div>


        <div data-role="fieldcontain">
            <label for="profile_skype">Skype</label>
            <input name="" id="profile_skype" placeholder="" value="" type="text">
        </div>

        <input type="submit" data-icon="plus" data-iconpos="left" value="Add " onclick="submitG()"/>

        <script>
            function submitG() {
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
            ;
        </script>
    </div>

</div>

</div>

</body>
</html>
