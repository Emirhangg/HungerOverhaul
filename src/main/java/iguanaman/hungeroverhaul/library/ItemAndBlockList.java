package iguanaman.hungeroverhaul.library;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class ItemAndBlockList
{
    private static LinkedHashSet<Class<?>> classes = new LinkedHashSet<Class<?>>();

    private static HashSet<Item> items = new HashSet<Item>();

    private static HashSet<Block> blocks = new HashSet<Block>();

    private static List<ItemStack> itemStacks = new ArrayList<ItemStack>();

    public void add(Class<?> clazz)
    {
        classes.add(clazz);
    }

    public void add(Item item)
    {
        items.add(item);
        this.add(new ItemStack(item));
    }

    public void add(Block block)
    {
        blocks.add(block);
        this.add(new ItemStack(block));
    }

    public void add(ItemStack itemStack)
    {
        if (!itemStack.isEmpty() && itemStack.getItem() != Items.AIR)
        {
            itemStacks.add(itemStack);
        }
    }

    public void add(String objectOrClassName) throws ClassNotFoundException
    {
        if (objectOrClassName.contains(":"))
        {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(objectOrClassName));
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(objectOrClassName));

            if (item != Items.AIR)
            {
                this.add(item);
            }
            if (block != null)
            {
                this.add(block);
            }
        }
        else
        {
            Class<?> clazz = Class.forName(objectOrClassName);
            this.add(clazz);
        }
    }

    public boolean contains(Class<?> clazz)
    {
        if (classes.contains(clazz))
        {
            return true;
        }

        Iterator<Class<?>> itr = classes.iterator();
        while (itr.hasNext())
        {
            Class<?> testClass = itr.next();
            if (testClass.isAssignableFrom(clazz))
            {
                return true;
            }
        }

        return false;
    }

    public boolean contains(ItemStack itemStack)
    {
        if (!itemStack.isEmpty() && itemStack.getItem() != Items.AIR)
        {
            for (ItemStack curItemStack : itemStacks)
            {
                if (OreDictionary.itemMatches(curItemStack, itemStack, false))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(Item item)
    {
        if (items.contains(item))
        {
            return true;
        }

        if (this.contains(new ItemStack(item)))
        {
            return true;
        }

        return this.contains(item.getClass());

    }

    public boolean contains(Block block)
    {
        if (blocks.contains(block))
        {
            return true;
        }

        if (this.contains(new ItemStack(block)))
        {
            return true;
        }

        return this.contains(block.getClass());

    }
}
