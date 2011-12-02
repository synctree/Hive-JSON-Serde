/*======================================================================*
 * Copyright (c) 2011, OpenX Technologies, Inc. All rights reserved.    *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License. Unless required     *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.openx.data.jsonserde;

import java.util.Map;
import java.util.HashMap;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import org.openx.data.jsonserde.json.JSONArray;
import org.apache.hadoop.io.Text;
import org.openx.data.jsonserde.json.JSONException;
import org.openx.data.jsonserde.json.JSONObject;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde.Constants;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.io.Writable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rcongiu
 */
public class JsonSerDeTest {
    
    public JsonSerDeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
        initialize();
    }
    
    @After
    public void tearDown() {
    }

    static JsonSerDe instance;
 
    static public void initialize() throws Exception {
        System.out.println("initialize");
        instance = new JsonSerDe();
        Configuration conf = null;
        Properties tbl = new Properties();
        tbl.setProperty(Constants.LIST_COLUMNS, "one,two,three,four");
        tbl.setProperty(Constants.LIST_COLUMN_TYPES, "boolean,float,array<string>,string");
        tbl.setProperty(JsonSerDe.PROP_RAW_JSON_COLUMN_NAME, "json");
        
        instance.initialize(conf, tbl);
    }

    /**
     * Test of deserialize method, of class JsonSerDe.
     */
    @Test
    public void testDeserialize() throws Exception {
        System.out.println("deserialize");
        Writable w = new Text("{\"one\":true,\"three\":[\"red\",\"yellow\",\"orange\"],\"two\":19.5,\"four\":\"poop\"}");
        Object expResult = null;
        JSONObject result = (JSONObject) instance.deserialize(w);
        assertEquals(result.get("four"),"poop");
        
        assertTrue( result.get("three") instanceof JSONArray);
        
        assertTrue( ((JSONArray)result.get("three")).get(0) instanceof String );
        assertEquals( ((JSONArray)result.get("three")).get(0),"red");
    }

 //   {"one":true,"three":["red","yellow",["blue","azure","cobalt","teal"],"orange"],"two":19.5,"four":"poop"}

    @Test
    public void testDeserialize2() throws Exception {
        final String JSON = "{\"one\":true,\"three\":[\"red\",\"yellow\",[\"blue\",\"azure\",\"cobalt\",\"teal\"],\"orange\"],\"two\":19.5,\"four\":\"poop\"}";

        Writable w = new Text(JSON);
        Object expResult = null;
        JSONObject result = (JSONObject) instance.deserialize(w);
        assertEquals(result.get("four"),"poop");
        
        assertTrue( result.get("three") instanceof JSONArray);
        
        assertTrue( ((JSONArray)result.get("three")).get(0) instanceof String );
        assertEquals( ((JSONArray)result.get("three")).get(0),"red");

        // test that the "json" virtual column exists
        assertTrue( result.get("json") instanceof String);
        // test that "json" column contains the original JSON
        assertEquals( result.get("json").toString(), JSON);
    }

    /**
     * Test of getSerializedClass method, of class JsonSerDe.
     */
    @Test
    public void testGetSerializedClass() {
        System.out.println("getSerializedClass");
        Class expResult = Text.class;
        Class result = instance.getSerializedClass();
        assertEquals(expResult, result);
       
    }

    /**
     * Test of serialize method, of class JsonSerDe.
     */
/*    @Test
    public void testSerialize() throws Exception {
        System.out.println("serialize");
        Object o = null;
        ObjectInspector oi = null;
        JsonSerDe instance = new JsonSerDe();
        Writable expResult = null;
        Writable result = instance.serialize(o, oi);
        assertEquals(expResult, result);
    }
     *  
     */
    
    
   // @Test
    public void testSerialize() throws SerDeException, JSONException {
        System.out.println("serialize");
        ArrayList row = new ArrayList();
        
        List<ObjectInspector> lOi = new LinkedList<ObjectInspector>();
        List<String> fieldNames = new LinkedList<String>();
        
        row.add("HELLO");
        fieldNames.add("atext");
        lOi.add(ObjectInspectorFactory.getReflectionObjectInspector(String.class, 
                   ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
        
        row.add(10);
        fieldNames.add("anumber");
        lOi.add(ObjectInspectorFactory.getReflectionObjectInspector(Integer.class, 
                   ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
        
        List<String> array = new LinkedList<String>();
        array.add("String1");
        array.add("String2");
        
        row.add(array);
        fieldNames.add("alist");
        lOi.add(ObjectInspectorFactory.getStandardListObjectInspector(
                ObjectInspectorFactory.getReflectionObjectInspector(String.class, 
                   ObjectInspectorFactory.ObjectInspectorOptions.JAVA)));
        
        Map<String,String> m = new HashMap<String,String>();
        m.put("k1","v1");
        m.put("k2","v2");
        
        row.add(m);
        fieldNames.add("amap");
        lOi.add(ObjectInspectorFactory.getStandardMapObjectInspector(
                ObjectInspectorFactory.getReflectionObjectInspector(String.class, 
                   ObjectInspectorFactory.ObjectInspectorOptions.JAVA),
                ObjectInspectorFactory.getReflectionObjectInspector(String.class, 
                   ObjectInspectorFactory.ObjectInspectorOptions.JAVA)));

        // add a simulated "json" field
        JSONObject json = new JSONObject();
        json.put("atext", "HELLO");
        json.put("anumber", 10);

        row.add(json.toString());
        fieldNames.add("json");
        lOi.add(ObjectInspectorFactory.getReflectionObjectInspector(String.class, 
                   ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
        
        
        StructObjectInspector soi = ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, lOi);
        
        Object result = instance.serialize(row, soi);
        
        JSONObject res = new JSONObject(result.toString());
        assertEquals(res.getString("atext"), row.get(0));
        
        assertEquals(res.get("anumber") , row.get(1));

        // assert "json" field was NOT serialized
        assertEquals(res.get("json"), null);
        
        // after serialization the internal contents of JSONObject are destroyed (overwritten by their string representation
       // (for map and arrays) 
      
       
        System.out.println("Serialized to " + result.toString());
        
    }
    
 }
