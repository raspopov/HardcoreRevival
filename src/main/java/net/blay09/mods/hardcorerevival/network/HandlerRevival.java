package net.blay09.mods.hardcorerevival.network;

import net.blay09.mods.hardcorerevival.ModConfig;
import net.blay09.mods.hardcorerevival.handler.RescueHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;
import java.util.List;

public class HandlerRevival implements IMessageHandler<MessageRevival, IMessage> {
	@Override
	@Nullable
	public IMessage onMessage(MessageRevival message, MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
			EntityPlayer player = ctx.getServerHandler().player;
			if(player.getHealth() <= 0) {
				return;
			}
			if(message.isActive()) {
				final float range = ModConfig.maxRescueDist;
				List<EntityPlayer> candidates = player.world.getEntitiesWithinAABB(EntityPlayer.class, player.getEntityBoundingBox().grow(range), p -> p != null && p.getHealth() <= 0f);
				float minDist = Float.MAX_VALUE;
				EntityPlayer target = null;
				for (EntityPlayer candidate : candidates) {
					float dist = candidate.getDistanceToEntity(player);
					if (dist < minDist) {
						target = candidate;
						minDist = dist;
					}
				}
				if (target != null) {
					RescueHandler.startRescue(player, target);
				}
			} else {
				RescueHandler.abortRescue(player);
			}
		});
		return null;
	}
}
