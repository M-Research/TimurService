<!DOCTYPE html>
<html>
<head>
    <title>Timur Service</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.1/jquery.mobile-1.3.1.min.css"/>
    <link rel="stylesheet" type="text/css" href="http://dev.jtsage.com/cdn/datebox/latest/jqm-datebox.min.css" />
    <link rel="stylesheet" href="map.css" type="text/css"/>
    <script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
    <script src="http://code.jquery.com/mobile/1.3.1/jquery.mobile-1.3.1.min.js"></script>
    <script src="http://maps.google.com/maps/api/js?sensor=true" type="text/javascript"></script>
    <script src="ui/min/jquery.ui.map.full.min.js" type="text/javascript"></script>
    <script src="http://cdnjs.cloudflare.com/ajax/libs/geo-location-javascript/0.4.8/geo-min.js"
            type="text/javascript"></script>
    <script type="text/javascript" src="http://dev.jtsage.com/cdn/datebox/latest/jqm-datebox.core.min.js"></script>
    <script type="text/javascript" src="http://dev.jtsage.com/cdn/datebox/latest/jqm-datebox.mode.flipbox.min.js"></script>
    <script type="text/javascript" src="http://dev.jtsage.com/cdn/datebox/i18n/jquery.mobile.datebox.i18n.en_US.utf8.js"></script>
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

        <script type="text/javascript">
            var job = {
                details: "do some job",
                time_elapsed: "4 hours 3 min",
                expiration_time: "19-00",
                progress: "not started",
                reward: "10 grn"
            }


            function success(position) {
                //TODO: load and show markers
                if (locationMarker) {
                    return;
                }
                console.log("Initial Position Found");
                var yourStartLatLng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
                $('#map_canvas').gmap({'center': yourStartLatLng, 'zoom': 14, 'mapTypeControl': false});

                //test marker at current location
                $('#map_canvas').gmap('addMarker', { 'position': yourStartLatLng }
                ).click(function () {
                            var html = "Details: " + job.details + "</br> " + "Expires " + job.expiration_time + " </br> Reward :" + job.reward + "</br>" +
                                    "<a href=\"#details\" data-rel=\"popup\" data-transition=\"pop\" data-position-to=\"window\" data-inline=\"true\">Show details</a>";
                            $('#map_canvas').gmap('openInfoWindow', {'content': html}, this);

                        });
            }

            function error(error) {
                console.log("Something went wrong: ", error);
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
                <!--TODO: add some method to enter location-->
                alert("Location not available");
            }
        </script>

        <div data-role="popup" id="details">
            <h1>
                Details:
                <script type="text/javascript"> document.write(job.details) </script>
            </h1>
            <h1>
                Time elapsed:
                <script type="text/javascript"> document.write(job.time_elapsed) </script>
            </h1>
            <h1>
                Progress:
                <script type="text/javascript"> document.write(job.progress) </script>
            </h1>
            <input type="submit" data-theme="a" data-icon="check" data-iconpos="left"
                   value="Accept" onClick="location.href='#map-page' ">
        </div>
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
    <div data-role="content"> <h1>Empty list of added works</h1></div>

</div>

<div data-role="page" id="accepted">

    <div data-role="header" data-id="navigbar">
        <div data-role="navbar">
            <ul>
                <li><a href="#map-page">Map</a></li>
                <li><a href="#addwork" >Add Task</a></li>
                <li><a href="#accepted" class="ui-btn-active ui-state-persist">My Tasks</a></li>
                <li><a href="#added" >My Offers</a></li>
                <li><a href="#profile">Profile</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
    <!-- /header -->

    <!-- TODO: implement -->
    <div data-role="content"> <h1>Empty list of accepted works</h1></div>

</div>

<div data-role="page" id="profile">
    <div data-role="header" data-id="navigbar">
        <div data-role="navbar">
            <ul>
                <li><a href="#map-page">Map</a></li>
                <li><a href="#addwork" >Add Task</a></li>
                <li><a href="#accepted">My Tasks</a></li>
                <li><a href="#added" >My Offers</a></li>
                <li><a href="#profile" class="ui-btn-active ui-state-persist">Profile</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
    <!-- /header -->

    <!-- TODO: implement -->
    <div data-role="content"> <h1>User profile</h1></div>
    <!--<script type="text/javascript">
      getProfile(function (res) {alert(res.email);});
    </script>-->
</div>

<div data-role="page" id="addwork">

    <div data-role="header" data-id="navigbar">
        <div data-role="navbar">
            <ul>
                <li><a href="#map-page">Map</a></li>
                <li><a href="#addwork" class="ui-btn-active ui-state-persist">Add Task</a></li>
                <li><a href="#accepted">My Tasks</a></li>
                <li><a href="#added" >My Offers</a></li>
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
            function addWork(){
                alert("To be implemented");
            }
        </script>

        <input type="submit" data-icon="plus" data-iconpos="left" value="Add "
               onclick="addWork()" />
    </div>

</div>

</body>
</html>
