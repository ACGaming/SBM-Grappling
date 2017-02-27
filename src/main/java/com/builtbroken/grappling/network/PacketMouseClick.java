package com.builtbroken.grappling.network;

import com.builtbroken.grappling.content.MovementHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/18/2016.
 */
public class PacketMouseClick extends Packet
{
    int slot;
    int button;
    int dwheel;
    boolean state;

    public PacketMouseClick()
    {
        //Needed for forge to construct the packet
    }

    public PacketMouseClick(int slotId, int button, boolean state, int dwheel)
    {
        this.slot = slotId;
        this.button = button;
        this.state = state;
        this.dwheel = dwheel;
    }

    @Override
    public void write(ByteBuf buffer)
    {
        buffer.writeInt(slot);
        buffer.writeInt(button);
        buffer.writeInt(dwheel);
        buffer.writeBoolean(state);
    }

    @Override
    public void read(ByteBuf buffer)
    {
        slot = buffer.readInt();
        button = buffer.readInt();
        dwheel = buffer.readInt();
        state = buffer.readBoolean();
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        player.addChatComponentMessage(new ChatComponentText("MouseButton: " + button + "  pressed: " + state + "  wheel: " + dwheel));

        if (button == 0)
        {
            if (MovementHandler.hasHook(player))
            {
                if (state)
                {
                    //TODO pull towards
                }
                else
                {
                    //TODO stop pulling
                }
            }
            else if (!state)
            {
                //TODO create on mouse release
            }
        }
        else if (button == 1)
        {
            if (MovementHandler.hasHook(player) && !state)
            {
                //TODO destroy hook
            }
        }
        else
        {
            //TODO scroll wheel movement
        }
    }
}