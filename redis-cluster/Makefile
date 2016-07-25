
REDIS_VERSION:=3.2.0

#########
# Cluster
#########
.PRECIOUS: work/cluster-%.conf

work/cluster-%.conf:
	@mkdir -p $(@D)

	echo port $* >> $@
	echo cluster-enabled yes  >> $@
	echo cluster-config-file $(shell pwd)/work/nodes-$*.conf  >> $@
	echo cluster-node-timeout 5  >> $@
	echo pidfile $(shell pwd)/work/cluster-$*.pid >> $@
	echo logfile $(shell pwd)/work/cluster-$*.log >> $@
	echo save \"\" >> $@

work/cluster-%.pid: work/cluster-%.conf work/redis/bin/redis-server
	work/redis/bin/redis-server $< &

cluster-start: work/cluster-7379.pid work/cluster-7380.pid work/cluster-7381.pid work/cluster-7382.pid

work/meet-%:
	-work/redis/bin/redis-cli -p $* cluster meet 127.0.0.1 7379

# Handled separately because this node is a slave
work/meet-7381:
	-work/redis/bin/redis-cli -p 7381 cluster meet 127.0.0.1 7379
	sleep 2
	-work/redis/bin/redis-cli -p 7381 cluster replicate $(shell work/redis/bin/redis-cli -p 7379 cluster myid)

# Handled separately because this node is a slave
work/meet-7382:
	-work/redis/bin/redis-cli -p 7382 cluster meet 127.0.0.1 7379
	sleep 2
	-work/redis/bin/redis-cli -p 7382 cluster replicate $(shell work/redis/bin/redis-cli -p 7380 cluster myid)

cluster-meet: work/meet-7380 work/meet-7381 work/meet-7382

cluster-stop: stop-7379 stop-7380 stop-7381 stop-7382

cluster-slots:
	-work/redis/bin/redis-cli -p 7379 cluster addslots $(shell seq 0 8191)
	-work/redis/bin/redis-cli -p 7380 cluster addslots $(shell seq 8192 16383)

cluster-init: cluster-start cluster-meet cluster-slots

########
# Global
########
clean:
	rm -rf work/*.conf work/*.log

clobber:
	rm -rf work

work/redis/bin/redis-cli work/redis/bin/redis-server:
	@mkdir -p work/redis

	curl -sSL https://github.com/antirez/redis/archive/$(REDIS_VERSION).tar.gz | tar xzf - -C work
	$(MAKE) -C work/redis-$(REDIS_VERSION) -j
	$(MAKE) -C work/redis-$(REDIS_VERSION) PREFIX=$(shell pwd)/work/redis install
	rm -rf work/redis-$(REDIS_VERSION)

start: cluster-init

stop-%: work/redis/bin/redis-cli
	-work/redis/bin/redis-cli -p $* shutdown

stop: cluster-stop
