package com.ss.design8or.service.job;

import java.util.Date;
import java.util.Timer;

import com.ss.design8or.error.exception.DesignationNotFoundException;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.Parameter;
import com.ss.design8or.service.DesignationService;
import com.ss.design8or.service.ParameterService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ezerbo
 *
 */
@Slf4j
public class StaleRequestHandler {
	
	private Timer timer;
	
	private final Parameter parameter;

	private final StaleRequestHandlerTask handlerTask;
	
	private final DesignationService designationService;
	
	public StaleRequestHandler(StaleRequestHandlerTask handlerTask,
							   ParameterService parameterService, DesignationService designationService) {
		this.handlerTask = handlerTask;
		this.designationService = designationService;
		parameter = parameterService.getParameter();
	}
	
	/**
	 * Start a timer to check for stale requests
	 * 
	 */
	public void startTimer() {
		log.info("Starting stale request handler timer");
		try {
			Designation designation = designationService.getCurrent();
			Long elapsedTime = new Date().getTime() - designation.getDesignationDate().getTime();//elapsed time between when the designation was created and now. 
			Long delay = elapsedTime + (parameter.getStaleRequestTimeDelay() * 60000);
			timer = new Timer();
			timer.schedule(handlerTask, delay);
		} catch (DesignationNotFoundException e) {
			log.info("Designation not found, no stale request handler timer will be created");
		}
	}
	
	
	/**
	 * Stops the current timer
	 */
	public void cancelTimer() {
		timer.cancel();
	}
}
