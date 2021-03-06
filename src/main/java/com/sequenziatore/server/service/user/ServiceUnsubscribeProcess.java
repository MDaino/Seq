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
 * 	File contentente la classe ServiceUnsubscribeProcess
 * 
 *	@file		ServiceUnsubscribeProcess.java
 *	@author		DeSQ
 *	@date		2014-05-12
 *	@version	1.0
 */
package com.sequenziatore.server.service.user;

import java.util.List;

import org.json.JSONObject;

import com.sequenziatore.server.databaseutil.dao.DAOFactory;
import com.sequenziatore.server.databaseutil.dao.IDAOFactory;
import com.sequenziatore.server.databaseutil.util.HibernateUtil;
import com.sequenziatore.server.entity.IEntity;
import com.sequenziatore.server.entity.Process;
import com.sequenziatore.server.entity.User;
import com.sequenziatore.server.service.interfaceservice.IService;

/**
 *	La classe ServiceUnsubscribeProcess offre il servizio per cancellare l'iscrizione a un processo.
 *
 *	@author 	DeSQ
 */
public class ServiceUnsubscribeProcess implements IService {
	
	/** Rappresenta l'interfaccia che garantisce l'accesso alle classi DAO per accedere al database. */
	private IDAOFactory iDAOFactory;
	
	/**
	 * Permette ad un utente di cancellare l'iscrizione a un processo.
	 * 
	 * @param entity contiene l'id dell'utente che fa la richiesta e id del processo a cui si vuole cancellare la partecipazione
	 * @return il risultato dell'avvenuta o meno cancellazione del iscrizione al processo
	 */
	@Override
	public JSONObject serviceOperation(List<IEntity> entity) {
		JSONObject objectJson=new JSONObject();
		iDAOFactory = new DAOFactory();
		User user=(User)entity.get(0);
		Process process=(Process)entity.get(1);
		Process processStep = null;
		try {
		    HibernateUtil.getSession().beginTransaction();
		    processStep = iDAOFactory.createDAOManagementProcessUser().findProcessById(process);
		    if(processStep != null){
		    	iDAOFactory.createDAOManagementProcessUser().deleteSubscription(user,process);
			    for(int i=0;i<processStep.getSteps().size();i++){
			    	iDAOFactory.createDAOManagementProcessUser().deleteDataCollection(user,processStep.getSteps().get(i));
			    }
			    objectJson.put("Confirmation", "successUnsubscribe");
		    }else objectJson.put("Confirmation", "notSuccessUnsubscribe");
		    HibernateUtil.commitTransaction();
		} catch(Exception e) {
			return objectJson.put("Confirmation", "connectionError");
		}
	    return objectJson;
	}

}
