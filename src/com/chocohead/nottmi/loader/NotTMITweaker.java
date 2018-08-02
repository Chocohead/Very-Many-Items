package com.chocohead.nottmi.loader;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.chocohead.nottmi.NotTMILog;

public class NotTMITweaker implements ITweaker {
	private List<String> args;

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
		this.args = new ArrayList<>(args);
		this.args.add("--version");
		this.args.add(profile);
		this.args.add("--gameDir");
		this.args.add(gameDir.getAbsolutePath());
		this.args.add("--assetsDir");
		this.args.add(assetsDir.getAbsolutePath());
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		NotTMILog.info("Injecting into class loader");
		
		classLoader.addTransformerExclusion("com.chocohead.nottmi.loader.");
		classLoader.registerTransformer(NotTMITransformer.class.getName());
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.client.main.Main";
	}

	@Override
	public String[] getLaunchArguments() {
		return args.toArray(new String[args.size()]);
	}
}