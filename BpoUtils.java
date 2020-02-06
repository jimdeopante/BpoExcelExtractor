package com.svi.excelextractor;

import java.util.HashMap;
import java.util.List;

import com.svi.bpo.api.entities.BpoElement;
import com.svi.bpo.api.entities.BpoNode;
import com.svi.bpo.api.entities.BpoWorker;
import com.svi.bpo.api.operations.BpoOperationResponse;
import com.svi.bpo.api.operations.impl.rest.requests.BpoConnector;
import com.svi.bpo.api.operations.impl.rest.requests.ElementOperationsRestRequest;
import com.svi.bpo.api.operations.impl.rest.requests.NodeOperationsRestRequest;
import com.svi.bpo.api.operations.impl.rest.requests.OtherOperationsRestRequest;
import com.svi.bpo.api.operations.impl.rest.requests.WorkerOperationsRestRequest;

public class BpoUtils {
	
	
	String bpoDomain = "http://192.168.143.111";
	String bpoPort = "8080";
	String bpoContextRoot = "bpo-websvc";
	BpoConnector bpoConnector = new BpoConnector(bpoDomain, bpoPort, bpoContextRoot);
	
	WorkerOperationsRestRequest workerOp = new WorkerOperationsRestRequest(bpoConnector);
	NodeOperationsRestRequest nodeOp = new NodeOperationsRestRequest(bpoConnector);
	
//	public ElementOperationsRestRequest getElemOpsRestReq() {
//		ElementOperationsRestRequest elementOp = new ElementOperationsRestRequest(bpoConnector);
//		return elementOp;	
//	}
	
	public NodeOperationsRestRequest getNodeOpsRestReq() {
		NodeOperationsRestRequest nodeOp = new NodeOperationsRestRequest(bpoConnector);
		return nodeOp;	
	} 
	
//	public WorkerOperationsRestRequest getWorkerOpsRestReq() {
//		WorkerOperationsRestRequest workerOp = new NodeOperationsRestRequest(bpoConnector);
//		return nodeOp;	
//	} 
	
	
	public void assignWorkersToNodes(HashMap<String, BpoWorker> workers, List<BpoNode> nodes) {


		for (BpoNode node : nodes) {
			String nodeIdentity = node.getNodeId().toString();		
			BpoWorker workerAssigned = workers.get(nodeIdentity);
			BpoOperationResponse responseWorker = nodeOp.assignWorkerToNode( nodeIdentity, workerAssigned, workerAssigned.getQueueIndex());
			if (responseWorker.isSuccessful()) {
				
//				System.out.println(workerAssigned.getWorkerId() + " Assigned to: " + nodeIdentity);
			}else{
//				System.out.println(responseWorker.getErrorCode());
			}
		}
		
	}
	
	public void assignElementsToNodes(List<BpoElement> elements) {

		int sizeOfElements = elements.size();
		
		for (BpoElement element : elements) {
			
			System.out.println(element.getNodeId());
			System.out.println(element.toString());
			
			BpoOperationResponse responseElement = nodeOp.insertElementToNode(element.getNodeId(), element);
			if (responseElement.isSuccessful()) {
				System.out.println(element.getElementId() + " Assigned to: " + element.getNodeId());
			}else{
				System.out.println(responseElement.getErrorCode());
			}
			
			
		}
			
		
//		for (BpoNode node : nodes) {
//			String nodeIdentity = node.getNodeId().toString();
//			System.out.println(nodeIdentity);
//			BpoElement elementAssigned = elements.get(nodeIdentity);
//			System.out.println(elementAssigned.toString());
//			BpoOperationResponse responseElement = nodeOp.insertElementToNode(nodeIdentity, elementAssigned);
//			if (responseElement.isSuccessful()) {
//				System.out.println(elementAssigned.getElementId() + " Assigned to: " + nodeIdentity);
//			}else{
//				System.out.println(responseElement.getErrorCode());
//			}
//			
//		}

	}
	
	
	public void assignWorkerIdToElement (List<BpoElement> elements, HashMap<String, BpoWorker> workers) {
		
		for (BpoElement element : elements) {
			String elemNodeIdentity = element.getNodeId();
			BpoWorker workerAssigned = workers.get(elemNodeIdentity);
			String workerAssignedId = workerAssigned.getWorkerId();		
		    element.setWorkerId(workerAssignedId);

		}
	}

}
