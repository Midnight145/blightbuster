package exterminatorJeff.undergroundBiomes.api;

import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.RegistryNamespaced;

public class NamedItem extends Names
{
    protected int id;
    protected Item item;

    public NamedItem(String internalName)
    {
        super(internalName);
    }

    public NamedItem(NamedBlock block) {
        this(block.internal());
    }

    public void register(int _id, Item _item)
    {
        reRegister(_id, _item);
    }

    public void reRegister(int _id, Item _item) {
        this.id = _id;
        this.item = _item;
        Item current = Item.getItemById(_id);
        if (current != _item)
            Item.itemRegistry.addObject(_id, internal(), _item);
    }

    public Item cachedItem()
    {
        if (this.item == null) {
            this.item = ((Item)Item.itemRegistry.getObject(external()));
            if (this.item == null) throw new RuntimeException(internal() + " has no item");
        }
        return this.item;
    }

    public Item registeredItem() {
        Item result = (Item)Item.itemRegistry.getObject(internal());
        if (result == null) {
            result = (Item)Item.itemRegistry.getObject(external());
            if (result == null)
            {
                Object key;
                for (Iterator i$ = Item.itemRegistry.getKeys().iterator(); i$.hasNext(); key = i$.next());
                throw new RuntimeException();
            }
        }
        return result;
    }

    public IIcon registerIcons(IIconRegister iconRegister) {
        return iconRegister.registerIcon(external());
    }

    public boolean matches(Item matched) {
        return this.item.equals(matched);
    }
}