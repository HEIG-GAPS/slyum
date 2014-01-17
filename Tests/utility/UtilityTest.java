/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author David
 */
public class UtilityTest {
  
  public UtilityTest() {
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
   * Test of count method, of class Utility.
   */
  @Test
  public void testCount() {
    System.out.println("count");
    int expResult = 2;
    
    List<Object> testList = new LinkedList<>();
    testList.add(new Integer(1));
    testList.add(new Integer(1));
    testList.add(new Double(1));
    testList.add(new Double(1));
    testList.add(new Double(1));
    testList.add(new Long(1));
    
    int result = Utility.count(Integer.class, testList);
    assertEquals(expResult, result);
  }

  /**
   * Test of scaleRect method, of class Utility.
   */
  @Test
  public void testScaleRect() {
    System.out.println("scaleRect");
    Rectangle rect = new Rectangle(1, 1, 1, 1);
    double scale = 2.0;
    Rectangle expResult = new Rectangle(2, 2, 2, 2);
    Rectangle result = Utility.scaleRect(rect, scale);
    assertEquals(expResult, result);
  }

  /**
   * Test of keystrokeToString method, of class Utility.
   */
  @Test
  public void testKeystrokeToString() {
    System.out.println("keystrokeToString");
    String a = "ctrl shift e";
    String expResult = "(Ctrl+Shift-E)";
    String result = Utility.keystrokeToString(a);
    assertEquals(expResult, result);
    a = "ctrl e";
    expResult = "(Ctrl-E)";
    result = Utility.keystrokeToString(a);
    assertEquals(expResult, result);
  }

  /**
   * Test of distanceToSegment method, of class Utility.
   */
  @Test
  public void testDistanceToSegment() {
    System.out.println("distanceToSegment");
    Point2D p1 = new Point(0, 0);
    Point2D p2 = new Point(10, 0);
    Point2D p3 = new Point(5, 5);
    Point2D expResult = new Point(5, 0);
    Point2D result = Utility.distanceToSegment(p1, p2, p3);
    assertEquals(expResult, result);
  }

  /**
   * Test of getExtension method, of class Utility.
   */
  @Test
  public void testGetExtension() {
    System.out.println("getExtension");
    File f = new File("test/test/test.fff.zzz.aaa");
    String expResult = "aaa";
    String result = Utility.getExtension(f);
    assertEquals(expResult, result);
  }

  /**
   * Test of growRectangle method, of class Utility.
   */
  @Test
  public void testGrowRectangle() {
    System.out.println("growRectangle");
    Rectangle rect = new Rectangle(2, 2, 2, 2),
              expResult = new Rectangle(2, 2, 2, 2);
    int size = 2;
    expResult.grow(size, size);
    Rectangle result = Utility.growRectangle(rect, size);
    assertEquals(expResult, result);
  }

  /**
   * Test of normalizeRect method, of class Utility.
   */
  @Test
  public void testNormalizeRect() {
    System.out.println("normalizeRect");
    Rectangle rect = new Rectangle(10, 10, -5, -5);
    Rectangle expResult = new Rectangle(5, 5, 5, 5);
    Rectangle result = Utility.normalizeRect(rect);
    assertEquals(expResult, result);
  }

  /**
   * Test of getAlphaColor method, of class Utility.
   */
  @Test
  public void testGetAlphaColor() {
    System.out.println("getAlphaColor");
    Color color = new Color(100, 100, 100);
    int alpha = 50;
    Color expResult = new Color(100, 100, 100, alpha);
    Color result = Utility.getAlphaColor(color, alpha);
    assertEquals(expResult, result);
  }
  
}
