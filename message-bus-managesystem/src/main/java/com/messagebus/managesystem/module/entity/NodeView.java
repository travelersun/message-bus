package com.messagebus.managesystem.module.entity;

import java.util.List;
import java.util.Map;

public class NodeView implements java.io.Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String secret;
	Node currentQueue;
	List<String> sinkTokens;
	Map<String, Node> relatedQueueNameNodeMap;
	List<Node> subscribedNodes;
	
	
	public List<Node> getSubscribedNodes() {
		return subscribedNodes;
	}

	public void setSubscribedNodes(List<Node> subscribedNodes) {
		this.subscribedNodes = subscribedNodes;
	}

	public String getSecret() {
		return secret;
	}

	public List<String> getSinkTokens() {
		return sinkTokens;
	}

	public Map<String, Node> getRelatedQueueNameNodeMap() {
		return relatedQueueNameNodeMap;
	}

	public void setSecret(String secret) {
		this.secret = secret;
		
	}

	public void setCurrentQueue(Node currentQueue) {
		this.currentQueue = currentQueue;
		
	}
	
	public Node getCurrentQueue() {
		return currentQueue;
		
	}

	public void setSinkTokens(List<String> sinkTokens) {
		this.sinkTokens = sinkTokens;
		
	}

	public void setRelatedQueueNameNodeMap(Map<String, Node> relatedQueueNameNodeMap) {
		this.relatedQueueNameNodeMap = relatedQueueNameNodeMap;
		
	}

	public void setSubscribeNodes(List<Node> subscribedNodes) {
		this.subscribedNodes = subscribedNodes;
		
	}

}
