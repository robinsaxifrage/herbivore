package herbivore.game;
import herbivore.game.entity.EntityActor;
import herbivore.game.entity.EntityItem;
import java.util.Iterator;

/**
 * a class representing an actors inventory 
 * @see herbivore.game.entity.EntityItem
 * @see herbivore.game.entity.EntityActor#inventory
 * @author herbivore
 */
public class Inventory
    implements Iterable<EntityItem> {

    /**
     * creates a new inventory for the specified owner of the specified size
     * @param owner the owner to use
     * @param size the size to use
     */
    public Inventory(EntityActor owner, int size){
        this.owner = owner;
        items = new EntityItem[size];
    }
    
    /**
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<EntityItem> iterator(){
        return new Iterator(){
            @Override
            public boolean hasNext(){
                return index < items.length;
            }
            @Override
            public EntityItem next(){
                index++;
                return items[index - 1];
            }
            @Override
            public void remove(){}
            private int index;
        };
    }
   
    /**
     * adds the specified item to the inventory, if there is an available slot
     * @param item the item to add
     */
    public void add(EntityItem item){
        int slot = -1;
        for (int index = 0; index < items.length; index++){
            if (items[index] == null){
                slot = index;
                break;
            }
        }
        if (slot != -1){
            add(slot, item);
        }
    }
    /**
     * adds the specified item to the inventory at the selected index
     * @param index the index of an empty slot for the item
     * @param item the item to add
     */
    public void add(int index, EntityItem item){
        item.doInit();
        item.pickup(owner.getEthnicity());
        if (item.getSpace() != null){
            item.getSpace().remove(item);
        }
        item.setOwner(owner);
        items[index] = item;
        if (index == currentItem){
            current().equip();
        }
    }
    
    /**
     * drops the specified entity into the inventory owners space
     * @param item the item to drop
     */
    public void drop(EntityItem item){
        for (int index = 0; index < items.length; index++){
            if (items[index] != null && items[index].equals(item)){
                drop(index);
            }
        }
    }
    
    /**
     * drops the item at the specified index into the inventory owners space
     * @param index the index of the item to drop
     */
    public void drop(int index){
        if (index == currentItem){
            current().dequip();
        }
        items[index].editBounds(owner.getBounds().x + owner.getBounds().width/2 - items[index].getBounds().width/2, owner.getBounds().y + owner.getBounds().height/2, -1, -1);
        owner.getSpace().add(items[index], 0f, 0f);
        items[index].drop();
        remove(index);
    }
    
    /**
     * removes the selected item from the inventory, essentially deleting it
     * @param item the item to remove
     */
    public void remove(EntityItem item){
        for (int index = 0; index < items.length; index++){
            if (items[index] != null && items[index].equals(item)){
                remove(index);
            }
        }
    }
    
    /**
     * removes the item at the specified index from the inventory, essentially deleting it
     * @param index the index to free
     */
    public void remove(int index){
        items[index] = null;
    }
    
    /**
     * modifies the current index of the selected item
     * @param amount to amount to move the index
     */
    public void modCurrent(int amount){
        setCurrent(currentItem + amount);
    }
    
    /**
     * set the current item index, and equip and dequip items when needed
     * @param index the new current index
     */
    public void setCurrent(int index){
        if (current() != null){
            current().dequip();
        }
        currentItem = index;
        if (currentItem < 0){
            currentItem = items.length - 1;
        }
        else if (currentItem > items.length - 1){
            currentItem = 0;
        }
        if (current() != null){
            current().equip();
        }
    }
    
    /**
     * @return the current item
     */
    public EntityItem current(){
        return items[currentItem];
    }
    
    /**
     * returns the item at the specified index
     * @param index the index to get
     * @return the item at the index
     */
    public EntityItem get(int index){
        return items[index];
    }
    
    /**
     * @return whether or not this inventory is only empty slots
     */
    public boolean empty(){
        for (int index = 0; index < items.length; index++){
            if (items[index] != null){
                return false;
            }
        }
        return true;
    }
    
    /**
     * @return whether or not this inventory is only full slots
     */
    public boolean full(){
        for (int index = 0; index < items.length; index++){
            if (items[index] == null){
                return false;
            }
        }
        return true;
    }
    
    protected void setOwner(EntityActor owner){this.owner = owner;}
    
    public EntityActor getOwner(){return owner;}
    public int getCurrentItem(){return currentItem;}
    public int size(){return items.length;}
    
    private EntityItem[] items;
    private EntityActor owner;
    private int currentItem;
}
