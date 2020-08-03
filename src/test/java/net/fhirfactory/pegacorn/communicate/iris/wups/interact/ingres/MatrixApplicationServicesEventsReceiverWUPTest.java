/*
 * The MIT License
 *
 * Copyright 2020 MAHun.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.communicate.iris.wups.interact.ingres;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 *
 * @author MAHun
 */
@RunWith(Arquillian.class)
public class MatrixApplicationServicesEventsReceiverWUPTest {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixApplicationServicesEventsReceiverWUPTest.class); 
    
    @Inject
    CamelContext camelContext;
    
    @Deployment
    public static WebArchive createDeployment() {
        WebArchive testWAR;

        File[] fileSet = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
        LOG.debug(".createDeployment(): ShrinkWrap Library Set for Pegacorn-Platform-CommonCode, length --> {}", fileSet.length);
        for (int counter = 0; counter < fileSet.length; counter++) {
            File currentFile = fileSet[counter];
            LOG.trace(".createDeployment(): Shrinkwrap Entry --> {}", currentFile.getName());
        }

        File topologyFile = new File("/TopologyConfig.json");
        File topicFile = new File("/TopicsFile.json");
        testWAR = ShrinkWrap.create(WebArchive.class, "pegacorn-communicate-iris.war")
                .addAsLibraries(fileSet)
                .addPackages(true, "net.fhirfactory.pegacorn.communicate.iris")
                .addAsManifestResource(topologyFile, "/TopologyConfig.json")
                .addAsManifestResource(topicFile, "/TopicsFile.json")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
        Map<ArchivePath, Node> content = testWAR.getContent();
        Set<ArchivePath> contentPathSet = content.keySet();
        Iterator<ArchivePath> contentPathSetIterator = contentPathSet.iterator();
        while(contentPathSetIterator.hasNext()){
            ArchivePath currentPath = contentPathSetIterator.next();
            LOG.trace(".createDeployment(): testWare Entry Path --> {}", currentPath.get());
        }
        return (testWAR);
    }
    
    /**
     * Test of configure method, of class MatrixApplicationServicesEventsReceiverWUP.
     */
    @Test
    public void testConfigure() throws Exception {
        System.out.println("configure");
        MatrixApplicationServicesEventsReceiverWUP instance = new MatrixApplicationServicesEventsReceiverWUP();
        instance.configure();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWUPInstanceName method, of class MatrixApplicationServicesEventsReceiverWUP.
     */
    @Test
    public void testGetWUPInstanceName() {
        System.out.println("getWUPInstanceName");
        MatrixApplicationServicesEventsReceiverWUP instance = new MatrixApplicationServicesEventsReceiverWUP();
        String expResult = "";
        String result = instance.getWupInstanceName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWUPVersion method, of class MatrixApplicationServicesEventsReceiverWUP.
     */
    @Test
    public void testGetWUPVersion() {
        System.out.println("getWUPVersion");
        MatrixApplicationServicesEventsReceiverWUP instance = new MatrixApplicationServicesEventsReceiverWUP();
        String expResult = "";
        String result = instance.getVersion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEndpointComponentDefinition method, of class MatrixApplicationServicesEventsReceiverWUP.
     */
    @Test
    public void testGetEndpointComponentDefinition() {
        System.out.println("getEndpointComponentDefinition");
        MatrixApplicationServicesEventsReceiverWUP instance = new MatrixApplicationServicesEventsReceiverWUP();
        String expResult = "";
        String result = instance.getEndpointComponentDefinition();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEndpointProtocol method, of class MatrixApplicationServicesEventsReceiverWUP.
     */
    @Test
    public void testGetEndpointProtocol() {
        System.out.println("getEndpointProtocol");
        MatrixApplicationServicesEventsReceiverWUP instance = new MatrixApplicationServicesEventsReceiverWUP();
        String expResult = "";
        String result = instance.getEndpointProtocol();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEndpointProtocolLeadIn method, of class MatrixApplicationServicesEventsReceiverWUP.
     */
    @Test
    public void testGetEndpointProtocolLeadIn() {
        System.out.println("getEndpointProtocolLeadIn");
        MatrixApplicationServicesEventsReceiverWUP instance = new MatrixApplicationServicesEventsReceiverWUP();
        String expResult = "";
        String result = instance.getEndpointProtocolLeadIn();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEndpointProtocolLeadout method, of class MatrixApplicationServicesEventsReceiverWUP.
     */
    @Test
    public void testGetEndpointProtocolLeadout() {
        System.out.println("getEndpointProtocolLeadout");
        MatrixApplicationServicesEventsReceiverWUP instance = new MatrixApplicationServicesEventsReceiverWUP();
        String expResult = "";
        String result = instance.getEndpointProtocolLeadout();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
