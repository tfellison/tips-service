# tips-service
**tips-service** is an example microservice that provides a RESTful API for managing tips. Tips can be submitted, updated (including adding comments), modified, or deleted. The service is built using the [Play Framework](https://www.playframework.com/), which allows for non-blocking I/O and treatment of JSON as a first-class citizen. Tips are persisted in [MongoDB](https://www.mongodb.com/) using [ReactiveMongo](http://reactivemongo.org/) as the database driver; this allows for asynchronous, non-blocking database operations.

In versions 3.0 and newer, MongoDB scales vertically reasonably well (primarily due to [major locking improvements](https://docs.mongodb.com/manual/faq/concurrency/) in v3.0) and [sharding](https://docs.mongodb.com/manual/sharding/) allows for horizontal scaling. If an idea of operation type makeup (writes, reads, inserts, etc.) was known, it would be worth benchmarking against other solutions such as Cassandra.

## Installation
\<work in progress\>

## Usage
\<work in progress\>

## Out of Scope
The topics described below were deemed out of scope fpr this particular project, but would likely be of concern in a real-world application of this service.

### Authentication / Authorization
Assuming this wasn't handled at some other layer, in a real-world application it would be necessary for clients of this service to authenticate themselves. For example, if this service was meant to be called from an app or by a specific host, we would want that client to authenticate itself before allowing any calls to be serviced. It wouldn't be ideal for anyone that can reach the service to be able to issue calls in an ad hoc fashion.

Some form of authorization would also likely be necessary depending on how tightly controlled the client environment is. For example, we wouldn't want "User A" to be able to delete or edit tips/comments submitted by "User B." If a concept of private tips existed, we wouldn't want users to be able to retrieve those tips unless they were authorized to do so.

### History
For recovering inadvertently deleted or edited tips, investigating instances of abuse, etc. it would likely be necessary to retain some amount of content history. This could also be useful in the event that some type of large-scale recovery is necessary, though presumably backup and redundancy would be handled through a more complete high availability / disaster recover solution.

### Robust Comments
Presumably comments should be stored with information detailing the submitter, creation timestamp, etc. and should be editable after initial submission, in a similar fashion to tips themselves. As this is essentially a duplication of functionality for tips, it's not included in this example; comments are simply stored as arrays of strings (rather than arrays of complex objects [or arrays of references to complex objects]).