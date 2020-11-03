package com.drillgon200.player_tracker;

import com.drillgon200.player_tracker.items.ModItems;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = RefStrings.MODID, name = RefStrings.NAME, version = RefStrings.VERSION)
public class MainRegistry {

	@EventHandler
	public void PreLoad(FMLPreInitializationEvent PreEvent) {
		ModItems.init();
	}
}
