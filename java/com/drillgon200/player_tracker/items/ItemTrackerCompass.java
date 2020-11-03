package com.drillgon200.player_tracker.items;

import java.util.Arrays;
import java.util.List;

import com.drillgon200.player_tracker.RefStrings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemTrackerCompass extends Item {
	
	@SideOnly(Side.CLIENT)
	public IIcon[] textures;

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(!world.isRemote){
			List<String> names = Arrays.asList(MinecraftServer.getServer().getAllUsernames());
			if(names.size() >= 2){
				String prevName = getTrackedName(stack);
				int idx = names.indexOf(prevName);
				if(idx == -1)
					idx = 0;
				for(int i = idx; i < idx+names.size(); i ++){
					String name = names.get(i%names.size());
					if(!name.equals(player.getCommandSenderName())){
						setTrackedPlayerName(stack, name);
						player.addChatMessage(new ChatComponentText("Now Tracking: " + name));
						return stack;
					}
				}
			}
			player.addChatMessage(new ChatComponentText("No valid players online!"));
		}
		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
		String name = getTrackedName(p_77624_1_);
		if(name != null)
			p_77624_3_.add("Currently Tracking: " + name);
	}
	
	public static String getTrackedName(ItemStack stack){
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("trackedPlayer"))
			return null;
		return stack.getTagCompound().getString("trackedPlayer");
	}
	
	public static EntityPlayer getTrackedPlayer(World world, ItemStack stack){
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("trackedPlayer"))
			return null;
		return world.getPlayerEntityByName(stack.getTagCompound().getString("trackedPlayer"));
	}
	
	public static void setTrackedPlayerName(ItemStack stack, String name){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setString("trackedPlayer", name);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister p_94581_1_) {
		textures = new IIcon[32];
		for(int i = 0; i < 32; i ++){
			textures[i] = p_94581_1_.registerIcon(RefStrings.MODID + ":compass_" + String.format("%02d", i));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int pass) {
		return calculateTextureForAngle(stack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack p_77650_1_) {
		return calculateTextureForAngle(p_77650_1_);
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon calculateTextureForAngle(ItemStack stack){
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		EntityPlayer target = getTrackedPlayer(Minecraft.getMinecraft().theWorld, stack);
		if(target != null){
			Vec3 targetPos = target.getPosition(1);
			double diffX = p.posX-targetPos.xCoord;
			double diffZ = p.posZ-targetPos.zCoord;
			float distanceBeforeSpin = 16;
			if(diffX*diffX+diffZ*diffZ > distanceBeforeSpin*distanceBeforeSpin){
				Vec3 look = p.getLookVec();
				Vec3 toTarget = targetPos.subtract(p.getPosition(1));
				
				//Normalize
				double lenLook = Math.sqrt(look.xCoord*look.xCoord + look.zCoord*look.zCoord);
				double lenTarget = Math.sqrt(toTarget.xCoord*toTarget.xCoord + toTarget.zCoord*toTarget.zCoord);
				look.xCoord/=lenLook;
				look.zCoord/=lenLook;
				toTarget.xCoord/=lenTarget;
				toTarget.zCoord/=lenTarget;
				
				double angle = Math.acos(look.xCoord*toTarget.xCoord + look.zCoord*toTarget.zCoord);
				double cross = Math.signum(look.zCoord*toTarget.xCoord-toTarget.zCoord*look.xCoord);
				angle = (angle*cross+Math.PI)/(Math.PI*2);
				angle+=1F/64F;
				int index = (int) (-angle*32)+16;
				if(index < 0){
					index += 32;
				}
				return textures[index%32];
			}
		}
		return textures[(int) (System.currentTimeMillis()*0.03%32)];
	}
	
}
