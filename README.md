# Circuit

Implementation of the Flux architecture as described at http://facebook.github.io/react/docs/flux-overview.html.


## Components and Data Flow

<p>
                                             Process(Action)
                      +-----------------+
                      |   Dispatcher    | +--------------+
                      |                 |                |
                      +-----------------+                |
                                                         |
                             ^                           v
                       Action|
+-----------+                |                   +----------+
|   View    |  Interaction   |       read        |  Store   |
|           | +----------+   |   +-------------> |          |
+-----------+            v   |   |               +----------+
                             |   |
    ^                 +------+---+------+                +
    |                 |    Presenter    |                |
    +---------------+ |                 |  <-------------+
           update     +-----------------+           Change Event

                           ^
                           |  Lifecycle
                       +---+------------+
                       |   Framework    |
                       |                |
                       +----------------+

</p>
