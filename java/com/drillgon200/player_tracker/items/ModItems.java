package com.drillgon200.player_tracker.items;

import com.drillgon200.player_tracker.RefStrings;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModItems {

	public static final Item tracker_compass = new ItemTrackerCompass().setMaxStackSize(1).setUnlocalizedName("tracker_compass").setCreativeTab(CreativeTabs.tabMisc).setTextureName(RefStrings.MODID + ":tracker_compass");
	
	public static void init(){
		GameRegistry.registerItem(tracker_compass, tracker_compass.getUnlocalizedName());
	}
}
