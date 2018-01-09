/*
 * Copyright 2005 Wavechain Consulting LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.wavechain.utilities;

import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.jboss.mx.loading.UnifiedLoaderRepository3;

/*
Tutorial on this topic can be found at: 
http://www.jboss.org/wiki/Wiki.jsp?page=JBossClassLoadingUseCases

Conclusions:
	1.  Loading a class or a resource that exists within the same jar file as this class is done via
	    an instance of org.jboss.util.loading.DelegatingClassLoader
	2.  Loading a class or a resource that exists within a jar in the $JBOSS_HOME/server/default/lib directory
	    is done via the JBoss boot classloader: 
                        an instance of org.jboss.system.server.NoAnnotationURLClassLoader
	3.  All classes and resources from within the same .ear will get loaded into the class repository via
	    the same instance of an org.jboss.mx.loading.UnifiedClassLoader
	4.  ClassLoader.getSystemClassLoader() returns an instance of sun.misc.Launcher$AppClassLoader
	5.  Thread.currentThread().getContextClassLoader() returns an instance of org.jboss.util.loading.DelegatingClassLoader 
*/
public class ClassLoaderUtil {

	static Logger log = null;

	static {
		log = Logger.getLogger(ClassLoaderUtil.class);
	}

	/*  Resource Name must be similar to following:
		com/wavechain/system/LineItem.hbm.xml
	*/
	public static void listClassLoaderResources(String resourceName) throws Exception {
		ClassLoader cl = ClassLoaderUtil.class.getClassLoader();
		Enumeration resourceEnum = cl.getResources(resourceName);
		if(!resourceEnum.hasMoreElements()) {
			log.info("CL = "+cl+" : resource NOT found = "+ resourceName);
		} else{
			while(resourceEnum.hasMoreElements()) {
				log.info("CL = "+cl+" : resource = "+resourceEnum.nextElement());
			}
		}
	}

	public static void loadClass(String className) throws Exception {
		ClassLoader cl = ClassLoaderUtil.class.getClassLoader();
		try {
			Class classObj = Class.forName(className);
			log.info("CL = "+classObj.getClassLoader()+" : class found = "+className);
		} catch(ClassNotFoundException x) {
			log.info("CL = "+cl+" : class NOT found = "+className);
		}
	}
}
