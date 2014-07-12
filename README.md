# Circuit

Circuit provides an unidirectional data flow model for GUI applications. It's intended to be used with GWT, but can be leveraged in any Java GUI framework. 

It resembles the ideas of the [Flux Architecture](http://facebook.github.io/react/docs/flux-overview.html) that can be found in the [React.js](http://facebook.github.io/react/index.html) framework, but adds certain semantics to the data flow model.

For a general introduction and problem statement we'd recommend to look at the Flux documentation first. The specifics of the Circuit implementation will be explained in the following sections.

## Core Components

### Data Flow Model

```
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

```

### Actions
Actions represent behaviour, data and state within an application. They signal state changes to the dispatcher, which in turn coordinates the processing of actions across stores.

Actions are most often initiated from user interaction, but they are not limited to that. It's also possible that the underlying framework or the service backend creates and dispatches actions.

### Dispatcher
The dispatcher acts as a central hub for processing actions. Any action passes through the dispatcher and the dispatcher delegates to Stores, that do ultimately process the Action.

The Dispatchers main responsibility is to coordinate the processing of Actions across Stores. 

### Stores
Stores keep the application state and act as proxies to the data model used by an application. Most often stores interact with service backends to read and modify a persistent data model, which they in turn expose in a read-only fashion to the actual view (or presenter-view tuples in MVP).

Stores are registered with the Dispatcher for Actions they are interested in. They can directly rely on the data passed with an Action, or listen for state changes in other parts of the data model.

Stores can emit Change Events to interested parties that rely on  the data or state managed by a particular Store.

### Presenter (as in MVP)

The Presenter (or presenter-view tuple as in MVP) creates and dispatches Actions. This happens on behalf of a user interaction, due to framework events or another signal from the service backend. 

Presenters listen to Store Change Events and in turn read data from Stores and update the views accordingly.

Presenters do only have read-only access to Stores and the data they maintain. Any modification to the data or state of an application has to be driven by Actions. 

