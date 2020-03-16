package com.redhat.naps;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPMN_ParserTest {

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(BPMN_ParserTest.class);
        logger.info("main");

        String expensesProcessDef = "expenses.bpm";
        String trisoProcessDef = "IdentifyPatient1ServiceOPER.bpmn2";
        RuntimeManager manager = getRuntimeManager(expensesProcessDef);
        RuntimeEngine runtime = manager.getRuntimeEngine(null);
        KieSession ksession = runtime.getKieSession();

        // KieBase kbase = readKnowledgeBase();
        // KieSession ksession = createSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
                .startProcess("Operationalized aaaaaaaa", params);
    }

    private static RuntimeManager getRuntimeManager(String process) {
        try {
            RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().addAsset(
                    KieServices.Factory.get().getResources().newClassPathResource(process), ResourceType.BPMN2).get();
            return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        } catch (Throwable x) {
            x.printStackTrace();
            throw x;
        }
    }

    private static KieBase readKnowledgeBase() throws Exception {
        ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());

        File testFile = new File("target/classes", "expenses.bpmn");
        System.out.println("readKnowledgeBase() testFile exists = " + testFile.exists());
        Resource expensesResource = ResourceFactory.newFileResource(testFile);

        // Resource expensesResource =
        // ResourceFactory.newClassPathResource("/expenses.bpmn");
        // Resource trisoResource =
        // ResourceFactory.newClassPathResource("/IdentifyPatient1ServiceOPER.bpmn2");

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(expensesResource, ResourceType.BPMN2);
        // kbuilder.add(trisoResource, ResourceType.BPMN2);
        return kbuilder.newKieBase();
    }

    private static KieSession createSession(KieBase kbase) {
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory",
                "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory",
                "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        return kbase.newKieSession(config, EnvironmentFactory.newEnvironment());
    }

}
