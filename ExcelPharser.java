package com.svi.excelextractor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONObject;

import com.svi.bpo.api.entities.BpoElement;
import com.svi.bpo.api.entities.BpoNode;
import com.svi.bpo.api.entities.BpoNodeStatus;
import com.svi.bpo.api.entities.BpoWorker;
import com.svi.bpo.api.entities.ProductionOutputUnit;
import com.svi.bpo.api.operations.BpoOperationResponse;
import com.svi.bpo.api.operations.impl.rest.requests.NodeOperationsRestRequest;

public class ExcelPharser {

	private static Workbook wb;
	private static Sheet nodeSheet;
	private static Sheet workerSheet;
	private static Sheet elementSheet;
	private static FileInputStream fis;
	private static FileOutputStream fos;
	private static Row row;
	private static Cell cell;
	private static String excelFileName = "./BPO.xls";
	
	static BpoUtils utils = new BpoUtils();

	static NodeOperationsRestRequest nodeOp = utils.getNodeOpsRestReq();
	
	static List<BpoNode> nodes = new ArrayList<>();
	static HashMap<String, BpoWorker> workers = new HashMap<String, BpoWorker>();
	static HashMap<String, BpoElement> elements = new HashMap<String, BpoElement>();

	public List<BpoNode> getListOfNodes(String excelFileName) throws InvalidFormatException, IOException {

		// int ctr = 0;
		fis = new FileInputStream(excelFileName);
		wb = WorkbookFactory.create(fis);
		nodeSheet = wb.getSheet("Node");
		int noOfRowsOfNodes = nodeSheet.getLastRowNum();
		Row rowOfNode = nodeSheet.getRow(0);
		int maxCellOfNode = rowOfNode.getLastCellNum();

		JSONObject nodeHeaderMap = new JSONObject();

		for (int i = 0; i < maxCellOfNode; i++) {

			String nodeHeader = nodeSheet.getRow(0).getCell(i).toString();

			// if ( nodeHeader.equals("Label") || nodeHeader.equals("Unit of
			// Measure") || nodeHeader.equals("Cost") ) {
			//
			// ctr++;
			// int numberOfRepetition = ctr/3;
			// int[] indexArray = new int[numberOfRepetition];
			// for (int j = 0; j < numberOfRepetition; j++) {
			// indexArray[j] = i;
			// nodeHeaderMap.put(nodeHeader, indexArray);
			// }
			//
			// } else {
			nodeHeaderMap.put(nodeHeader, i);
			// }

		}

		for (int i = 1; i <= noOfRowsOfNodes; i++) {

			String nodeId = nodeSheet.getRow(i).getCell(nodeHeaderMap.getInt("Node ID")).toString();
			String nodeName = nodeSheet.getRow(i).getCell(nodeHeaderMap.getInt("Node Name")).toString();
			String nodeCluster = nodeSheet.getRow(i).getCell(nodeHeaderMap.getInt("Cluster/ Location")).toString();
			String nodeStats = nodeSheet.getRow(i).getCell(nodeHeaderMap.getInt("Status")).toString();
			String nodeElemType = nodeSheet.getRow(i).getCell(nodeHeaderMap.getInt("Element Type")).toString();
			// System.out.println(nodeId);

			BpoNodeStatus nodeStatus = BpoNodeStatus.valueOf(nodeStats);

			// Cell cellWaitDur = nodeSheet.getRow(i).getCell(nodeWaitDurIndex);
			Cell cellWaitDur = nodeSheet.getRow(i).getCell(nodeHeaderMap.getInt("Waiting Duration Limit"));
			cellWaitDur.setCellType(Cell.CELL_TYPE_NUMERIC);
			Double doubleWaitDur = cellWaitDur.getNumericCellValue();
			long aveWaitDurLimit = doubleWaitDur.longValue();

			// Cell cellProcDur = nodeSheet.getRow(i).getCell(nodeProcDurIndex);
			Cell cellProcDur = nodeSheet.getRow(i).getCell(nodeHeaderMap.getInt("Processing Duration Limit"));
			cellProcDur.setCellType(Cell.CELL_TYPE_NUMERIC);
			Double doubleProcDur = cellProcDur.getNumericCellValue();
			long aveProcDurLimit = doubleWaitDur.longValue();

			Map<String, ProductionOutputUnit> productionOutputUnits = new HashMap<>();

			for (int j = 0; j < 5; j++) {
				String prodOutUnitKey = nodeSheet.getRow(i).getCell(7 + (j * 3)).toString();
				String unitOfMeasure = nodeSheet.getRow(i).getCell(8 + (j * 3)).toString();
				String cost = nodeSheet.getRow(i).getCell(9 + (j * 3)).toString();

				productionOutputUnits.put(prodOutUnitKey,
						new ProductionOutputUnit(unitOfMeasure, new BigDecimal(cost)));
			}
			// System.out.println(productionOutputUnits);
			//
			// System.out.println(aveWaitDurLimit);
			// System.out.println(aveProcDurLimit);

			BpoNode bpoNode = new BpoNode(nodeId, nodeName, nodeCluster, nodeStatus, productionOutputUnits,
					nodeElemType, aveWaitDurLimit, aveProcDurLimit);

			BpoOperationResponse responseNode = nodeOp.createNode(bpoNode);

			nodes.add(bpoNode);

			// System.out.println(responseNode.getErrorCode());

			// System.out.println(responseNode);

			if (responseNode.isSuccessful()) {
				// List<BpoNode> nodes = new ArrayList<>();

				// nodes.add(bpoNode);

				// System.out.println("node creation success" + i);
			} else {
				// System.out.println("node creation failed" + i);
				// System.out.println(bpoNode.toString());
			}

		}
		return nodes;

		// workerSheet = wb.getSheet("Workers");
		// elementSheet = wb.getSheet("Elements");
		// int noOfRowsOfWorkers = workerSheet.getLastRowNum();
		// Row rowOfWorker = workerSheet.getRow(0);
		// int maxCellOfWorker = rowOfWorker.getLastCellNum();
		//
		// int noOfRowsOfElements = elementSheet.getLastRowNum();
		// Row rowOfElement = elementSheet.getRow(0);
		// int maxCellOfElement = rowOfElement.getLastCellNum();

	}

	public HashMap<String, BpoWorker> getListOfWorkers(String excelFileName)
			throws InvalidFormatException, IOException {

		HashMap<String, BpoWorker> workers = new HashMap<String, BpoWorker>();

		Map<String, Integer> workerHeaderMap = new HashMap<String, Integer>();
		fis = new FileInputStream(excelFileName);
		wb = WorkbookFactory.create(fis);
		workerSheet = wb.getSheet("Workers");
		int noOfRowsOfWorkers = workerSheet.getLastRowNum();
		Row rowOfWorker = workerSheet.getRow(0);
		int maxCellOfWorker = rowOfWorker.getLastCellNum();

		for (int i = 0; i < maxCellOfWorker; i++) {

			String workerHeader = workerSheet.getRow(0).getCell(i).toString();
			workerHeaderMap.put(workerHeader, i);
		}

		for (int i = 1; i < noOfRowsOfWorkers; i++) {
			String workerId = workerSheet.getRow(i).getCell(workerHeaderMap.get("Worker ID")).toString();
			String workerName = workerSheet.getRow(i).getCell(workerHeaderMap.get("Worker Name")).toString();
			String workerNodeId = workerSheet.getRow(i).getCell(workerHeaderMap.get("Node ID")).toString();

			String[] workerQueueString = workerSheet.getRow(i).getCell(workerHeaderMap.get("QUEUE INDEX")).toString()
					.split(",");
			// System.out.println("workerQueueString" +
			// Arrays.toString(workerQueueString));

			List<Integer> workerQueueIntList = new ArrayList<Integer>();
			int checkStart = 0;

			for (int k = 0; k < workerQueueString.length; k++) {
				String tmpDigit = workerQueueString[k];
				if (tmpDigit.contains("-")) {
					String[] startAndEnd = tmpDigit.toString().split("-");
					int start = Integer.parseInt(startAndEnd[0]);
					int end = Integer.parseInt(startAndEnd[1]);
					// System.out.println(start);
					// System.out.println(end);
					for (int j = start; j <= end; j++) {
						if (!workerQueueIntList.contains(j)) {

							workerQueueIntList.add(j);
						}
					}
				}

				else {
					int queueInt = Integer.parseInt(tmpDigit);
					workerQueueIntList.add(queueInt);
				}
			}

			int[] workerQueueIntArray = workerQueueIntList.stream().mapToInt(e -> e.intValue()).toArray();

			// System.out.println("workerQueueIntArray" +
			// Arrays.toString(workerQueueIntArray));

			BpoWorker worker = new BpoWorker(workerId, workerName, workerQueueIntArray);

			workers.put(workerNodeId, worker);

		}

		return workers;

	}

	public List<BpoElement> getListOfElements(String excelFileName)
			throws InvalidFormatException, IOException {
		List<BpoElement> elements = new ArrayList<BpoElement>();
		fis = new FileInputStream(excelFileName);
		wb = WorkbookFactory.create(fis);
		elementSheet = wb.getSheet("Elements");
		int noOfRowsOfElements = elementSheet.getLastRowNum();
		Row rowOfElement = elementSheet.getRow(0);
		int maxCellOfElement = rowOfElement.getLastCellNum();

		Map<String, Integer> elementHeaderMap = new HashMap<String, Integer>();

		for (int h = 0; h < maxCellOfElement; h++) {

			String elementHeader = elementSheet.getRow(0).getCell(h).toString();
			elementHeaderMap.put(elementHeader, h);
		}

		for (int i = 1; i <= noOfRowsOfElements; i++) {
			// System.out.println(noOfRowsOfElements);
			String elementId = elementSheet.getRow(i).getCell(elementHeaderMap.get("Element ID")).toString();
			String elementName = elementSheet.getRow(i).getCell(elementHeaderMap.get("Element Name")).toString();
			String elementNodeId = elementSheet.getRow(i).getCell(elementHeaderMap.get("Node ID")).toString();
			String[] elempriorityArrayString = elementSheet.getRow(i).getCell(elementHeaderMap.get("Priority"))
					.toString().split("\\.");
			String elempriorityString = elempriorityArrayString[0];
			int elemPriorityInt = Integer.parseInt(elempriorityString);
			String[] elemQueueArrayString = elementSheet.getRow(i).getCell(elementHeaderMap.get("Queue Index"))
					.toString().split("\\.");
			String elemQueueString = elemQueueArrayString[0];
			int elemQueueInt = Integer.parseInt(elemQueueString);
			String elementFileLoc = elementSheet.getRow(i).getCell(elementHeaderMap.get("File Location")).toString();

			// Object extraDetailsObject = new Object ();
			// Object extraDetail = new Object();

			Map<String, Object> extraDetails = new HashMap<>();

			for (int j = 0; j < 3; j++) {
				String label = elementSheet.getRow(i).getCell(6 + (j * 2)).toString();

				// System.out.println(label);

				Object extraDetail = new Object();

				extraDetail = elementSheet.getRow(i).getCell(7 + (j * 2));

				extraDetails.put(label, extraDetail);

				// System.out.println(extraDetails.get(label));
			}

			//
//			System.out.println("elemID: " + elementId);
//			System.out.println("elemname: " + elementName);
//			System.out.println("elemnodeID: " + elementNodeId);
//			System.out.println("elempriority: " + elemPriorityInt);
//			System.out.println("elemqueue: " + elemQueueInt);
//			System.out.println("elemfileloc: " + elementFileLoc);
//			System.out.println("elemextra: " + extraDetails.toString());

			BpoElement element = new BpoElement(elementId, elementName, elementNodeId, elemQueueInt, elemPriorityInt,
					elementFileLoc, extraDetails);

			// System.out.println(element.getNodeId().toString());

			elements.add(element);

		}
		return elements;
	}
}
