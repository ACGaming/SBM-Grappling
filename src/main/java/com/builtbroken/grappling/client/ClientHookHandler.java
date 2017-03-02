package com.builtbroken.grappling.client;

import com.builtbroken.grappling.GrapplingHookMod;
import com.builtbroken.grappling.content.Hook;
import com.builtbroken.grappling.content.MovementHandler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/28/2017.
 */
public class ClientHookHandler
{
    /** Client side instance of the player's grappling hook */
    public static Hook hook;

    public static void setHook(Hook hook)
    {
        ClientHookHandler.hook = hook;
        //Override movement controls to improve handling
        if (hook != null && !(Minecraft.getMinecraft().thePlayer.movementInput instanceof MovementInputOverride))
        {
            Minecraft.getMinecraft().thePlayer.movementInput = new MovementInputOverride(Minecraft.getMinecraft().thePlayer.movementInput);
        }
    }

    @SubscribeEvent
    public void playerTickEvent(TickEvent.PlayerTickEvent event)
    {
        if (hook != null && event.player == Minecraft.getMinecraft().thePlayer)
        {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

            double xDifference = hook.x - player.posX;
            double yDifference = hook.y - player.posY;
            double zDifference = hook.z - player.posZ;
            double distance = Math.sqrt(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);

            //Stop motion if outside of max distance
            if (distance > hook.distance)
            {
                player.motionX = 0;
                player.motionY = 0;
                player.motionZ = 0;
            }

            if (!player.onGround && hook.movement == 0)
            {
                //Reset motion so we fall / move

                //If we are not bellow the point we need to add motion
                if (Math.abs(xDifference) > 0.01 || Math.abs(zDifference) > 0.01)
                {
                    //TODO adjust pull motion by distance to reduce jerking motion
                    //TODO add moment along a curve to improve visual affect
                    Vec3 motion = MovementHandler.getPullDirection(hook, player);

                    player.motionX = motion.xCoord * GrapplingHookMod.HOOK_PULL_PERCENT;
                    player.motionZ = motion.zCoord * GrapplingHookMod.HOOK_PULL_PERCENT;
                }

                //Add gravity if we are above the point
                if (yDifference < 0)
                {
                    player.motionY = -0.15;
                }
            }

            if (event.phase == TickEvent.Phase.END)
            {
                FxRope rope = new FxRope(event.player.worldObj,
                        player.posX,
                        player.posY - 0.8,
                        player.posZ,
                        hook.x, hook.y, hook.z, 1);
                FMLClientHandler.instance().getClient().effectRenderer.addEffect(rope);
            }
        }
    }
}
