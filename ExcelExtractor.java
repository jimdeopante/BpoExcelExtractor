package com.svi.excelextractor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
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
import com.svi.bpo.api.operations.impl.rest.requests.BpoConnector;
import com.svi.bpo.api.operations.impl.rest.requests.ElementOperationsRestRequest;
import com.svi.bpo.api.operations.impl.rest.requests.NodeOperationsRestRequest;
import com.svi.bpo.api.operations.impl.rest.requests.OtherOperationsRestRequest;
import com.svi.bpo.api.operations.impl.rest.requests.WorkerOperationsRestRequest;

public class ExcelExtractor {

	static ExcelPharser pharser = new ExcelPharser();

	static BpoUtils utils = new BpoUtils();

	private static String excelFileName = "./BPO.xls";

	static List<BpoNode> nodes = new ArrayList<>();
	static HashMap<String, BpoWorker> workers = new HashMap<String, BpoWorker>();
	static List<BpoElement> elements = new ArrayList<BpoElement>();

	public static void main(String[] args) throws IOException, InvalidFormatException {

		// args[0] = excelFileName;

		nodes = pharser.getListOfNodes(excelFileName);

		workers = pharser.getListOfWorkers(excelFileName);

		elements = pharser.getListOfElements(excelFileName);

		utils.assignWorkersToNodes(workers, nodes);

		utils.assignElementsToNodes(elements);
		

	}

}