# tips-service
**tips-service** is an example microservice that provides a RESTful API for managing tips. Tips can be submitted, fetched, updated (including adding comments), and deleted.

The service uses [Play Framework](https://www.playframework.com/), an event-driven web framework built on Akka which allows for fully non-blocking I/O. Tips are persisted in [MongoDB](https://www.mongodb.com/) using [ReactiveMongo](http://reactivemongo.org/) as the database driver; this allows for asynchronous, non-blocking database operations. The **tips-service** web service application itself is entirely stateless, and can be deployed out to as many nodes as required to handle incoming request volume.

In versions 3.0 and newer, MongoDB scales vertically reasonably well (primarily due to [major locking improvements](https://docs.mongodb.com/manual/faq/concurrency/) in v3.0) and [sharding](https://docs.mongodb.com/manual/sharding/) allows for horizontal scaling. If an idea of operation type makeup (writes, reads, inserts, etc.) was known, it would perhaps be worth benchmarking against other database solutions such as Cassandra, or other web frameworks. [TechEmpower Web Framework Benchmarks](https://www.techempower.com/benchmarks/#section=data-r13&hw=ph&test=db) provide a good starting point for this.

## Table of Contents
- [Installation](#installation)
  * [Quick Start](#quick-start)
  * [Full Installation Details](#full-installation-details)
- [Usage](#usage)
  * [create-tip](#create-tip)
  * [fetch-tip](#fetch-tip)
  * [update-tip](#update-tip)
  * [add-comment](#add-comment)
  * [delete-tip](#delete-tip)
  * [fetch-all-tips](#fetch-all-tips)
  * [delete-all-tips](#delete-all-tips)
- [Testing](#testing)
- [Scope](#scope)


## Installation
### Quick Start
The easiest way to get **tips-service** up and running is to run the published [Docker](https://docs.docker.com/engine/getstarted/step_one/) container, along with a container running MongoDB. To achieve this, simply execute the following:

```
docker run -p 27017:27017 mongo:3.4
```
```
docker run --net="host" tfellison/tips-service
```
Once both containers are running, you'll be able to access tips-service at https://localhost:9000.

### Full Installation Details
This section is redundant with the above quick start, in that the quick start itself contains everything needed to run **tips-service**. This section provides more details on the installation process, as well as information on some alternative ways to install/run the required applications.

**tips-service** requires an instance of MongoDB, so the first step in installing tips service is to get MongoDB running. The easiest way to do this is using Docker:

```
docker run -p 27017:27017 mongo:3.4
```
Alternatively (if required for running on Windows, for example) MongoDB can be installed by following the official installation instructions: [Install MongoDB](https://docs.mongodb.com/manual/installation/).

Once MongoDB is running, **tips-service** can also be run in a Docker container:
```
docker run --net="host" tfellison/tips-service
```

Including the _--net="host"_ argument will result in the Docker container sharing the full network stack with the Docker host. This allows for the application running in the container to reference MongoDB via "localhost." In a production environment, the connection string would point to a specific external host and this tag would not be required.

**tips-service** can also be built and run from source using [Lightbend Activator](https://www.lightbend.com/activator/download). To do this, obtain the project source from GitHub, nagivate to the project root (the level containing the _build.sbt_ file), and run the following:
```
activator start
```
Once both MongoDB and **tips-service** are running, you'll be able to access **tips-service** at http://localhost:9000.

## Usage
**tips-service** listens for HTTP requests on port 9000 by default, at the path _/api/tips_. For example: http://localhost:9000/api/tips/fetch-all-tips. Operations that receive input expect POST requests with a _Content-Type_ of "application/json". Operations that do not required input expect GET requests.

The following table details all operations supported by **tips-service**:

| Operation         | HTTP Request Type | Input Format                                                                              | Behavior                                       |
|-------------------|:-----------------:|-------------------------------------------------------------------------------------------|------------------------------------------------|
| create-tip        |        POST       | { "submitter":"\<username\>", "message":"\<tip message body\>" }                        | Create and store a new tip; returns the ID of the created tip                     |
| fetch-tip         |        POST       | { "id":"\<ID of tip to fetch\>" }                                                        | Fetch a previously created tip                 |
| update-tip        |        POST       | { "id":"\<ID of tip to update\>", "message":"\<updated tip message body\>"}             | Update the message of a previously created tip |
| delete-tip        |        POST       | { "id":"\<ID of tip to delete\>" }                                                       | Delete a previously created tip                |
| add-comment        |        POST       | { "id":"\<ID of tip to which to add comment\>", "comment":"\<comment message body\>" } | Add a comment to an existing tip               |
| fetch-all-tips  |        GET        | _n/a_                                                                                     | Fetch all existing tips                        |
| delete-all-tips |        GET        | _n/a_                                                                                     | Drop the entire collection of tips             |

The final two operations, _fetch-all-tips_ and _delete-all-tips_, exist primarily to ease testing and verification. These would not likely be exposed in a production environment.

Following are example _curl_ requests and subsequent responses that illustrate usage of these operations.

### create-tip
Request:
```
curl -H "Content-Type: application/json" -X POST -d '{"submitter":"Joe Bob", "message":"This is a new tip."}' http://localhost:9000/api/tips/create-tip
```
Response:
```
{
	"id": "cab2a79a-f1df-4fca-ab13-c9ecb0237a1e"
}
```

### fetch-tip
Request:
```
curl -H "Content-Type: application/json" -X POST -d '{"id":"cab2a79a-f1df-4fca-ab13-c9ecb0237a1e"}' http://localhost:9000/api/tips/fetch-tip
```
Response:
```
{
	"_id": {
		"$oid": "58d1ac966c19ee7295398c57"
	},
	"id": "cab2a79a-f1df-4fca-ab13-c9ecb0237a1e",
	"submitter": "Joe Bob",
	"createdTime": "2017-03-21T22:43:34.024Z",
	"lastUpdatedTime": "2017-03-21T22:43:34.024Z",
	"message": "This is a new tip.",
	"comments": []
}
```

### update-tip
Request:
```
curl -H "Content-Type: application/json" -X POST -d '{"id":"cab2a79a-f1df-4fca-ab13-c9ecb0237a1e", "message":"This is an updated tip."}' http://localhost:9000/api/tips/update-tip
```
Response:
```
{
	"result": "Operation successful: Message updated for tip cab2a79a-f1df-4fca-ab13-c9ecb0237a1e."
}
```

### add-comment
Request:
```
curl -H "Content-Type: application/json" -X POST -d '{"id":"cab2a79a-f1df-4fca-ab13-c9ecb0237a1e", "comment":"This is a new comment on the tip."}' http://localhost:9000/api/tips/add-comment
```
Response:
```
{
	"result": "Operation successful: Comment added to tip cab2a79a-f1df-4fca-ab13-c9ecb0237a1e."
}
```

### delete-tip
Request:
```
curl -H "Content-Type: application/json" -X POST -d '{"id":"cab2a79a-f1df-4fca-ab13-c9ecb0237a1e"}' http://localhost:9000/api/tips/delete-tip
```
Response:
```
{
	"result": "Operation successful: Tip cab2a79a-f1df-4fca-ab13-c9ecb0237a1e deleted."
}
```

### fetch-all-tips
Request:
```
curl -X GET http://localhost:9000/api/tips/fetch-all-tips
```
Response:
```
[{
	"_id": {
		"$oid": "58d1ac966c19ee7295398c57"
	},
	"id": "cab2a79a-f1df-4fca-ab13-c9ecb0237a1e",
	"submitter": "Joe Bob",
	"createdTime": "2017-03-21T22:43:34.024Z",
	"lastUpdatedTime": "2017-03-21T22:48:42.908Z",
	"message": "This is an updated tip.",
	"comments": ["This is a new comment on the tip."]
}, {
	"_id": {
		"$oid": "58d1ae116c19ee729539913e"
	},
	"id": "950d1a59-7743-4781-8a76-8dc87bbd0e36",
	"submitter": "Jill Bob",
	"createdTime": "2017-03-21T22:49:53.486Z",
	"lastUpdatedTime": "2017-03-21T22:49:53.486Z",
	"message": "This is another new tip.",
	"comments": []
}]
```

### delete-all-tips
Request:
```
curl -X GET http://localhost:9000/api/tips/delete-all-tips
```
Response:
```
{
	"result": "Operation successful: Tips collection cleared."
}
```
## Testing
Basic integration tests ensure the application properly processes requests from a browser, and can connect to and execute operations against the database:

![Integration Tests](/static/images/integration_tests.png?raw=true)

This does not constitute proper load or performance testing, but [JMeter](https://jmeter.apache.org/) was used to ensure the application can handle a constant stream of incoming requests.

Incoming _create-tip_ requests:

![Database Writes](/static/images/database_reads.png?raw=true)

Incoming _fetch-tip_ requests:

![Database Reads](/cd static/images/database_writes.png?raw=true)

## Scope
The topics described below were deemed out of scope for this particular project, but would likely be of concern in a real-world application of this service.

### Authentication / Authorization
Assuming this wasn't handled at some other layer, in a real-world application it would be necessary for clients of this service to authenticate themselves. For example, if this service was meant to be called from an app or by a specific host, we would want that client to authenticate itself before allowing any calls to be serviced. It wouldn't be ideal for anyone that can reach the service to be able to issue calls in an ad hoc fashion.

Some form of authorization would also likely be necessary depending on how tightly controlled the client environment is. For example, we wouldn't want "User A" to be able to delete or edit tips/comments submitted by "User B." If a concept of private tips existed, we wouldn't want users to be able to retrieve those tips unless they were authorized to do so.

### History
For recovering inadvertently deleted or edited tips, investigating instances of abuse, etc. it would likely be necessary to retain some amount of content history. This could also be useful in the event that some type of large-scale recovery is necessary, though presumably backup and redundancy would be handled through a more complete high availability / disaster recover solution.

### Robust Comments
Presumably comments should be stored with information detailing the submitter, creation timestamp, etc. and should be editable after initial submission, in a similar fashion to tips themselves. As this is essentially a duplication of functionality for tips, it's not included in this example. Comments are simply stored as arrays of strings (rather than arrays of complex objects [or arrays of references to complex objects]).
