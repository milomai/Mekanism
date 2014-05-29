package mekanism.common.miner;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import mekanism.api.Coord4D;
import mekanism.api.ItemInfo;
import mekanism.common.IBoundingBlock;
import mekanism.common.tile.TileEntityDigitalMiner;
import mekanism.common.util.MekanismUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ThreadMinerSearch extends Thread
{
	public TileEntityDigitalMiner tileEntity;

	public State state = State.IDLE;

	public BitSet oresToMine = new BitSet();

	public Map<ItemInfo, Boolean> acceptedItems = new HashMap<ItemInfo, Boolean>();

	public int found = 0;

	public ThreadMinerSearch(TileEntityDigitalMiner tile)
	{
		tileEntity = tile;
	}

	@Override
	public void run()
	{
		state = State.SEARCHING;

		if(!tileEntity.inverse && tileEntity.filters.isEmpty())
		{
			state = State.FINISHED;
			return;
		}

		Coord4D coord = tileEntity.getStartingCoord();
		int diameter = tileEntity.getDiameter();
		int size = tileEntity.getTotalSize();
		ItemInfo info = new ItemInfo(0, 0);

		for(int i = 0; i < size; i++)
		{
			int x = coord.xCoord+i%diameter;
			int z = coord.zCoord+(i/diameter)%diameter;
			int y = coord.yCoord+(i/diameter/diameter);

			if(tileEntity.isInvalid())
			{
				return;
			}

			if(tileEntity.xCoord == x && tileEntity.yCoord == y && tileEntity.zCoord == z)
			{
				continue;
			}

			if(!tileEntity.getWorldObj().getChunkProvider().chunkExists(x >> 4, z >> 4))
			{
				continue;
			}

			TileEntity bte;
			if ((bte = tileEntity.getWorldObj().getTileEntity(x, y, z)) != null && bte instanceof IBoundingBlock)
		{
				continue;
			}

			info.block = tileEntity.getWorldObj().getBlock(x, y, z);
			info.meta = tileEntity.getWorldObj().getBlockMetadata(x, y, z);

			if(info.block != null && info.block != Blocks.bedrock)
			{
				boolean canFilter = false;

				if(acceptedItems.containsKey(info))
				{
					canFilter = acceptedItems.get(info);
				}
				else {
					ItemStack stack = new ItemStack(info.block, 1, info.meta);

					if(tileEntity.replaceStack != null && tileEntity.replaceStack.isItemEqual(stack))
					{
						continue;
					}

					boolean hasFilter = false;

					for(MinerFilter filter : tileEntity.filters)
					{
						if(filter.canFilter(stack))
						{
							hasFilter = true;
						}
					}

					canFilter = tileEntity.inverse ? !hasFilter : hasFilter;
					acceptedItems.put(info, canFilter);
				}

				if(canFilter)
				{
					oresToMine.set(i);
					found++;
				}
			}
		}

		state = State.FINISHED;
		tileEntity.oresToMine = oresToMine;
		MekanismUtils.saveChunk(tileEntity);
	}

	public void reset()
	{
		state = State.IDLE;
	}

	public static enum State
	{
		IDLE("Not ready"),
		SEARCHING("Searching"),
		PAUSED("Paused"),
		FINISHED("Ready");

		public String desc;

		private State(String s)
		{
			desc = s;
		}
	}
}
