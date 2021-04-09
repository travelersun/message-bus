package com.messagebus.client.model;

import java.util.List;
import java.util.Map;



public class NodeView implements java.io.Serializable {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1858589790855016802L;
	
	String secret;
	Node currentQueue;
	List<String> sinkTokens;
	Map<String, Node> relatedQueueNameNodeMap;
	List<Node> subscribedNodes;
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public Node getCurrentQueue() {
		return currentQueue;
	}
	public void setCurrentQueue(Node currentQueue) {
		this.currentQueue = currentQueue;
	}
	public List<String> getSinkTokens() {
		return sinkTokens;
	}
	public void setSinkTokens(List<String> sinkTokens) {
		this.sinkTokens = sinkTokens;
	}
	public Map<String, Node> getRelatedQueueNameNodeMap() {
		return relatedQueueNameNodeMap;
	}
	public void setRelatedQueueNameNodeMap(Map<String, Node> relatedQueueNameNodeMap) {
		this.relatedQueueNameNodeMap = relatedQueueNameNodeMap;
	}
	public List<Node> getSubscribedNodes() {
		return subscribedNodes;
	}
	public void setSubscribedNodes(List<Node> subscribedNodes) {
		this.subscribedNodes = subscribedNodes;
	}
	
}
