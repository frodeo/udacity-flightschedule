# Android Fundamentals Project Description - Flight Schedule

## Content

The content of this repository is as follows:

* **app**: The source code for the Android app.
* **doc**: The extra items that are required to be uploaded:
  * Android+Fundamentals+Problem Description: the problem description, in Pages and PDF formats.
  * Android+Fundamentals+Project+Self-Evaluation: the self evaluation, in Pages and PDF formats.
  * mockups: a directory with mockups in PNG format (snapshots from the running app).
  * app-release.apk: a signed .apk of the app.

The rest of this readme file contains the problem description, the self evaluation, and
my evaluation of the rubric.


## Problem
A user travelling or picking up persons at an airport needs flight schedule information.
He also wants to get notified if any changes occurs for his flight.

## Proposed Solution
The user can select which airport he in interested in. He then gets a list of future flights
with the information available for the flight (like destination, airline, scheduled time,
status time and status information, gate, checkin area, luggage belt etc.

He can either view a list of flights, or select a specific flight in order to get more detailed
information. He can also register a specific flight in order to be notified if there are any
status changes for that flight.

Information should be cached on the phone so that it also will work in flight mode or otherwise
when network connection is not available.

Currently this application uses data from Avinor, and only covers Norwegian airports as a base
airport. All flights to and from the Norwegian airports are covered, both domestic and
international flights.

# Self Evaluation

## Questions about Required Components

### Permissions

#### Please elaborate on why you chose the permissions in your app.

Currently the fllowing permissions are used:

* *android.permission.INTERNET*: Required in order to get information from the external REST services.
* *android.permission.READ_SYNC_SETTINGS*: Required in order to read sync adapter settings.
* *android.permission.WRITE_SYNC_SETTINGS*: Required in order to control sync adapter settings,
  like adding a periodic sync.
* *android.permission.AUTHENTICATE_ACCOUNTS*: Required in order to use an authenticator for the
  sync adapter.

### Content Provider

#### What is the name of your Content Provider, and how is it backed?

The Content Provider is called *net.oldervoll.flightschedule.data.FlightProvider*, and it is
backed by an SQLite database. The database contains the following tables:

* *airline*: for storing information about all airlines used by flights.
* *airport*: for storing information about all airports used by flights.
* *status*: for storing information about status codes used by flights.
* *flight*: for storing information about flights. This is where the flight schedule is stored.

#### What backend does it talk to?

The Content Provider talks to a [REST service from the Norwegian company Avinor]
(https://avinor.no/konsern/tjenester/flydata/#!flydata-i-xml-format-8564) (description only
available in Norwegian). This application uses the following REST endpoints for fetching data:

* [Flight information](http://flydata.avinor.no/XmlFeed.asp?airport=BGO):
  List of flights for a specified airport. This includes name of airport the flight arrives from or
  departs to, the name of the airline, the scheduled time, any status time and status message,
  the gate, the luggage belt etc. No need to update these data more often than every
  3 minutes.
* [Airline information](http://flydata.avinor.no/airlineNames.asp):
  Mapping between airline codes (identifiers) and the name of the airline (like BA=British Airways).
  No need to update these data more than once every 24 hours.
* [Airport information](http://flydata.avinor.no/airportNames.asp):
  Mapping between airport codes (identifiers) and the name of the airport (like BGO=Bergen).
  No need to update these data more than once every 24 hours.
* [Status code information](http://flydata.avinor.no/flightStatuses.asp):
  Mapping between status codes (identifiers) and the name of the sttaus (like D=Departed).
  No need to update these data more than once every 24 hours.

#### If your app uses a SyncAdapter, what is it called? What mechanism is used to actually talk over the network?

The app uses a SyncAdapter called *net.oldervoll.flightschedue.sync.SyncAdapter* and uses
the [Retrofit library](http://square.github.io/retrofit/) to talk with the backend over HTTP using
a REST service provided by Avinor. The REST service is defined in 
*net.oldervoll.flightschedule.remote.AvinorService* and receives a payload in XML format. The
XML data is parsed to Java classes using the [Simple framework](http://simple.sourceforge.net/).
The Retrofit REST adapter is built in the *net.oldervoll.flightschedule.remote.AvinorServiceImpl()*
constructor.

#### What loaders/adapters are used?

Data is loaded from the Content Provider into a *ListView* using a *CursorLoader* defined in
*net.oldervoll.flightschedule.FlightFragment*. The *CursorAdapter*
*net.oldervoll.flightschedule.FlightAdapter* is used to populate the *ListView* with data from
the *CursorLoader*.

For the detail view the *net.oldervoll.flightschedule.DetailFragment*
contains a *CursorLoader*. Here the views are populated with data directly from the
*onLoadFinished* callback, so no adapters are used here.

### User/App State

#### Please elaborate on how/where your app correctly preserves and restores user or app state.

In *net.oldervoll.flightschedule.FlightFragment* the position in the *ListView* is saved in a
*Bundle* in the *onSaveInstanceState* callback method. The position is then fetched in the
*onCreateView* callback method from the *savedInstanceState Bundle* if that is not null. The
*ListView* is then scrolled to the saved position in the *onLoadFinished* callback method. In
this way it is possible to rotate the app and still preserve the position in the list. 

User state is also saved and persisted from the *net.oldervoll.flightschedule.SettingsActivity* to
*SharedPreferences*:

* The *airport* which we should receive data for.
* The *flight* that notifications should be generated for.
* A flag for enabling or disabling notifications.
* The time for the last sync of data updated every 24 hours.
* The time for the last notification sent (used to prevent overwhelming the user with notifications)

# Questions about Optional Components

### Notifications

#### Please elaborate on how/where you implemented Notifications in your app:

The user can select a flight for which he wants notifications when the status changes. This can
for instance be a flight the user is going to travel with. These notifications gives the user
instant feedback when a flight is delayed, when the gate is changed etc. Notifications are sent for
every state change, as well as 60 minutes before departure of a flight. Notifications are based on
the effective schedule time for a flight, taken any delays into account.

Notifications are sent from the *net.oldervoll.flightschedule.sync.SyncAdapter* in the
*onPerformSync* callback method, right after the data is loaded from the remote REST service.
We make sure to minimise the number of notifications that are sent, and also to reuse existing
notifications that are not removed. The *NotificationCompat.Builder* class is used to create
notifications, and the *NotoficationManager* is used to send them.

### ShareActionProvider

#### Please elaborate on how/where you implemented ShareActionProvider:

The ShareActionProvider is implemented in *net.oldervoll.flightschedule.DetailFragment*.
A description of a flight is created in the *onLoadFinished* callback, and this description
is made available via ShareActionProvider. The shared intent can be created in two different
types:

- A plain text version containing status information about the flight.
- A rich content (HTML) version containing status information about the flight, the 
arrival/destination icon for the flight and a link to detailed flight information about this
specific flight at the Avinor web site. It also contains an image visualising either
arrival or departure.

### Broadcast Events

#### Please elaborate on how/where you implemented Broadcast Events:

The detail fragment in the app includes a custom view *net.oldervoll.flightschedule.CompassView*
that shows the direction between the departing airport and the arriving airport. In order to get the
direction the *Google Geocoding API* is used. The call to the Google Geocoding API is done using the
Retrofit REST client and is implemented in
*net.oldervoll.flightschedule.remote.GoogleGeocodingService*. This service is called from the detail
fragment using the *IntentService* called *net.oldervoll.flightschedule.GeoService*.

The detail fragment registers a *BroadcastReceiver* 
*net.oldervoll.flightschedule.DetailFragment$GeoReceiver* with *LocalBroadcastManager* in order to 
get notified when the calculation of direction has finished (the registration is done from the
*onCreateView* method in DetailFragment because we need the *ViewHolder* object in the
BroadcastReceiver. The GeoService then uses the LocalBroadcastManager to broadcast an intent with
the direction when that has been calculated (sendBroadcast is done from the *onHandleIntent*
method in *net.oldervoll.flightschedule.GeoService*.

*The use of BroadcastReceiver and Custom View is not a crucial part of the app. It is primarily
used in order to learn these mechanisms. A more useful usage of the same mechanisms could have
been to show weather information including wind direction for the airports.*

### Custom Views

#### Please elaborate on how/where you implemented Custom Views:

A *Custom View* in form of a compass is used to visualise the flight direction (from the departing
airport to the arriving airport). The custom view is implemented in the class 
*net.oldervoll.flightschedule.CompassView* and is used is the layouts *fragment_detail.xml* and
*fragment_detail_wide.xml*. The direction of the compass needle is set from the method *onReceive*
in the *BroadcastReceiver* called *GeoReceiver* 
(inside *net.oldervoll.flightschedule.DetailFragment*).

*The use of BroadcastReceiver and Custom View is not a crucial part of the app. It is primarily
used in order to learn these mechanisms. A more useful usage of the same mechanisms could have
been to show weather information including wind direction for the airports.*

# The Rubric - Self Evaluation

## Required Components

| Criteria                                                                                                                                              | Meets specification |
|-------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------|
| **Standard design**                                                                                                                                   |                     |
| App does not redefine the expected function of a system icon (such as the Back button)                                                                | OK                  |
| App does not redefine or misuse Android UI patterns, such that icons or behaviors could be misleading or confusing to users                           | OK                  |
| App includes a tablet layout which takes advantage of the additional screen space.                                                                    | OK                  |
| App includes at least two distinct views and uses intents properly to move between these views.                                                       | OK                  |
| **Navigation**                                                                                                                                        |                     |
| App supports standard system Back button navigation and does not make use of any custom, on-screen "Back button" prompts.                             | OK                  |
| All dialogs are dismissible using the Back button.                                                                                                    | OK                  |
| Pressing the Home button at any point navigates to the Home screen of the device.                                                                     | OK                  |
| **Permissions**                                                                                                                                       |                     |
| App requests only the absolute minimum permissions that it needs to support core functionality.                                                       | OK                  |
| App does not request permissions to access sensitive data or services that can cost the user money, unless related to a core capability of the app.   | OK                  |
| **Performance and Stability**                                                                                                                         |                     |
| App does not crash, force close, freeze, or otherwise function abnormally on any targeted device.                                                     | OK                  |
| **ContentProvider**                                                                                                                                   |                     |
| App implements a ContentProvider to access locally stored data.                                                                                       | OK                  |
| Must implement at least one of the three                                                                                                              |                     |
| * If it regularly pulls or sends data to/from a web service or API, app updates data in its cache at regular intervals using a SyncAdapter.           | OK                  |
| * If it needs to pull or send data to/from a web service or API only once, or on a per request basis, app uses an IntentService to do so.             | OK                  |
| * If it performs short duration, on-demand requests (such as search), app uses an AsyncTask.                                                          | OK                  |
| App uses a Loader to move its data to its views.                                                                                                      | OK                  |
| **User/App State**                                                                                                                                    |                     |
| App correctly preserves and restores user or app state. For example,                                                                                  | OK                  |
| * When a list item is selected, it remains selected on rotation.                                                                                      | OK                  |
| * When an activity is displayed, the same activity appears on rotation.                                                                               | OK                  |
| * User text input is preserved on rotation.                                                                                                           | OK                  |
| When the app is resumed after the device wakes from sleep (locked) state, the app returns the user to the exact state in which it was last used.      | OK                  |
| When the app is relaunched from Home or All Apps, the app restores the app state as closely as possible to the previous state.                        | OK                  |

## Optional Components

| Criteria                                                                                                                                              | Status              |
|-------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------|
| **Notifications**                                                                                                                                     |                     |
| Notifications do not contain advertising or content unrelated to the core function of the app.                                                        | OK                  |
| Notifications are persistent only if related to ongoing events (such as music playback or a phone call).                                              | OK                  |
| Multiple notifications are stacked into a single notification object, where possible.                                                                 | OK                  |
| App uses notifications only to indicate a context change relating to the user personally (such as an incoming message).                               | OK                  |
| App uses notifications only to expose information/controls relating to an ongoing event (such as music playback or a phone call).                     | OK                  |
| **ShareActionProvider**                                                                                                                               |                     |
| Uses ShareActionProvider to share content with an outside application.                                                                                | OK                  |
| Makes use of Intent Extras to send rich content (i.e. a paragraph of content-specific text, a link and description, an image, etc).                   | OK                  |
| **Broadcast Events**                                                                                                                                  |                     |
| App intercepts broadcast events.                                                                                                                      | OK                  |
| App responds to Broadcast events in a meaningful way.                                                                                                 | OK                  |
| **Custom Views**                                                                                                                                      |                     |
| App creates and uses a custom View.                                                                                                                   | OK                  |
| App uses a novel View that couldn’t sufficiently be satisfied by the core Views in Android.                                                           | OK                  |

# Tests

* A set of integration tests requiring a device is found in the *src/androidTest* folder.
  Use Build Variant *Android Instrumentation Tests* when running these tests.
* A set of unit tests is found in the *src/test* folder
  Use Build Variant *Unit Tests* when running these tests.

# Possible future improvements

* Show airport in map
* Show weather information - link to *Sunshine* app
* Collect data in Google App Engine app and use Google Cloud Messaing to push changes to apps
