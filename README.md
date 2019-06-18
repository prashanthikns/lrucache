PROBLEM:
ign and implement a data structure for Least Recently Used (LRU) cache service.
It should
support the following operations: get and put.
get(key) - Get the value (will always be positive) of the key if the key exists
in the cache,
otherwise return 404.
put(key, value) - Set or insert the value if the key is not already present.
When the cache
reached its capacity, it should invalidate the least recently used item before
inserting a new item
and return the return the invalidated item.
These operations are to be externalized as a service exposed by a REST API.

ASSUMPTION: I am also handling the case of Put of an entry which is already
existing. This will update the value(if a different value given), and move the
node to the end of the list. This is the reason for the implementation and use
of Doubly linked list, rather than an inbuilt container like Deque.

SOLUTION:
The data structures used to implement the LRU cache are a HashMap and a doubly
linked list.

HashMap is used, as access is faster with a key and O(1). The key is the key
and the value is the node, which holds the value.
The reason for picking double linked list is there is a pointer to the head and
the tail node, which makes deleting from the head and/or tail easy. And due to
the pointers to previous and next node and a reference to the node in the hash
map, deleting an intermediate node is also order of O(1).

CLASSES:
Class CacheEntry has the key and value.

Class DoubleLLImpl is the implementation of the double linked list.
Node has data, and pointers to previous and next node in the list.
I have implemented the functions insertAtEnd, as all entries need to be
inserted at the end. Even a reference causes the entry to be moved back to the
end of the list.
Delete needs to handle all conditions of empty list, deleting head or tail or
an intermediate node.
getHead to return pointer to head and getSize to know size of the list to know
if capacity is full and if any entries need to be evicted.

Class LruHash has the private members capacity, map and list.
Map’s key is the key and data is the Node, where data is of type CacheEntry.
Put(key, value)
If a new entry, it will insert and return 200
If the capacity is full, it will evict the entry from the head, insert the new
entry and return the evicted entry.
If the entry already exists, then i am assuming it is an update to the value,
and move the node to the end of the list with the updated value.
Get(key)
If key not present, return 404
If key present, return the key and value, and move this to the end of the list
as it was just referenced.

Class MyHttpServer creates the httpserver, the handler for handling put and
get.
If it is a GET request, example curl XGET http://cache.service/api/v1/get/1
Get the requestURI, tokenize it using “/“ as the tokenizer and get the 5th
token and use the key to index into the hash map.

If it is PUT request, example curl XPUT http://cache.service/api/v1/put/2 -d
"value=800”
Get the requestURI, tokenize it using “/“ as the tokenizer and get the 5th
token and use it as the key, next get the request body and use “=“ to tokenize
and get the value. Use this key, value to insert into the hashMap.


Class MyHttpServerTest
@Before: Is the setup function, HttpServer is started
@After: teardown the server
SendPutValid - To send valid put commands
SendPut - To send nvalid put commands
SentGetValid - To send valid get commands
SendGet - To send invalid get commands

The send and the put, create a connection with the URL, set the request method,
return the response code and any other response obtained.
Each of the tests, check the response is as expected and assert if not.
