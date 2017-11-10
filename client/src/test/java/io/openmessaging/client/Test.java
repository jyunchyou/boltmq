package io.openmessaging.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-11-10.
 */
public class Test {

    public static void main(String[] args){

        Logger logger = LoggerFactory.getLogger(Test.class);


        logger.trace("======trace") ;
        logger.info("fads");
        logger.warn("======warn");
        logger.error("=====assembly=error");
    }
}
