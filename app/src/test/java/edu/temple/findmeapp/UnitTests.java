package edu.temple.findmeapp;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class UnitTests {

    private Item item;
    private FoundItemMessage foundItemMessage;

    @Before
    public void setUp() {
        item = new Item(1, "Book", "My book");
        foundItemMessage = new FoundItemMessage(10.0, 10.0, "10:00 AM", "Found your item");
    }

    @Test
    public void checkItemCreated() {
        assertNotNull(item);
    }

    @Test
    public void checkFimCreated(){
        assertNotNull(item);
    }

    @Test
    public void checkNewItem() {
        assertEquals(1, item.getId());
        assertEquals("Book", item.getName());
        assertEquals("My book", item.getDescription());
    }

    @Test
    public void checkCloneItem() {
        Item clone = item.clone();
        assertEquals(item.getId(), clone.getId());
        assertEquals(item.getName(), clone.getName());
        assertEquals(item.getDescription(), clone.getDescription());
    }
    @Test
    public void modifyItemId() {
        Item clone = item.clone();
        clone.setId(2);
        assertNotEquals(item.getId(), clone.getId());
        assertEquals(2, clone.getId());
    }

    @Test
    public void modifyItemName() {
        Item clone = item.clone();
        clone.setName("Notebook");
        assertNotEquals(item.getName(), clone.getName());
        assertEquals("Notebook", clone.getName());
    }

    @Test
    public void modifyItemDescription() {
        Item clone = item.clone();
        clone.setDescription("My other book");
        assertNotEquals(item.getDescription(), clone.getDescription());
        assertEquals("My other book", clone.getDescription());
    }

    @Test
    public void itemFound() {
        Item clone = item.clone();
        clone.setLost(0);
        assertFalse(clone.isLost());
    }

    @Test
    public void itemLost() {
        Item clone = item.clone();
        clone.setLost(1);
        assertTrue(clone.isLost());
    }

    @Test
    public void modifyUserId() {
        Item clone = item.clone();
        clone.setId(1);
        assertEquals(1, clone.getId());
    }

    @Test
    public void checkFimLat() {
        assertEquals(10.0, foundItemMessage.getLat(), 0.1);
    }

    @Test
    public void checkFimLon() {
        assertEquals(10.0, foundItemMessage.getLon(), 0.1);
    }

    @Test
    public void checkFimFoundOn() {
        assertEquals("10:00 AM", foundItemMessage.getFoundOn());
    }

    @Test
    public void checkFimMessage() {
        assertEquals("Found your item", foundItemMessage.getMessage());
    }
}
