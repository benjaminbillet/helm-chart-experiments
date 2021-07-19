# Helm experiments for dataflow engine

## Run Confluent stack in Kubernetes

Download the chart:
```
helm repo add confluentinc https://confluentinc.github.io/cp-helm-charts
helm repo update
```

Run the stack:
```
helm install my-confluent confluentinc/cp-helm-charts --version 0.6.0 --set cp-schema-registry.enabled=false,cp-kafka-rest.enabled=false,cp-kafka-connect.enabled=false,cp-ksql-server.enabled=false,cp-zookeeper.servers=1,cp-kafka.brokers=1,cp-control-center.configurationOverrides."replication\.factor"=1,cp-kafka.configurationOverrides."offsets\.topic\.replication\.factor"=1,cp-kafka.configurationOverrides."default\.replication\.factor"=1,cp-kafka.configurationOverrides."min\.insync\.replicas"=1
```

Port forward for control center:
```
kubectl port-forward pod/my-confluent-cp-control-center-85779cf5bb-mwq2j 9021:9021
```

Test read/write to a topic:
```
kubectl exec -c cp-kafka-broker -it my-confluent-cp-kafka-0 -- /bin/bash /usr/bin/kafka-console-producer --broker-list localhost:9092 --topic test
kubectl exec -c cp-kafka-broker -it my-confluent-cp-kafka-0 -- /bin/bash  /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning
```

Kill the whole confluent stack:
```
helm delete my-confluent
kubectl delete statefulset my-confluent-cp-kafka my-confluent-cp-zookeeper
kubectl delete pvc --selector=release=my-confluent
```

## Build

JAR artifacts:
```
mvn clean package
```

Docker images:
```
docker build -f api-poller-source/Dockerfile -t benjaminbillet/api-poller-source:1.0.0 api-poller-source

docker build -f jolt-transformer/Dockerfile -t benjaminbillet/jolt-transformer:1.0.0 jolt-transformer

docker build -f in-memory-store/Dockerfile -t benjaminbillet/in-memory-store:1.0.0 in-memory-store
```

## Run services locally using helm

```
helm install api-poller-source-1 ./kube/api-poller-source --set endpointUri="https://random-data-api.com/api/users/random_user",outputTopic=rawUsers,brokers=my-confluent-cp-kafka-headless:9092,dataflowName=my-dataflow,customerName=benjamin,instanceName=api-poller-source-1
helm install jolt-transformer-1 ./kube/jolt-transformer --set inputTopic=rawUsers,outputTopic=users,brokers=my-confluent-cp-kafka-headless:9092,dataflowName=my-dataflow,customerName=benjamin,instanceName=jolt-transformer-1,joltSpecification="[{\"operation\":\"shift\",\"spec\":{\"uid\":\"userId\",\"first_name\":\"firstName\",\"last_name\":\"lastName\",\"email\":\"emailAddress\",\"address\":{\"coordinates\":{\"*\":\"location\"}}}},{\"operation\":\"default\",\"spec\":{\"type\":\"USER\"}},{\"operation\":\"sort\"}]"
helm install in-memory-store-1 ./kube/in-memory-store --set keyJsonPath="$.userId",apiPort=9999,inputTopic=users,brokers=my-confluent-cp-kafka-headless:9092,dataflowName=my-dataflow,customerName=benjamin,instanceName=in-memory-store-1

# start a second in-memory-store in the same dataflow
helm install in-memory-store-2 ./kube/in-memory-store --set keyJsonPath="$.userId",apiPort=9999,inputTopic=users,brokers=my-confluent-cp-kafka-headless:9092,dataflowName=my-dataflow,customerName=benjamin,instanceName=in-memory-store-2
```

Get logs:
```
kubectl logs -l app=api-poller-source -f
kubectl logs -l app=jolt-transformer -f
kubectl logs -l app=in-memory-store -f
```

Kill everthing:
```
helm delete api-poller-source-1
helm delete jolt-transformer-1
helm delete in-memory-store-1
```


