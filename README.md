

### Overview

A simple distributed cache. This can run on cluster. When a node (X) gets up it notifies other nodes in the cluster using Multicast. It (X) then chooses a node form the cluster and sends a sync request. Post that the data is synced to the new node (X).

Each write request is replicated across nodes within the cluster in an asynchronous manner.Consistency is maintained by keeping timestamp with the data. On conflict the old data is replaced with new data.

### Configuration

###### Add the configuration in the the config file.Example is given below.

```
#Rest server port. Defaults to 8080
server.port=9087
#Id of this node.Should be unique. If not provided a random node id is assigned 
cluster.node.id=1
#Initial size of the cache. Default value 100
cache.size=100
#After this interval cluster will send info about them. Default 4000
cluster.join.refresh.interval=4000
#MultiCast if addr(Group)
cluster.join.multicast.ip=230.0.0.0
#MultiCast port addr(Group)
cluster.join.multicast.port=4555
````
### Usage

Run : java -jar dist-cache-1.0.0-SNAPSHOT.jar

##### Facing Issues
With Ip6
java -Djava.net.preferIP4Stack=true -jar dist-cache-1.0.0-SNAPSHOT.jar

