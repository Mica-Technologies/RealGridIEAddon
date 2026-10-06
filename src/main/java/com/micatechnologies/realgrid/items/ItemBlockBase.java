package com.micatechnologies.realgrid.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemBlockBase extends ItemBlock
{
    /** How far past its own block a clicked block's box may reach before the click is re-read. */
    private static final float OVERFLOW = 1.0e-3f;

    public ItemBlockBase(Block block)
    {
        super(block);
    }

    @Override
    public int getMetadata(int damage)
    {
        return 0;
    }

    /**
     * Some poles RealGrid hardware goes on have a box reaching past their own block: CSM's fiberglass
     * poles take in the climbing steps front and back. Minecraft's ray then enters that box before it
     * reaches the pole's own block, starts inside it, and reports where it leaves: the far face. A
     * mount clicked onto the front of such a pole would go on its back. When the hit lies outside the
     * clicked block, the click is read again against the block's own cube, from the player's eyes.
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ)
    {
        if (outside(hitX) || outside(hitY) || outside(hitZ))
        {
            Vec3d eye = player.getPositionEyes(1.0f);
            Vec3d end = eye.add(player.getLook(1.0f).scale(8.0));
            RayTraceResult cube = new AxisAlignedBB(pos).calculateIntercept(eye, end);
            if (cube != null)
            {
                facing = cube.sideHit;
                hitX = (float) (cube.hitVec.x - pos.getX());
                hitY = (float) (cube.hitVec.y - pos.getY());
                hitZ = (float) (cube.hitVec.z - pos.getZ());
            }
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    private static boolean outside(float hit)
    {
        return hit < -OVERFLOW || hit > 1.0f + OVERFLOW;
    }
}
