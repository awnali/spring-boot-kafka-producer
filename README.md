## Spring boot Kafka Producer (With Integration & Unit Test Cases)

### Run Producer


* `mvn clean install` 
* `java -jar target/KafkaProducer-0.0.1-SNAPSHOT.jar`

### Send Method

* Send Message without key

```
    public ListenableFuture<SendResult<K, V>> send(String topic, V message){
        ListenableFuture<SendResult<K, V>> res = template.send(topic, message);
        res.addCallback(messageCallBacks());
        return res;
    }
```

* Send Message with Key
```
    public void send(String topic, V message, K key){
        ListenableFuture<SendResult<K, V>> res = template.send(topic, key, message);
        res.addCallback(messageCallBacks());
    }
```

* Send Message with key and headers

```
public void send(String topic, V message, K key, Map<String, String> headerList){
        List<Header> messageHeaders = headerList.keySet()
                  .stream()
                  .map(k -> new RecordHeader(k, headerList.get(k).getBytes()))
                                   .collect(Collectors.toList());
        ProducerRecord<K,V> record = new ProducerRecord<>(topic, null, key, message, messageHeaders);
        ListenableFuture<SendResult<K, V>> res = template.send(record);
        res.addCallback(messageCallBacks());
    }
```
* Handle Error and Exceptions

```
private ListenableFutureCallback<SendResult<K, V>> messageCallBacks(){
        return new ListenableFutureCallback<SendResult<K, V>>() {
            @SneakyThrows
            @Override
            public void onFailure(Throwable ex) {
                log.error(ex.getMessage());
                throw ex;
            }

            @Override
            public void onSuccess(SendResult<K, V> result) {
                log.info("Message sent on, {}", result.getRecordMetadata().topic());
            }
        };
    }
```


### How to Send Message
* GET: `localhost:8081/message/send`
* POST: 

```
curl --location --request POST 'http://localhost:8081/message/send' \
--header 'Content-Type: application/json' \
--data-raw '{"id":706,"name":"Randall Schimmel","address":"Suite 541 699 Abshire Drive, South John, OH 14686","phone":"575-004-3925","active":true}'
```
