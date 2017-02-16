package org.measure.platform.agent.smmengine.impl;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.measure.platform.agent.repository.api.IMeasureCatalogueService;
import org.measure.platform.agent.smmengine.api.IRemoteExecutionService;
import org.measure.smm.log.MeasureLog;
import org.measure.smm.measure.api.IDirectMeasure;
import org.measure.smm.measure.api.IMeasurement;
import org.measure.smm.remote.RemoteExecutionResult;
import org.measure.smm.remote.RemoteMeasureInstanceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RemoteExecutionService implements IRemoteExecutionService {

    private final Logger log = LoggerFactory.getLogger(RemoteExecutionService.class);

	@Inject
	private IMeasureCatalogueService measureCatalogue;

	@Override
	public RemoteExecutionResult executeMeasure(RemoteMeasureInstanceData measureData) {

		RemoteExecutionResult result = new RemoteExecutionResult();
		MeasureLog executionLog = new MeasureLog();
		
		executionLog.setMeasureInstanceName(measureData.getInstanceName());
		executionLog.setMeasureName(measureData.getMeasureName());

		IDirectMeasure measure = measureCatalogue.getMeasureImplementation(measureData.getMeasureName().substring(0, measureData.getMeasureName().indexOf("(") - 1));

		try {
			if (measure != null) {
				
				for(Entry<String,String> entry : measureData.getProperties().entrySet()){
					measure.getProperties().put(entry.getKey(), entry.getValue());
				}
				
				Date start = new Date();
				List<IMeasurement> measurements = measure.getMeasurement();
			
				executionLog.setExectionDate(new Date());
				executionLog.setExecutionTime(new Date().getTime() - start.getTime());
				executionLog.setMesurement(measurements);
				executionLog.setSuccess(true);

				for(Entry<String,String> entry : measureData.getProperties().entrySet()){
					if(!measure.getProperties().get(entry.getKey()).equals(entry.getValue())){
						result.getUpdatedProperties().put(entry.getKey(), measure.getProperties().get(entry.getKey()));
					}
				}
				
				
			}else{
				executionLog.setExceptionMessage("Measure Unknown on Agent");
				executionLog.setSuccess(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Execution Failled [" + measureData.getMeasureName() +"] :" + e.getMessage());
			
			executionLog.setExceptionMessage(e.getMessage());
			executionLog.setSuccess(false);
		}
		
		result.setExecutionLog(executionLog);
		return result;
	}

}
