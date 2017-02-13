package org.measure.platform.agent.smmengine.api;

import org.measure.smm.remote.RemoteExecutionResult;
import org.measure.smm.remote.RemoteMeasureInstanceData;

public interface IRemoteExecutionService {
	public RemoteExecutionResult executeMeasure(RemoteMeasureInstanceData data);
}
