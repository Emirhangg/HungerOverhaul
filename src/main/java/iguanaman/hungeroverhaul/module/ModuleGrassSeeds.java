package iguanaman.hungeroverhaul.module;

import iguanaman.hungeroverhaul.HungerOverhaul;
import iguanaman.hungeroverhaul.config.IguanaConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeHooks.SeedEntry;

public class ModuleGrassSeeds
{
    static class SeedEntry extends WeightedRandom.Item
    {
        public final ItemStack seed;
        public SeedEntry(ItemStack seed, int weight)
        {
            super(weight);
            this.seed = seed;
        }
        public ItemStack getStack(Random rand, int fortune)
        {
            return seed.copy();
        }
    }
    public static List<SeedEntry> hoeSeedList = new ArrayList<SeedEntry>();

    public static Class<?> SeedEntry = null;
    public static Constructor<?> SeedEntryConstructor = null;
    public static Field weightField = null;
    public static Field seedListField = null;
    public static Field seedField = null;

    public static ItemStack getSeedFromTillingGrass(World world)
    {
        if (IguanaConfig.removeTallGrassSeeds)
        {
            try
            {
                Object entry = WeightedRandom.getRandomItem(world.rand, hoeSeedList);
                if (entry != null && seedField.get(entry) != null)
                    return ((ItemStack) seedField.get(entry)).copy();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
        else
        {
            return ForgeHooks.getGrassSeed(world);
        }
    }

    @SuppressWarnings("unchecked")
    public static void init()
    {
        initReflection();

        List<Object> seedList = null;
        try
        {
            seedList = (List<Object>) seedListField.get(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        if (IguanaConfig.allSeedsEqual)
        {
            try
            {
                for (Object entry : seedList)
                {
                    weightField.set(entry, 1);
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        if (IguanaConfig.removeTallGrassSeeds)
        {
            HungerOverhaul.Log.info("Removing tall grass seeds");
            for (Object seedEntry : seedList)
            {
                hoeSeedList.add(seedEntry);
            }
            seedList.clear();
            MinecraftForge.addGrassSeed(null, 10);
        }
    }

    private static void initReflection()
    {
        try
        {
            for (Class<?> declaredClass : ForgeHooks.class.getDeclaredClasses())
            {
                if (declaredClass.getSimpleName().equals("SeedEntry"))
                {
                    SeedEntry = declaredClass;
                    break;
                }
            }
            SeedEntryConstructor = SeedEntry.getConstructor(ItemStack.class, int.class);
            seedField = SeedEntry.getDeclaredField("seed");
            seedField.setAccessible(true);
            seedListField = ForgeHooks.class.getDeclaredField("seedList");
            seedListField.setAccessible(true);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        try
        {
            weightField = SeedEntry.getSuperclass().getDeclaredField("field_76292_a");
            weightField.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            try
            {
                weightField = SeedEntry.getSuperclass().getDeclaredField("itemWeight");
                weightField.setAccessible(true);
            }
            catch (NoSuchFieldException e2)
            {
                throw new RuntimeException(e2);
            }
        }
    }
}
