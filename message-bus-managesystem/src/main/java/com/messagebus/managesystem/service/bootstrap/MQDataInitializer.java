package com.messagebus.managesystem.service.bootstrap;


import com.google.common.base.Strings;
import com.messagebus.interactor.rabbitmq.AbstractInitializer;
import com.messagebus.managesystem.module.entity.Node;
import com.messagebus.managesystem.service.core.Exchange;
import com.messagebus.managesystem.service.core.Queue;
import com.rabbitmq.client.AMQP;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class MQDataInitializer extends AbstractInitializer {

    private static          Log               logger   = LogFactory.getLog(MQDataInitializer.class);
    private static volatile MQDataInitializer instance = null;

    private MQDataInitializer(String host) {
        super(host);
    }

    public static MQDataInitializer getInstance(String mqHost) {
        if (instance == null) {
            synchronized (MQDataInitializer.class) {
                if (instance == null) {
                    instance = new MQDataInitializer(mqHost);
                }
            }
        }

        return instance;
    }

    public void deleteQueueNoWait(String queueName) {
        try {
            super.init();
            AMQP.Queue.DeleteOk deleteOk = channel.queueDelete(queueName);
            if (deleteOk == null) {
                throw new RuntimeException("delete queue with name : " + queueName + " failed.");
            }
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } finally {
            super.close();
        }
    }

    public void initExchange(List<Exchange> sortedExchanges, Map<Integer, Exchange> exchangeMap) {
        try {
            super.init();

            //declare exchange
            for (Exchange exchange : sortedExchanges) {
                channel.exchangeDeclare(exchange.getExchangeName(), exchange.getRouterType(), true);
            }

            //bind exchange
            for (Exchange exchange : sortedExchanges) {
                if (exchange.getParentId() == -1)
                    continue;

                channel.exchangeBind(exchange.getExchangeName(),
                        exchangeMap.get(exchange.getParentId()).getExchangeName(),
                        exchange.getRoutingKey());
            }
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } finally {
            super.close();
        }
    }

    public void initQueue(List<Queue> queues) {

        try {
            super.init();

            //declare queue
            for (Queue queue : queues) {
                Map<String, Object> queueConfig = new HashMap<String, Object>(2);

                if (queue.getThreshold() != -1) {
                    queueConfig.put("x-max-length", queue.getThreshold());
                }

                if (queue.getThreshold() != -1 && queue.getMsgBodySize() != -1) {
                    int allMsgSize = queue.getThreshold() * queue.getMsgBodySize();
                    queueConfig.put("x-max-length-bytes", allMsgSize);
                }

                if (queue.getTtl() != -1) {
                    channel.queueDelete(queue.getQueueName());
                    queueConfig.put("x-expires", queue.getTtl());
                }

                if (queue.getTtlPerMsg() != -1) {
                    channel.queueDelete(queue.getQueueName());
                    queueConfig.put("x-message-ttl", queue.getTtlPerMsg());
                }

                channel.queueDeclare(queue.getQueueName(), true, false, false, queueConfig);
            }

            //bind queue
            for (Queue queue : queues) {
                channel.queueBind(queue.getQueueName(),
                        queue.getBindExchange(),
                        queue.getRoutingKey());
            }
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } finally {
            super.close();
        }
    }
    

	public void initTopologyComponent(Node[] nodes) throws IOException {
		Map<String, Node> nodeMap = this.buildNodeMap(nodes);
		TreeSet<Node> sortedExchangeNodes = this.extractExchangeNodes(nodes);
		TreeSet<Node> sortedQueueNodes = this.extractQueueNodes(nodes);
		super.init();
		String notificationExchangeRealName = null;
		Iterator i$ = sortedExchangeNodes.iterator();

		Node node;
		while (i$.hasNext()) {
			node = (Node) i$.next();
			this.channel.exchangeDeclare(node.getValue(), node.getRouterType(), true);
			if (node.getName().equals("notification")) {
				notificationExchangeRealName = node.getValue();
			}
		}

		if (Strings.isNullOrEmpty(notificationExchangeRealName)) {
			logger.error("can not find a exchange named : notification");
			throw new RuntimeException("can not find a exchange named : notification");
		} else {
			i$ = sortedExchangeNodes.iterator();

			while (i$.hasNext()) {
				node = (Node) i$.next();
				if (!node.getParentId().equals("-1")) {
					this.channel.exchangeBind(node.getValue(), ((Node) nodeMap.get(node.getParentId())).getValue(),
							node.getRoutingKey());
				}
			}

			i$ = sortedQueueNodes.iterator();

			while (i$.hasNext()) {
				node = (Node) i$.next();
				if (!"1".equals(node.getIsVirtual())) {
					Map<String, Object> queueConfig = new HashMap(2);
					String thresholdStr = node.getThreshold();
					if (!Strings.isNullOrEmpty(thresholdStr)) {
						int threshold = Integer.parseInt(thresholdStr);
						queueConfig.put("x-max-length", threshold);
					}

					String msgSizeOfBodyStr = node.getMsgBodySize();
					if (!Strings.isNullOrEmpty(thresholdStr) && !Strings.isNullOrEmpty(msgSizeOfBodyStr)) {
						int threshold = Integer.parseInt(thresholdStr);
						int msgSizeOfBody = Integer.parseInt(msgSizeOfBodyStr);
						int allMsgSize = threshold * msgSizeOfBody;
						queueConfig.put("x-max-length-bytes", allMsgSize);
					}

					String ttl = node.getTtl();
					if (!Strings.isNullOrEmpty(ttl)) {
						this.channel.queueDelete(node.getValue());
						queueConfig.put("x-expires", Integer.parseInt(ttl));
					}

					String ttlPerMsg = node.getTtlPerMsg();
					if (!Strings.isNullOrEmpty(ttlPerMsg)) {
						this.channel.queueDelete(node.getValue());
						queueConfig.put("x-message-ttl", Integer.parseInt(ttlPerMsg));
					}

					this.channel.queueDeclare(node.getValue(), true, false, false, queueConfig);
				}
			}

			i$ = sortedQueueNodes.iterator();

			while (i$.hasNext()) {
				node = (Node) i$.next();
				if (!"1".equals(node.getIsVirtual())) {
					this.channel.queueBind(node.getValue(), ((Node) nodeMap.get(node.getParentId())).getValue(),
							node.getRoutingKey());
					this.channel.queueBind(node.getValue(), notificationExchangeRealName, "");
				}
			}

			super.close();
		}
	}

    private void destroyTopologyComponent() throws IOException {
        //call reset-app
    }
    
    private Map<String, Node> buildNodeMap(Node[] nodes) {
		Map<String, Node> nodeMap = new HashMap(nodes.length);
		Node[] arr$ = nodes;
		int len$ = nodes.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			Node node = arr$[i$];
			nodeMap.put(node.getNodeId().toString(), node);
		}

		return nodeMap;
	}

	private TreeSet<Node> extractExchangeNodes(Node[] nodes) {
		TreeSet<Node> exchangeSet = new TreeSet();
		Node[] arr$ = nodes;
		int len$ = nodes.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			Node node = arr$[i$];
			if (node.getType().equals("0")) {
				exchangeSet.add(node);
			}
		}

		return exchangeSet;
	}

	private TreeSet<Node> extractQueueNodes(Node[] nodes) {
		TreeSet<Node> queueSet = new TreeSet();
		Node[] arr$ = nodes;
		int len$ = nodes.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			Node node = arr$[i$];
			if (node.getType().equals("1")) {
				queueSet.add(node);
			}
		}

		return queueSet;
	}

	private boolean exchangeExists(String exchangeName) throws IOException {
		boolean result = true;

		try {
			this.channel.exchangeDeclarePassive(exchangeName);
		} catch (IOException var4) {
			result = false;
			if (!this.channel.isOpen()) {
				super.init();
			}
		}

		return result;
	}

	private boolean queueExists(String queueName) throws IOException {
		boolean result = true;

		try {
			this.channel.queueDeclarePassive(queueName);
		} catch (IOException var4) {
			result = false;
			if (!this.channel.isOpen()) {
				super.init();
			}
		}

		return result;
	}



}
