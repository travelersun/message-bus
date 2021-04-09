# overview

message-bus 是一个基于rabbitmq,zookeeper,mysql的消息总线.
先谈谈消息总线跟消息队列的区别，以及对于企业级应用需要将消息队列封装成消息总线的必要性。
消息总线跟消息队列有何区别？如果有人问你这个问题，你的答案是什么？
如果你的消息总线是基于一个已经相当成熟的消息队列或者消息系统做二次封装。
为什么需要用你的客户端，而不直接用原始的（这是一个大家都相信权威的时代，请注意这里用的是相信，而不是迷信，你确实应该相信权威，至少比相信一个新手来得靠谱，当然我这里指的权威，是正面的意思）？
对这个问题的思考：
* 消息队列clientAPI权限太大，clientAPI信任级别太高
* 消息队列clientAPI面向技术，消息总线clientAPI面向技术+业务
* 消息队列无法隐藏通信细节
* 消息队列无法实施实时管控
* 总线的优势：统一入口，简化拦截成本

这里为了理解简单，就暂且先把RabbitMQ当做是个消息队列，其实它不只是个消息队列，其他的一些基于JMS的消息队列对于回答这个问题而言，也能成立。
无论是消息总线还是服务总线，其实所谓的总线就是进行先收拢再发散的过程. 
先收拢，从统一的入口进去，完成必要的统一处理逻辑；
再发散，按照路由规则，路由到各个组件去处理。
事实上这就是代理的作用：屏蔽内部细节，对外统一入口。
在基于代理的基础上，我们可以对消息总线上所有的消息做日志记录（因为所有消息的通信都必须经过代理），
并且还是在不切断RabbitMQ自身Channel的基础上，而如果想在路由上实现一个Proxy，那基本上离不开一个树形拓扑结构。
其实市面上已经有一些成熟的消息队列可以开箱即用，如果你针对消息队列来封装出一个消息总线，总有人会认为是否有这个必要性。
如果没有这些开源的消息队列，那么完全有你自己来实现消息总线的话，你还是需要实现出一个跟市面上类似的MQ或者MessageBroker，
因此消息队列只是实现消息总线的基础，或者是它的消息通信方式；
而选择基于一些成熟的MessageBroker来进行开发，既能省去很多的工作量，又能享有它们提供的稳定性以及社区的贡献。
此消息总线与Spring Cloud Bus和Spring Cloud Stream的区别是啥？使用场景有啥区别？
Spring Cloud Stream通过对消息中间件进行抽象封装，提供一个统一的接口供我们发送和监听消息，
而Bus则是在Stream基础之上再次进行抽象封装，使得我们可以在不用理解消息发送、监听等概念的基础上使用消息来完成业务逻辑的处理。
Spring Cloud Stream中，异步调用能让各个服务充分解耦而且也更加灵活。
而Spring Cloud Bus就是借助消息驱动来实现将消息（事件）广播到各个服务中，然后服务对这些消息进行消费。
Spring Cloud Bus和Spring Cloud Stream 都是直接连接直接用原始的消息中间件，
有前述讨论的问题，当然如果有需要消息总线完全可以配合这两者使用

消息总线提供的功能及使用场景：
* 缓冲类——自生产自消费
* 解耦类、异步类——生产者消费者模型
* 服务调用类（RPC）——请求/响应
* 管控协调类——发布/订阅
* 通知类——广播
* 提供http api 方便跨平台使用（httpserver 应用提供）
* 提供web-console UI 方便管理消息总线（managesystem 应用提供）
* httpserver,managesystem及其依赖的底层如rabbitmq,zookeeper(管理总线元数据),mysql 可提供分布式部署和高可用

应用架构图

![img 21][21]

消息路由结构

![img 22][22]

## web-console 


# overview
消息总线管控台，前后端分离，前端使用vue-element-admin模板构建，后端用Spring boot。用于提供对RabbitMQ的内部信息可视化、消息总线核心实体的管理、消息总线管控指令的下发等功能。
与此同时它也是一些监控服务的运行容器以及消息总线对外提供服务API的web容器。


消息总线控制台展示：

![img 1][1]

![img 2][2]

![img 3][3]

![img 4][4]

![img 5][5]

![img 6][6]

![img 7][7]


rabbitmq 元数据展示：

![img 10][10]

![img 11][11]

![img 12][12]

![img 13][13]


# Restful API 与消息总线通信的http api

此api是另一个独立的Spring boot应用提供的
考虑到效率、性能、网络、吞吐量
目前采用异步io的模式，后续计划改成Spring webflux react io 模式

消息对象参数结构
```js
    {
      "messageId": 1380447784758575105,
      "type": null,
      "timestamp": 1617959374000,
      "priority": 0,
      "expiration": null,
      "headers": null,
      "contentEncoding": null,
      "contentType": "text/plain",
      "replyTo": null,
      "appId": "100",
      "userId": null,
      "clusterId": null,
      "correlationId": "cms-test-publish",
      "deliveryMode": 2,
      "content": [
        116,
        101,
        115,
        116
      ]
    }
```
其中content为字节数组 byte[] 类型,如果需要使用其它数据类型建议先序列化为byte[] , 或者反序列化byte[]为相应的类型
如 [116,101,115,116] 为 "test" 字符串字节数组
## produce：

```
/messagebus/queues/{qname}/messages?secret={secret}&apiType={produce}&token={token}
```

http method : `POST`

request params :

* path : qname - queue name
* querystring :
    * secret - auth key （must）
    * apiType - identify API，value `produce` （must）
* request body :
    * messages - message object list （must）

example
linux curl 命令需要对&字符进行转义 \&

curl http://localhost:8350/messagebus/queues/oa-consume/messages?secret=8R4hpZ2t1pC4AOOw0KLc\&apiType=produce\&token=8R4hpZ2t1pC4AOOw0KLc -X POST \
-d 'messages=[{"messageId":0,"type":null,"timestamp":0,"priority":0,"expiration":null,"headers":null,"contentEncoding":null,"contentType":"text/plain","replyTo":null,"appId":null,"userId":null,"clusterId":null,"correlationId":null,"deliveryMode":2,"content":[116,101,115,116]}]' \
--header "Content-Type: application/x-www-form-urlencoded"

response :

```js
{
	statusCode: 10200,
	error: "",
	msg: "",
	data: ''
}
```

## consume:

```
/messagebus/queues/messages?secret={secret}&apiType={consume}&mode={pull}&num={num}
```

http method : `GET`

request params :

* path : qname - queue name
* querystring :
    * secret - auth key （must）
    * apiType - identify API，value `consume` （must）
    * mode - value `pull` or `push` （must）
    * num - except num，from  0 < num to 100(equals) （mode must be pull）

curl http://localhost:8350/messagebus/queues/messages?secret=9LC68n4M3eejFuV2488a\&apiType=consume&mode=pull&num=1

response :

```js
{
	statusCode: 10200,
	error: "",
	msg: "",
	data: [
		{
			messageHeader: {
				messageId: 520133271997313000,
				type: "appMessage",
				timestamp: null,
				priority: 0,
				expiration: null,
				deliveryMode: 2,
				headers: null,
				contentEncoding: null,
				contentType: null,
				replyTo: null,
				appId: null,
				userId: null,
				clusterId: null,
				correlationId: null
			},
			messageBody: {
				messageBody: [
					116,
					101,
					115,
					116
				]
			},
			messageType: "AppMessage"
		}
	]
}

```

## publish:

```
/messagebus/queues/messages?secret={secret}&apiType={publish}
```

http method : `POST`

request params :

* path : qname - queue name
* querystring :
    * secret - auth key（must）
    * type - identify API，value `publish`（must）
* request body :
    * message - message object （client blocked and just once）

example 

![img 8][8]

curl http://localhost:8350/messagebus/queues/messages?secret=b7ewDoHijoyH93vTnJcJ\&apiType=publish\&token=b7ewDoHijoyH93vTnJcJ -X POST \
-d 'messages=[{"messageId":0,"type":null,"timestamp":0,"priority":0,"expiration":null,"headers":null,"contentEncoding":null,"contentType":"text/plain","replyTo":null,"appId":null,"userId":null,"clusterId":null,"correlationId":null,"deliveryMode":2,"content":[116,101,115,116]}]' \
--header "Content-Type: application/x-www-form-urlencoded"

response :

```js
{
  "statusCode": 10200,
          "error": "",
          "msg": "",
          "data": ""
}
```


## subscribe:

```
/messagebus/queues/messages?secret={secret}&apiType=subscribe
```
http method : `GET`

request params :

* path : qname - queue name
* querystring :
    * secret - auth key（must）
    * apiType - identify API，value `subscribe`（must）
* request body :
    * message - message object

example

![img 9][9]

curl http://localhost:8350/messagebus/queues/messages?secret=Vi5595g30TAw17RZ58su\&apiType=subscribe

response :

```js
{
  "statusCode": 10200,
          "error": "",
          "msg": "",
          "data": [
    {
      "messageId": 1380447784758575105,
      "type": null,
      "timestamp": 1617959374000,
      "priority": 0,
      "expiration": null,
      "headers": null,
      "contentEncoding": null,
      "contentType": "text/plain",
      "replyTo": null,
      "appId": "100",
      "userId": null,
      "clusterId": null,
      "correlationId": "cms-test-publish",
      "deliveryMode": 2,
      "content": [
        116,
        101,
        115,
        116
      ]
    }
  ]
}
```
感谢
vinoYang. http://vinoyang.com
提供思路
消息总线专栏:[消息总线专栏](http://blog.csdn.net/yanghua_kobe/article/category/2898357)


## licence

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

```
http://www.apache.org/licenses/LICENSE-2.0
```
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

[1]:https://github.com/travelersun/message-bus/blob/master/images/1.png
[2]:https://github.com/travelersun/message-bus/blob/master/images/2.png
[3]:https://github.com/travelersun/message-bus/blob/master/images/3.png
[4]:https://github.com/travelersun/message-bus/blob/master/images/4.png
[5]:https://github.com/travelersun/message-bus/blob/master/images/5.png
[6]:https://github.com/travelersun/message-bus/blob/master/images/6.png
[7]:https://github.com/travelersun/message-bus/blob/master/images/7.png
[8]:https://github.com/travelersun/message-bus/blob/master/images/8.png
[9]:https://github.com/travelersun/message-bus/blob/master/images/9.png
[10]:https://github.com/travelersun/message-bus/blob/master/images/10.png
[11]:https://github.com/travelersun/message-bus/blob/master/images/11.png
[12]:https://github.com/travelersun/message-bus/blob/master/images/12.png
[13]:https://github.com/travelersun/message-bus/blob/master/images/13.png
[21]:https://github.com/travelersun/message-bus/blob/master/images/architecture.png
[22]:https://github.com/travelersun/message-bus/blob/master/images/router-topology.png