package iguanaman.hungeroverhaul.util;

import java.util.ArrayList;
import java.util.List;

import com.pam.harvestcraft.blocks.growables.BlockPamCrop;

import iguanaman.hungeroverhaul.module.PamsModsHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class BlockHelper
{
    public static List<ItemStack> modifyCropDrops(List<ItemStack> drops, IBlockState state, int minSeeds, int maxSeeds, int minProduce, int maxProduce)
    {
        List<ItemStack> modifiedDrops = new ArrayList<ItemStack>();

        int seeds = RandomHelper.getRandomIntFromRange(minSeeds, maxSeeds);
        int produce = RandomHelper.getRandomIntFromRange(minProduce, maxProduce);
        ItemStack seedItem = BlockHelper.getSeedsOfBlock(state, seeds);
        ItemStack produceItem = BlockHelper.getProduceOfBlock(state, produce);
        boolean produceIsNotSeed = (seedItem.getItem() != produceItem.getItem() || seedItem.getItemDamage() != produceItem.getItemDamage());

        for (ItemStack item : drops)
        {
            // don't include seed/produce already in the list; we'll add them back afterwards
            if (item.isItemEqual(seedItem) || item.isItemEqual(produceItem))
            {
                continue;
            }

            modifiedDrops.add(item);
        }

        // only add seeds if they are different from produce
        if (produceIsNotSeed && seedItem.stackSize > 0)
            modifiedDrops.add(seedItem);

        if (produceItem.stackSize > 0)
            modifiedDrops.add(produceItem);

        return modifiedDrops;
    }

    public static ItemStack getSeedOfBlock(IBlockState state)
    {
        return getSeedsOfBlock(state, 1);
    }

    public static ItemStack getSeedsOfBlock(IBlockState state, int num)
    {
        return new ItemStack(getSeedItem(state), num, getSeedMetadata(state));
    }

    public static ItemStack getProduceOfBlock(IBlockState state)
    {
        return getProduceOfBlock(state, 1);
    }

    public static ItemStack getProduceOfBlock(IBlockState state, int num)
    {
        return new ItemStack(getProduceItem(state), num, getProduceMetadata(state));
    }

    public static Item getSeedItem(IBlockState state)
    {
        Block block = state.getBlock();

        Item itemDropped = block.getItemDropped(state, RandomHelper.random, 0);
        if (Loader.isModLoaded("harvestcraft") && block instanceof BlockPamCrop)
        {
            Item seedForProduct = PamsModsHelper.productToSeedMap.get(itemDropped);
            if (seedForProduct != null)
                return seedForProduct;
        }
        return itemDropped;
    }

    public static Item getProduceItem(IBlockState state)
    {
        Block block = state.getBlock();

        return block.getItemDropped(state, RandomHelper.random, 0);
    }

    public static int getProduceMetadata(IBlockState state)
    {
        Block block = state.getBlock();

        return block.damageDropped(state);
    }

    public static int getSeedMetadata(IBlockState state)
    {
        Block block = state.getBlock();
        //TODO: FIX
        /*if (Loader.isModLoaded("Natura") && block == NContent.crops)
        {
            return ((CropBlock) block).seedDamageDropped(meta);
        }
        else
        {
            return block.damageDropped(0);
        }*/
        return block.damageDropped(state);
    }
}
