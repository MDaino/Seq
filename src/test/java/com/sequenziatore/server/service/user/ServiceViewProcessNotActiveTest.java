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
 * 	File contentente la classe ServiceViewProcessNotActiveTest
 * 
 *	@file		ServiceViewProcessNotActiveTest.java
 *	@author		DeSQ
 *	@date		2014-05-14
 *	@version	1.0
 */

package com.sequenziatore.server.service.user;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Query;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.sequenziatore.server.databaseutil.util.HibernateUtil;
import com.sequenziatore.server.entity.DataCollection;
import com.sequenziatore.server.entity.IEntity;
import com.sequenziatore.server.entity.Process;
import com.sequenziatore.server.entity.Step;
import com.sequenziatore.server.entity.Subscription;
import com.sequenziatore.server.entity.User;
import com.sequenziatore.server.entity.DataCollection.EnumState;
import com.sequenziatore.server.entity.Step.EnumParallelism;

/**
 *	Classe contenente tutti i test di unità dei metodi della classe ServiceViewProcessNotActive.
 *
 *	@author 	DeSQ
 */
public class ServiceViewProcessNotActiveTest {

	/**
	 * Pulisce il database prima di iniziare ad eseguire i test.
	 * @throws Exception segnala un problema di connessione al database
	 */
	@Before
	public void clean() throws Exception
	{
		//pulizia database
	    HibernateUtil.getSession().beginTransaction();
	    
	    //elimina le data collection
	    Query deleteDataCollection = HibernateUtil.getSession().createQuery("delete DataCollection");
	    deleteDataCollection.executeUpdate();
	    
	    //elimina le iscrizioni ai processi
	    Query unsubscribeToProcess = HibernateUtil.getSession().createQuery("delete Subscription");
	    unsubscribeToProcess.executeUpdate();
	    
	    //elimina i passi di tutti i processi
	    Query deleteTestStep = HibernateUtil.getSession().createQuery("delete Step");
	    deleteTestStep.executeUpdate();
	    
	    //elimina i processi
	    Query deleteTestProcess = HibernateUtil.getSession().createQuery("delete Process");
	    deleteTestProcess.executeUpdate();
	    
	    //elimina gli utenti registrati
	    Query deleteTestUser = HibernateUtil.getSession().createQuery("delete User");
	    deleteTestUser.executeUpdate();
	    
	    HibernateUtil.commitTransaction();
	}
	
	/**
	 *	Pulisce il database quando sono terminati tutti i test.
	 * @throws Exception segnala un problema di connessione al database
	 */
	@AfterClass
	public static void finalClean() throws Exception
	{
		//pulizia database
	    HibernateUtil.getSession().beginTransaction();
	    
	    //elimina le data collection
	    Query deleteDataCollection = HibernateUtil.getSession().createQuery("delete DataCollection");
	    deleteDataCollection.executeUpdate();
	    
	    //elimina le iscrizioni ai processi
	    Query unsubscribeToProcess = HibernateUtil.getSession().createQuery("delete Subscription");
	    unsubscribeToProcess.executeUpdate();
	    
	    //elimina i passi di tutti i processi
	    Query deleteTestStep = HibernateUtil.getSession().createQuery("delete Step");
	    deleteTestStep.executeUpdate();
	    
	    //elimina i processi
	    Query deleteTestProcess = HibernateUtil.getSession().createQuery("delete Process");
	    deleteTestProcess.executeUpdate();
	    
	    //elimina gli utenti registrati
	    Query deleteTestUser = HibernateUtil.getSession().createQuery("delete User");
	    deleteTestUser.executeUpdate();
	    
	    HibernateUtil.commitTransaction();
	}
	
	/**
	 * Test del metodo serviceOperation() che permette di visualizzare i processi non attivi a cui un utente ha partecipato.
	 * @throws Exception segnala un problema di connessione al database
	 */
	@Test
	public void testServiceOperation() throws Exception {
		ServiceViewProcessNotActive serviceViewProcessNotActive = new ServiceViewProcessNotActive();
		
		//creazione ambiente per l'esecuzione della query
        HibernateUtil.getSession().beginTransaction();
        
        //creazione utente
        User testUser = new User();
        testUser.setEmail("asd@asd.com");
        testUser.setIsAdmin(false);
        testUser.setPassword("test");
        testUser.setUsername("test");
        HibernateUtil.getSession().saveOrUpdate(testUser);
        
        //creazione processo
        Process testProcess = new Process();
        testProcess.setAvailable(true);
        testProcess.setDescription("Processo di test");
        testProcess.setEndSubscriptionDate(new GregorianCalendar(2020,12,31,23,59,59));
        testProcess.setClosingDate(new GregorianCalendar(2020,12,31,23,59,59));
        testProcess.setName("test");
        testProcess.setPublicationDate(new GregorianCalendar(2012,12,31,23,59,59));
        testProcess.setTotalLevel(1);
        HibernateUtil.getSession().saveOrUpdate(testProcess);
        
        //creazione di un passo per il processo
        Step testStep = new Step();
        testStep.setAdminVerify(false);
        testStep.setDescription("test");
        testStep.setIsGeolocation(false);
        testStep.setIsPhoto(false);
        testStep.setIsText(false);
        testStep.setLevel(1);
        testStep.setParallelism(EnumParallelism.AND);
        testStep.setIdProcess(testProcess);
        HibernateUtil.getSession().saveOrUpdate(testStep);
        
        //inserimento del passo nel processo
        List<Step> testStepList = new ArrayList<Step>();
        testStepList.add(testStep);
        testProcess.setSteps(testStepList);
        HibernateUtil.getSession().saveOrUpdate(testProcess);
        
        //iscrizione dell'utente al processo 
        Subscription testSubscription = new Subscription();
        testSubscription.setComplete(false);
        testSubscription.setIdUser(testUser);
        testSubscription.setIdProcess(testProcess);
        HibernateUtil.getSession().saveOrUpdate(testSubscription);
        
        //creazione di una data collection
        DataCollection testCollection = new DataCollection();
        testCollection.setState(EnumState.FAILED);
        testCollection.setIdStep(testStep);
        testCollection.setIdUser(testUser);
        testCollection.setPhoto("null");
        testCollection.setText("null");
        testCollection.setLongitude("null");
        testCollection.setLatitude("null");
        HibernateUtil.getSession().saveOrUpdate(testCollection);
	    HibernateUtil.commitTransaction();
	    
	    //creazione della lista di entità
	    List<IEntity> iEntityList = new ArrayList<IEntity>();
	    iEntityList.add(testUser);
        
        JSONObject jsonReturnedObject = serviceViewProcessNotActive.serviceOperation(iEntityList);
        
        assertEquals("notReceivedCloseProcess", jsonReturnedObject.get("Confirmation"));
        
        testProcess.setAvailable(true);
        
        HibernateUtil.getSession().beginTransaction();
        testSubscription.setComplete(true);
        HibernateUtil.getSession().saveOrUpdate(testSubscription);
        testCollection.setState(EnumState.VERIFIED);
        HibernateUtil.getSession().saveOrUpdate(testCollection);
        HibernateUtil.commitTransaction();
        
        jsonReturnedObject = serviceViewProcessNotActive.serviceOperation(iEntityList);
        assertEquals("receivedCloseProcess", jsonReturnedObject.get("Confirmation"));
	}

}
