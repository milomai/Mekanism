package mekanism.common.block.states;

import mekanism.common.Tier.BaseTier;
import mekanism.common.base.IFactory.RecipeType;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;

public class BlockStateFacing extends BlockState
{
	public static final PropertyDirection facingProperty = PropertyDirection.create("facing");

	public BlockStateFacing(Block block, PropertyEnum typeProperty, PropertyBool activeProperty, PropertyEnum<BaseTier> tierProperty, PropertyEnum<RecipeType> recipeProperty)
	{
		super(block, facingProperty, typeProperty, activeProperty, tierProperty, recipeProperty);
	}
	
	public BlockStateFacing(Block block, PropertyEnum typeProperty, PropertyBool activeProperty)
	{
		super(block, facingProperty, typeProperty, activeProperty);
	}

	public BlockStateFacing(Block block, PropertyEnum typeProperty)
	{
		super(block, facingProperty, typeProperty);
	}

	public BlockStateFacing(Block block)
	{
		super(block, facingProperty);
	}
}
