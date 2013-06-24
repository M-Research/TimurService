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

    <script type="text/javascript">
        $("#map-page").on('pageshow', initMapPage);
    </script>

    <div data-role="header">
        <div data-role="navbar" data-id="navigbar">
            <ul>
                <li><a href="#map-page" class="ui-btn-active ui-state-persist">Map</a></li>
                <li><a href="#addwork">Add Task</a></li>
                <li><a href="#accepted">My Tasks</a></li>
                <li><a href="#added">My Offers</a></li>
                <li><a href="#profile">Profile</a></li>
                <li><a href="#notifications">Notifications</a></li>
            </ul>
        </div>
    </div>

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

            <h4 id="det_contacts">
                Contacts:
            </h4>

            <input type="submit" data-theme="a" data-icon="check" data-iconpos="left"
                   value="Accept" onClick="$.mobile.changePage('#map-page') ">
        </div>
    </div>

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
                <li><a href="#notifications">Notifications</a></li>
            </ul>
        </div>
    </div>

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
                <li><a href="#notifications">Notifications</a></li>
            </ul>
        </div>
    </div>

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
                <li><a href="#notifications">Notifications</a></li>
            </ul>
        </div>
    </div>

    <!-- TODO: implement -->
    <div data-role="content">
        <div data-role="fieldcontain">
            <label for="task_name"> Name </label>
            <input name="" id="task_name" placeholder="" value="" type="text" />
        </div>

        <div data-role="fieldcontain">
            <label for="task_details"> Description </label>
            <textarea name="" id="task_details" placeholder=""></textArea>
        </div>

        <div data-role="fieldcontain">
            <label for="task_reward"> Reward </label>
            <input name="" id="task_reward" placeholder="" value="" type="text" />
        </div>

        <div data-role="fieldcontain">
            <label for="task_address"> Address </label>
            <input name="" id="task_address" placeholder="" value="" type="text" />
        </div>

        <label for="task_due_date">Due date</label>
        <input name="mydate" id="task_due_date" type="date" data-role="datebox"
           data-options='{"mode": "flipbox", "overrideTimeFormat": 24}'>

        <label for="task_due_time">Due Time</label>
        <input name="mydate" id="task_due_time" type="date" data-role="datebox"
               data-options='{"mode": "timeflipbox", "overrideTimeFormat": 24}'>

        <input type="submit" data-icon="plus" data-iconpos="left" value="Add " onclick="addWork()"/>
    </div>

</div>

<div data-role="page" id="profile">

    <script type="text/javascript">
        $("#profile").on('pageshow', initProfileForm);
    </script>

    <div data-role="header" data-id="navigbar">
        <div data-role="navbar">
            <ul>
                <li><a href="#map-page">Map</a></li>
                <li><a href="#addwork">Add Task</a></li>
                <li><a href="#accepted">My Tasks</a></li>
                <li><a href="#added">My Offers</a></li>
                <li><a href="#profile" class="ui-btn-active ui-state-persist">Profile</a></li>
                <li><a href="#notifications">Notifications</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>

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

        <input type="submit" data-icon="plus" data-iconpos="left" value="Update " onclick="submitProfileForm()"/>

    </div>
</div>


<div data-role="page" id="notifications">

    <div data-role="header" data-id="navigbar">
        <div data-role="navbar">
            <ul>
                <li><a href="#map-page">Map</a></li>
                <li><a href="#addwork">Add Task</a></li>
                <li><a href="#accepted">My Tasks</a></li>
                <li><a href="#added">My Offers</a></li>
                <li><a href="#profile">Profile</a></li>
                <li><a href="#notifications" class="ui-btn-active ui-state-persist">Notifications</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
    <!-- /header -->

    <!-- TODO: implement -->
    <div data-role="content"><h1>Notifications</h1></div>

</div>

</body>
</html>
