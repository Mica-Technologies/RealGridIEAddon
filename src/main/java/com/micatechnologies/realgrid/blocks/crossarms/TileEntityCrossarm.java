package com.micatechnologies.realgrid.blocks.crossarms;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * What a crossarm segment carries that its neighbours can't tell: the sign on its front face and
 * whether a wood arm's open ends have their metal end caps (issue #38). Both are set with the
 * Engineer's Hammer and drawn by the segment's multipart model through {@link BlockCrossarm#SIGN}
 * and {@link BlockCrossarm#CAPS}, so the client needs them too: they are sent with the chunk and
 * whenever they change.
 */
public class TileEntityCrossarm extends TileEntity
{
    private CrossarmSign sign = CrossarmSign.NONE;
    private boolean caps = false;

    public CrossarmSign getSign()
    {
        return sign;
    }

    public void setSign(CrossarmSign sign)
    {
        this.sign = sign;
        changed();
    }

    public boolean hasCaps()
    {
        return caps;
    }

    public void setCaps(boolean caps)
    {
        this.caps = caps;
        changed();
    }

    private void changed()
    {
        markDirty();
        if (world != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    // -----------------------------------------------------------------------
    // Saving and syncing
    // -----------------------------------------------------------------------

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setString("sign", sign.getName());
        nbt.setBoolean("caps", caps);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        sign = CrossarmSign.byName(nbt.getString("sign"));
        caps = nbt.getBoolean("caps");
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        readFromNBT(packet.getNbtCompound());
        if (world != null)
            world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);
    }
}
