package utility;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link Utility}.
 *
 * @author David
 */
class UtilityTest {

    /**
     * Test of count method, of class Utility.
     */
    @Test
    void testCount() {
        int expResult = 2;

        List<Object> testList = new LinkedList<>();
        testList.add(Integer.valueOf(1));
        testList.add(Integer.valueOf(1));
        testList.add(Double.valueOf(1));
        testList.add(Double.valueOf(1));
        testList.add(Double.valueOf(1));
        testList.add(Long.valueOf(1));

        int result = Utility.count(Integer.class, testList);
        assertEquals(expResult, result);
    }

    /**
     * Test of scaleRect method, of class Utility.
     */
    @Test
    void testScaleRect() {
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
    void testKeystrokeToString() {
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
    void testDistanceToSegment() {
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
    void testGetExtension() {
        File f = new File("test/test/test.fff.zzz.aaa");
        String expResult = "aaa";
        String result = Utility.getExtension(f);
        assertEquals(expResult, result);
    }

    /**
     * Test of growRectangle method, of class Utility.
     */
    @Test
    void testGrowRectangle() {
        Rectangle rect = new Rectangle(2, 2, 2, 2);
        Rectangle expResult = new Rectangle(2, 2, 2, 2);
        int size = 2;
        expResult.grow(size, size);
        Rectangle result = Utility.growRectangle(rect, size);
        assertEquals(expResult, result);
    }

    /**
     * Test of normalizeRect method, of class Utility.
     */
    @Test
    void testNormalizeRect() {
        Rectangle rect = new Rectangle(10, 10, -5, -5);
        Rectangle expResult = new Rectangle(5, 5, 5, 5);
        Rectangle result = Utility.normalizeRect(rect);
        assertEquals(expResult, result);
    }

    /**
     * Test of getAlphaColor method, of class Utility.
     */
    @Test
    void testGetAlphaColor() {
        javafx.scene.paint.Color color = javafx.scene.paint.Color.rgb(100, 100, 100);
        int alpha = 50;
        javafx.scene.paint.Color expResult = javafx.scene.paint.Color.rgb(100, 100, 100, alpha / 255.0);
        javafx.scene.paint.Color result = Utility.getAlphaColor(color, alpha);
        assertEquals(expResult, result);
    }
}
