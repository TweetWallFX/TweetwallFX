/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.devoxx2017be.dataprovider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.OffsetTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.bind.JsonbBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.tweetwall.devoxx.api.cfp.client.Schedule;

/**
 *
 * @author sven
 */
public class SessionDataTest {
    
    public SessionDataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of from method, of class SessionData.
     */
    @Test
    public void testFrom1035OnMonday() throws IOException {
        System.out.println("from");        
        URL jsonFile = this.getClass().getResource("/Devoxx2017BeMonday.json");
        try (InputStream inputStream = jsonFile.openStream()){
            Schedule schedule = JsonbBuilder.create().fromJson(inputStream, Schedule.class);
            List<SessionData> result = SessionData.from(schedule, OffsetTime.parse("10:35Z"));
            assertEquals(6, result.size());
        } 
    }

    @Test
    public void testFrom1300OnMonday() throws IOException {
        System.out.println("from");        
        URL jsonFile = this.getClass().getResource("/Devoxx2017BeMonday.json");
        try (InputStream inputStream = jsonFile.openStream()){
            Schedule schedule = JsonbBuilder.create().fromJson(inputStream, Schedule.class);
            List<SessionData> result = SessionData.from(schedule, OffsetTime.parse("13:00Z"));
            assertEquals(7, result.size());
        } 
    }
    
}
