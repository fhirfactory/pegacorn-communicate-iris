package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.work;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.workshops.base.Workshop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatrixTaskExecutorWorkshop extends Workshop {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixTaskExecutorWorkshop.class);
    private static String WORKSHOP_NAME = "MatrixTaskExecutor";
    private static String WORKSHOP_VERSION = "1.0.0";

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWorkshopName() {
        return (WORKSHOP_NAME);
    }

    @Override
    protected String specifyWorkshopVersion() {
        return (WORKSHOP_VERSION);
    }

    @Override
    protected TopologyNodeTypeEnum specifyWorkshopType() {
        return (TopologyNodeTypeEnum.WORKSHOP);
    }

    @Override
    protected void invokePostConstructInitialisation() {

    }
}
