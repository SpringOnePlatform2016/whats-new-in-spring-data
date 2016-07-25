# Spring Data Redis - Cluster Examples #

This project contains Redis 3 Cluster specific features of Spring Data Redis.

To run the code in this sample a running cluster environment is required. Please refer to the  [redis cluster-tutorial](http://redis.io/topics/cluster-tutorial) for detailed information or check the Cluster Setup section below. 

## Support for Cluster ##

Cluster Support uses the same building blocks as the non clustered counterpart. We use `application.properties` to point to an initial set of known cluster nodes which will be picked up by the auto configuration.

```properties
spring.redis.cluster.nodes[0]=127.0.0.1:7379
spring.redis.cluster.nodes[1]=127.0.0.1:7380
spring.redis.cluster.nodes[2]=127.0.0.1:7381
```

**INFORMATION:** The tests flush the db of all known instances during the JUnit _setup_ phase to allow inspecting data directly on the cluster nodes after a test is run.

## Cluster Setup ##

To quickly set up a cluster of 4 nodes (2 master | 2 slave) simply invoke `make start`.


```bash
$ ./make start
```

It is now possible to connect to the cluster using the `redis-cli`.

```bash
redis/src $ ./redis-cli -c -p 7379
  127.0.0.1:7379> cluster nodes

  b85eeb... 127.0.0.1:7380 master - 0 1466584139197 0 connected 8192-16383
  952146... 127.0.0.1:7381 slave 42b9e7... 0 1466584139197 2 connected
  42b9e7... 127.0.0.1:7379 myself,master - 0 0 1 connected 0-8191
  208c1b... 127.0.0.1:7382 slave b85eeb... 0 1466584139197 3 connected
```

To shutdown the cluster use the `make stop` command.

```bash
$ ./make stop
```
