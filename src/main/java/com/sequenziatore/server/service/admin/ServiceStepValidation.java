/*
 * Copyright 2014 Dainese Matteo, De Nadai Andrea, Girotto Giacomo, Pavanello Mirko, Romagnosi Nicolò, Sartoretto Massimiliano, Visentin Luca
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 	File contentente la classe ServiceStepValidation
 * 
 *	@file		ServiceStepValidation.java
 *	@author		DeSQ
 *	@date		2014-05-12
 *	@version	1.0
 */

package com.sequenziatore.server.service.admin;

import java.util.List;

import com.sequenziatore.server.databaseutil.dao.DAOFactory;
import com.sequenziatore.server.databaseutil.dao.IDAOFactory;
import com.sequenziatore.server.databaseutil.util.HibernateUtil;
import com.sequenziatore.server.entity.DataCollection;
import com.sequenziatore.server.entity.IEntity;
import com.sequenziatore.server.entity.Subscription;
import com.sequenziatore.server.entity.DataCollection.EnumState;
import com.sequenziatore.server.entity.Step.EnumParallelism;

import org.json.JSONObject;

import com.sequenziatore.server.service.interfaceservice.IService;

/**
 *	La classe ServiceStepValidation offre il servizio di validazione di una raccolta dati.
 *
 *	@author 	DeSQ
 */
public class ServiceStepValidation implements IService {
 
	/** Rappresenta l'interfaccia che garantisce l'accesso alle classi DAO per accedere al database. */
	private IDAOFactory iDAOFactory;
	
	/**
	 * Permette la validazione di una raccolta dati grazie alle informazioni acquisite dal front-end.
	 * 
	 * @param entity contiene l'id della raccolta dati da validare
	 * @return l'esito dell'operazione di validazione
	 */
	@Override
	public JSONObject serviceOperation(List<IEntity> entity) {
		JSONObject objectJson=new JSONObject();
		iDAOFactory = new DAOFactory();
		DataCollection data=(DataCollection)entity.get(0);
		DataCollection dataCollection=null;
		try {
		    HibernateUtil.getSession().beginTransaction();
		    dataCollection = iDAOFactory.createDAOManagementProcessAdmin().findDataCollection(data);
		    if(dataCollection != null){
		    	if(!dataCollection.getState().equals(EnumState.TOVERIFY))
		    		objectJson.put("Confirmation", "AlreadyVerified");
		    	else if(data.getWrongText().toString().equals("true") == true && data.getWrongGeolocation().toString().equals("true") == true && data.getWrongPhoto().toString().equals("true") == true){
		    		dataCollection.setState(EnumState.VERIFIED);
		    		iDAOFactory.createDAOManagementProcessAdmin().insertDataCollection(dataCollection);
		    		this.lastStep(dataCollection);
		    		objectJson.put("Confirmation", "successValidation");
		    	}else{
		    		dataCollection.setState(EnumState.FAILED);
					dataCollection.setWrongText(data.getWrongText());
					dataCollection.setWrongPhoto(data.getWrongPhoto());
					dataCollection.setWrongGeolocation(data.getWrongGeolocation());
					objectJson.put("Confirmation", "successValidation");
					iDAOFactory.createDAOManagementProcessAdmin().insertDataCollection(dataCollection);
		    	}
		    }else objectJson.put("Confirmation", "wrongValidation");
		    HibernateUtil.commitTransaction();
		} catch (Exception e) {
			return objectJson.put("Confirmation", "connectionError");
		}
	    return objectJson;
	}
	
	/**
	 * Permette di verificare se è l'ultimo passo di un processo.
	 * 
	 * @param dataCollection contiene i dati di un passo validati
	 * @throws Exception
	 */
	private void lastStep(DataCollection dataCollection) throws Exception {
		if(dataCollection.getIdStep().getParallelism().equals(EnumParallelism.OR)){
			this.lastStepOr(dataCollection);
		}else if(dataCollection.getIdStep().getParallelism().equals(EnumParallelism.AND)){
			this.lastStepAnd(dataCollection);
		}else lastStepNot(dataCollection);
	}
	
	/**
	 * Permette di verificare se è l'ultimo passo di un processo in un livello in OR.
	 * 
	 * @param dataCollection contiene i dati di un passo validati
	 * @throws Exception
	 */
	private void lastStepOr(DataCollection dataCollection) throws Exception {
		List<DataCollection> datacollections=null;
		Subscription subsrcription=null;
		Integer lvlMax=new Integer(dataCollection.getIdStep().getIdProcess().getTotalLevel());
		datacollections = iDAOFactory.createDAOManagementProcessAdmin().findDataCollectionByLevel(dataCollection);
		for(int i=0;i<datacollections.size();i++){
			datacollections.get(i).setState(EnumState.SKIPPED);
			iDAOFactory.createDAOManagementProcessAdmin().insertDataCollection(datacollections.get(i));
		}
		if(dataCollection.getIdStep().getLevel().compareTo(lvlMax-1) == 0){
			subsrcription = iDAOFactory.createDAOManagementProcessAdmin().findSubscription(dataCollection.getIdUser(), dataCollection.getIdStep().getIdProcess());
			subsrcription.setComplete(true);
			iDAOFactory.createDAOManagementProcessAdmin().insertSubscription(subsrcription);
		}
	}
	
	/**
	 * Permette di verificare se è l'ultimo passo di un processo in un livello in AND.
	 * 
	 * @param dataCollection contiene i dati di un passo validati
	 * @throws Exception
	 */
	private void lastStepAnd(DataCollection dataCollection) throws Exception {
		Subscription subsrcription=null;
		List<DataCollection> datacollections=null;
		boolean verified=true;
		Integer lvlMax=new Integer(dataCollection.getIdStep().getIdProcess().getTotalLevel());
		if(dataCollection.getIdStep().getLevel().compareTo(lvlMax-1) == 0){
			datacollections = iDAOFactory.createDAOManagementProcessAdmin().findDataCollectionByLevel(dataCollection);
			for(int i=0; i<datacollections.size() && verified; i++){
				if(!(datacollections.get(i).getState().equals(EnumState.VERIFIED)))
					verified=false;
			}
			if(verified == true){
				subsrcription = iDAOFactory.createDAOManagementProcessAdmin().findSubscription(dataCollection.getIdUser(), dataCollection.getIdStep().getIdProcess());
				subsrcription.setComplete(true);
				iDAOFactory.createDAOManagementProcessAdmin().insertSubscription(subsrcription);
			}
		}
	}
	
	/**
	 * Permette di verificare se è l'ultimo passo di un processo in un livello con un solo passo.
	 * 
	 * @param dataCollection contiene i dati di un passo validati
	 * @throws Exception
	 */
	private void lastStepNot(DataCollection dataCollection) throws Exception{
		Integer lvlMax=new Integer(dataCollection.getIdStep().getIdProcess().getTotalLevel());
		Subscription subsrcription=null;
		if(dataCollection.getIdStep().getLevel().compareTo(lvlMax-1) == 0){
			subsrcription = iDAOFactory.createDAOManagementProcessAdmin().findSubscription(dataCollection.getIdUser(), dataCollection.getIdStep().getIdProcess());
			subsrcription.setComplete(true);
			iDAOFactory.createDAOManagementProcessAdmin().insertSubscription(subsrcription);
		}
	}
	 
}
 
